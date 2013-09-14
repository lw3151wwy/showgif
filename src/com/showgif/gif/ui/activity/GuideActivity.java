package com.showgif.gif.ui.activity;

import java.util.ArrayList;

import com.showgif.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
 
public class GuideActivity extends Activity {
    public static final String SHAREDPREFERENCES_NAME = "my_pref";
    public static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    
	
    private ViewPager viewPager;
     
    /**װ��ҳ��ʾ��view������*/
    private ArrayList<View> pageViews;    
    private TextView textView;
     
    /**��СԲ���ͼƬ�������ʾ*/
    private TextView[] textViews;
     
    //��������ͼƬ��LinearLayout
    private ViewGroup viewPics;
     
    //����СԲ���LinearLayout
    private ViewGroup viewPoints;
     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        //��Ҫ��ҳ��ʾ��Viewװ��������
        LayoutInflater inflater = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.guidepage1, null));
        pageViews.add(inflater.inflate(R.layout.guidepage2, null));
        pageViews.add(inflater.inflate(R.layout.guidepage3, null));
//        pageViews.add(inflater.inflate(R.layout.guidepage3, null));
//        pageViews.add(inflater.inflate(R.layout.guidepage4, null));
        pageViews.add(inflater.inflate(R.layout.guidepage5, null));
         
        //����imageviews���飬��С��Ҫ��ʾ��ͼƬ������
        textViews = new TextView[pageViews.size()];
        //��ָ����XML�ļ�������ͼ
        viewPics = (ViewGroup) inflater.inflate(R.layout.guidegroup, null);
         
        //ʵ����СԲ���linearLayout��viewpager
        viewPoints = (ViewGroup) viewPics.findViewById(R.id.viewGroup);
        viewPager = (ViewPager) viewPics.findViewById(R.id.guidePages);
      
        //���СԲ���ͼƬ
        for(int i=0;i<pageViews.size();i++){
            textView = new TextView(GuideActivity.this);
            //����СԲ��imageview�Ĳ���
            textView.setLayoutParams(new LayoutParams(60,40));//����һ����߾�Ϊ20 �Ĳ���
            
            //imageView.setPadding(0, 0, 0, 0);
            //��СԲ��layout��ӵ�������
            textViews[i] = textView;
            //Ĭ��ѡ�е��ǵ�һ��ͼƬ����ʱ��һ��СԲ����ѡ��״̬����������
            if(i==0){
                textViews[i].setBackgroundResource(R.drawable.guideslip_tabbar_btn_pressed);
            }else{
            	textViews[i].setBackgroundResource(R.drawable.guideslip_tabbar_btn_nor);
            }
             
            //��imageviews��ӵ�СԲ����ͼ��
            viewPoints.addView(textViews[i]);
            final int temp = i;
            viewPoints.getChildAt(i).setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					viewPager.setCurrentItem(temp, true);
				}			
					 
			});
        }
         
        //��ʾ����ͼƬ����ͼ
        setContentView(viewPics);
        //����viewpager���������ͼ����¼�

        viewPager.setAdapter(new GuidePageAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());        
       
    }
     
    private Button.OnClickListener  Button_OnClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            //�����Ѿ�����
            setGuided();
            //��ת
            Intent mIntent = new Intent();
            //mIntent.setClass(GuideActivity.this, MainActivity.class);
            mIntent.setClass(GuideActivity.this,  MainActivity.class);
            GuideActivity.this.startActivity(mIntent);
          //  overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            GuideActivity.this.finish();
            
        }
    }; 
     

    private void setGuided(){
        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_GUIDE_ACTIVITY, "false");
        editor.commit();
    }
     
     
    class GuidePageAdapter extends PagerAdapter{
    	
        //����positionλ�õĽ���
        @Override
        public void destroyItem(View v, int position, Object arg2) {

        	((ViewPager)v).removeView(pageViews.get(position));
        }
 
        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
        }
         
        //��ȡ��ǰ���������
        @Override
        public int getCount() {
            return pageViews.size();
        }
 
        //��ʼ��positionλ�õĽ���
        @Override
        public Object instantiateItem(View v, int position) {
        	System.out.println("p"+position);
            // TODO Auto-generated method stub
            ((ViewPager) v).addView(pageViews.get(position));  
             // �������һҳ�İ�ť�¼�  
            if (position == textViews.length-1) {  
                Button btn = (Button) v.findViewById(R.id.btn_close_guide);  
                btn.setOnClickListener(Button_OnClickListener);  
            }  
            return pageViews.get(position);  
        }
 
        // �ж��Ƿ��ɶ������ɽ���
        @Override
        public boolean isViewFromObject(View v, Object arg1) {
            // TODO Auto-generated method stub
            return v == arg1;
        }
        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
             
        }
 
        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }
 
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub
             
        }
 
        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }
    }
     
     
    class GuidePageChangeListener implements OnPageChangeListener{
 
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }
 
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }
 
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            for(int i=0;i<textViews.length;i++){
                textViews[position].setBackgroundResource(R.drawable.guideslip_tabbar_btn_pressed);
                //���ǵ�ǰѡ�е�page����СԲ������Ϊδѡ�е�״̬
                if(position !=i){
                	textViews[i].setBackgroundResource(R.drawable.guideslip_tabbar_btn_nor);
                }
            }
        }
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

