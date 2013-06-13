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
 * �X�R�A�p�l��
 */

public class ScorePanel{
	
	private static final String TAG = "myTag";

	private final CCLayer layer;
	private final int ptmRatio;
	private int baseLevel;      //�d�Ȃ菇�̏����ʒu
	
	private CGPoint   cPoint;   //�����_
	
	private CCSprite  backSprite;
	
	private static final int TAG_BACK  = 0;

	//x,y�̓��[�g�����Z
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
        //�w�i�̃X�v���C�g�V�[�g
		CCSpriteSheet ssBack = CCSpriteSheet.spriteSheet("score_panel.png", 100);
		layer.addChild(ssBack, 0, TAG_BACK);
		
		CGRect rect = CGRect.make(0, 0, 320, 75);
		
		backSprite = new CCSprite();
		backSprite.setTexture(ssBack.getTexture());
		backSprite.setTextureRect(rect);

		float bw = size * ptmRatio;//���[�g�����Z�ł̃s�N�Z����
		float bh = (rect.size.height / rect.size.width) * bw;
		
		backSprite.setScaleX(bw/rect.size.width);
		backSprite.setScaleY(bh/rect.size.height);
		
		backSprite.setPosition(x * ptmRatio, y * ptmRatio);
		
		layer.addChild(backSprite, baseLevel++);//�d�Ȃ菇�̐������グ�Ă���
		
		//�����N
		CCLabel rankLabel = CCLabel.makeLabel(""+rank, "Pollyanna.ttf", txSize);
		rankLabel.setColor(ccColor3B.ccBLACK);
		rankLabel.setAnchorPoint(0.5f, 0.5f);
		rankLabel.setPosition((x-0.08f) * ptmRatio, y * ptmRatio);
		layer.addChild(rankLabel, baseLevel++);

		//�X�R�A
		CCLabel scoreLabel = CCLabel.makeLabel(""+score, "Pollyanna.ttf", txSize);
		scoreLabel.setColor(ccColor3B.ccBLACK);
		scoreLabel.setAnchorPoint(0.5f, 0.5f);
		scoreLabel.setPosition((x+0.03f) * ptmRatio, y * ptmRatio);
		layer.addChild(scoreLabel, baseLevel++);
	}
	
	//�����_�𓾂�
	public CGPoint getCenterPoint(){
		return cPoint;
	}
}
