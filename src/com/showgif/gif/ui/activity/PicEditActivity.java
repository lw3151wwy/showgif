package com.showgif.gif.ui.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.BreakIterator;

import com.showgif.R;
import com.showgif.gif.listener.MyGestureListener;
import com.showgif.gif.listener.ShakeListener;
import com.showgif.gif.listener.ShakeListener.OnShakeListener;
import com.showgif.gif.ui.dialog.Word;
import com.showgif.gif.util.AppConstantS;
import com.showgif.gif.util.Util;
import com.showgif.gif.widget.MyImageView;
import com.showgif.gifview.GifView;
import com.showgif.gifview.GifImageType;
import com.showgif.jpg2gif.*;
import com.tencent.mm.sdk.openapi.GetMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXEmojiObject;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.umeng.analytics.MobclickAgent;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Element;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PicEditActivity extends Activity implements OnClickListener {
	public static final String TAG = "com.showgif.gif.ui.PicEditActivity";
	// 本地GIF源图片数量
	// private final static int GIF_HEAD_NUMBER = 3;
	// private final static int GIF_BODY_NUMBER = 6;
	private int GIF_HEAD_NUMBER = 0;
	private int GIF_BODY_NUMBER = 0;
	private final static int GIF_HEAD_FRAME_NUMBER = 12;
	private final static int GIF_BODY_FRAME_NUMBER = 12;
	// 摇一摇的监听类
	private ShakeListener mShakeListener;
	public static boolean isFromWX = false;
	// 动图和静图的view
	private ImageView myImageView;
	private GifView myGifView;
	private RelativeLayout myGifViewLayout;
	// 生成和编辑动图和加字
	// private Button showGif;
	// private Button editGif;
	// private Button addWord;
	// private Button finishEditWord;
	// 判断是showGif还是editGif，一进入时是编辑状态
	private Boolean isShowGif = false;
	private Word wordDialog;

	// 头，身子，背景，用于画图
	private Bitmap backgroundbitm;
	private Bitmap headbitm;
	private Bitmap bodybitm;
	// 手势类
	GestureDetector gestureMyIvDetector;
	// 用于线程控制
	private static final int SENSOR_SHAKE = 10;
	public static final int BODY_CHANGEPRE = 1;
	public static final int BODY_CHANGENEXT = 2;
	public static final int HEAD_CHANGEPRE = 3;
	public static final int HEAD_CHANGENEXT = 4;
	public static final int WORD_CHANGE = 5;
	// 存放目录
	public static final String GIF_STORENAME = "result.gif";
	public static final String JPG_STORENAME = "result.jpg";
	public static final String PNG_STORENAME = "result.png";

	public static final String[] jokes = {
			"一群蚂蚁爬上了大象的背，但被摇了下来，只有一只蚂蚁死死地抱着大象的脖子不放，下面的蚂蚁大叫：掐死他，掐死他，小样，还他妈反了！",
			"乌龟受伤，让蜗牛去买药，两个小时 过去了，蜗牛还没回来，乌龟骂到：“他妈的还没回来，老子快死了！”这时候门外传来一声：“他妈的 ，再骂老子不去了！”",
			"一个胖子从十二楼摔了下来,——结果就变成了,死胖子！",
			"蚂蚁懒洋洋地躺在土里，伸出一只腿，朋友问你干嘛呢？蚂蚁：待会大象来了，绊他一跟头。",
			"两根香蕉一前一后逛街，走着走着前面的香蕉感觉很热，于是就把衣服脱了，结果你猜怎么——后面的香蕉跌倒了。 " };
	private RelativeLayout topbarlLayout;
	private RelativeLayout botbarLayout;
	ProgressDialog progressDialog;
	EditText et;
	Button topbar_btnLeft;
	Button topbar_btnRight;
	RelativeLayout progressBar;
	Bundle bundle;
	Button ibHelp;
	ImageView ivImage;
	ImageView ivTopbar;
	TextView joke;
	private IWXAPI mmAPI;
	private boolean isMakingGif;
	ImageView gifHead;
	GifView gifBody;
	Context context;
	MyImageView ts;
	RelativeLayout prosessingLayout;

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		bundle = intent.getExtras();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picedit);
		bundle = getIntent().getExtras();
		context = this;
		mmAPI = WXAPIFactory.createWXAPI(PicEditActivity.this,
				"wxbd56405fa9634bc6", false);
		mmAPI.registerApp("wxbd56405fa9634bc6");

		gifHead = (ImageView) findViewById(R.id.edit_head);
		
		gifBody = (GifView) findViewById(R.id.edit_body);
		gifHead.setImageBitmap(Util.getImageFromAssetFile(this, "gif",
				"v1_head1.gif"));
		for (int i = 0;i < 12;i++)
			Util.head[i] = Util.getImageFromAssetFile(this, "gif","v1_head1.gif");
		gifBody.setGifImage(Util.getInputStreamFromAsset(this, "gif",
				"v1_body1.gif"));
		Util.curShowingHead = 1;
		Util.curShowingBody = 1;
		//gifHead.setGifImageType(GifImageType.COVER);
		//gifHead.setLoopAnimation();
		gifBody.setGifImageType(GifImageType.COVER);
		gifBody.setLoopAnimation();
		wordDialog = new Word(this, handler);
		prosessingLayout = (RelativeLayout) findViewById(R.id.prosessingLayout);
		initButton();
		initData();
	}

	private OnTouchListener etTouch = new OnTouchListener() {
		int[] temp = new int[] { 0, 0 };
		public boolean onTouch(View v, MotionEvent event) {
			int eventaction = event.getAction();
			Log.i(" ", "onTouchEvent:" + eventaction);
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();

			switch (eventaction) {
			case MotionEvent.ACTION_DOWN: //
				temp[0] = (int) event.getX();
				temp[1] = y - v.getTop();
				break;
			case MotionEvent.ACTION_MOVE: // touch drag with the ball
				Log.v(" ", "move");
				v.layout(x - temp[0], y - temp[1], x + v.getWidth() - temp[0],
						y - temp[1] + v.getHeight());
				// textX = x - temp[0];
				// textY = y - temp[1];
				v.postInvalidate();
				break;

			case MotionEvent.ACTION_UP:
				break;
			}

			return false;
		}
	};

	int num = 0;

	private void initButton() {

		// ts = (MyImageView) this.findViewById(R.id.ts);
		progressBar = (RelativeLayout) findViewById(R.id.progress_layout);

		ivImage = (ImageView) this.findViewById(R.id.pic_help);
		ivImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ivImage.setVisibility(View.INVISIBLE);
				ivTopbar.setVisibility(View.INVISIBLE);
			}
		});

		ivTopbar = (ImageView) this.findViewById(R.id.picedit_helptop);
		ivTopbar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ivImage.setVisibility(View.INVISIBLE);
				ivTopbar.setVisibility(View.INVISIBLE);
			}
		});

		ibHelp = (Button) this.findViewById(R.id.pic_button);
		ibHelp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ivImage.setVisibility(View.VISIBLE);
				ivTopbar.setVisibility(View.VISIBLE);
			}
		});

		Log.v(TAG, "初始化界面按钮");
		// miv = (MyImageView)findViewById(R.id.miv);
		Typeface mFace = Typeface.createFromAsset(this.getAssets(),
				"fonts/huawencaiyun.ttf");
		et = (EditText) this.findViewById(R.id.picedit_et);
		//et.setOnTouchListener(etTouch);
		et.setTypeface(mFace);
		/*et.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wordDialog.show(et.getText().toString());
			}
		});*/

		topbarlLayout = (RelativeLayout) this.findViewById(R.id.picedit_topbar);
		// botbarLayout = (RelativeLayout)
		// this.findViewById(R.id.editpic_botbar);

		// miv.setOnTouchListener(touch);
		// 初始化topbar的标题
		TextView tv = (TextView) findViewById(R.id.topbar_titletxt);

		// tv.setText("秀GIF", TextView.BufferType.SPANNABLE);
		// 初始化topbar左边按钮
		topbar_btnLeft = (Button) findViewById(R.id.topbar_btn_back);
		topbar_btnLeft.setVisibility(View.VISIBLE);
		topbar_btnLeft.setOnClickListener(this);
		// 初始化topbar右边按钮
		topbar_btnRight = (Button) findViewById(R.id.topbar_btn_right);
		topbar_btnRight.setVisibility(View.VISIBLE);
		topbar_btnRight.setOnClickListener(this);
		// 初始化静态图片和GIF图片，
		myImageView = (ImageView) findViewById(R.id.imageview);
		myImageView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		
		// 设置针对myimageview 的手势监听类
		// 并且当有触摸时自动触发手势监听类里的listener
		gestureMyIvDetector = new GestureDetector(this, new MyGestureListener(
				handler, myImageView));
		myImageView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureMyIvDetector.onTouchEvent(event);
			}
		});

		myGifView = (GifView) findViewById(R.id.gifview);
		myGifViewLayout = (RelativeLayout) findViewById(R.id.layout_gifview);
		// //初始化传感器，摇一摇
		// sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		joke = (TextView) findViewById(R.id.picedit_joke);
	}

	private void initData() {

		Log.v(TAG, "初始化数据，生成第一张静态图");

		// 创建存储目录
		File file = new File(Util.getAppStorePath());
		if (!file.exists()) {
			file.mkdir();
		}

		// 获取头和身子的个数
		try {
			String[] f = this.getAssets().list("gif");
			for (int i = 0; i < f.length; i++) {
				Log.v("gif", f[i]);
				if (f[i].startsWith("v1_head")) {
					String ftemp = f[i].replace(".gif", "");
					int k = Integer.valueOf(ftemp.replace("v1_head", ""));
					if (k > GIF_HEAD_NUMBER)
						GIF_HEAD_NUMBER = k;
				}
				if (f[i].startsWith("v1_body")) {
					String ftemp = f[i].replace(".gif", "");
					int k = Integer.valueOf(ftemp.replace("v1_body", ""));
					if (k > GIF_BODY_NUMBER)
						GIF_BODY_NUMBER = k;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.v("gif", "Count:" + GIF_HEAD_NUMBER + "  " + GIF_BODY_NUMBER);

		String[] f1;
		try {
			f1 = this.getAssets().list("source");
			for (int i = 0; i < f1.length; i++)
				Log.v("123", "source " + f1[i]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * File dir = Environment.getExternalStorageDirectory(); File picPath =
		 * new File("" + dir + File.separatorChar + "gifShow" +
		 * File.separatorChar + "pic"); for (int i = 0;i < f.length;i++){
		 * Log.v("123",f[i].getName()); if (f[i].isDirectory()) { File[] son =
		 * f[i].listFiles(); for (int j = 0;j < son.length;j++)
		 * Log.v("123","   " + son[j]); } }
		 */

		// 此处不应该直接调用
		// Util.curShowingHead = 0;
		// Util.curShowingBody = 0;
		// change
		// Bitmap src = matchPic(headsrc[Util.curShowingHead][0],
		// bodysrc[Util.curShowingBody][0]);
		//myImageView.setBackgroundResource(R.drawable.blue_bg_320);
	}

	private OnTouchListener touch = new OnTouchListener() {
		int[] temp = new int[] { 0, 0 };

		public boolean onTouch(View v, MotionEvent event) {
			int eventaction = event.getAction();

			Log.i("123", "onTouchEvent:" + eventaction);
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();

			switch (eventaction) {
			case MotionEvent.ACTION_DOWN: //
				temp[0] = (int) event.getX();
				temp[1] = y - v.getTop();
				break;
			case MotionEvent.ACTION_MOVE: // touch drag with the ball
				Log.v("123", "move");
				v.layout(x - temp[0], y - temp[1], x + v.getWidth() - temp[0],
						y - temp[1] + v.getHeight());
				v.postInvalidate();
				break;

			case MotionEvent.ACTION_UP:
				break;
			}

			return false;
		}
	};

	private void initShakeLisener() {
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {

			public void onShake() {
				if (!isShowGif) {
					mShakeListener.stop();
					handler.postDelayed(new Runnable() {
						public void run() {
							// Toast.makeText(getApplicationContext(), "检测到摇晃",
							// Toast.LENGTH_LONG).show();
							MediaPlayer mp = MediaPlayer.create(
									PicEditActivity.this, R.raw.shake);
							mp.start();
							// 发送线程消息
							Message msg = new Message();
							msg.what = SENSOR_SHAKE;
							handler.sendMessage(msg);
							// mShakeListener.start();
						}
					}, 100);
				}
			}

		});
	}

	// 后台线程处理点击事件和摇动操作
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "handerler线程收到消息" + msg);
			super.handleMessage(msg);
			Bitmap bm;
			switch (msg.what) {
			case SENSOR_SHAKE:
				Log.v(TAG, "编辑页面中，用户在摇动手机，正在执行随机为用户选图");
				// 改变cur图的编号
				Util.curShowingHead = (int) (Math.random() * GIF_HEAD_NUMBER);
				Util.curShowingBody = (int) (Math.random() * GIF_BODY_NUMBER);
				// myImageView.setImageBitmap(bm);
				gifHead.setImageBitmap(Util.getImageFromAssetFile(context,
						"gif", "v1_head" + Util.curShowingHead + ".gif"));
				for (int i = 0;i < 12;i++)
					Util.head[i] = Util.getImageFromAssetFile(context,
							"gif", "v1_head" + Util.curShowingHead + ".gif");
				gifBody.setGifImage(Util.getInputStreamFromAsset(context,
						"gif", "v1_body" + Util.curShowingBody + ".gif"));
				mShakeListener.start();
				break;
			case BODY_CHANGEPRE:
				Log.v(TAG, "编辑页面中，handler收到用户往前切换身子的msg,执行");
				// Toast.makeText(PicEditActivity.this, "body change",
				// Toast.LENGTH_SHORT).show();
				// 切到身子的前一张图，如果是已经是材料库第一张图，默认切到最后一张
				if (Util.curShowingBody <= 1) {
					Util.curShowingBody = GIF_BODY_NUMBER;
				} else {
					Util.curShowingBody--;
				}
				// myImageView.setImageBitmap(bm);
				gifBody.setGifImage(Util.getInputStreamFromAsset(context,
						"gif", "v1_body" + Util.curShowingBody + ".gif"));
				break;
			case BODY_CHANGENEXT:
				Log.v(TAG, "编辑页面中，handler收到用户往后切换身子的msg,执行");
				// Toast.makeText(PicEditActivity.this, "body change",
				// Toast.LENGTH_SHORT).show();
				// 切到前一张身子图，如果是已经是材料库第最后一张图，默认切到第一张
				if (Util.curShowingBody + 1 > GIF_BODY_NUMBER) {
					Util.curShowingBody = 1;
				} else {
					Util.curShowingBody++;
				}
				gifBody.setGifImage(Util.getInputStreamFromAsset(context,
						"gif", "v1_body" + Util.curShowingBody + ".gif"));
				// myImageView.setImageBitmap(bm);
				break;

			case HEAD_CHANGEPRE:
				Log.v(TAG, "编辑页面中，handler收到用户往前切换头部的msg,执行");
				// Toast.makeText(PicEditActivity.this, "body change",
				// Toast.LENGTH_SHORT).show();
				// 切到身子的前一张图，如果是已经是材料库第一张图，默认切到最后一张
				if (Util.curShowingHead - 1 <= 0) {
					Util.curShowingHead = GIF_HEAD_NUMBER;
				} else {
					Util.curShowingHead--;
				}
				Log.v("123", "before matchPic " + Util.curShowingHead);
				gifHead.setImageBitmap(Util.getImageFromAssetFile(context,
						"gif", "v1_head" + Util.curShowingHead + ".gif"));
				for (int i = 0;i < 12;i++)
					Util.head[i] = Util.getImageFromAssetFile(context,
							"gif", "v1_head" + Util.curShowingHead + ".gif");
				// myImageView.setImageBitmap(bm);
				break;

			case HEAD_CHANGENEXT:
				Log.v(TAG, "编辑页面中，handler收到用户往后切换身子的msg,执行");
				// Toast.makeText(PicEditActivity.this, "body change",
				// Toast.LENGTH_SHORT).show();
				// 切到前一张身子图，如果是已经是材料库第最后一张图，默认切到第一张
				if (Util.curShowingHead + 1 > GIF_HEAD_NUMBER) {
					Util.curShowingHead = 1;
				} else {
					Util.curShowingHead++;
				}
				gifHead.setImageBitmap(Util.getImageFromAssetFile(context,
						"gif", "v1_head" + Util.curShowingHead + ".gif"));
				for (int i = 0;i < 12;i++)
					Util.head[i] = Util.getImageFromAssetFile(context,
							"gif", "v1_head" + Util.curShowingHead + ".gif");
				// myImageView.setImageBitmap(bm);
				break;
			case WORD_CHANGE:
				String textContent = (String) msg.obj;
				//System.out.println("text=" + textContent);
				// ts.changephoto(textContent);
				et.setText(textContent);
				break;
			}
		}
	};

	Bitmap matchPic(Bitmap head, Bitmap body) {
		Bitmap drawBit = Bitmap.createBitmap(320, 450, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(drawBit);
		Paint paint = new Paint();
		// 用来对位图进行滤波处理
		paint.setFilterBitmap(true);

		// 画上背景
		Bitmap backgroundbitm = Util.getImageFromAssetFile(this,
				AppConstantS.Asset_BG_FolderName,
				AppConstantS.Asset_320BG_FileName);
		// backgroundbitm =Util.zoomBitmap(backgroundbitm, 450, 700);
		Rect dsRect = new Rect(0, 0, backgroundbitm.getWidth(),
				backgroundbitm.getHeight());

		canvas.drawBitmap(backgroundbitm, null, dsRect, paint);

		// 画上头部，设定顶部起始位0
		int top = 0;
		Bitmap headbitm = head;
		Rect dstRect = new Rect(0, 0, headbitm.getWidth(), headbitm.getHeight());
		canvas.drawBitmap(headbitm, null, dstRect, paint);

		// 画上身子,设定顶部起始位
		top = top + headbitm.getHeight() - AppConstantS.headAndBodyOverLap;
		Bitmap bodybitm = body;
		dstRect = new Rect(0, top, bodybitm.getWidth(), bodybitm.getHeight()
				+ top);
		canvas.drawBitmap(bodybitm, null, dstRect, paint);

		// canvas.drawText("我要打酱油", 100, 400 , paint);
		// 文字部分

		// if(isHasText) {
		if (!isShowGif) {
			if (et.getText().toString() != null) {
				System.out.println("et" + et.getText().toString());
				Typeface mFace = Typeface.createFromAsset(this.getAssets(),
						"fonts/huawencaiyun.ttf");
				TextPaint tp = new TextPaint();
				// tp.setFakeBoldText(true);
				tp.setColor(Color.rgb(8, 98, 104));
				tp.setTextSize(30);
				tp.setTypeface(mFace);
				tp.setFakeBoldText(true);
				StaticLayout layout = new StaticLayout(et.getText().toString(),
						tp, 320, Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
				canvas.translate(0, 380);
				layout.draw(canvas);

				// }
				Log.v(TAG, "有文字拼接部分完成");
				// return drawTextBit;
			}
		}

		return drawBit;
	}

	/**
	 * 整张图的大小为640*900 头的部分为640*528 身子部分为640*702 重叠部分为640*330 整张图的大小为320*450
	 * 头的部分为320*264 身子部分为320*351 重叠部分为320*160
	 */
	Bitmap matchPic(String headName, String bodyName) {
		if (headName.equals("") || bodyName.equals("")) {
			Log.v(TAG, "身子和头的图片没有从asset中取到，跳出方法makeCover");
		}
		Log.v(TAG, "拼接静态图片中...");

		String headFolder = "head"
				+ headName.replace("v1_head", "").substring(0, 2);
		String bodyFolder = "body"
				+ bodyName.replace("v1_body", "").substring(0, 2);
		headFolder = headFolder.replace("_", "");
		bodyFolder = bodyFolder.replace("_", "");
		// Log.v("123",headFolder);
		// Log.v("123",bodyFolder);
		Bitmap head = Util.getImageFromAssetFile(this, headFolder, headName);
		Bitmap body = Util.getImageFromAssetFile(this, bodyFolder, bodyName);

		// 生成画布，画笔
		// Bitmap drawBit = Bitmap.createBitmap(head.getWidth(),
		// AppConstantS.makePicOriHeightPx, Bitmap.Config.ARGB_8888);

		Bitmap drawBit = Bitmap.createBitmap(320, 450, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(drawBit);
		Paint paint = new Paint();
		// 用来对位图进行滤波处理
		paint.setFilterBitmap(true);

		// 画上背景
		Bitmap backgroundbitm = Util.getImageFromAssetFile(this,
				AppConstantS.Asset_BG_FolderName,
				AppConstantS.Asset_320BG_FileName);
		// backgroundbitm =Util.zoomBitmap(backgroundbitm, 450, 700);
		Rect dsRect = new Rect(0, 0, backgroundbitm.getWidth(),
				backgroundbitm.getHeight());

		// System.out.println("w"+topbarlLayout.getWidth()+"b"+botbarLayout.getWidth());
		// Rect dsRect = new Rect(0, 0, 480,700);
		// Rect dsRect = new Rect(0, 0,
		// myImageView.getWidth(),myImageView.getHeight());
		canvas.drawBitmap(backgroundbitm, null, dsRect, paint);

		// 画上头部，设定顶部起始位0
		int top = 0;
		Bitmap headbitm = head;
		Rect dstRect = new Rect(0, 0, headbitm.getWidth(), headbitm.getHeight());
		canvas.drawBitmap(headbitm, null, dstRect, paint);

		// 画上身子,设定顶部起始位
		top = top + headbitm.getHeight() - AppConstantS.headAndBodyOverLap;
		Bitmap bodybitm = body;
		dstRect = new Rect(0, top, bodybitm.getWidth(), bodybitm.getHeight()
				+ top);
		canvas.drawBitmap(bodybitm, null, dstRect, paint);

		// canvas.drawText("我要打酱油", 100, 400 , paint);
		// 文字部分

		// if(isHasText) {
		if (isShowGif) {
			if (et.getText().toString() != null) {
				System.out.println("et" + et.getText().toString());

				Typeface mFace = Typeface.createFromAsset(this.getAssets(),
						"fonts/huawencaiyun.ttf");
				Paint tp = new Paint(Paint.ANTI_ALIAS_FLAG);
				tp.setTypeface(null);
				tp.setTypeface(mFace);
				// tp.setFakeBoldText(true);
				// tp.setColor(Color.rgb(8, 98, 104));
				tp.setTextSize(30);
				// tp.setFakeBoldText(true);

				// StaticLayout layout = new
				// StaticLayout(et.getText().toString(),
				// tp, 320, Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
				// canvas.translate(0, 380);
				// layout.draw(canvas);
				canvas.drawText(et.getText().toString(), 0, 380, tp);
				// }
				Log.v(TAG, "有文字拼接部分完成");
				// return drawTextBit;
				Log.v(TAG, "无文字拼接部分完成");
				// ts.setVisibility(View.INVISIBLE);
				// Bitmap drawTextBit =
				// Bitmap.createBitmap(myImageView.getWidth(),
				// myImageView.getHeight(), Bitmap.Config.ARGB_8888);
				// ts.setDrawingCacheEnabled(true);
				// Bitmap bit =Util.convertViewToBitmap(ts);
				//
				// canvas = new Canvas(drawTextBit);
				// canvas.drawBitmap(drawBit,null,dstRect,paint);
				// dstRect = new Rect(0, 0,320, 450);
				// canvas.drawBitmap(bit, null,dstRect, paint);
				// ts.drawPic(drawTextBit, canvas,paint);
			}
		}

		return drawBit;

	}

	private Bitmap[] matchPicsFromCurStaticPic() {
		Log.v(TAG, "执行makeImages,获取当前展示的头和身子,获取他们的多张静态图片,进行多次的拼接(makeimage)");
		Bitmap[] images = new Bitmap[12];
		try {
			for (int i = 0; i < images.length; i++) {
				Bitmap src = matchPic(Util.head[i], Util.body[i]);
				images[i] = src;
			}
		} catch (Exception e) {
			Log.v(TAG, "执行makeImages失败");
			e.printStackTrace();
		}
		return images;
	}

	public void picsToGif() {
		// Bitmap[] pics =
		JpgToGif j = new JpgToGif();
		String path = Util.getAppStorePath() + File.separatorChar
				+ GIF_STORENAME;
		try {
			// 参数为,images和存储的目标路径
			j.jpgToGif(matchPicsFromCurStaticPic(), path);
			Log.v(TAG, "制作Gif完毕，图片被保存入：" + path);
		} catch (Exception e) {
			Log.v(TAG, "制作Gif失败了,请检查jpgToGif");
		}

		// 清空图片内存
		// for(int i=0;i<pics.length;i++) {
		// pics[i].recycle();
		//
		// }
	}

	public void onClick(View v) {
		switch (v.getId()) {
		// 切回编辑页面
		// case R.id.editgif:
		// //myGifView.pauseGifAnimation();
		// myGifView.setVisibility(View.INVISIBLE);
		// myGifViewLayout.setVisibility(View.INVISIBLE);
		// myImageView.setVisibility(View.VISIBLE);
		// isShowGif = false;
		// break;

		case R.id.topbar_btn_back:
			if (!isShowGif) {
				Log.v("123", "back");
				clearData();
				PicEditActivity.this.finish();
			} else {
				topbar_btnRight.setBackgroundDrawable(PicEditActivity.this
						.getResources().getDrawable(
								R.drawable.topbar_btn_show_sel));
				myGifView.setVisibility(View.INVISIBLE);
				myImageView.setVisibility(View.VISIBLE);
				prosessingLayout.setVisibility(View.INVISIBLE);
				gifHead.setVisibility(View.VISIBLE);
				gifBody.setVisibility(View.VISIBLE);
				ibHelp.setVisibility(View.VISIBLE);
				et.setVisibility(View.VISIBLE);
				isShowGif = false;
			}
			break;

		case R.id.topbar_btn_right:
			Log.v("123", "coming in make");
			System.out.println("isfromwx:" + PicEditActivity.isFromWX);
			// if (!isShowGif) {
			// Log.v("123","coming in make");
			// isShowGif = true;
			// topbar_btnRight.setVisibility(View.INVISIBLE);
			// ibHelp.setVisibility(View.INVISIBLE);
			// // progressDialog..show();
			// new MakeGifTask().execute();
			// topbar_btnRight.setBackgroundDrawable(PicEditActivity.this
			// .getResources().getDrawable(
			// R.drawable.topbar_btn_right_selector));
			// } else
			//
			// if(!isShowGif) {
			// new MakeGifTask().execute();
			//
			// }

			if (isFromWX) {
				new MakeGifTask().execute();
				// mmAPI.sendResp(resp);
				// finish();
				// System.exit(0);
			} else {
				new MakeGifTask().execute();
				// Intent in = new Intent(this, PicShareActivity.class);
				// startActivity(in);
				// clearData();
				MobclickAgent.onEvent(PicEditActivity.this, "share_click");
				// this.finish();
			}
			break;
		// case R.id.showgif:
		// isShowGif = true;
		// new MakeGifTask().execute();
		// break;
		default:
			break;
		}

	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		System.out.println(TAG + "OnPause");
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		System.out.println(TAG + "OnResume");
		initShakeLisener();
	}

	class MakeGifTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			topbar_btnLeft.setClickable(false);
			progressBar.setVisibility(View.VISIBLE);
			myImageView.setVisibility(View.INVISIBLE);
			prosessingLayout.setVisibility(View.VISIBLE);
			gifHead.setVisibility(View.INVISIBLE);
			gifBody.setVisibility(View.INVISIBLE);
			int k = (int) (Math.random() * jokes.length);
			if (k == jokes.length)
				k--;
			joke.setText(jokes[k]);
			et.setVisibility(View.INVISIBLE);
			isMakingGif = true;
			// .show(
			// PicEditActivity.this, "请稍等...", "生成中...", true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = true;
			Log.v(TAG, "开始执行异步任务jpg2gif，gif生成中");
			try {
				picsToGif();
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
				Log.v(TAG, "gif生成失败");
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (isFromWX) {
				final String path = Util.getAppStorePath() + File.separator
						+ PicEditActivity.GIF_STORENAME;
				WXEmojiObject emoji = new WXEmojiObject();
				emoji.emojiPath = path;

				WXMediaMessage msg = new WXMediaMessage(emoji);
				msg.title = "Emoji Title";
				msg.description = "Emoji Description";

				Bitmap bmp = BitmapFactory.decodeFile(path);
				Bitmap thumbBmp = Bitmap
						.createScaledBitmap(bmp, 150, 150, true);
				bmp.recycle();
				msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

				GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
				resp.transaction = getTransaction();
				resp.message = msg;
				MobclickAgent.onEvent(PicEditActivity.this, "share_click");
				mmAPI.sendResp(resp);
				finish();
				System.exit(0);
			}
			if (!isShowGif) {
				Intent in = new Intent(PicEditActivity.this,
						PicShareActivity.class);
				startActivity(in);
				clearData();
				PicEditActivity.this.finish();
				// MobclickAgent.onEvent(PicEditActivity.this, "share_click");
				// PicEditActivity.this.finish();
			} else {
				if (result) {
					// if(myGifView==null) {
					// myGifView = (GifView)
					// PicEditActivity.this.findViewById(R.id.gifview);
					// myGifView.reDraw();
					// }
					myGifView.setGifImageType(GifImageType.COVER);
					myGifView.setLoopAnimation();
					String path = Util.getAppStorePath() + File.separatorChar
							+ GIF_STORENAME;
					try {
						// if(!myGifView.animationRun) {
						myGifView.setGifImage(path);
						// }else {
						// myGifView.restartGifAnimation();
						// }
						myImageView.setVisibility(View.INVISIBLE);
						prosessingLayout.setVisibility(View.VISIBLE);
						gifHead.setVisibility(View.INVISIBLE);
						gifBody.setVisibility(View.INVISIBLE);
						myGifView.setVisibility(View.VISIBLE);
						myGifViewLayout.setVisibility(View.VISIBLE);
						Log.v(TAG, "GifView解码完毕，设置GIF图片到当前UI成功");
					} catch (Exception e) {
						e.printStackTrace();
						Log.v(TAG, "设置GIF图片到当前UI失败");
					}
				} else {
					Toast.makeText(
							PicEditActivity.this,
							PicEditActivity.this.getResources().getString(
									R.string.makegif_failed), Toast.LENGTH_LONG)
							.show();
				}
			}
			// progressBar.setVisibility(View.INVISIBLE);
			topbar_btnRight.setVisibility(View.VISIBLE);
			topbar_btnLeft.setClickable(true);
			isMakingGif = false;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (isMakingGif) {
				return false;
			}
			if (!isShowGif) {
				clearData();
				PicEditActivity.this.finish();
			} else {
				topbar_btnRight.setBackgroundDrawable(PicEditActivity.this
						.getResources().getDrawable(
								R.drawable.topbar_btn_show_sel));
				myGifView.setVisibility(View.INVISIBLE);
				myImageView.setVisibility(View.VISIBLE);
				prosessingLayout.setVisibility(View.INVISIBLE);
				gifHead.setVisibility(View.VISIBLE);
				gifBody.setVisibility(View.VISIBLE);
				et.setVisibility(View.VISIBLE);
				ibHelp.setVisibility(View.VISIBLE);
				isShowGif = false;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void clearData() {
		if (isShowGif) {
			myGifView.destroy();
		}
		// 清空头部和脚的图片内存，防止OOM。
		/*
		 * for(int i=0;i<headsrc.length;i++) { for(int
		 * j=0;j<headsrc[i].length;j++) { headsrc[i][j].recycle(); } } for(int
		 * i=0;i<bodysrc.length;i++) { for(int j=0;j<bodysrc[i].length;j++) {
		 * bodysrc[i][j].recycle(); } }
		 */
	}

	private String getTransaction() {
		final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle);
		return req.transaction;
	}
}