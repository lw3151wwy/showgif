package com.showgif.gif.ui.activity;

import com.showgif.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * ���ܣ�ʹ��ViewPagerʵ�ֳ��ν���Ӧ��ʱ������ҳ
 * 
 * (1)�ж��Ƿ����״μ���Ӧ��--��ȡ��ȡSharedPreferences�ķ���
 * (2)�ǣ����������activity���������MainActivity
 * (3)1s��ִ��(2)����
 * 
 * @author wangwy
 *
 */
public class WelcomeActivity extends Activity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
 
        
        boolean mFirst = isFirstEnter(WelcomeActivity.this,WelcomeActivity.this.getClass().getName());
        if(mFirst)
            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY,2000);
        else
            mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY,2000);
    }   
     
    //****************************************************************
    // �ж�Ӧ���Ƿ���μ��أ���ȡSharedPreferences�е�guide_activity�ֶ�
    //****************************************************************

    private boolean isFirstEnter(Context context,String className){
        if(context==null || className==null||"".equalsIgnoreCase(className)) {
        	 return false;
		}
		String mResultStr = context.getSharedPreferences(GuideActivity.SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
				.getString(GuideActivity.KEY_GUIDE_ACTIVITY, "");// ȡ���������� �� com.my.MainActivity
		if (mResultStr.equalsIgnoreCase("false")) {
			return false;
		} else {
			return true;
		}
	}
     
    //*************************************************
    // Handler:��ת����ͬҳ��
    //*************************************************
    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
            case SWITCH_MAINACTIVITY:
                Intent mIntent = new Intent();
                mIntent.setClass(WelcomeActivity.this, MainActivity.class);
                WelcomeActivity.this.startActivity(mIntent);
                WelcomeActivity.this.finish();
                break;
            case SWITCH_GUIDACTIVITY:
                mIntent = new Intent();
                mIntent.setClass(WelcomeActivity.this, GuideActivity.class);
                WelcomeActivity.this.startActivity(mIntent);
                WelcomeActivity.this.finish();
                break;
            }
            super.handleMessage(msg);
        }
    };
    
	@Override
	protected void onResume() {
		super.onResume();
	    MobclickAgent.onResume(this);
	}

	@Override
    protected void onPause() {  
        super.onPause();  
        MobclickAgent.onPause(this);
    }  
}


