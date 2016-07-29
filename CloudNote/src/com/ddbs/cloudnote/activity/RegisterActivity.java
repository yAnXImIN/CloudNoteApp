package com.ddbs.cloudnote.activity;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.ddbs.cloudnote.R;
import com.ddbs.cloudnote.config.ApplicationConfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Created in <b>2015-11-17 19:38:48</b><br>      
 * This class is designed for user to <b>register a new count</b>
 * @author yanximin
 * @version 1.0
 * */
public class RegisterActivity extends BaseActivity implements OnClickListener{
	private EditText usernameEditText;
	private EditText passwordEditText;
	private EditText passwordConfirmEditText;
	private Button registerButton;
	public static final int FAIL_NET = 0;
	public static final int SUCCESS_LOG = 1;
	public static final int FAIL_WRONG_PASS = -1;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FAIL_NET:
				Toast.makeText(RegisterActivity.this, "网络异常，请稍后再试", Toast.LENGTH_LONG).show();
				break;
			case SUCCESS_LOG:
				Toast.makeText(RegisterActivity.this, "恭喜，注册成功。即将转跳", Toast.LENGTH_LONG).show();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Toast.makeText(RegisterActivity.this, "转跳失败，请手动转跳", Toast.LENGTH_LONG).show();
				}
				RegisterActivity.this.finish();
				break;
			case FAIL_WRONG_PASS:
				Toast.makeText(RegisterActivity.this, "亲，用户名已存在", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		passwordConfirmEditText = (EditText) findViewById(R.id.password_confirm);
		registerButton = (Button) findViewById(R.id.register);
		registerButton.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		if(usernameEditText.getText().toString().equals("")){
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			
			//获得焦点
			usernameEditText.setFocusable(true);
			usernameEditText.setFocusableInTouchMode(true);
			usernameEditText.requestFocus();
			usernameEditText.requestFocusFromTouch();
			
			return;
		}
		if(!passwordEditText.getText().toString().equals(passwordConfirmEditText.getText().toString())){
			Toast.makeText(this, "两次密码输入不统一，请重新输入！", Toast.LENGTH_SHORT).show();
			
			//获得焦点
			passwordEditText.setFocusable(true);
			passwordEditText.setFocusableInTouchMode(true);
			passwordEditText.requestFocus();
			passwordEditText.requestFocusFromTouch();
			
			return;
		}
		
		//TODO 获取到注册成功与否的消息，如注册成功则跳转，否则重新填写!
		final String username = usernameEditText.getText().toString();
		final String password = passwordEditText.getText().toString();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpParams httpParams = new BasicHttpParams(); 
				HttpConnectionParams.setConnectionTimeout(httpParams, 10000); //设置连接超时
				HttpConnectionParams.setSoTimeout(httpParams, 10000); //设置请求超时
				HttpClient httpClient = new DefaultHttpClient(httpParams);
				
				HttpPost httpPost = new HttpPost(ApplicationConfig.serverBase+"/register.php");
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", username));
				params.add(new BasicNameValuePair("password", password));
				UrlEncodedFormEntity entity = null;
				Message message = new Message();
				try {
					entity = new UrlEncodedFormEntity(params, "utf-8");
				} catch (UnsupportedEncodingException e) {
					message.what = FAIL_NET;
				}
				httpPost.setEntity(entity);
				try {
					HttpResponse response = httpClient.execute(httpPost);
					if(response.getStatusLine().getStatusCode() == 200){
						String strRes = EntityUtils.toString(response.getEntity(), "utf-8");
						JSONObject jsonObject = new JSONObject(strRes);
						int messageid = jsonObject.getInt("messageId");
						if(messageid==1){
							message.what = SUCCESS_LOG;
						}else{
							message.what = FAIL_WRONG_PASS;
						}
					}else{
						message.what = FAIL_NET;
					}
				} catch (Exception e) {
					message.what = FAIL_NET;
				}
				handler.sendMessage(message);
			}
		}).start();
	}

}
