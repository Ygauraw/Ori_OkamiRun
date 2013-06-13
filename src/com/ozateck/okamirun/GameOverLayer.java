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
	
	//backボタン
	private BtnBack btnBack;
	//retryボタン
	private BtnRetry btnRetry;
	//scoreボタン
	private BtnScore btnScore;
	
	//scoreラベル
	private CCLabel scoreLabel;
	
	//DBManager
	private DBManager dbManager;
	
	public GameOverLayer(Context context){
		
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
		SoundEngine.sharedEngine().playSound(context, R.raw.bgm_over, false);

        //ステージ生成
        makeStage();
	}
	
	@Override
	public void onExit(){
		super.onExit();
		//BGM停止
		SoundEngine.sharedEngine().pauseSound();
	}
	
	//ステージ生成
	private void makeStage(){

		//背景のスプライトシート(画面一杯に広げる)
		CCSprite bgSprite = CCSprite.sprite("gameover_back.png", 
								CGRect.make(0, 0, 800, 480));
		bgSprite.setScaleX(dispSize.width  / 800);
		bgSprite.setScaleY(dispSize.height / 480);
        bgSprite.setPosition(CGPoint.make(dispSize.width/2, dispSize.height/2));
        addChild(bgSprite, 0);
        
        //タイトル
		CCSprite titleSprite = CCSprite.sprite("gameover_title.png", 
				CGRect.make(0, 0, 300, 50));
		titleSprite.setPosition(CGPoint.make(dispSize.width/2,
								(WORLD_HEIGHT_METER - 0.08f)*ptmRatio));
		addChild(titleSprite, 0);
		
		//ユーザーデータの読込み
		Object obj = getUserData();
		String str = (String)obj;
		int score = Integer.valueOf(str);
		
		//スコアラベル
		int txSize = (int)(dispSize.width/20);
		scoreLabel = CCLabel.makeLabel(score + "meter", "Pollyanna.ttf", txSize);
		scoreLabel.setColor(ccColor3B.ccBLACK);
		scoreLabel.setAnchorPoint(0.5f, 0.5f);
		scoreLabel.setPosition(0.5f * ptmRatio,
							   (WORLD_HEIGHT_METER-0.15f) * ptmRatio);
		addChild(scoreLabel, 0);

		//DBのアップデート
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
		
		//マーク
		CCSprite markSprite = CCSprite.sprite("gameover_mark.png", 
				CGRect.make(0, 0, 300, 240));
		markSprite.setScaleX(0.9f);
		markSprite.setScaleY(0.9f);
		markSprite.setPosition(CGPoint.make(
								0.48f*ptmRatio,
								0.28f*ptmRatio));
		addChild(markSprite, 0);

        //backボタン
		btnBack  = new BtnBack(this, ptmRatio, 1, 0.07f, WORLD_HEIGHT_METER-0.07f, 0.08f);
		//retryボタン,scoreボタン
		btnRetry  = new BtnRetry(this, ptmRatio, 1, 0.35f, 0.08f, 0.25f);
		btnScore = new BtnScore(this, ptmRatio, 1, 0.65f, 0.08f, 0.25f);
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
		}else if(btnScore.isInside(point)){
			btnScore.on();
		}else if(btnRetry.isInside(point)){
			btnRetry.on();
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
		//gameTopへ
		CCScene scene = CCScene.node();
		CCLayer layer = new GameTopLayer(context);
		scene.addChild(layer);
		CCDirector.sharedDirector().replaceScene(scene);
	}
	
	private void gamePlay(){
		this.removeAllChildren(true);
		//gamePlayへ
		CCScene scene = CCScene.node();
		CCLayer layer = new GamePlayLayer(context);
		scene.addChild(layer);
		CCDirector.sharedDirector().replaceScene(scene);
	}

	private void gameScore(){
		this.removeAllChildren(true);
		//gameScoreへ
		CCScene scene = CCScene.node();
		CCLayer layer = new GameScoreLayer(context);
		scene.addChild(layer);
		CCDirector.sharedDirector().replaceScene(scene);
	}
}
