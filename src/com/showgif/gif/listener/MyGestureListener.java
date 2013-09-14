package com.showgif.gif.listener;

import com.showgif.gif.ui.activity.PicEditActivity;
import com.showgif.gif.util.Util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
	public static final String TAG = "com.showgif.listener.MyGestureListener";
	public static final int TYPE_CHANGEHEADBODY = 1;
	private View curView;
	private Handler handler;

	public MyGestureListener(Handler paramHandler, View paramView) {
		this.handler = paramHandler;
		this.curView = paramView;
	}

	public boolean onDoubleTap(MotionEvent paramMotionEvent) {
		return true;
	}

	public boolean onDoubleTapEvent(MotionEvent paramMotionEvent) {
		return true;
	}

	public boolean onDown(MotionEvent paramMotionEvent) {
		return true;
	}

	public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		Log.v("com.showgif.listener.MyGestureListener", "��������������ONFLING�Ļص�����");
		Log.v("com.showgif.listener.MyGestureListener", "��ǰ����ҳ��Ϊ��ͷ�����ӵ��л�����ʼִ��onfling����");
		Message msg = new Message();
		if (this.curView != null) {
			float line = this.curView.getHeight() * Util.getHeadAndBodyScaleAfterOverLap();
			System.out.println("cutOff" + line);
			System.out.println("curH" + this.curView.getHeight());
			System.out.println("scale" + Util.getHeadAndBodyScaleAfterOverLap());
	
		
			if (paramMotionEvent1.getY() < line) {
				Log.v("com.showgif.listener.MyGestureListener", "�û���ͷ������������ͷ���л���handlermsg");
				if (paramMotionEvent1.getX() - paramMotionEvent2.getX() > 100) {
					Log.v("com.showgif.listener.MyGestureListener","��������FLING");
					msg.what = PicEditActivity.HEAD_CHANGENEXT;
					this.handler.sendMessage(msg);
				} else if (paramMotionEvent2.getX() - paramMotionEvent1.getX() > 100){
					Log.v("com.showgif.listener.MyGestureListener","��������FLING");
					msg.what = PicEditActivity.HEAD_CHANGEPRE;
					this.handler.sendMessage(msg);
				}
			} else {
				Log.v("com.showgif.listener.MyGestureListener", "�û������ӻ��������������л���handlermsg");
				if (paramMotionEvent1.getX() - paramMotionEvent2.getX() > 100.0) {
					Log.v("com.showgif.listener.MyGestureListener","��������FLING");
					msg.what = PicEditActivity.BODY_CHANGENEXT;
					this.handler.sendMessage(msg);
				} else if (paramMotionEvent2.getX() - paramMotionEvent1.getX() > 100.0) {
					Log.v("com.showgif.listener.MyGestureListener","��������FLING");
					msg.what = PicEditActivity.BODY_CHANGEPRE;
					handler.sendMessage(msg);
				}
			}		
			return true;
		} else {
			return false;
		}
	}

	public void onLongPress(MotionEvent paramMotionEvent) {
		super.onLongPress(paramMotionEvent);
	}

	public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		return true;
	}

	public void onShowPress(MotionEvent paramMotionEvent) {
		super.onShowPress(paramMotionEvent);
	}

	public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent) {
		return true;
	}

	public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
		return true;
	}
}
