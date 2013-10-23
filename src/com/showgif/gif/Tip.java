package com.showgif.gif;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import com.showgif.R;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;

public class Tip {
	private String type;
	private ImageView image;
	private Dialog mDialog;
	private TableLayout layout;
	private Handler handler;
	private Listener listener;
	private TabHost my;
	private String[] picPath;
	private String fold;

	private static final int BODY_CHANGE = 1;
	private static final int HEAD_CHANGE = 2;

	public String[] getPath(Context context,String foldName) {
		try {
			String[] f = context.getAssets().list(foldName);
			return f;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}
	
	private Bitmap getImageFromAssetFile(Context context,String fileName) {
		Bitmap image = null;
		try {
			AssetManager am = context.getAssets();
			InputStream is = am.open(fold + "/" + fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public Tip(Context context, final Handler h, String s,String foldName) {
		type = s;
		fold = foldName;
		picPath = getPath(context,foldName);
		mDialog = new Dialog(context, R.style.tip);
		mDialog.setCanceledOnTouchOutside(true);
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = -30;
		wl.y = 20;
		window.setAttributes(wl);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		// window.setGravity(Gravity.CENTER);
		window.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mDialog.setContentView(R.layout.dialog);
		mDialog.setTitle("—°‘Ò…ÌÃÂ");
		mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		layout = (TableLayout) mDialog.findViewById(R.id.dialog_layout);
		handler = h;
		listener = new Listener();

		addView(context);
	}

	private void addView(Context context) {
		if (type.equals("head")) {
			int count = 0;
			ImageView[] save = new ImageView[3];
			String[] f = picPath;
			for (int j = 0; j < f.length; j++) {
				if (f[j].startsWith("small_head")) {
					ImageView i = new ImageView(context);
					Log.v("123", f[j]);
					Log.v("123", String.valueOf(count));
					Bitmap b = getImageFromAssetFile(context,f[j]);
					i.setImageBitmap(b);
					i.setTag(f[j].replace("small_", ""));
					i.setPadding(10, 10, 10, 10);
					i.setOnClickListener(listener);
					save[count % 3] = i;
					count++;
					if (count % 3 == 0) {
						TableRow tr = new TableRow(context);
						tr.setLayoutParams(new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
						tr.addView(save[0]);
						tr.addView(save[1]);
						tr.addView(save[2]);
						Log.v("123", "new row");
						layout.addView(tr);
					}
				}
			}
		} else {
			Field[] fields = R.drawable.class.getDeclaredFields();
			int count = 0;
			ImageView[] save = new ImageView[3];
			String[] f = picPath;
			for (int j = 0; j < f.length; j++) {
				if (f[j].startsWith("small_body")) {
					ImageView i = new ImageView(context);
					Log.v("123", f[j]);
					Log.v("123", String.valueOf(count));
					Bitmap b = getImageFromAssetFile(context,f[j]);
					i.setImageBitmap(b);
					i.setTag(f[j].replace("small_", ""));
					i.setPadding(10, 10, 10, 10);
					i.setOnClickListener(listener);
					save[count % 3] = i;
					count++;
					if (count % 3 == 0) {
						TableRow tr = new TableRow(context);
						tr.setLayoutParams(new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
						tr.addView(save[0]);
						tr.addView(save[1]);
						tr.addView(save[2]);
						Log.v("123", "new row");
						layout.addView(tr);
					}
				}
			}
		}
	}

	private class Listener implements OnClickListener {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.v("123", "clicked");
			Message msg = new Message();
			if (type.equals("head"))
				msg.what = HEAD_CHANGE;
			else
				msg.what = BODY_CHANGE;
			msg.obj = v.getTag();
			Log.v("123", String.valueOf(v.getTag()));
			handler.sendMessage(msg);
			mDialog.dismiss();
		}

	}

	/*public Bitmap getImage(Context context, String name) {
		ApplicationInfo appInfo = context.getApplicationInfo();
		int resID = context.getResources().getIdentifier(name, "drawable",
				appInfo.packageName);
		return BitmapFactory.decodeResource(context.getResources(), resID);
	}*/

	public void show() {
		mDialog.show();
	}

}
