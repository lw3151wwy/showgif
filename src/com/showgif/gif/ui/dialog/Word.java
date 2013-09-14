package com.showgif.gif.ui.dialog;

import java.io.IOException;
import java.io.InputStream;

import com.showgif.R;
import com.showgif.gif.ui.activity.PicEditActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;

public class Word {
	private String type;
	private Dialog mDialog;
	private Handler handler;
	private TabHost my;
	private EditText et;
	private Button save;
	private Button drop;
	
	private static final int WORD_CHANGE = 4;

	public Word(Context context, final Handler h) {
		mDialog = new Dialog(context, R.style.tip);
		mDialog.setCanceledOnTouchOutside(true);
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		window.setAttributes(wl);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		// window.setGravity(Gravity.CENTER);
		window.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mDialog.setContentView(R.layout.word);
		mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		handler = h;
		
		et = (EditText) mDialog.findViewById(R.id.word_et);
		save = (Button) mDialog.findViewById(R.id.word_save);
		save.setTag("save");
		drop = (Button) mDialog.findViewById(R.id.word_drop);
		drop.setTag("drop");
		
		OnClickListener listener = new Listener();
		save.setOnClickListener(listener);
		drop.setOnClickListener(listener);
	}
	
	private class Listener implements OnClickListener {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.v("123", "clicked");
			Message msg = new Message();
			Log.v("123",v.getTag().toString());
			msg.what = PicEditActivity.WORD_CHANGE;
			if (v.getTag().toString().equals("save"))
				msg.obj = et.getText().toString();
			else
				msg.obj = "";
			handler.sendMessage(msg);
			mDialog.dismiss();
		}

	}
	
	public void show(String s) {
		et.setText(s);
		mDialog.show();
	}
}
