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
                // 设置播放的延迟时间
                e.setDelay(100);
                e.addFrame(pic[i]); // 添加到帧中
                pic[i].recycle();
            }
            e.finish();// 刷新任何未决的数据，并关闭输出文件
            Log.v("123","jpgtoGifover" + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
