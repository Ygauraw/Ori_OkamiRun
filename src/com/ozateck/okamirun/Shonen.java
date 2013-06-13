package com.ozateck.okamirun;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.MotionEvent;
import android.util.Log;

import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCBlink;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCGLSurfaceView;

/*
 * オオカミ少年
 */

public class Shonen{
	
	private static final String TAG = "myTag";

	private final CCLayer layer;
	private final int ptmRatio;
	private int baseLevel;//重なり順の初期位置
	
	private CCSprite  backSprite;
	private CCSprite  shonenSprite;//少年
	private CCAnimate animateRun;//走る
	private CCAnimate animateJump;//ジャンプ
	
	private static final int TAG_BACK  = 0;
	private static final int TAG_ANIME = 0;
	
	private boolean running;
	private boolean jumping;

	//x,yはメートル換算
	public Shonen(CCLayer layer, int ptmRatio, int baseLevel,
						float x, float y, float size){
		
		this.layer     = layer;
		this.ptmRatio  = ptmRatio;
		this.baseLevel = baseLevel;
		
		init(x, y, size);
	}
	
	private void init(float x, float y, float size){

		/////////////////////
        //背景のスプライトシート
		CCSpriteSheet ssBack = CCSpriteSheet.spriteSheet("shonen_base.png", 100);
		layer.addChild(ssBack, 0, TAG_BACK);
		
		CGRect rect = CGRect.make(0, 0, 210, 280);
		
		backSprite = new CCSprite();
		backSprite.setTexture(ssBack.getTexture());
		backSprite.setTextureRect(rect);

		float bw = size * ptmRatio;//メートル換算でのピクセル数
		float bh = (rect.size.height / rect.size.width) * bw;
		
		backSprite.setScaleX(bw/rect.size.width);
		backSprite.setScaleY(bh/rect.size.height);
		
		backSprite.setPosition(x * ptmRatio, y * ptmRatio);
		
		layer.addChild(backSprite, baseLevel++);//重なり順の数字も上げておく
		
		/////////////////////
		//少年のスプライトシート
		CCSpriteSheet shonenSSheet = CCSpriteSheet.spriteSheet("shonen_anime.png", 100);
		layer.addChild(shonenSSheet, 0, TAG_ANIME);
		
		CGRect shonenRect = CGRect.make(0, 0, 200, 280);
		
		shonenSprite = new CCSprite();
		shonenSprite.setTexture(shonenSSheet.getTexture());
		shonenSprite.setTextureRect(shonenRect);
		
		shonenSprite.setScale(bw/rect.size.width);//背景と同じスケールにする
		
		shonenSprite.setPosition(x * ptmRatio, y * ptmRatio);
		
		layer.addChild(shonenSprite, baseLevel++);//重なり順の数字も上げておく

		/////////////////////
		//フレーム
		CCSpriteFrame frame0 = CCSpriteFrame.frame(
				shonenSSheet.getTexture(), CGRect.make(0, 0*280, 210, 280), CGPoint.ccp(0, 0));
		CCSpriteFrame frame1 = CCSpriteFrame.frame(
				shonenSSheet.getTexture(), CGRect.make(0, 1*280, 210, 280), CGPoint.ccp(0, 0));
		CCSpriteFrame frame2 = CCSpriteFrame.frame(
				shonenSSheet.getTexture(), CGRect.make(0, 2*280, 210, 280), CGPoint.ccp(0, 0));
		CCSpriteFrame frame3 = CCSpriteFrame.frame(
				shonenSSheet.getTexture(), CGRect.make(0, 3*280, 210, 280), CGPoint.ccp(0, 0));
		CCSpriteFrame frame4 = CCSpriteFrame.frame(
				shonenSSheet.getTexture(), CGRect.make(210, 0*280, 210, 280), CGPoint.ccp(0, 0));
		CCSpriteFrame frame5 = CCSpriteFrame.frame(
				shonenSSheet.getTexture(), CGRect.make(210, 1*280, 210, 280), CGPoint.ccp(0, 0));
		CCSpriteFrame frame6 = CCSpriteFrame.frame(
				shonenSSheet.getTexture(), CGRect.make(210, 2*280, 210, 280), CGPoint.ccp(0, 0));
		CCSpriteFrame frame7 = CCSpriteFrame.frame(
				shonenSSheet.getTexture(), CGRect.make(210, 3*280, 210, 280), CGPoint.ccp(0, 0));
		
		/////////////////////
		//アニメーション(走る)
		CCAnimation animationRun = CCAnimation.animation("animationRun", 0.1f);
			animationRun.addFrame(frame0);
			animationRun.addFrame(frame1);
			animationRun.addFrame(frame2);
			animationRun.addFrame(frame3);
		animateRun = CCAnimate.action(animationRun, true);

		/////////////////////
		//アニメーション(ジャンプ)
		CCAnimation animationJump = CCAnimation.animation("animationJump", 0.1f);
			animationJump.addFrame(frame4);
			animationJump.addFrame(frame5);
			animationJump.addFrame(frame6);
		animateJump = CCAnimate.action(animationJump, true);
		
		running = false;
		jumping = false;
		
	}
	
	//走る
	public void run(){
		//Log.d(TAG, "run");
		if(!running){
			CCSequence run = CCSequence.actions(animateRun);
			CCRepeatForever actions = CCRepeatForever.action(run);//repeat
			shonenSprite.runAction(actions);
			running = true;
			jumping = false;
		}
	}
	
	//ジャンプ
	public void jump(){
		//Log.d(TAG, "jump");
		if(!jumping){
			CCSpawn spawn = CCSpawn.actions(
					CCJumpBy.action(0.8f, CGPoint.ccp(0,0), 200, 1),
					animateJump);
			CCCallFuncN jumpDone = CCCallFuncN.action(this, "jumpDone");
			CCSequence actions = CCSequence.actions(spawn, jumpDone);
			shonenSprite.runAction(actions);
			running = false;
			jumping = true;
		}
	}
	
	//ジャンプ後の処理
	public void jumpDone(Object sender){
		//Log.d(TAG, "jumpDone");
		run();
	}
	
	//ダメージ
	public void damage(){
		//Log.d(TAG, "damage");
		CCBlink blink = CCBlink.action(1, 5);
		shonenSprite.runAction(blink);
	}
	
	//タッチ判定
	public boolean isInside(CGPoint point){
		CGRect rect = backSprite.getBoundingBox();
		
		CGRect innerRect = CGRect.make(
				rect.origin.x, rect.origin.y,
				rect.size.width,
				rect.size.height);
		
		if(innerRect.contains(point.x, point.y)){
			return true;
		}else{
			return false;
		}
	}

	//当たり判定のRect
	public CGRect getHittingRect(){
		//小さい判定にしておく
		CGRect baseRect = shonenSprite.getBoundingBox();
		float cx = baseRect.origin.x;
		float cy = baseRect.origin.y;
		float width  = baseRect.size.width * 80 / 100;
		float height = baseRect.size.height * 80 / 100;
		CGRect hittingRect = CGRect.make(cx+(baseRect.size.width-width)/2,
			     cy-(baseRect.size.height-height)/2,
			     width, height);
		return hittingRect;
	}
}
