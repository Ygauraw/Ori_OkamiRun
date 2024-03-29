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
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCGLSurfaceView;

/*
 * ヒツジ
 */

public class Hituji{
	
	private static final String TAG = "myTag";

	private final CCLayer layer;
	private final int ptmRatio;
	private int baseLevel;//重なり順の初期位置
	
	private CGPoint cPoint;//中央点
	
	private CCSprite backSprite;
	private CCSprite hitujiSprite;//オオカミ
	private CCAnimate animateRun;//走る
	private CCAnimate animateJump;//ジャンプ
	
	private static final int TAG_BACK  = 0;
	private static final int TAG_ANIME = 0;

	private boolean running;

	//x,yはメートル換算
	public Hituji(CCLayer layer, int ptmRatio, int baseLevel,
						float x, float y, float size){
		
		this.layer     = layer;
		this.ptmRatio  = ptmRatio;
		this.baseLevel = baseLevel;
		
		cPoint = CGPoint.make(x, y);
		
		init(x, y, size);
	}
	
	private void init(float x, float y, float size){

		/////////////////////
        //背景のスプライトシート
		CCSpriteSheet ssBack = CCSpriteSheet.spriteSheet("hituji_base.png", 100);
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
		//オオカミのスプライトシート
		CCSpriteSheet shonenSSheet = CCSpriteSheet.spriteSheet("hituji_anime.png", 100);
		layer.addChild(shonenSSheet, 0, TAG_ANIME);
		
		CGRect masuRect = CGRect.make(0, 0, 200, 280);
		
		hitujiSprite = new CCSprite();
		hitujiSprite.setTexture(shonenSSheet.getTexture());
		hitujiSprite.setTextureRect(masuRect);
		
		hitujiSprite.setScale(bw/rect.size.width);//背景と同じスケールにする
		
		hitujiSprite.setPosition(x * ptmRatio, y * ptmRatio);
		
		layer.addChild(hitujiSprite, baseLevel++);//重なり順の数字も上げておく

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
		animateJump = CCAnimate.action(animationJump, false);
		
	}
	
	//走る
	public void run(){
		//Log.d(TAG, "run");
		if(!running){
			CCSequence run = CCSequence.actions(animateRun);
			CCRepeatForever actions = CCRepeatForever.action(run);//repeat
			hitujiSprite.runAction(actions);
			running = true;
		}
	}
	
	//死ぬ
	public void die(){
		//Log.d(TAG, "die");
		CCSpawn spawn = CCSpawn.actions(
				CCJumpBy.action(0.8f, CGPoint.ccp(0,0), 100, 1),
				CCRotateBy.action(0.8f, 360),
				animateJump);
		CCCallFuncN dieDone = CCCallFuncN.action(this, "dieDone");
		CCSequence actions  = CCSequence.actions(CCDelayTime.action(0.4f), spawn, dieDone);
		hitujiSprite.runAction(actions);
		running = false;
	}
	
	//死んだ後の処理
	public void dieDone(Object sender){
		//Log.d(TAG, "dieDone");
		backSprite.removeFromParentAndCleanup(true);
		hitujiSprite.removeFromParentAndCleanup(true);
	}
}
