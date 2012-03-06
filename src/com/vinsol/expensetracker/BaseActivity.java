package com.vinsol.expensetracker;

import com.vinsol.expensetracker.listing.FavoriteActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_optionsmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.removeItem(R.id.manage_favorite);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent intent = new Intent(this, SetPreferences.class);
            startActivity(intent);
			break;

		case R.id.rate_app:
			Intent startMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.EXPENSE_TRACKER_MARKET_URI));
			startActivity(startMarket);
			break;
			
		case R.id.manage_favorite:
			Intent startManagingFavorite = new Intent(this, FavoriteActivity.class);
			startManagingFavorite.putExtra(Constants.KEY_MANAGE_FAVORITE, true);
			startActivity(startManagingFavorite);
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
