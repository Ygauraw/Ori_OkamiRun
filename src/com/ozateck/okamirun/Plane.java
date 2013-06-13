package com.ozateck.okamirun;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

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
 * �퓬�@
 */

public class Plane extends Obstacle{
	
	private float fromX, fromY, size;
	private float sectX, sectY, delayS;
	private float destX, destY, delayD;
	
	private CCSprite planeSprite;//�퓬�@
	
	private static final int TAG_PLANE = 0;
	
	private boolean moving;
	private boolean falling;
	
	private Random random;

	//x,y�̓��[�g�����Z
	public Plane(CCLayer layer, int ptmRatio, int baseLevel,
				float fromX, float fromY, float size,
				float sectX, float sectY, float delayS,
				float destX, float destY, float delayD){
		
		super(layer, ptmRatio, baseLevel);
		
		this.fromX  = fromX;
		this.fromY  = fromY;
		this.size   = size;
		this.sectX  = sectX;
		this.sectY  = sectY;
		this.delayS = delayS;
		this.destX  = destX;
		this.destY  = destY;
		this.delayD = delayD;
		
		random = new Random();
		
		init();
	}
	
	public void init(){
		
		CGRect rect = CGRect.make(0, 0, 380, 300);

		float bw = size * ptmRatio;//���[�g�����Z�ł̃s�N�Z����
		float bh = (rect.size.height / rect.size.width) * bw;
		
		/////////////////////
		//�퓬�@�̃X�v���C�g�V�[�g
		CCSpriteSheet shonenSSheet = CCSpriteSheet.spriteSheet("plane_base.png", 100);
		layer.addChild(shonenSSheet, 0, TAG_PLANE);
		
		planeSprite = new CCSprite();
		planeSprite.setTexture(shonenSSheet.getTexture());
		planeSprite.setTextureRect(rect);
		
		planeSprite.setScale(bw/rect.size.width);//�w�i�Ɠ����X�P�[���ɂ���
		
		CGPoint fromPoint = CGPoint.make(fromX * ptmRatio, fromY * ptmRatio);
		planeSprite.setPosition(fromPoint);
		
		layer.addChild(planeSprite, baseLevel++);//�d�Ȃ菇�̐������グ�Ă���
		
		moving  = false;
		falling = false;
	}
	
	//�����ʒu��
	@Override
	public void move(){
		
		stand();
		
		CGPoint fromPoint = CGPoint.make(fromX * ptmRatio, fromY * ptmRatio);
		planeSprite.setPosition(fromPoint);
		
		//�ړ�
		int high = (int)(ptmRatio * ((0.2f * random.nextFloat()) + 0.2f));
		CGPoint sectPoint = CGPoint.make(sectX * ptmRatio, high);
		CGPoint destPoint = CGPoint.make(destX * ptmRatio, destY * ptmRatio);
		CCSequence actions = CCSequence.actions(
				CCMoveTo.action(delayS, sectPoint),
				CCMoveTo.action(delayD, destPoint),
	            CCCallFuncN.action(this, "moveDone"));
		
		planeSprite.runAction(actions);
		
		moving  = true;
		falling = false;
	}
	
	//����܂̏���
	@Override
	public void moveDone(Object sender){
		moving = false;
	}
	
	//���ݒn�_���擾
	@Override
	public CGPoint getPosition(){
		return planeSprite.getPosition();
	}
	
	//���쒆���ǂ���
	@Override
	public boolean isMoving(){
		return moving;
	}
	
	//�|��Ă��邩�ǂ���
	@Override
	public boolean isFalling(){
		return falling;
	}
	
	//�|���
	@Override
	public void fall(){
		//Log.d(TAG, "fall");
		if(!falling){
			planeSprite.setTextureRect(CGRect.make(0, 0, 380, 300));
			falling = true;
		}
	}
	
	//�N���オ��
	@Override
	public void stand(){
		//Log.d(TAG, "stand");
		if(falling){
			planeSprite.setTextureRect(CGRect.make(0, 0, 380, 300));
			falling = false;
		}
	}
	
	//�^�b�`����
	@Override
	public boolean isInside(CGPoint point){
		CGRect rect = planeSprite.getBoundingBox();
		
		CGRect innerRect = CGRect.make(
				rect.origin.x, rect.origin.y,
				rect.size.width,
				rect.size.height);
		
		if(innerRect.contains(point.x, point.y)){
			return true;
		}else{
			return false;
		}
	}

	//�����蔻���Rect
	@Override
	public CGRect getHittingRect(){
		//����������ɂ��Ă���
		CGRect baseRect = planeSprite.getBoundingBox();
		float cx = baseRect.origin.x;
		float cy = baseRect.origin.y;
		float width  = baseRect.size.width * 80 / 100;
		float height = baseRect.size.height * 80 / 100;
		CGRect hittingRect = CGRect.make(cx+(baseRect.size.width-width)/2,
									     cy-(baseRect.size.height-height)/2,
									     width, height);
		return hittingRect;
	}
}
