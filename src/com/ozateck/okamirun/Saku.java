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
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.config.ccMacros;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.opengl.CCGLSurfaceView;

/*
 * ��Q��
 */

public class Saku extends Obstacle{
	
	private float fromX, fromY, size;
	private float destX, destY, delay;
	
	private CCSprite sakuSprite;//��
	
	private static final int TAG_SAKU = 0;
	
	private boolean moving;
	private boolean falling;

	//x,y�̓��[�g�����Z
	public Saku(CCLayer layer, int ptmRatio, int baseLevel,
				float fromX, float fromY, float size,
				float destX, float destY, float delay){
		
		super(layer, ptmRatio, baseLevel);
		
		this.fromX = fromX;
		this.fromY = fromY;
		this.size  = size;
		this.destX = destX;
		this.destY = destY;
		this.delay = delay;
		
		init();
	}
	
	public void init(){
		
		CGRect rect = CGRect.make(0, 0, 90, 100);

		float bw = size * ptmRatio;//���[�g�����Z�ł̃s�N�Z����
		float bh = (rect.size.height / rect.size.width) * bw;
		
		/////////////////////
		//��̃X�v���C�g�V�[�g
		CCSpriteSheet shonenSSheet = CCSpriteSheet.spriteSheet("saku_base.png", 100);
		layer.addChild(shonenSSheet, 0, TAG_SAKU);
		
		sakuSprite = new CCSprite();
		sakuSprite.setTexture(shonenSSheet.getTexture());
		sakuSprite.setTextureRect(rect);
		
		sakuSprite.setScale(bw/rect.size.width);//�w�i�Ɠ����X�P�[���ɂ���
		
		CGPoint fromPoint = CGPoint.make(fromX * ptmRatio, fromY * ptmRatio);
		sakuSprite.setPosition(fromPoint);
		
		layer.addChild(sakuSprite, baseLevel++);//�d�Ȃ菇�̐������グ�Ă���
		
		moving  = false;
		falling = false;
	}
	
	//�����ʒu��
	public void move(){
		
		stand();
		
		CGPoint fromPoint = CGPoint.make(fromX * ptmRatio, fromY * ptmRatio);
		sakuSprite.setPosition(fromPoint);
		
		//�ړ�
		CGPoint destPoint = CGPoint.make(destX * ptmRatio, destY * ptmRatio);
		CCSequence actions = CCSequence.actions(
	            CCMoveTo.action(delay, destPoint),
	            CCCallFuncN.action(this, "moveDone"));
		
		sakuSprite.runAction(actions);
		
		moving  = true;
		falling = false;
	}
	
	//��̏���
	public void moveDone(Object sender){
		moving = false;
	}
	
	//���ݒn�_���擾
	public CGPoint getPosition(){
		return sakuSprite.getPosition();
	}
	
	//���쒆���ǂ���
	public boolean isMoving(){
		return moving;
	}
	
	//�|��Ă��邩�ǂ���
	public boolean isFalling(){
		return falling;
	}
	
	//�|���
	public void fall(){
		//Log.d(TAG, "fall");
		if(!falling){
			sakuSprite.setTextureRect(CGRect.make(90, 0, 90, 90));
			falling = true;
		}
	}
	
	//�N���オ��
	public void stand(){
		//Log.d(TAG, "stand");
		if(falling){
			sakuSprite.setTextureRect(CGRect.make(0, 0, 90, 90));
			falling = false;
		}
	}
	
	//�^�b�`����
	@Override
	public boolean isInside(CGPoint point){
		CGRect rect = sakuSprite.getBoundingBox();
		
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
		CGRect baseRect = sakuSprite.getBoundingBox();
		float cx = baseRect.origin.x;
		float cy = baseRect.origin.y;
		float width  = baseRect.size.width * 60 / 100;
		float height = baseRect.size.height * 60 / 100;
		CGRect hittingRect = CGRect.make(cx+(baseRect.size.width-width)/2,
			     cy-(baseRect.size.height-height)/2,
			     width, height);
		return hittingRect;
	}
}
