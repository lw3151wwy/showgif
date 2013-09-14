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
 * 功能：使用ViewPager实现初次进入应用时的引导页
 * 
 * (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 * (2)是，则进入引导activity；否，则进入MainActivity
 * (3)1s后执行(2)操作
 * 
 * @author wangwy
 *
 */
public class MainActivity extends Activity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //设置MAIN的大标题
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


