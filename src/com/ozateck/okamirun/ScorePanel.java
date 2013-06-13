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
 * スコアパネル
 */

public class ScorePanel{
	
	private static final String TAG = "myTag";

	private final CCLayer layer;
	private final int ptmRatio;
	private int baseLevel;      //重なり順の初期位置
	
	private CGPoint   cPoint;   //中央点
	
	private CCSprite  backSprite;
	
	private static final int TAG_BACK  = 0;

	//x,yはメートル換算
	public ScorePanel(CCLayer layer, int ptmRatio, int baseLevel,
						float x, float y, float size,
						int txSize, int rank, int score){
		
		this.layer     = layer;
		this.ptmRatio  = ptmRatio;
		this.baseLevel = baseLevel;
		
		cPoint    = CGPoint.make(x, y);
		
		init(x, y, size, txSize, rank, score);
	}
	
	private void init(float x, float y, float size,
						int txSize, int rank, int score){

		/////////////////////
        //背景のスプライトシート
		CCSpriteSheet ssBack = CCSpriteSheet.spriteSheet("score_panel.png", 100);
		layer.addChild(ssBack, 0, TAG_BACK);
		
		CGRect rect = CGRect.make(0, 0, 320, 75);
		
		backSprite = new CCSprite();
		backSprite.setTexture(ssBack.getTexture());
		backSprite.setTextureRect(rect);

		float bw = size * ptmRatio;//メートル換算でのピクセル数
		float bh = (rect.size.height / rect.size.width) * bw;
		
		backSprite.setScaleX(bw/rect.size.width);
		backSprite.setScaleY(bh/rect.size.height);
		
		backSprite.setPosition(x * ptmRatio, y * ptmRatio);
		
		layer.addChild(backSprite, baseLevel++);//重なり順の数字も上げておく
		
		//ランク
		CCLabel rankLabel = CCLabel.makeLabel(""+rank, "Pollyanna.ttf", txSize);
		rankLabel.setColor(ccColor3B.ccBLACK);
		rankLabel.setAnchorPoint(0.5f, 0.5f);
		rankLabel.setPosition((x-0.08f) * ptmRatio, y * ptmRatio);
		layer.addChild(rankLabel, baseLevel++);

		//スコア
		CCLabel scoreLabel = CCLabel.makeLabel(""+score, "Pollyanna.ttf", txSize);
		scoreLabel.setColor(ccColor3B.ccBLACK);
		scoreLabel.setAnchorPoint(0.5f, 0.5f);
		scoreLabel.setPosition((x+0.03f) * ptmRatio, y * ptmRatio);
		layer.addChild(scoreLabel, baseLevel++);
	}
	
	//中央点を得る
	public CGPoint getCenterPoint(){
		return cPoint;
	}
}
