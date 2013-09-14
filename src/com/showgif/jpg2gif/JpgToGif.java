package com.showgif.jpg2gif;

import com.weibo.sdk.android.api.WeiboAPI.SRC_FILTER;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class JpgToGif {
        public void jpgToGif(Bitmap pic[], String newPic) {
        	Log.v("123","jpgToGif" + System.currentTimeMillis());
           try {
            AnimatedGifEncoder1 e = new AnimatedGifEncoder1();
            e.setRepeat(0);
            e.start(newPic);
            
            for (int i = 0; i < pic.length; i++) {
                // ���ò��ŵ��ӳ�ʱ��
                e.setDelay(100);
                e.addFrame(pic[i]); // ��ӵ�֡��
                pic[i].recycle();
            }
            e.finish();// ˢ���κ�δ�������ݣ����ر�����ļ�
            Log.v("123","jpgtoGifover" + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
