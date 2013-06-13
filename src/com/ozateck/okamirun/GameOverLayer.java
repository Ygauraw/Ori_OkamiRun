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

public class GameOverLayer extends CCLayer{
	
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
	
	//back�{�^��
	private BtnBack btnBack;
	//retry�{�^��
	private BtnRetry btnRetry;
	//score�{�^��
	private BtnScore btnScore;
	
	//score���x��
	private CCLabel scoreLabel;
	
	//DBManager
	private DBManager dbManager;
	
	public GameOverLayer(Context context){
		
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
	}

	@Override
	public void onEnter(){
		super.onEnter();
		//BGM�ĊJ
		SoundEngine.sharedEngine().playSound(context, R.raw.bgm_over, false);

        //�X�e�[�W����
        makeStage();
	}
	
	@Override
	public void onExit(){
		super.onExit();
		//BGM��~
		SoundEngine.sharedEngine().pauseSound();
	}
	
	//�X�e�[�W����
	private void makeStage(){

		//�w�i�̃X�v���C�g�V�[�g(��ʈ�t�ɍL����)
		CCSprite bgSprite = CCSprite.sprite("gameover_back.png", 
								CGRect.make(0, 0, 800, 480));
		bgSprite.setScaleX(dispSize.width  / 800);
		bgSprite.setScaleY(dispSize.height / 480);
        bgSprite.setPosition(CGPoint.make(dispSize.width/2, dispSize.height/2));
        addChild(bgSprite, 0);
        
        //�^�C�g��
		CCSprite titleSprite = CCSprite.sprite("gameover_title.png", 
				CGRect.make(0, 0, 300, 50));
		titleSprite.setPosition(CGPoint.make(dispSize.width/2,
								(WORLD_HEIGHT_METER - 0.08f)*ptmRatio));
		addChild(titleSprite, 0);
		
		//���[�U�[�f�[�^�̓Ǎ���
		Object obj = getUserData();
		String str = (String)obj;
		int score = Integer.valueOf(str);
		
		//�X�R�A���x��
		int txSize = (int)(dispSize.width/20);
		scoreLabel = CCLabel.makeLabel(score + "meter", "Pollyanna.ttf", txSize);
		scoreLabel.setColor(ccColor3B.ccBLACK);
		scoreLabel.setAnchorPoint(0.5f, 0.5f);
		scoreLabel.setPosition(0.5f * ptmRatio,
							   (WORLD_HEIGHT_METER-0.15f) * ptmRatio);
		addChild(scoreLabel, 0);

		//DB�̃A�b�v�f�[�g
		List<List<String>> scoreList = dbManager.getList(5);
		for(int i=0; i<scoreList.size(); i++){
			int current = Integer.valueOf(scoreList.get(i).get(0));
			if(score >= current){
				List<String> itemData = new ArrayList<String>();
				itemData.add(""+score);
				try{
					dbManager.insert(itemData);
				}catch(Exception e){
					Log.d(TAG, "error:" + e.toString());
				}
				break;
			}
		}
		Log.d(TAG, "db:" + dbManager.getList(dbManager.getTotalCount()));
		
		//�}�[�N
		CCSprite markSprite = CCSprite.sprite("gameover_mark.png", 
				CGRect.make(0, 0, 300, 240));
		markSprite.setScaleX(0.9f);
		markSprite.setScaleY(0.9f);
		markSprite.setPosition(CGPoint.make(
								0.48f*ptmRatio,
								0.28f*ptmRatio));
		addChild(markSprite, 0);

        //back�{�^��
		btnBack  = new BtnBack(this, ptmRatio, 1, 0.07f, WORLD_HEIGHT_METER-0.07f, 0.08f);
		//retry�{�^��,score�{�^��
		btnRetry  = new BtnRetry(this, ptmRatio, 1, 0.35f, 0.08f, 0.25f);
		btnScore = new BtnScore(this, ptmRatio, 1, 0.65f, 0.08f, 0.25f);
	}
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event){

		//�^�b�`���ꂽ���W
		CGPoint point = 
				CCDirector.sharedDirector().convertToGL(
						CGPoint.make(event.getX(), event.getY()));
		
		//btn�̔���
		if(btnBack.isInside(point)){
			btnBack.on();
		}else if(btnScore.isInside(point)){
			btnScore.on();
		}else if(btnRetry.isInside(point)){
			btnRetry.on();
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
		if(btnBack.isInside(point)){
			SoundEngine.sharedEngine().playEffect(context, R.raw.effect_btn);
			gameTop();
		}else if(btnRetry.isInside(point)){
			SoundEngine.sharedEngine().playEffect(context, R.raw.effect_btn);
			gamePlay();
		}else if(btnScore.isInside(point)){
			SoundEngine.sharedEngine().playEffect(context, R.raw.effect_btn);
			gameScore();
		}
		btnBack.off();
		btnRetry.off();
		btnScore.off();
		
		return CCTouchDispatcher.kEventHandled;
	}

	private void gameTop(){
		this.removeAllChildren(true);
		//gameTop��
		CCScene scene = CCScene.node();
		CCLayer layer = new GameTopLayer(context);
		scene.addChild(layer);
		CCDirector.sharedDirector().replaceScene(scene);
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
