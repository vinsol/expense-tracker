package com.vinsol.expensetracker;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.location.LocationLast;
import com.vinsol.expensetracker.utils.DateHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FavoriteActivity extends Activity {
	
	private ListView text_voice_camera_body_favorite_listview;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<HashMap<String, String>> mList;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView text_voice_camera_date_bar_dateview;
	private String dateViewString;
	private Bundle intentExtras;
	private MyAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ///// ****** No Title Bar ********* /////////

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.text_voice_camera);

		// ///// ******* Hide Main Body of layout and make favorite body visible
		// ******* ///////
		handleUI();
		
		text_voice_camera_body_favorite_listview = (ListView) findViewById(R.id.text_voice_camera_body_favorite_listview);
		text_voice_camera_date_bar_dateview = (TextView) findViewById(R.id.text_voice_camera_date_bar_dateview);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mDatabaseAdapter = new DatabaseAdapter(this);
		intentExtras = getIntent().getBundleExtra("favoriteBundle");
		
		// ///// ******* Handles Date Bar ******* ////////
		new DateHandler(this);

		// ////// ********* Get Last most accurate location info *********
		// /////////
		LocationLast mLocationLast = new LocationLast(this);
		mLocationLast.getLastLocation();
	}

	@Override
	protected void onResume() {
		
		dateViewString = text_voice_camera_date_bar_dateview.getText().toString();
		mList = mConvertCursorToListString.getFavoriteList();
		mAdapter = new MyAdapter(this, R.layout.favorite_row , mList);
		text_voice_camera_body_favorite_listview.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		super.onResume();
	}
	
	private class MyAdapter extends ArrayAdapter<String>{
		
		
		private LayoutInflater mInflater;
		List<HashMap<String, String>> mList;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private MyAdapter(Context context, int resource,List list1) {
			super(context, resource,list1);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mList = list1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.favorite_row, null);
				viewHolder = new ViewHolder();
				viewHolder.favorite_row_tag = (TextView) convertView.findViewById(R.id.favorite_row_tag);
				viewHolder.favorite_row_amount = (TextView) convertView.findViewById(R.id.favorite_row_amount);
				viewHolder.favorite_row_imageview = (ImageView) convertView.findViewById(R.id.favorite_row_imageview);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String tag = mList.get(position).get(DBAdapterFavorite.KEY_TAG);
			String amount = mList.get(position).get(DBAdapterFavorite.KEY_AMOUNT);
			String type = mList.get(position).get(DBAdapterFavorite.KEY_TYPE);
			String _id = mList.get(position).get(DBAdapterFavorite.KEY_ID);
			
			
			if(type.equals(getString(R.string.voice))){
				if(tag != null){
					if(!tag.equals("") &&!tag.equals(R.string.unfinished_voiceentry)){
						viewHolder.favorite_row_tag.setText(tag);
					}
				} else {
					viewHolder.favorite_row_tag.setText("Voice Entry");
				}
				if(amount != null ){
					if(!amount.equals("")){
						viewHolder.favorite_row_amount.setText(amount);
					}
				} else {
					viewHolder.favorite_row_amount.setText("?");
				}
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						
						//TODO image set for voice entry
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/Audio/"+ _id + ".amr");
						if (mFile.canRead()) {
							viewHolder.favorite_row_imageview.setImageResource(R.drawable.audio_play_list_icon);
						} else {
							viewHolder.favorite_row_imageview.setImageResource(R.drawable.no_voice_file_thumbnail);
						}
					}catch(Exception e){
						viewHolder.favorite_row_imageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					}
				} else {
					viewHolder.favorite_row_imageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					return convertView;
				}
			}
			else if(type.equals(getString(R.string.camera))){
				
				if(tag != null){
					if(!tag.equals("") && !tag.equals(R.string.unfinished_cameraentry)){
						viewHolder.favorite_row_tag.setText(tag);
					}
				} else {
					viewHolder.favorite_row_tag.setText("Camera Entry");
				}
				if(amount != null ){
					if(!amount.equals("")){
						viewHolder.favorite_row_amount.setText(amount);
					}
				} else {
					viewHolder.favorite_row_amount.setText("?");
				}
				//TODO image set for camera entry
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/Favorite/"+ _id + "_thumbnail.jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/Favorite/"+ _id + "_small.jpg");
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/"+ _id + ".jpg");
						if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							Drawable drawable = Drawable.createFromPath(mFile.getPath());
							viewHolder.favorite_row_imageview.setImageDrawable(drawable);
						} else {
							viewHolder.favorite_row_imageview.setImageResource(R.drawable.no_image_thumbnail);
						}
					} catch (Exception e) {
						// TODO if image not available on sdcard
						viewHolder.favorite_row_imageview.setImageResource(R.drawable.no_image_thumbnail);
						e.printStackTrace();
					}
				} else {
					viewHolder.favorite_row_imageview.setImageResource(R.drawable.no_image_thumbnail);
					return convertView;
					// TODO if sdcard not available
				}
			}
			else if(type.equals(getString(R.string.text))){
				Log.v("text", "text");
				if(tag != null){
					if(!tag.equals("") && !tag.equals(R.string.unfinished_textentry)){
						viewHolder.favorite_row_tag.setText(tag);
					}
				} else {
					viewHolder.favorite_row_tag.setText("Text Entry");
				}
				if(amount != null ){
					if(!amount.equals("")){
						viewHolder.favorite_row_amount.setText(amount);
					}
				} else {
					viewHolder.favorite_row_amount.setText("?");
				}
				//TODO image set for camera entry
				
				if(tag != null){
					if (!tag.equals("") && !tag.equals(getString(R.string.unfinished_textentry)) ) {
						viewHolder.favorite_row_imageview.setImageResource(R.drawable.text_list_icon);
					} else {
						viewHolder.favorite_row_imageview.setImageResource(R.drawable.text_list_icon_no_tag);
					}
				} else {
					viewHolder.favorite_row_imageview.setImageResource(R.drawable.text_list_icon_no_tag);
				}
			}
			return convertView;
		}
		
	}
	
	private class ViewHolder {
		TextView favorite_row_tag;
		TextView favorite_row_amount;
		ImageView favorite_row_imageview;
	}
	
	private void handleUI() {
		// ///// ******* Hide Main Body of layout and make favorite body visible
		// ******* ///////
		ScrollView mScrollView = (ScrollView) findViewById(R.id.text_voice_camera_body);
		mScrollView.setVisibility(View.GONE);
		RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.text_voice_camera_body_favorite);
		mRelativeLayout.setVisibility(View.VISIBLE);
		LinearLayout text_voice_camera_footer = (LinearLayout) findViewById(R.id.text_voice_camera_footer);
		text_voice_camera_footer.setVisibility(View.GONE);
	}
}
