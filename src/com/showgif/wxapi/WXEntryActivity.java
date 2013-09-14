package com.showgif.wxapi;


import com.showgif.gif.ui.activity.PicEditActivity;
import com.showgif.gif.ui.activity.PicShareActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private Button gotoBtn, regBtn, launchBtn, checkBtn;

	// IWXAPI�ǵ�����app��΢��ͨ�ŵ�openapi�ӿ�
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ͨ��WXAPIFactory��������ȡIWXAPI��ʵ��
    	api = WXAPIFactory.createWXAPI(this, "", false);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		System.out.println("onNewIntent");
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// ΢�ŷ������󵽵�����Ӧ��ʱ����ص����÷���
	public void onReq(BaseReq req) {
		System.out.println("onReq����������");
		System.out.println("isfromwx:" + PicEditActivity.isFromWX);
		PicEditActivity.isFromWX = true;
		
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				System.out.println("COMMAND_GETMESSAGE_FROM_WX");	
				Intent in = new Intent();
			    in.setClass(this, PicEditActivity.class);
				in.putExtras(getIntent());
			    startActivity(in);
			    finish();
			    Log.v("123","getmsg from wx");
	
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				System.out.println("COMMAND_SHOWMESSAGE_FROM_WX");	
				break;
			default:
				break;
		}
	}

	public void onResp(BaseResp arg0) {
		System.out.println("onResp����������");
		//Intent in = new Intent();
	   //in.setClass(this, PicShareActivity.class);
		//in.putExtras(getIntent());
	    //startActivity(in);
	    finish();
		Log.v("123","123");
	}
}