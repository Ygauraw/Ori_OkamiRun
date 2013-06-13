package com.ozateck.okamirun;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.lang.Math;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.MotionEvent;
import android.util.Log;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCParallaxNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCIntervalAction;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.particlesystem.CCParticleSystem;
import org.cocos2d.particlesystem.CCQuadParticleSystem;

import com.ozateck.db.DBManager;

public class GameTopLayer extends CCLayer{
	
	private static final String TAG = "myTag";
	
	private Context context;
	
	//mWorld�Ŏg�p�����ʂ̉��T�C�Y�͈��(�P�ʂ̓��[�g��)
	//���j�^�̉��T�C�Y����ɂ��āA���[�g���P�ʂŐ��䂷��B
	protected static final float WORLD_WIDTH_METER = 1.0f;
	private float WORLD_HEIGHT_METER = 0.0f;
	
	//���j�^�T�C�Y
	private CGSize dispSize;
	//1���[�g���ɂ����s�N�Z����
	private int ptmRatio;
	//���j�^�̒��S�_
	private CGPoint cPoint;
	
	//play�{�^��
	private BtnPlay btnPlay;
	//score�{�^��
	private BtnScore btnScore;
	
	//DBManager
	private DBManager dbManager;
	
	public GameTopLayer(Context context){
		
		this.context = context;
		
		//�^�b�`�A�N�V������L���ɂ���
		setIsTouchEnabled(true);
		
		//���j�^�T�C�Y���m��
		dispSize = CCDirector.sharedDirector().winSize();
		
		//���j�^�T�C�Y�̊m���AWORLD_HEIGHT_METER���m��
		WORLD_HEIGHT_METER = WORLD_WIDTH_METER * (dispSize.height/dispSize.width);
		
		//1���[�g���ɂ����s�N�Z�������m��
		ptmRatio = (int)(dispSize.width / WORLD_WIDTH_METER);
		
		//���j�^�̒��S�_���m��
		cPoint = CCDirector.sharedDirector().convertToGL(
				CGPoint.make(dispSize.width/2, dispSize.height/2));

		Log.d(TAG, "WORLD_WIDTH_METER:" + WORLD_WIDTH_METER);
		Log.d(TAG, "mSize:" + dispSize.width + "_" + dispSize.height);
		Log.d(TAG, "ptmRatio:" + ptmRatio);
		
		//DBManager
		dbManager = new DBManager(context);
		if(dbManager.getTotalCount() == 0){
			dbManager.initialize();
		}
		Log.d(TAG, "db:" + dbManager.getList(5));
	}

	@Override
	public void onEnter(){
		super.onEnter();
		//BGM�ĊJ
		//SoundEngine.sharedEngine().playSound(context, R.raw.bgm_play, true);

        //�X�e�[�W����
        makeStage();
	}
	
	@Override
	public void onExit(){
		super.onExit();
		//BGM��~
		//SoundEngine.sharedEngine().pauseSound();
	}
	
	//�X�e�[�W����
	private void makeStage(){

		//�w�i�̃X�v���C�g�V�[�g(��ʈ�t�ɍL����)
		CCSprite bgSprite = CCSprite.sprite("top_back.png", 
								CGRect.make(0, 0, 800, 480));
		bgSprite.setScaleX(dispSize.width  / 800);
		bgSprite.setScaleY(dispSize.height / 480);
        bgSprite.setPosition(CGPoint.make(dispSize.width/2, dispSize.height/2));
        addChild(bgSprite, 0);
        
        //�^�C�g��
		CCSprite titleSprite = CCSprite.sprite("top_title.png", 
				CGRect.make(0, 0, 400, 140));
		titleSprite.setPosition(CGPoint.make(dispSize.width/2,
								(WORLD_HEIGHT_METER - 0.12f)*ptmRatio));
		addChild(titleSprite, 0);
		
		//�}�[�N
		CCSprite markSprite = CCSprite.sprite("top_mark.png", 
				CGRect.make(0, 0, 300, 240));
		markSprite.setScaleX(0.9f);
		markSprite.setScaleY(0.9f);
		markSprite.setPosition(CGPoint.make(dispSize.width/2,
								0.27f*ptmRatio));
		addChild(markSprite, 0);
		
		//play�{�^��,score�{�^��
		btnPlay  = new BtnPlay(this, ptmRatio, 1, 0.35f, 0.08f, 0.25f);
		btnScore = new BtnScore(this, ptmRatio, 1, 0.65f, 0.08f, 0.25f);
	}
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event){

		//�^�b�`���ꂽ���W
		CGPoint point = 
				CCDirector.sharedDirector().convertToGL(
						CGPoint.make(event.getX(), event.getY()));
		
		//btn�̔���
		if(btnPlay.isInside(point)){
			btnPlay.on();
		}else if(btnScore.isInside(point)){
			btnScore.on();
		}
		
		return CCTouchDispatcher.kEventHandled;
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event){
		//�^�b�`���ꂽ���W
		CGPoint point = 
				CCDirector.sharedDirector().convertToGL(
						CGPoint.make(event.getX(), event.getY()));

		//btn�̔���
		if(btnPlay.isInside(point)){
			SoundEngine.sharedEngine().playEffect(context, R.raw.effect_btn);
			gamePlay();
		}else if(btnScore.isInside(point)){
			SoundEngine.sharedEngine().playEffect(context, R.raw.effect_btn);
			gameScore();
		}
		btnPlay.off();
		btnScore.off();
		
		return CCTouchDispatcher.kEventHandled;
	}
	
	private void gamePlay(){
		this.removeAllChildren(true);
		//gamePlay��
		CCScene scene = CCScene.node();
		CCLayer layer = new GamePlayLayer(context);
		scene.addChild(layer);
		CCDirector.sharedDirector().replaceScene(scene);
	}

	private void gameScore(){
		this.removeAllChildren(true);
		//gameScore��
		CCScene scene = CCScene.node();
		CCLayer layer = new GameScoreLayer(context);
		scene.addChild(layer);
		CCDirector.sharedDirector().replaceScene(scene);
	}
}
