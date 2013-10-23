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
	// ����GIFԴͼƬ����
	// private final static int GIF_HEAD_NUMBER = 3;
	// private final static int GIF_BODY_NUMBER = 6;
	private int GIF_HEAD_NUMBER = 0;
	private int GIF_BODY_NUMBER = 0;
	private final static int GIF_HEAD_FRAME_NUMBER = 12;
	private final static int GIF_BODY_FRAME_NUMBER = 12;
	// ҡһҡ�ļ�����
	private ShakeListener mShakeListener;
	public static boolean isFromWX = false;
	// ��ͼ�;�ͼ��view
	private ImageView myImageView;
	private GifView myGifView;
	private RelativeLayout myGifViewLayout;
	// ���ɺͱ༭��ͼ�ͼ���
	// private Button showGif;
	// private Button editGif;
	// private Button addWord;
	// private Button finishEditWord;
	// �ж���showGif����editGif��һ����ʱ�Ǳ༭״̬
	private Boolean isShowGif = false;
	private Word wordDialog;

	// ͷ�����ӣ����������ڻ�ͼ
	private Bitmap backgroundbitm;
	private Bitmap headbitm;
	private Bitmap bodybitm;
	// ������
	GestureDetector gestureMyIvDetector;
	// �����߳̿���
	private static final int SENSOR_SHAKE = 10;
	public static final int BODY_CHANGEPRE = 1;
	public static final int BODY_CHANGENEXT = 2;
	public static final int HEAD_CHANGEPRE = 3;
	public static final int HEAD_CHANGENEXT = 4;
	public static final int WORD_CHANGE = 5;
	// ���Ŀ¼
	public static final String GIF_STORENAME = "result.gif";
	public static final String JPG_STORENAME = "result.jpg";
	public static final String PNG_STORENAME = "result.png";

	public static final String[] jokes = {
			"һȺ���������˴���ı�������ҡ��������ֻ��һֻ���������ر��Ŵ���Ĳ��Ӳ��ţ���������ϴ�У�����������������С���������跴�ˣ�",
			"�ڹ����ˣ�����ţȥ��ҩ������Сʱ ��ȥ�ˣ���ţ��û�������ڹ����������Ļ�û���������ӿ����ˣ�����ʱ�����⴫��һ����������� ���������Ӳ�ȥ�ˣ���",
			"һ�����Ӵ�ʮ��¥ˤ������,��������ͱ����,�����ӣ�",
			"���������������������һֻ�ȣ�������������أ����ϣ�����������ˣ�����һ��ͷ��",
			"�����㽶һǰһ���֣���������ǰ����㽶�о����ȣ����ǾͰ��·����ˣ���������ô����������㽶�����ˡ� " };
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

		Log.v(TAG, "��ʼ�����水ť");
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
		// ��ʼ��topbar�ı���
		TextView tv = (TextView) findViewById(R.id.topbar_titletxt);

		// tv.setText("��GIF", TextView.BufferType.SPANNABLE);
		// ��ʼ��topbar��߰�ť
		topbar_btnLeft = (Button) findViewById(R.id.topbar_btn_back);
		topbar_btnLeft.setVisibility(View.VISIBLE);
		topbar_btnLeft.setOnClickListener(this);
		// ��ʼ��topbar�ұ߰�ť
		topbar_btnRight = (Button) findViewById(R.id.topbar_btn_right);
		topbar_btnRight.setVisibility(View.VISIBLE);
		topbar_btnRight.setOnClickListener(this);
		// ��ʼ����̬ͼƬ��GIFͼƬ��
		myImageView = (ImageView) findViewById(R.id.imageview);
		myImageView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		
		// �������myimageview �����Ƽ�����
		// ���ҵ��д���ʱ�Զ��������Ƽ��������listener
		gestureMyIvDetector = new GestureDetector(this, new MyGestureListener(
				handler, myImageView));
		myImageView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureMyIvDetector.onTouchEvent(event);
			}
		});

		myGifView = (GifView) findViewById(R.id.gifview);
		myGifViewLayout = (RelativeLayout) findViewById(R.id.layout_gifview);
		// //��ʼ����������ҡһҡ
		// sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		joke = (TextView) findViewById(R.id.picedit_joke);
	}

	private void initData() {

		Log.v(TAG, "��ʼ�����ݣ����ɵ�һ�ž�̬ͼ");

		// �����洢Ŀ¼
		File file = new File(Util.getAppStorePath());
		if (!file.exists()) {
			file.mkdir();
		}

		// ��ȡͷ�����ӵĸ���
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

		// �˴���Ӧ��ֱ�ӵ���
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
							// Toast.makeText(getApplicationContext(), "��⵽ҡ��",
							// Toast.LENGTH_LONG).show();
							MediaPlayer mp = MediaPlayer.create(
									PicEditActivity.this, R.raw.shake);
							mp.start();
							// �����߳���Ϣ
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

	// ��̨�̴߳������¼���ҡ������
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "handerler�߳��յ���Ϣ" + msg);
			super.handleMessage(msg);
			Bitmap bm;
			switch (msg.what) {
			case SENSOR_SHAKE:
				Log.v(TAG, "�༭ҳ���У��û���ҡ���ֻ�������ִ�����Ϊ�û�ѡͼ");
				// �ı�curͼ�ı��
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
				Log.v(TAG, "�༭ҳ���У�handler�յ��û���ǰ�л����ӵ�msg,ִ��");
				// Toast.makeText(PicEditActivity.this, "body change",
				// Toast.LENGTH_SHORT).show();
				// �е����ӵ�ǰһ��ͼ��������Ѿ��ǲ��Ͽ��һ��ͼ��Ĭ���е����һ��
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
				Log.v(TAG, "�༭ҳ���У�handler�յ��û������л����ӵ�msg,ִ��");
				// Toast.makeText(PicEditActivity.this, "body change",
				// Toast.LENGTH_SHORT).show();
				// �е�ǰһ������ͼ��������Ѿ��ǲ��Ͽ�����һ��ͼ��Ĭ���е���һ��
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
				Log.v(TAG, "�༭ҳ���У�handler�յ��û���ǰ�л�ͷ����msg,ִ��");
				// Toast.makeText(PicEditActivity.this, "body change",
				// Toast.LENGTH_SHORT).show();
				// �е����ӵ�ǰһ��ͼ��������Ѿ��ǲ��Ͽ��һ��ͼ��Ĭ���е����һ��
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
				Log.v(TAG, "�༭ҳ���У�handler�յ��û������л����ӵ�msg,ִ��");
				// Toast.makeText(PicEditActivity.this, "body change",
				// Toast.LENGTH_SHORT).show();
				// �е�ǰһ������ͼ��������Ѿ��ǲ��Ͽ�����һ��ͼ��Ĭ���е���һ��
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
		// ������λͼ�����˲�����
		paint.setFilterBitmap(true);

		// ���ϱ���
		Bitmap backgroundbitm = Util.getImageFromAssetFile(this,
				AppConstantS.Asset_BG_FolderName,
				AppConstantS.Asset_320BG_FileName);
		// backgroundbitm =Util.zoomBitmap(backgroundbitm, 450, 700);
		Rect dsRect = new Rect(0, 0, backgroundbitm.getWidth(),
				backgroundbitm.getHeight());

		canvas.drawBitmap(backgroundbitm, null, dsRect, paint);

		// ����ͷ�����趨������ʼλ0
		int top = 0;
		Bitmap headbitm = head;
		Rect dstRect = new Rect(0, 0, headbitm.getWidth(), headbitm.getHeight());
		canvas.drawBitmap(headbitm, null, dstRect, paint);

		// ��������,�趨������ʼλ
		top = top + headbitm.getHeight() - AppConstantS.headAndBodyOverLap;
		Bitmap bodybitm = body;
		dstRect = new Rect(0, top, bodybitm.getWidth(), bodybitm.getHeight()
				+ top);
		canvas.drawBitmap(bodybitm, null, dstRect, paint);

		// canvas.drawText("��Ҫ����", 100, 400 , paint);
		// ���ֲ���

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
				Log.v(TAG, "������ƴ�Ӳ������");
				// return drawTextBit;
			}
		}

		return drawBit;
	}

	/**
	 * ����ͼ�Ĵ�СΪ640*900 ͷ�Ĳ���Ϊ640*528 ���Ӳ���Ϊ640*702 �ص�����Ϊ640*330 ����ͼ�Ĵ�СΪ320*450
	 * ͷ�Ĳ���Ϊ320*264 ���Ӳ���Ϊ320*351 �ص�����Ϊ320*160
	 */
	Bitmap matchPic(String headName, String bodyName) {
		if (headName.equals("") || bodyName.equals("")) {
			Log.v(TAG, "���Ӻ�ͷ��ͼƬû�д�asset��ȡ������������makeCover");
		}
		Log.v(TAG, "ƴ�Ӿ�̬ͼƬ��...");

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

		// ���ɻ���������
		// Bitmap drawBit = Bitmap.createBitmap(head.getWidth(),
		// AppConstantS.makePicOriHeightPx, Bitmap.Config.ARGB_8888);

		Bitmap drawBit = Bitmap.createBitmap(320, 450, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(drawBit);
		Paint paint = new Paint();
		// ������λͼ�����˲�����
		paint.setFilterBitmap(true);

		// ���ϱ���
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

		// ����ͷ�����趨������ʼλ0
		int top = 0;
		Bitmap headbitm = head;
		Rect dstRect = new Rect(0, 0, headbitm.getWidth(), headbitm.getHeight());
		canvas.drawBitmap(headbitm, null, dstRect, paint);

		// ��������,�趨������ʼλ
		top = top + headbitm.getHeight() - AppConstantS.headAndBodyOverLap;
		Bitmap bodybitm = body;
		dstRect = new Rect(0, top, bodybitm.getWidth(), bodybitm.getHeight()
				+ top);
		canvas.drawBitmap(bodybitm, null, dstRect, paint);

		// canvas.drawText("��Ҫ����", 100, 400 , paint);
		// ���ֲ���

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
				Log.v(TAG, "������ƴ�Ӳ������");
				// return drawTextBit;
				Log.v(TAG, "������ƴ�Ӳ������");
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
		Log.v(TAG, "ִ��makeImages,��ȡ��ǰչʾ��ͷ������,��ȡ���ǵĶ��ž�̬ͼƬ,���ж�ε�ƴ��(makeimage)");
		Bitmap[] images = new Bitmap[12];
		try {
			for (int i = 0; i < images.length; i++) {
				Bitmap src = matchPic(Util.head[i], Util.body[i]);
				images[i] = src;
			}
		} catch (Exception e) {
			Log.v(TAG, "ִ��makeImagesʧ��");
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
			// ����Ϊ,images�ʹ洢��Ŀ��·��
			j.jpgToGif(matchPicsFromCurStaticPic(), path);
			Log.v(TAG, "����Gif��ϣ�ͼƬ�������룺" + path);
		} catch (Exception e) {
			Log.v(TAG, "����Gifʧ����,����jpgToGif");
		}

		// ���ͼƬ�ڴ�
		// for(int i=0;i<pics.length;i++) {
		// pics[i].recycle();
		//
		// }
	}

	public void onClick(View v) {
		switch (v.getId()) {
		// �лر༭ҳ��
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
			// PicEditActivity.this, "���Ե�...", "������...", true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = true;
			Log.v(TAG, "��ʼִ���첽����jpg2gif��gif������");
			try {
				picsToGif();
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
				Log.v(TAG, "gif����ʧ��");
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
						Log.v(TAG, "GifView������ϣ�����GIFͼƬ����ǰUI�ɹ�");
					} catch (Exception e) {
						e.printStackTrace();
						Log.v(TAG, "����GIFͼƬ����ǰUIʧ��");
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
		// ���ͷ���ͽŵ�ͼƬ�ڴ棬��ֹOOM��
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