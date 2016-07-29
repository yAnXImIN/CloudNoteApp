package com.ddbs.cloudnote.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.json.JSONArray;
import org.json.JSONObject;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.ddbs.cloudnote.R;
import com.ddbs.cloudnote.config.ApplicationConfig;
import com.ddbs.cloudnote.dao.MyDatabaseHelper;
import com.ddbs.cloudnote.entity.Note;
import com.ddbs.cloudnote.util.ActivityCollector;
import com.ddbs.cloudnote.util.DensityUtils;
import com.ddbs.cloudnote.util.RefreshableView;
import com.ddbs.cloudnote.util.RefreshableView.PullToRefreshListener;
import com.ddbs.cloudnote.util.WebUtil;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NoteTitleActivity extends BaseActivity{
	private SwipeMenuListView noteTitleList;
	private Button addNewNote;
	private RefreshableView refreshableView;
	private List<Note> noteList = new ArrayList<Note>();
	private NoteAdapter adapter;
	private MyDatabaseHelper dbHelper;
	private long lastReturnTime = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dbHelper = new MyDatabaseHelper(this, "MyNote.db", null, 1);
		setContentView(R.layout.note_title);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO 从服务器获取数据
				new RefreshFromServer().execute();
			}
		}, 0);
		noteTitleList = (SwipeMenuListView) findViewById(R.id.note_title_list_view);
		noteTitleList.setMenuCreator(new SwipeMenuCreator() {
			
			@Override
			public void create(SwipeMenu menu) {
				//	修改操作
			    SwipeMenuItem openItem = new SwipeMenuItem(
		                getApplicationContext());
		        openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
		                0xCE)));
		        openItem.setWidth(DensityUtils.dp2px(getApplicationContext(),90));
		        openItem.setTitle("修改");
		        openItem.setTitleSize(18);
		        openItem.setTitleColor(Color.WHITE);
		        menu.addMenuItem(openItem);

		        // 删除操作
		        SwipeMenuItem deleteItem = new SwipeMenuItem(
		                getApplicationContext());
		        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
		                0x3F, 0x25)));
		        deleteItem.setWidth(DensityUtils.dp2px(getApplicationContext(),90));
		        deleteItem.setTitle("删除");
		        deleteItem.setTitleSize(18);
		        deleteItem.setTitleColor(Color.WHITE);
		        menu.addMenuItem(deleteItem);
				
			}
		});
		noteTitleList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					//TODO 更新操作
					EditActivity.start(NoteTitleActivity.this, noteList.get(position));
					break;
				case 1:
					//TODO 删除操作
					final int pos = position;
					new AlertDialog.Builder(NoteTitleActivity.this).setTitle("确认删除")
					.setMessage("此条笔记将被删除")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 添加删除功能
							new DeleteFromServer().execute(pos);
							SQLiteDatabase db = dbHelper.getWritableDatabase();
							db.delete("note", "id = ?", new String[]{""+adapter.getItem(pos).getId()});
							adapter.remove(adapter.getItem(pos));
							Toast.makeText(NoteTitleActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							adapter.notifyDataSetChanged();
						}
					}).show();
					break;
				default:
					break;
				}
				return false;
			}
		});
		//使ListView可以左滑
		noteTitleList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
		
		adapter = new NoteAdapter(this, R.layout.note_item, noteList);
		noteTitleList.setAdapter(adapter);
		// 添加点击事件
		noteTitleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NoteContentActivity.start(NoteTitleActivity.this,
						noteList.get(position));
			}
		});
		addNewNote = (Button) findViewById(R.id.add_new_note);
		addNewNote.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Note note = new Note();
				note.setId(Note.NEWCREATE);
				EditActivity.start(NoteTitleActivity.this,note);
			}
		});
	}
	
	@Override
	protected void onResume() {
		initList();
		super.onResume();
	}

	public class NoteAdapter extends ArrayAdapter<Note> {
		private int resourceId;

		public NoteAdapter(Context context, int resource, List<Note> objects) {
			super(context, resource, objects);
			resourceId = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Note note = getItem(position);
			View view;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(resourceId,
						null);
			} else {
				view = convertView;
			}
			TextView noteTitleText = (TextView) view
					.findViewById(R.id.note_title);
			noteTitleText.setText(note.getTitle());
			TextView noteContentText = (TextView) view
					.findViewById(R.id.note_content);
			noteContentText.setText(note.getContent());
			return view;
		}

	}

	// 获取笔记
	private void initList() {
		noteList.clear();
		new RefreshFromDB().execute();
	}

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - lastReturnTime > 2500) {
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			lastReturnTime = System.currentTimeMillis();
		} else {
			ActivityCollector.finishAll();
		}
	}
	class RefreshFromDB extends AsyncTask<Void, Integer, Boolean>{
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
			changeButton();
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			SQLiteDatabase database = dbHelper.getWritableDatabase();
			Cursor cursor = database.query("note", null, null, null, null, null, "time");
			if(cursor.moveToFirst()){
				do{
					int id = cursor.getInt(cursor.getColumnIndex("id"));
					String title = cursor.getString(cursor.getColumnIndex("title"));
					String content = cursor.getString(cursor.getColumnIndex("content"));
					long time = cursor.getInt(cursor.getColumnIndex("time"));
					int comfirmed = cursor.getInt(cursor.getColumnIndex("comfirmed"));
					Note note = new Note();
					note.setContent(content);
					note.setTitle(title);
					note.setTime(time);
					note.setId(id);
					note.setComfirmed(comfirmed);
					if(id<0){
						new SyncFromServer().execute(note);
					}
					noteList.add(note);
				}while(cursor.moveToNext());
			}
			cursor.close();
			return null;
		}
	}
	
	class RefreshFromServer extends AsyncTask<Void, Integer, Boolean>{
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			changeButton();
			adapter.notifyDataSetChanged();
			refreshableView.finishRefreshing();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String noteURL = "/querydiary.php";
			noteURL = ApplicationConfig.serverBase+noteURL;
			Long startTime = System.currentTimeMillis();
			HttpParams httpParams = new BasicHttpParams(); 
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000); //设置连接超时
			HttpConnectionParams.setSoTimeout(httpParams, 10000); //设置请求超时
			
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpPost httpPost = new HttpPost(noteURL);
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("user_id",ApplicationConfig.userId+""));
			requestParams.add(new BasicNameValuePair("username", ApplicationConfig.username));
			requestParams.add(new BasicNameValuePair("password", ApplicationConfig.password));
			UrlEncodedFormEntity entity = null;
			try {
				entity = new UrlEncodedFormEntity(requestParams, "utf-8");
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost);
				if(response.getStatusLine().getStatusCode()==200){
					String strRes = EntityUtils.toString(response.getEntity(), "utf-8");
					JSONObject resObject = new JSONObject(strRes);
					JSONArray notes = resObject.getJSONArray("responseData");
					for(int i=0;i<notes.length();i++){
						int id = notes.getJSONObject(i).getInt("id");
						String title = notes.getJSONObject(i).getString("title");
						String content = notes.getJSONObject(i).getString("content");
						String timeStr = notes.getJSONObject(i).getString("time");
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						Date date = df.parse(timeStr);
						Long time = date.getTime();
						Note note = new Note(title, content);
						note.setComfirmed(Note.COMFIRMED);
						note.setId(id);
						note.setTime(time);
						boolean sameIdFlag = false;
						int sameIdIndex = -1;
						SQLiteDatabase db = dbHelper.getWritableDatabase();
						for(int k=0;k<noteList.size();k++){
							if(noteList.get(k).getId()==id){
								sameIdFlag = true;
								sameIdIndex = k;
								break;
							}
						}
						if(sameIdFlag==false){
							//如果本地数据库中不存在，则插入
							ContentValues values = new ContentValues();
							values.put("id", id);
							values.put("title", title);
							values.put("content", content);
							values.put("time", time);
							values.put("comfirmed", Note.COMFIRMED);
							db.insert("note", null, values);
							noteList.add(note);
						}else{
							if(noteList.get(sameIdIndex).getContent().equals(content)||
									noteList.get(sameIdIndex).getTitle().equals(title)){
								
							}else{
								ContentValues values = new ContentValues();
								values.put("title", title);
								values.put("content", content);
								values.put("time", time);
								if(noteList.get(sameIdIndex).getComfirmed()==Note.COMFIRMED){
									db.update("note", values, "id=?", new String[]{""+id});
									noteList.get(sameIdIndex).setContent(content);
									noteList.get(sameIdIndex).setTime(time);
									noteList.get(sameIdIndex).setTitle(title);
								}else{
									//TODO 执行冲突操作 
								}
							}
						}
					}
				}else{
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Long endTime = System.currentTimeMillis();
			if(endTime-startTime<1000){
				try {
					Thread.sleep(1000-(endTime-startTime));
				} catch (InterruptedException e) {
				}
			}
			return null;
		}
		
	}
	class DeleteFromServer extends AsyncTask<Integer, Integer, Boolean>{
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), "本地删除成功", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			String URL = ApplicationConfig.serverBase +"/delete.php";
			List<NameValuePair> reqParams = new ArrayList<NameValuePair>();
			reqParams.add(new BasicNameValuePair("note_id", ""+adapter.getItem(params[0]).getId()));
			try {
				String resStr = WebUtil.getResponceByPost(URL, reqParams);
				JSONObject returnObj = new JSONObject(resStr);
				if(returnObj.getInt("messageId")==1){
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
	}
	class SyncFromServer extends AsyncTask<Note, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Note... params) {
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("title",params[0].getTitle()));
			requestParams.add(new BasicNameValuePair("content", params[0].getContent()));
			try {
				String returnStr = WebUtil.getResponceByPost(ApplicationConfig.serverBase+"/edit.php", requestParams);
				JSONObject returnObj = new JSONObject(returnStr);
				int messageId = returnObj.getInt("messageId");
				JSONObject data = returnObj.getJSONObject("responseData");
				int new_id = data.getInt("id");
				if(messageId==1){
					ContentValues values = new ContentValues();
					values.put("id", new_id);
					SQLiteDatabase database = dbHelper.getWritableDatabase();
					database.update("note", values, "id=?", new String[]{""+params[0].getId()});
					params[0].setId(new_id);
					return true;
				}
			} catch (Exception e) {
				
				return false;
			}
			return false;
		}
		
	}
	private void changeButton(){
		if(noteList.size()==0){
			addNewNote.setText("本地没有笔记，点我添加或者下拉刷新");
		}else{
			addNewNote.setText("添加");
		}
	}

}
