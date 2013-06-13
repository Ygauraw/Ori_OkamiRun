package com.ozateck.okamirun;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

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
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCGLSurfaceView;

/*
 * だるま
 */

public class Daruma extends Obstacle{
	
	private float fromX, fromY, size;
	private float destX, destY, delay;
	
	private CCSprite darumaSprite;//だるま
	
	private static final int TAG_DARUMA = 0;
	
	private boolean moving;
	private boolean falling;
	
	private Random random;

	//x,yはメートル換算
	public Daruma(CCLayer layer, int ptmRatio, int baseLevel,
				float fromX, float fromY, float size,
				float destX, float destY, float delay){
		
		super(layer, ptmRatio, baseLevel);
		
		this.fromX = fromX;
		this.fromY = fromY;
		this.size  = size;
		this.destX = destX;
		this.destY = destY;
		this.delay = delay;
		
		random = new Random();
		
		init();
	}
	
	public void init(){
		
		CGRect rect = CGRect.make(0, 0, 90, 100);

		float bw = size * ptmRatio;//メートル換算でのピクセル数
		float bh = (rect.size.height / rect.size.width) * bw;
		
		/////////////////////
		//だるまのスプライトシート
		CCSpriteSheet shonenSSheet = CCSpriteSheet.spriteSheet("daruma_base.png", 100);
		layer.addChild(shonenSSheet, 0, TAG_DARUMA);
		
		darumaSprite = new CCSprite();
		darumaSprite.setTexture(shonenSSheet.getTexture());
		darumaSprite.setTextureRect(rect);
		
		darumaSprite.setScale(bw/rect.size.width);//背景と同じスケールにする
		
		CGPoint fromPoint = CGPoint.make(fromX * ptmRatio, fromY * ptmRatio);
		darumaSprite.setPosition(fromPoint);
		
		layer.addChild(darumaSprite, baseLevel++);//重なり順の数字も上げておく
		
		moving  = false;
		falling = false;
	}
	
	//初期位置へ
	@Override
	public void move(){
		
		stand();
		
		CGPoint fromPoint = CGPoint.make(fromX * ptmRatio, fromY * ptmRatio);
		darumaSprite.setPosition(fromPoint);
		
		//移動
		CGPoint destPoint = CGPoint.make(destX * ptmRatio, destY * ptmRatio);
		int high = (int)(ptmRatio * ((0.2f * random.nextFloat()) + 0.1f));
		int times = random.nextInt(3) + 3;
		CCSequence actions = CCSequence.actions(
				CCJumpTo.action(delay, destPoint, high, times),
	            CCCallFuncN.action(this, "moveDone"));
		
		darumaSprite.runAction(actions);
		
		moving  = true;
		falling = false;
	}
	
	//だるまの消去
	@Override
	public void moveDone(Object sender){
		moving = false;
	}
	
	//現在地点を取得
	@Override
	public CGPoint getPosition(){
		return darumaSprite.getPosition();
	}
	
	//動作中かどうか
	@Override
	public boolean isMoving(){
		return moving;
	}
	
	//倒れているかどうか
	@Override
	public boolean isFalling(){
		return falling;
	}
	
	//倒れる
	@Override
	public void fall(){
		//Log.d(TAG, "fall");
		if(!falling){
			darumaSprite.setTextureRect(CGRect.make(90, 0, 90, 90));
			falling = true;
		}
	}
	
	//起き上がる
	@Override
	public void stand(){
		//Log.d(TAG, "stand");
		if(falling){
			darumaSprite.setTextureRect(CGRect.make(0, 0, 90, 90));
			falling = false;
		}
	}
	
	//タッチ判定
	@Override
	public boolean isInside(CGPoint point){
		CGRect rect = darumaSprite.getBoundingBox();
		
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
	@Override
	public CGRect getHittingRect(){
		//小さい判定にしておく
		CGRect baseRect = darumaSprite.getBoundingBox();
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