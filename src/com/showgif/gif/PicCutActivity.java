package com.showgif.gif;
//package com.example.gif;
//
//import java.io.File;
//import java.net.URL;
//import java.security.PublicKey;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import com.weibo.sdk.android.Oauth2AccessToken;
//import com.weibo.sdk.android.Weibo;
//import com.weibo.sdk.android.WeiboAuthListener;
//import com.weibo.sdk.android.WeiboDialogError;
//import com.weibo.sdk.android.WeiboException;
//import com.weibo.sdk.android.sso.SsoHandler;
//import com.weibo.sdk.android.util.Utility;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class PicCutActivity extends Activity implements OnClickListener {
//	private ImageButton img_btn;
//	private Button btn;
//	private Button shareSinaWeiboBtn;
//	public Button sharePicWeiboBtn;
//	private TextView tv;
//	
//	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// ����
//	private static final int PHOTO_REQUEST_GALLERY = 2;// �������ѡ��
//	private static final int PHOTO_REQUEST_CUT = 3;// ���
//	
//	private Weibo mWeibo;
//	private static final String CONSUMER_KEY = "966056985";// �滻Ϊ�����ߵ�appkey������"1646212860";
//	private static final String REDIRECT_URL = "http://www.sina.com";
//	private Intent it = null;
//	private Button authBtn,apiBtn,ssoBtn,cancelBtn;
//	private TextView mText;
//	public static Oauth2AccessToken accessToken ;
//	
//	public static final String TAG="PicCutActivity";
//	
//	
//	
//	SsoHandler mSsoHandler;
//	// ����һ���Ե�ǰʱ��Ϊ���Ƶ��ļ�
//	File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
//	
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//		init();
//	}
//
//	// ��ʼ���ؼ�
//	private void init() {
//		
//		img_btn = (ImageButton) findViewById(R.id.img_btn);
//		btn = (Button) findViewById(R.id.btn);
//		shareSinaWeiboBtn = (Button)findViewById(R.id.sharesina_btn);
//		sharePicWeiboBtn= (Button)findViewById(R.id.sharepicsina_btn);
//		tv = (TextView) findViewById(R.id.tv);
//		
//		// ΪImageButton��Button��Ӽ����¼�
//		img_btn.setOnClickListener(this);
//		btn.setOnClickListener(this);
//		shareSinaWeiboBtn.setOnClickListener(this);
//		sharePicWeiboBtn.setOnClickListener(this);
//		
//		PicCutActivity.accessToken=AccessTokenKeeper.readAccessToken(this);
//		System.out.println("oncreate");
//		if(PicCutActivity.accessToken.isSessionValid()){
//        	Weibo.isWifi=Utility.isWifi(this);
//            try {
//                Class sso=Class.forName("com.weibo.sdk.android.api.WeiboAPI");//���֧��weiboapi�Ļ�����ʾapi������ʾ��ڰ�ť
//                sharePicWeiboBtn.setVisibility(View.VISIBLE);
//            } catch (ClassNotFoundException e) {
////                e.printStackTrace();
//                Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");
//               
//            }
//            String date = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new java.util.Date(PicCutActivity.accessToken.getExpiresTime()));
//            tv.setText("access_token ������Ч����,�����ٴε�¼: \naccess_token:"+ PicCutActivity.accessToken.getToken() + "\n��Ч�ڣ�"+date);
//       
//		
//		}
//	}
//
//	// ����¼�
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.img_btn:
//			showDialog();
//			break;
//
//		case R.id.btn:
//			showDialog();
//			break;
//				
//		case R.id.sharesina_btn:
//			mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
//			
//			try {
//	            Class sso=Class.forName("com.weibo.sdk.android.sso.SsoHandler");
//	        } catch (ClassNotFoundException e) {
////	            e.printStackTrace();
//	            Log.i(TAG, "com.weibo.sdk.android.sso.SsoHandler not found");
//	            
//	        }
//	        System.out.println("�����֤");
//            mSsoHandler = new SsoHandler(PicCutActivity.this,mWeibo);
//            mSsoHandler.authorize( new AuthDialogListener()); 
//            
//		case R.id.sharepicsina_btn:
//			Intent it = new Intent(PicCutActivity.this, ShareActivity.class);
//            it.putExtra(ShareActivity.EXTRA_ACCESS_TOKEN, PicCutActivity.accessToken.getToken());
//            it.putExtra(ShareActivity.EXTRA_EXPIRES_IN, PicCutActivity.accessToken.getExpiresTime());
//            it.putExtra(ShareActivity.EXTRA_PIC_PATH_,tempFile.getPath());
//            startActivity(it);
//			break;
//		}
//		
//			
//	}
//	
//	class AuthDialogListener implements WeiboAuthListener {
//
//		public void onComplete(Bundle values) {
//			String token = values.getString("access_token");
//			String expires_in = values.getString("expires_in");
//			PicCutActivity.accessToken = new Oauth2AccessToken(token, expires_in);
//			if (PicCutActivity.accessToken.isSessionValid()) {
//				System.out.println("��֤�ɹ�");
//				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(PicCutActivity.accessToken.getExpiresTime()));
//				tv.setText("��֤�ɹ�: \r\n access_token: "+ token + "\r\n" + "expires_in: " + expires_in+"\r\n��Ч�ڣ�"+date);
//				try {
//	                Class sso=Class.forName("com.weibo.sdk.android.api.WeiboAPI");//���֧��weiboapi�Ļ�����ʾapi������ʾ��ڰ�ť
//	                sharePicWeiboBtn.setVisibility(View.VISIBLE);
//	            } catch (ClassNotFoundException e) {
////	                e.printStackTrace();
//	                Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");
//	               
//	            }
//				//cancelBtn.setVisibility(View.VISIBLE);
//				AccessTokenKeeper.keepAccessToken(PicCutActivity.this, accessToken);
//				Toast.makeText(PicCutActivity.this, "��֤�ɹ�", Toast.LENGTH_SHORT).show();
//			}
//			System.out.println("��֤ʧ��");
//		}
//
//		public void onError(WeiboDialogError e) {
//			System.out.println("��֤����");
//			Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(),
//					Toast.LENGTH_LONG).show();
//		}
//
//		public void onCancel() {
//			System.out.println("��֤ȡ��");
//			Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
//		}
//
//		public void onWeiboException(WeiboException e) {
//			System.out.println("��֤����");
//			Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(),
//					Toast.LENGTH_LONG).show();
//		}
//
//	}
//
// 
//
//	// ��ʾ�Ի��򷽷�
//	private void showDialog() {
//		new AlertDialog.Builder(this).setTitle("ͷ������").setPositiveButton("����", new DialogInterface.OnClickListener() {
//
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//				// ����ϵͳ�����չ���
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				// ָ������������պ���Ƭ�Ĵ���·��
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
//				startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
//			}
//		}).setNegativeButton("���", new DialogInterface.OnClickListener() {
//
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//				Intent intent = new Intent(Intent.ACTION_PICK, null);
//				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
//			}
//		}).show();
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		/**
//         * ��������ע�͵��Ĵ��룬����sdk֧��ssoʱ��Ч��
//         */
//        if(mSsoHandler!=null){
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//		
//		
//		switch (requestCode) {
//		case PHOTO_REQUEST_TAKEPHOTO:
//			startPhotoZoom(Uri.fromFile(tempFile), 150);
//			break;
//
//		case PHOTO_REQUEST_GALLERY:
//			if (data != null)
//				startPhotoZoom(data.getData(), 150);
//			break;
//
//		case PHOTO_REQUEST_CUT:
//			if (data != null)
//				setPicToView(data);
//			break;
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//
//	}
//
//	private void startPhotoZoom(Uri uri, int size) {
//		Intent intent = new Intent("com.android.camera.action.CROP");
//		intent.setDataAndType(uri, "image/*");
//		// cropΪtrue�������ڿ�����intent��������ʾ��view���Լ���
//		intent.putExtra("crop", "true");
//
//		// aspectX aspectY �ǿ�ߵı���
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//
//		// outputX,outputY �Ǽ���ͼƬ�Ŀ��
//		intent.putExtra("outputX", size);
//		intent.putExtra("outputY", size);
//		intent.putExtra("return-data", true);
//
//		startActivityForResult(intent, PHOTO_REQUEST_CUT);
//	}
//
//	// �����м��ú��ͼƬ��ʾ��UI������
//	private void setPicToView(Intent picdata) {
//		Bundle bundle = picdata.getExtras();
//		if (bundle != null) {
//			Bitmap photo = bundle.getParcelable("data");
//			Drawable drawable = new BitmapDrawable(photo);
//			img_btn.setBackgroundDrawable(drawable);
//		}
//	}
//
//	// ʹ��ϵͳ��ǰ���ڼ��Ե�����Ϊ��Ƭ������
//	private String getPhotoFileName() {
//		Date date = new Date(System.currentTimeMillis());
//		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
//		return dateFormat.format(date) + ".jpg";
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		return true;
//	}
//
//}
