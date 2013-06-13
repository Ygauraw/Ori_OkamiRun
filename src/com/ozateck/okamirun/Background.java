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
import org.cocos2d.nodes.CCParallaxNode;
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
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCGLSurfaceView;

/*
 * —¬‚ê‚é”wŒi
 */

public class Background{
	
	private static final String TAG = "myTag";

	private final CCLayer layer;
	private final int ptmRatio;
	
	private CGSize dispSize;
	private CCParallaxNode voidNode;
	
	private static final int TAG_BACK  = 0;

	private boolean scrolling;

	//x,y‚Íƒ[ƒgƒ‹Š·ŽZ
	public Background(CCLayer layer, int ptmRatio,
					  CGSize dispSize, boolean scrolling){
		
		this.layer     = layer;
		this.ptmRatio  = ptmRatio;
		this.dispSize  = dispSize;
		this.scrolling = scrolling;
		
		init();
	}
	
	private void init(){
		//”wŒi‚ÌƒXƒvƒ‰ƒCƒgƒV[ƒg(‰æ–Êˆê”t‚ÉL‚°‚é)
		CCSprite bgSprite = CCSprite.sprite("game_back00.png", 
								CGRect.make(0, 0, 800, 480));
		bgSprite.setScaleX(dispSize.width  / 800);
		bgSprite.setScaleY(dispSize.height / 480);
        bgSprite.setPosition(CGPoint.make(dispSize.width/2, dispSize.height/2));
        layer.addChild(bgSprite, 0);
		
		//‘
		CCSprite grassSprite1 = CCSprite.sprite("game_back01.png");
		//grassSprite1.setScaleX(dispSize.width  / 800);
		//grassSprite1.setScaleY(dispSize.height  / 480);
		grassSprite1.setAnchorPoint(CGPoint.make(0, 0));
		
		CCSprite grassSprite2 = CCSprite.sprite("game_back01.png");
		//grassSprite2.setScaleX(dispSize.width  / 800);
		//grassSprite2.setScaleY(dispSize.height  / 480);
		grassSprite2.setAnchorPoint(CGPoint.make(0, 0));
		
		CCSprite grassSprite3 = CCSprite.sprite("game_back01.png");
		//grassSprite3.setScaleX(dispSize.width  / 800);
		//grassSprite3.setScaleY(dispSize.height  / 480);
		grassSprite3.setAnchorPoint(CGPoint.make(0, 0));
		
		CCSprite grassSprite4 = CCSprite.sprite("game_back01.png");
		//grassSprite4.setScaleX(dispSize.width  / 800);
		//grassSprite4.setScaleY(dispSize.height  / 480);
		grassSprite4.setAnchorPoint(CGPoint.make(0, 0));
		
		//–Ø
		CCSprite treeSprite1 = CCSprite.sprite("game_back02.png");
		treeSprite1.setAnchorPoint(CGPoint.make(0, 0));
		
		CCSprite treeSprite2 = CCSprite.sprite("game_back02.png");
		treeSprite2.setAnchorPoint(CGPoint.make(0, 0));

		CCSprite treeSprite3 = CCSprite.sprite("game_back02.png");
		treeSprite3.setAnchorPoint(CGPoint.make(0, 0));
		
		//‰_
		CCSprite crowdSprite1 = CCSprite.sprite("game_back03.png");
		crowdSprite1.setAnchorPoint(CGPoint.make(0, 0));
		
		CCSprite crowdSprite2 = CCSprite.sprite("game_back03.png");
		crowdSprite2.setAnchorPoint(CGPoint.make(0, 0));
		
		//Parallax
		voidNode = CCParallaxNode.node();
		
		//z-index, ratioX, ratioY, offsetX, offsetY
		//‰æ‘œ‚ÌƒTƒCƒY‚Í(800*90)
		voidNode.addChild(grassSprite1, 2, 3.0f, 1.0f, 0, 0); //‘
		voidNode.addChild(grassSprite2, 2, 3.0f, 1.0f, dispSize.width*1, 0);
		voidNode.addChild(grassSprite3, 2, 3.0f, 1.0f, dispSize.width*2, 0);
		voidNode.addChild(grassSprite4, 2, 3.0f, 1.0f, dispSize.width*3, 0);
		
		voidNode.addChild(treeSprite1,  1, 2.0f, 1.0f, 0, 50);//–Ø
		voidNode.addChild(treeSprite2,  1, 2.0f, 1.0f, dispSize.width*1, 50);
		voidNode.addChild(treeSprite3,  1, 2.0f, 1.0f, dispSize.width*2, 50);
		
		voidNode.addChild(crowdSprite1, 0, 1.0f, 1.0f, 0, 150);//‰_
		voidNode.addChild(crowdSprite2, 0, 1.0f, 1.0f, dispSize.width*1, 150);
		
		CCIntervalAction goForward = CCMoveTo.action(8.0f, CGPoint.make(-dispSize.width, 0));
		CCIntervalAction goBack    = goForward.reverse();
		CCIntervalAction reset     = CCMoveTo.action(0, CGPoint.make(0, 0));
		CCIntervalAction seq = CCSequence.actions(goForward, reset);
		voidNode.runAction(CCRepeatForever.action(seq));
		
		layer.addChild(voidNode);
		
		scrolling = true;
	}
	
	public void start(){
		if(!scrolling){
			scrolling = true;
		}
	}
	
	public void stop(){
		if(scrolling){
			scrolling = false;
		}
	}
	
	public boolean isScrolling(){
		return scrolling;
	}
	
	public void update(){
		if(scrolling){
			/*
			CGPoint point = voidNode.getPosition();
			Log.d(TAG, "point:" + point.x + "_" + point.y);
			if(point.x > dispSize.width){
				point.x = 0.0f;
			}else{
				point.x += 10.0f;
			}
			voidNode.setPosition(point);
			*/
		}
	}
}
