package com.showgif.gif.ui.activity;

import com.tencent.mm.sdk.openapi.WXTextObject;

import java.io.File;

import com.showgif.gif.util.MMAlert;

import com.showgif.R;
import com.showgif.gif.ShareActivity;
import com.showgif.gif.util.AccessTokenKeeper;
import com.showgif.gif.util.AppConstantS;
import com.showgif.gif.util.Util;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.GetMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXEmojiObject;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 分享页
 */
public class PicShareActivity extends Activity implements IWXAPIEventHandler {
	public static Oauth2AccessToken weiboAccessToken;
	SsoHandler mSsoHandler;
	private Weibo mWeibo;
	private static final int MMAlertSelect1 = 0;
	private static final int MMAlertSelect2 = 1;
	private static final int MMAlertSelect3 = 2;

	// 微博缩略图
	private static final int THUMB_SIZE = 150;
	private IWXAPI mmAPI;

	public static boolean isFromWX = false;
	public Bundle bundle;

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		bundle = intent.getExtras();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picshare);
		bundle = getIntent().getExtras();
		// 设置MAIN的大标题
		TextView tv = (TextView) findViewById(R.id.topbar_titletxt);
		tv.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.topbar_txt_share));

		// 左边按钮
		Button btnLeft = (Button) findViewById(R.id.topbar_btn_back);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				PicShareActivity.this.finish();
				Intent intent = new Intent(PicShareActivity.this,
						PicEditActivity.class);
				startActivity(intent);
				PicShareActivity.this.finish();
			}
		});
		weiboAccessToken = AccessTokenKeeper.readAccessToken(this);

		// 微信分享
		// 测试id
		mmAPI = WXAPIFactory.createWXAPI(PicShareActivity.this,
				"wxbd56405fa9634bc6", false);
		mmAPI.registerApp("wxbd56405fa9634bc6");

		// 正式注册id
		// mmAPI = WXAPIFactory.createWXAPI(PicShareActivity.this,
		// "wxc905f64571ad9581 ", false);
		// mmAPI.registerApp("wxc905f64571ad9581 ");
		Button btnShare2Weixin = (Button) findViewById(R.id.share2weixin);
		btnShare2Weixin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final String path = Util.getAppStorePath() + File.separator
						+ PicEditActivity.GIF_STORENAME;
				MMAlert.showAlert(
						PicShareActivity.this,
						getString(R.string.pic_share2weixin),
						PicShareActivity.this.getResources().getStringArray(
								R.array.pic_share2weixin_items), null,
						new MMAlert.OnAlertSelectId() {
							public void onClick(int whichButton) {
								File file = new File(path);
								if (!file.exists()) {
									String tip = "文件不存在 ";
									Toast.makeText(PicShareActivity.this,
											tip + " path = " + mmAPI,
											Toast.LENGTH_LONG).show();
									return;
								}

								switch (whichButton) {
								case MMAlertSelect1: {
									Log.i("123", "weixin1");
									System.out.println("111");
									WXImageObject imgObj = new WXImageObject();
									imgObj.setImagePath(Util.getAppStorePath()
											+ File.separator
											+ PicEditActivity.GIF_STORENAME);
									WXMediaMessage msg = new WXMediaMessage();
									msg.mediaObject = imgObj;

									Bitmap bmp = BitmapFactory.decodeFile(Util
											.getAppStorePath()
											+ File.separator
											+ PicEditActivity.GIF_STORENAME);
									Bitmap thumbBmp = Bitmap
											.createScaledBitmap(bmp,
													THUMB_SIZE, THUMB_SIZE,
													true);
									bmp.recycle();
									msg.thumbData = Util.bmpToByteArray(
											thumbBmp, true);
									//
									SendMessageToWX.Req req = new SendMessageToWX.Req();
									req.transaction = buildTransaction("img");
									req.message = msg;
									req.scene = SendMessageToWX.Req.WXSceneSession;
									mmAPI.sendReq(req);
									Toast.makeText(
											PicShareActivity.this,
											PicShareActivity.this
													.getResources()
													.getString(
															R.string.app_undofunction),
											Toast.LENGTH_LONG).show();
									break;
								}
								case MMAlertSelect2: {
									if (isFromWX) {
										Log.i("123", "weixin2");
										WXEmojiObject emoji = new WXEmojiObject();
										emoji.emojiPath = path;

										WXMediaMessage msg = new WXMediaMessage(
												emoji);
										msg.title = "Emoji Title";
										msg.description = "Emoji Description";

										Bitmap bmp = BitmapFactory
												.decodeFile(path);
										Bitmap thumbBmp = Bitmap
												.createScaledBitmap(bmp,
														THUMB_SIZE, THUMB_SIZE,
														true);
										bmp.recycle();
										msg.thumbData = Util.bmpToByteArray(
												thumbBmp, true);

										GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
										resp.transaction = getTransaction();
										resp.message = msg;

										mmAPI.sendResp(resp);
									} else {
										Log.i("123", "weixin2else");
										WXEmojiObject emoji = new WXEmojiObject();
										emoji.emojiPath = path;

										WXMediaMessage msg = new WXMediaMessage(
												emoji);
										msg.title = "Emoji Title";
										msg.description = "Emoji Description";

										Bitmap bmp = BitmapFactory
												.decodeFile(path);
										Bitmap thumbBmp = Bitmap
												.createScaledBitmap(bmp,
														THUMB_SIZE, THUMB_SIZE,
														true);
										bmp.recycle();
										msg.thumbData = Util.bmpToByteArray(
												thumbBmp, true);
										// msg.thumbData =
										// Util.readFromFile(EMOJI_FILE_THUMB_PATH,
										// 0, (int) new
										// File(EMOJI_FILE_THUMB_PATH).length());

										SendMessageToWX.Req req = new SendMessageToWX.Req();
										req.transaction = buildTransaction("emoji");
										req.message = msg;
										req.scene =
										// SendMessageToWX.Req.WXSceneTimeline;
										SendMessageToWX.Req.WXSceneSession;
										mmAPI.sendReq(req);
									}
									//PicShareActivity.this.finish();
									break;
								}
								case MMAlertSelect3: {
									Log.i("123", "weixin3");
									System.out.println("111");
									WXImageObject imgObj = new WXImageObject();
									imgObj.setImagePath(Util.getAppStorePath()
											+ File.separator
											+ PicEditActivity.GIF_STORENAME);
									WXMediaMessage msg = new WXMediaMessage();
									msg.mediaObject = imgObj;

									Bitmap bmp = BitmapFactory.decodeFile(Util
											.getAppStorePath()
											+ File.separator
											+ PicEditActivity.GIF_STORENAME);
									Bitmap thumbBmp = Bitmap
											.createScaledBitmap(bmp,
													THUMB_SIZE, THUMB_SIZE,
													true);
									bmp.recycle();
									msg.thumbData = Util.bmpToByteArray(
											thumbBmp, true);
									//
									SendMessageToWX.Req req = new SendMessageToWX.Req();
									req.transaction = buildTransaction("img");
									req.message = msg;
									req.scene = SendMessageToWX.Req.WXSceneSession;
									mmAPI.sendReq(req);
									Toast.makeText(
											PicShareActivity.this,
											PicShareActivity.this
													.getResources()
													.getString(
															R.string.app_undofunction),
											Toast.LENGTH_LONG).show();

								}
								default:
									break;
								}
							}
						});
			}
		});

		// 新浪微博分享
		Button btnShare2SinaWeibo = (Button) findViewById(R.id.share2sinaweibo);
		btnShare2SinaWeibo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mWeibo = Weibo.getInstance(AppConstantS.APP_KEY,
						AppConstantS.REDIRECT_URL);

				mSsoHandler = new SsoHandler(PicShareActivity.this, mWeibo);
				mSsoHandler.authorize(new AuthDialogListener());
				// mWeibo.anthorize(PicShareActivity.this, new
				// AuthDialogListener());
				try {
					Class sso = Class
							.forName("com.weibo.sdk.android.sso.SsoHandler");
				} catch (ClassNotFoundException e) {
					// e.printStackTrace();
					Log.i("123",
							"com.weibo.sdk.android.sso.SsoHandler not found");
				}
			}
		});

		// 新浪微博分享
		// LinearLayout btnShare2QQWeibo= (LinearLayout)
		// findViewById(R.id.share2qqweibo);
		// btnShare2QQWeibo.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// Toast.makeText(PicShareActivity.this,
		// PicShareActivity.this.getResources()
		// .getString(R.string.app_undofunction),Toast.LENGTH_LONG).show();
		// }
		// });
	}

	// 腾讯微博记录操作时间
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	// 新浪微波的sso回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// sso 授权回调
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	class AuthDialogListener implements WeiboAuthListener {
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			System.out.println("token" + token);
			String expires_in = values.getString("expires_in");
			System.out.println("EI" + expires_in);
			for (String key : values.keySet()) {
				System.out.println("values:key = " + key + " value = "
						+ values.getString(key));
			}
			weiboAccessToken = new Oauth2AccessToken(token, expires_in);

			// String code = values.getString("code");
			// EditPicActivity.accessToken = new Oauth2AccessToken(code);
			System.out.println("到我了");
			if (weiboAccessToken.isSessionValid()) {
				System.out.println("认证成功");
				String path = Util.getAppStorePath() + "/"
						+ PicEditActivity.GIF_STORENAME;
				Intent it = new Intent(PicShareActivity.this,
						ShareActivity.class);
				it.putExtra(ShareActivity.EXTRA_ACCESS_TOKEN,
						weiboAccessToken.getToken());
				it.putExtra(ShareActivity.EXTRA_EXPIRES_IN,
						weiboAccessToken.getExpiresTime());
				it.putExtra(ShareActivity.EXTRA_PIC_PATH_, path);
				startActivity(it);

				AccessTokenKeeper.keepAccessToken(PicShareActivity.this,
						weiboAccessToken);
			} else {
				System.out.println("认证失败");
			}
		}

		public void onError(WeiboDialogError e) {
			System.out.println("认证错误");
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		public void onCancel() {
			System.out.println("认证取消");
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		public void onWeiboException(WeiboException e) {
			System.out.println("认证报错");
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
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

	private String getTransaction() {
		final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle);
		return req.transaction;
	}

	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub

	}

	public void onResp(BaseResp arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			PicShareActivity.this.finish();
			Intent intent = new Intent(PicShareActivity.this,
					PicEditActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
