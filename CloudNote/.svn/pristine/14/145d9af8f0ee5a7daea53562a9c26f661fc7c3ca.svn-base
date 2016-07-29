package com.ddbs.cloudnote.activity;

import com.ddbs.cloudnote.R;
import com.ddbs.cloudnote.util.ActivityCollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addAct(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeAct(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.exit:
			ActivityCollector.finishAll();
			break;
		case R.id.settings:
			Intent intent2 = new Intent(getApplicationContext(),SettingsActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
		return true;
	}
	
}
