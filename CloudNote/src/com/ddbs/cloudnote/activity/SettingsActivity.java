package com.ddbs.cloudnote.activity;

import com.ddbs.cloudnote.R;
import com.ddbs.cloudnote.config.ApplicationConfig;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {
	private Button saveButton;
	private EditText serverEditText;
	private SharedPreferences.Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		saveButton = (Button) findViewById(R.id.save_settings);
		serverEditText = (EditText) findViewById(R.id.server_base);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(serverEditText.getText().toString().equals("")){
					Toast.makeText(SettingsActivity.this, "SERVER URL不能为空", Toast.LENGTH_SHORT).show();
				}else{
					editor = getSharedPreferences("data", MODE_PRIVATE).edit();
					editor.putString("serverBase", serverEditText.getText().toString());
					ApplicationConfig.serverBase = serverEditText.getText().toString();
					Toast.makeText(SettingsActivity.this, "修改成功\n 请返回登录界面登录", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
}
