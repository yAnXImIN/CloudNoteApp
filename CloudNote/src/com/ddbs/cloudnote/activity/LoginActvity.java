package com.ddbs.cloudnote.activity;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ddbs.cloudnote.R;
import com.ddbs.cloudnote.config.ApplicationConfig;
import com.ddbs.cloudnote.util.ActivityCollector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Created in <b>2015-11-17 19:44:15</b><br>      
 * This class is designed for user to <b>Login</b>
 * @author yanximin
 * @version 1.0
 * */
public class LoginActvity extends BaseActivity implements OnClickListener{
	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button loginButton;
	private Button registerButton;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private CheckBox remeberPass;
	public static final int FAIL_NET = 0;
	public static final int SUCCESS_LOG = 1;
	public static final int FAIL_WRONG_PASS = -1;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FAIL_NET:
				Toast.makeText(LoginActvity.this, "网络异常，请稍后再试", Toast.LENGTH_LONG).show();
				break;
			case SUCCESS_LOG:
				String username = usernameEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				editor = pref.edit();
				if(remeberPass.isChecked()){
					editor.putBoolean("remember_password", true);
					editor.putString("username", username);
					editor.putString("password", password);
				}else{
					editor.clear();
				}
				editor.commit();
				Intent intent = new Intent(LoginActvity.this,NoteTitleActivity.class);
				startActivity(intent);
				break;
			case FAIL_WRONG_PASS:
				Toast.makeText(LoginActvity.this, "用户名密码错误", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addAct(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.login);
		registerButton = (Button) findViewById(R.id.register);
		loginButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		remeberPass = (CheckBox) findViewById(R.id.remeberPass);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isRemeber = pref.getBoolean("remember_password", false );
		if(isRemeber){
			String userName = pref.getString("username", "");
			String password = pref.getString("password", "");
			usernameEditText.setText(userName);
			passwordEditText.setText(password);
			remeberPass.setChecked(true);
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			String username = usernameEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			editor = pref.edit();
			login(username, password);
			break;
		case R.id.register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ActivityCollector.removeAct(this);
		super.onDestroy();
	}
	private void login(String username1 ,String password1){
		//TODO 完成登录
		final String username = username1;
		final String password = password1;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 设置请求头
				HttpParams httpParams = new BasicHttpParams(); 
				HttpConnectionParams.setConnectionTimeout(httpParams, 10000); //设置连接超时
				HttpConnectionParams.setSoTimeout(httpParams, 10000); //设置请求超时
				//设置请求
				HttpClient httpClient = new DefaultHttpClient(httpParams);
				//设置请求参数
				HttpPost httpPost = new HttpPost(ApplicationConfig.serverBase+"/login.php");
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
				
				//发送请求
				try {
					HttpResponse response = httpClient.execute(httpPost);
					if(response.getStatusLine().getStatusCode() == 200){
						String strRes = EntityUtils.toString(response.getEntity(), "utf-8");
						JSONObject jsonObject = new JSONObject(strRes);
						int messageid = jsonObject.getInt("messageId");
						if(messageid==1){
							int userid = jsonObject.getJSONObject("responseData").getInt("id");
							ApplicationConfig.userId = userid;
							ApplicationConfig.username = username;
							ApplicationConfig.password = password;
							message.what = SUCCESS_LOG;
							JSONObject responseData = jsonObject.getJSONObject("responseData");
							ApplicationConfig.userId = responseData.getInt("id");
						}else{
							message.what = FAIL_WRONG_PASS;
						}
					}else{
						message.what = FAIL_NET;
					}
				}catch (Exception e) {
					message.what = FAIL_NET;
				}
				handler.sendMessage(message);
			}
		}).start();
	}
	
}
