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
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCGLSurfaceView;

/*
 * スコアボタン
 */

public class BtnScore{
	
	private static final String TAG = "myTag";

	private final CCLayer layer;
	private final int ptmRatio;
	private int baseLevel;      //重なり順の初期位置
	
	private CGPoint   cPoint;   //中央点
	
	private boolean   active;
	
	private CCSprite  backSprite;
	
	private static final int TAG_BACK  = 0;
	
	private int itemInd;

	//x,yはメートル換算
	public BtnScore(CCLayer layer, int ptmRatio, int baseLevel,
						float x, float y, float size){
		
		this.layer     = layer;
		this.ptmRatio  = ptmRatio;
		this.baseLevel = baseLevel;
		
		cPoint    = CGPoint.make(x, y);
		
		active = false;
		
		init(x, y, size);
	}
	
	private void init(float x, float y, float size){

		/////////////////////
        //背景のスプライトシート
		CCSpriteSheet ssBack = CCSpriteSheet.spriteSheet("btn_score.png", 100);
		layer.addChild(ssBack, 0, TAG_BACK);
		
		CGRect rect = CGRect.make(0, 0, 210, 70);
		
		backSprite = new CCSprite();
		backSprite.setTexture(ssBack.getTexture());
		backSprite.setTextureRect(rect);

		float bw = size * ptmRatio;//メートル換算でのピクセル数
		float bh = (rect.size.height / rect.size.width) * bw;
		
		backSprite.setScaleX(bw/rect.size.width);
		backSprite.setScaleY(bh/rect.size.height);
		
		backSprite.setPosition(x * ptmRatio, y * ptmRatio);
		
		
		
		layer.addChild(backSprite, baseLevel++);//重なり順の数字も上げておく
	}
	
	//中央点を得る
	public CGPoint getCenterPoint(){
		return cPoint;
	}

	public void on(){
		if(!active){
			active = true;
			CGRect activeRect = CGRect.make(0, 70, 210, 70);
			backSprite.setTextureRect(activeRect);
		}
	}
	
	public void off(){
		if(active){
			active = false;
			CGRect inactiveRect = CGRect.make(0, 0, 210, 70);
			backSprite.setTextureRect(inactiveRect);
		}
	}
	
	public void changeToggle(){
		if(!active){
			active = true;
			on();
		}else{
			active = false;
			off();
		}
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

	public boolean isActive(){
		return active;
	}
}
