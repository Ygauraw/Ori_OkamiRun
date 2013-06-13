package com.ozateck.okamirun;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.Random;
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

public class GamePlayLayer extends CCLayer{
	
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
	
	//DBManager
	private DBManager dbManager;
	
	//背景
	private Background background;
	
	//Backボタン
	private BtnBack btnBack;
	
	//スコアラベル
	private int score;
	private CCLabel scoreLabel;
	
	//少年
	private Shonen shonen;
	//ヒツジ
	private List<Hituji> hitujiList;
	//オオカミ
	private Okami okami;
	//障害物リスト
	private List<Obstacle> obstacleList;
	
	private Random random;
	
	public GamePlayLayer(Context context){
		
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
		
        //ステージ生成
        makeStage();
        
        //ランダム
        random = new Random();
	}

	@Override
	public void onEnter(){
		super.onEnter();
		//BGM再開
		SoundEngine.sharedEngine().playSound(context, R.raw.bgm_play, true);
		
		//スケジュール
		schedule("gameLogic", 0.2f);
		schedule("obstacleLogic", 1.0f);
	}
	
	@Override
	public void onExit(){
		super.onExit();
		//BGM停止
		SoundEngine.sharedEngine().pauseSound();
		
		//スケジュール
		unschedule("gameLogic");
		unschedule("obstacleLogic");
	}
	
	//ステージ生成
	private void makeStage(){
		
		//背景
		background = new Background(this, ptmRatio, dispSize, true);

        //backボタン
		btnBack  = new BtnBack(this, ptmRatio, 1, 0.07f, WORLD_HEIGHT_METER-0.07f, 0.08f);
		
		//スコアラベル
		score = 0;
		int txSize = (int)(dispSize.width/18);
		scoreLabel = CCLabel.makeLabel(score + "meter", "Pollyanna.ttf", txSize);
		scoreLabel.setColor(ccColor3B.ccBLACK);
		scoreLabel.setAnchorPoint(1.0f, 0.5f);
		scoreLabel.setPosition((WORLD_WIDTH_METER-0.05f) * ptmRatio,
							   (WORLD_HEIGHT_METER-0.07f) * ptmRatio);
		addChild(scoreLabel, 1);
		
		//少年
        shonen = new Shonen(this, ptmRatio, 1, 0.40f, 0.12f, 0.08f);
        shonen.run();
        
        //ヒツジ
        hitujiList = new ArrayList<Hituji>();
        float interval = 0.05f;
        for(int i=0; i<5; i++){
        	Hituji hituji = new Hituji(this, ptmRatio, 1, 0.40f - (interval*(i+1)), 0.10f, 0.05f);
            hituji.run();
        	hitujiList.add(hituji);
        }
        
        //オオカミ
        okami = new Okami(this, ptmRatio, 2, 0.10f, 0.12f, 0.08f);
        okami.run();
        
        //障害物を格納するリスト
        obstacleList = new ArrayList<Obstacle>();
        //柵
        for(int i=0; i<3; i++){
            obstacleList.add(new Saku(this, ptmRatio, 3,
					   1.1f, 0.08f, 0.06f,
					  -0.1f, 0.08f, 4.00f));
        }
        //だるま
        for(int i=0; i<2; i++){
        	obstacleList.add(new Daruma(this, ptmRatio, 3,
					   1.1f, 0.075f, 0.06f,
					  -0.1f, 0.075f, 4.00f));
        }
        //戦闘機
        for(int i=0; i<1; i++){
        	obstacleList.add(new Plane(this, ptmRatio, 3,
					   1.10f, 0.40f, 0.15f,
					   0.40f, 0.23f, 2.00f,
					  -0.10f, 0.40f, 1.00f));
        }
        //Collections.shuffle(obstacleList);
	}
	
	//test
	public void testLogic(String str){
		Log.d(TAG, "hello");
	}
	
	//判定ロジック
	public void gameLogic(float dt){
		
		//スコア加算
		score++;
		scoreLabel.setString(score + "meter");
		
		//障害物との当たり判定
		for(int i=0; i<obstacleList.size(); i++){
			
			//少年
			if(obstacleList.get(i).isMoving() && !obstacleList.get(i).isFalling() &&
				CGRect.intersects(obstacleList.get(i).getHittingRect(), shonen.getHittingRect())){
				//少年の点滅
				shonen.damage();
				//障害物が倒れる
				obstacleList.get(i).fall();
				//オオカミが迫る
				if(hitujiList.size() > 0){
					SoundEngine.sharedEngine().playEffect(context, R.raw.effect_damage);
					int lastInd = hitujiList.size() - 1;
					hitujiList.get(lastInd).die();
					hitujiList.remove(lastInd);
					okami.eat();
				}else{
					gameOver();
				}
			}
		}
	}
	
	//障害物の発生ロジック
	public void obstacleLogic(float dt){

		int onCnt  = obstacleList.size();
		int offCnt = 2;
		int total  = onCnt + offCnt;
		int index  = random.nextInt(total);
		Log.d(TAG, "index:" + index);
		if(index < onCnt){
			//発生
			Obstacle obstacle = obstacleList.get(index);
			if(!obstacle.isMoving()){
				obstacle.move();
			}
		}
		System.gc();
	}
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event){

		//タッチされた座標
		CGPoint point = 
				CCDirector.sharedDirector().convertToGL(
						CGPoint.make(event.getX(), event.getY()));
		
		if(btnBack.isInside(point)){
			btnBack.on();
		}else{
			SoundEngine.sharedEngine().playEffect(context, R.raw.effect_jump);
			shonen.jump();
		}
		
		return CCTouchDispatcher.kEventHandled;
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event){
		//タッチされた座標
		CGPoint point = 
				CCDirector.sharedDirector().convertToGL(
						CGPoint.make(event.getX(), event.getY()));

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

	private void gameOver(){
		this.removeAllChildren(true);
		//gameOverへ
		CCScene scene = CCScene.node();
		CCLayer layer = new GameOverLayer(context);
		String str = "" + score;
		layer.setUserData(str);
		scene.addChild(layer);
		CCDirector.sharedDirector().replaceScene(scene);
	}
}
