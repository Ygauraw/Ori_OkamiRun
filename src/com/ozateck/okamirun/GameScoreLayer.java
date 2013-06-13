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

public class GameScoreLayer extends CCLayer{
	
	private static final String TAG = "myTag";
	
	private Context context;
	
	//mWorldで使用する画面の横サイズは一定(単位はメートル)
	//モニタの横サイズを基準にして、メートル単位で制御する。
	protected static final float WORLD_WIDTH_METER = 1.0f;
	private float WORLD_HEIGHT_METER = 0.0f;
	
	//モニタサイズ
	private CGSize dispSize;
	//1メートルにつき何ピクセルか
	private int ptmRatio;
	//モニタの中心点
	private CGPoint cPoint;
	
	//Backボタン
	private BtnBack btnBack;
	
	//DBManager
	private DBManager dbManager;
	
	public GameScoreLayer(Context context){
		
		this.context = context;
		
		//タッチアクションを有効にする
		setIsTouchEnabled(true);
		
		//モニタサイズを確定
		dispSize = CCDirector.sharedDirector().winSize();
		
		//モニタサイズの確定後、WORLD_HEIGHT_METERを確定
		WORLD_HEIGHT_METER = WORLD_WIDTH_METER * (dispSize.height/dispSize.width);
		
		//1メートルにつき何ピクセルかを確定
		ptmRatio = (int)(dispSize.width / WORLD_WIDTH_METER);
		
		//モニタの中心点を確定
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
		//BGM再開
		//SoundEngine.sharedEngine().playSound(context, R.raw.bgm_play, true);

        //ステージ生成
        makeStage();
	}
	
	@Override
	public void onExit(){
		super.onExit();
		//BGM停止
		//SoundEngine.sharedEngine().pauseSound();
	}
	
	//ステージ生成
	private void makeStage(){

		//背景のスプライトシート(画面一杯に広げる)
		CCSprite bgSprite = CCSprite.sprite("score_back.png", 
								CGRect.make(0, 0, 800, 480));
		bgSprite.setScaleX(dispSize.width  / 800);
		bgSprite.setScaleY(dispSize.height / 480);
        bgSprite.setPosition(CGPoint.make(dispSize.width/2, dispSize.height/2));
        addChild(bgSprite, 0);
        
        //タイトル
		CCSprite titleSprite = CCSprite.sprite("score_title.png", 
				CGRect.make(0, 0, 300, 50));
		titleSprite.setPosition(CGPoint.make(dispSize.width/2,
								(WORLD_HEIGHT_METER - 0.08f)*ptmRatio));
		addChild(titleSprite, 0);
		
		//マーク
		CCSprite markSprite = CCSprite.sprite("score_mark.png", 
				CGRect.make(0, 0, 300, 240));
		markSprite.setScaleX(0.8f);
		markSprite.setScaleY(0.8f);
		markSprite.setPosition(CGPoint.make(
								0.15f*ptmRatio,
								0.15f*ptmRatio));
		addChild(markSprite, 0);

        //backボタン
		btnBack  = new BtnBack(this, ptmRatio, 1, 0.07f, WORLD_HEIGHT_METER-0.07f, 0.08f);
		
		//スコアリスト
		List<List<String>> scoreList = dbManager.getList(5);
		String score0 = scoreList.get(0).get(0);
		String score1 = scoreList.get(1).get(0);
		String score2 = scoreList.get(2).get(0);
		String score3 = scoreList.get(3).get(0);
		String score4 = scoreList.get(4).get(0);
		
		//スコアパネル
		int texSize = (int)(dispSize.width / 28);
		float centerX  = WORLD_WIDTH_METER/2;
		float centerY  = WORLD_HEIGHT_METER/2-0.05f;
		float paddingW = 0.15f;
		float paddingH = 0.08f;
		ScorePanel panel0 = new ScorePanel(this, ptmRatio, 1,
				centerX-paddingW*2, centerY+paddingH*2, 0.28f, texSize, 1, Integer.valueOf(score0));
		ScorePanel panel1 = new ScorePanel(this, ptmRatio, 1,
				centerX-paddingW*1, centerY+paddingH*1, 0.28f, texSize, 2, Integer.valueOf(score1));
		ScorePanel panel2 = new ScorePanel(this, ptmRatio, 1,
				centerX, centerY, 0.28f, texSize, 3, Integer.valueOf(score2));
		ScorePanel panel3 = new ScorePanel(this, ptmRatio, 1,
				centerX+paddingW*1, centerY-paddingH*1, 0.28f, texSize, 4, Integer.valueOf(score3));
		ScorePanel panel4 = new ScorePanel(this, ptmRatio, 1,
				centerX+paddingW*2, centerY-paddingH*2, 0.28f, texSize, 5, Integer.valueOf(score4));
	}
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event){

		//タッチされた座標
		CGPoint point = 
				CCDirector.sharedDirector().convertToGL(
						CGPoint.make(event.getX(), event.getY()));
		
		//btnの反応
		if(btnBack.isInside(point)){
			btnBack.on();
		}
		
		return CCTouchDispatcher.kEventHandled;
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event){
		//タッチされた座標
		CGPoint point = 
				CCDirector.sharedDirector().convertToGL(
						CGPoint.make(event.getX(), event.getY()));

		//btnの反応
		if(btnBack.isInside(point)){
			SoundEngine.sharedEngine().playEffect(context, R.raw.effect_btn);
			gameTop();
		}
		btnBack.off();
		
		return CCTouchDispatcher.kEventHandled;
	}

	private void gameTop(){
		this.removeAllChildren(true);
		//gameTopへ
		CCScene scene = CCScene.node();
		CCLayer layer = new GameTopLayer(context);
		scene.addChild(layer);
		CCDirector.sharedDirector().replaceScene(scene);
	}
}
