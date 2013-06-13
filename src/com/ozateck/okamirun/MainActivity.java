package com.ozateck.okamirun;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.util.Log;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCGLSurfaceView;

public class MainActivity extends Activity{

public static final String TAG = "myTag";
	
	@Override
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		Log.d(TAG, "onCreate()");
		
		//window setting
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横
		
		//glsurfaceview
		CCGLSurfaceView mCCGLSurfaceView = new CCGLSurfaceView(this);
		CCDirector.sharedDirector().attachInView(mCCGLSurfaceView);
		setContentView(mCCGLSurfaceView);
		
		//show FPS
		CCDirector.sharedDirector().setDisplayFPS(false);
		
		//GameTopへ
		CCScene scene = CCScene.node();
		CCLayer layer = new GameTopLayer(this);
		scene.addChild(layer);
		
		//frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 30);
		CCDirector.sharedDirector().runWithScene(scene);
		
		//Sounds
		SoundEngine.sharedEngine().preloadSound(this, R.raw.bgm_play);
		SoundEngine.sharedEngine().preloadSound(this, R.raw.bgm_over);
		SoundEngine.sharedEngine().preloadEffect(this, R.raw.effect_btn);
		SoundEngine.sharedEngine().preloadEffect(this, R.raw.effect_success);
		SoundEngine.sharedEngine().preloadEffect(this, R.raw.effect_damage);
		SoundEngine.sharedEngine().preloadEffect(this, R.raw.effect_jump);
		SoundEngine.sharedEngine().preloadEffect(this, R.raw.effect_plane);
		SoundEngine.sharedEngine().setEffectsVolume(0.4f);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.d(TAG, "onStart()");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG, "onResume()");
		CCDirector.sharedDirector().resume();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG, "onPause()");
		SoundEngine.sharedEngine().pauseSound();
		CCDirector.sharedDirector().pause();//エラーが起こる
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		CCDirector.sharedDirector().end();//エラーが起こる
	}
}