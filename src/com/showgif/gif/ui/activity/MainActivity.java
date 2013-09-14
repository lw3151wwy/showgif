package com.showgif.gif.ui.activity;


import com.showgif.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
public class MainActivity extends Activity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //����MAIN�Ĵ����
//		TextView tv = (TextView)findViewById(R.id.topbar_titletxt);
//		tv.setText("Gif View", TextView.BufferType.SPANNABLE);
        TextView tv = (TextView)findViewById(R.id.first_play);
        tv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(MainActivity.this,PicEditActivity.class);
//				Intent in = new Intent(FirstActivity.this,PicShareActivity.class);
				MainActivity.this.startActivity(in);
			}
		});
    }  
    
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


