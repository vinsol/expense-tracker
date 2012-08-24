/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.entry.FavoriteEntry;

public abstract class BaseActivity extends Activity {
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_optionsmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		removeManageFavorite(menu);
		removeGenerateReport(menu);
		return true;
	}
	
	public boolean removeGenerateReport(Menu menu){
		menu.removeItem(R.id.generate_report);
		return true;
	}
	
	public boolean removeManageFavorite(Menu menu){
		menu.removeItem(R.id.manage_favorite);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent intent = new Intent(this, Preferences.class);
            startActivity(intent);
			break;

		case R.id.rate_app:
			FlurryAgent.onEvent(getString(R.string.rate_app));
			Intent startMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.EXPENSE_TRACKER_MARKET_URI));
			startActivity(startMarket);
			break;
			
		case R.id.manage_favorite:
			Intent startManagingFavorite = new Intent(this, FavoriteEntry.class);
			startManagingFavorite.putExtra(Constants.KEY_MANAGE_FAVORITE, true);
			startActivity(startManagingFavorite);
			break;
			
		case R.id.generate_report:
			startGenerateReportActivity();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void startGenerateReportActivity() {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Intent generateReport = new Intent(this, GenerateReport.class);
			startActivity(generateReport);
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}

}
