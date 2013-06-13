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
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCGLSurfaceView;

/*
 * 障害物
 */

public abstract class Obstacle{
	
	protected static final String TAG = "myTag";

	protected final CCLayer layer;
	protected final int ptmRatio;
	protected int baseLevel;//重なり順の初期位置

	public Obstacle(CCLayer layer, int ptmRatio, int baseLevel){
		
		this.layer     = layer;
		this.ptmRatio  = ptmRatio;
		this.baseLevel = baseLevel;
	}
	
	//初期位置へ
	public abstract void move();
	
	//動作終了
	public abstract void moveDone(Object sender);
	
	//現在地点を取得
	public abstract CGPoint getPosition();
	
	//動作中かどうか
	public abstract boolean isMoving();
	
	//倒れているかどうか
	public abstract boolean isFalling();
	
	//倒れる
	public abstract void fall();
	
	//起き上がる
	public abstract void stand();
	
	//タッチ判定
	public abstract boolean isInside(CGPoint point);

	//当たり判定のRect
	public abstract CGRect getHittingRect();
}
