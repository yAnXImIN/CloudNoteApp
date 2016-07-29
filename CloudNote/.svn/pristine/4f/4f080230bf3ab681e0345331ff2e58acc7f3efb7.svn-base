package com.ddbs.cloudnote.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import com.ddbs.cloudnote.dao.MyDatabaseHelper;
import com.ddbs.cloudnote.entity.Note;
import com.ddbs.cloudnote.util.WebUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends BaseActivity implements OnClickListener{
	private EditText titleEditText;
	private EditText contentEditText;
	private Button saveButton;
	private Note note;
	private static final String ADDURL = "/edit.php";
	private static final String UPDATEURL = "/update.php";
	private MyDatabaseHelper dbHelper;
	public static void start(Context context,Note note){
		Intent intent = new Intent(context, EditActivity.class);
		intent.putExtra("note", note);
		context.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.note_content_edit);
		dbHelper = new MyDatabaseHelper(this, "MyNote.db", null, 1);
		note = (Note)getIntent().getSerializableExtra("note");
		String title = note.getTitle();
		String content = note.getContent();
		titleEditText = (EditText) findViewById(R.id.note_title_edit);
		contentEditText = (EditText) findViewById(R.id.note_content_edit);
		saveButton = (Button) findViewById(R.id.save_note);
		saveButton.setOnClickListener(this);
		titleEditText.setText(title);
		contentEditText.setText(content);
		
	}
	@Override
	public void onClick(View v) {
		if(this.note.getId()==Note.NEWCREATE){
			//TODO 新建笔记
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			Random random = new Random();
			int id = -1*random.nextInt(10000);
			values.put("id", id);
			values.put("title", titleEditText.getText().toString());
			values.put("content", contentEditText.getText().toString());
			values.put("time", System.currentTimeMillis());
			database.insert("note", null, values);
			new AddNote().execute(id);
		}else{
			//TODO 修改笔记
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("title", titleEditText.getText().toString());
			values.put("content", contentEditText.getText().toString());
			database.update("note", values, "id=?", new String[]{""+this.note.getId()});
			new UpdateNote().execute(this.note.getId());
		}
	}
	class AddNote extends AsyncTask<Integer, Integer, Boolean>{
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG).show();
				EditActivity.this.finish();
			}else{
				Toast.makeText(getApplicationContext(), "处于离线添加模式", Toast.LENGTH_LONG).show();
				EditActivity.this.finish();
			}
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("title",titleEditText.getText().toString()));
			requestParams.add(new BasicNameValuePair("content", contentEditText.getText().toString()));
			
			
			try {
				String returnStr = WebUtil.getResponceByPost(ApplicationConfig.serverBase+ADDURL, requestParams);
				JSONObject returnObj = new JSONObject(returnStr);
				int messageId = returnObj.getInt("messageId");
				JSONObject data = returnObj.getJSONObject("responseData");
				int new_id = data.getInt("id");
				if(messageId==1){
					ContentValues values = new ContentValues();
					values.put("id", new_id);
					SQLiteDatabase database = dbHelper.getWritableDatabase();
					database.update("note", values, "id=?", new String[]{""+params[0]});
					return true;
				}
			} catch (Exception e) {
				
				return false;
			}
			return false;
		}
		
	}
	class UpdateNote extends AsyncTask<Integer, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Integer... params) {
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("title",titleEditText.getText().toString()));
			requestParams.add(new BasicNameValuePair("content", contentEditText.getText().toString()));
			requestParams.add(new BasicNameValuePair("note_id", ""+params[0]));
			try {
				String returnStr = WebUtil.getResponceByPost(ApplicationConfig.serverBase+UPDATEURL, requestParams);
				JSONObject returnObj = new JSONObject(returnStr);
				int messageId = returnObj.getInt("messageId");
				
				if(messageId==1){
					return true;
				}
			} catch (Exception e) {
				
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
				EditActivity.this.finish();
			}else{
				Toast.makeText(getApplicationContext(), "处于离线修改模式", Toast.LENGTH_LONG).show();
				EditActivity.this.finish();
			}
			super.onPostExecute(result);
		}
		
	}
}
