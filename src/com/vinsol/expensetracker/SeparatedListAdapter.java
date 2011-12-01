package com.vinsol.expensetracker;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.utils.DateHelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

class SeparatedListAdapter extends BaseAdapter {

	public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	public final ArrayAdapter<String> headers;
	public final ArrayAdapter<String> footers;
	public final static int TYPE_SECTION_HEADER = 0;
	public final static int TYPE_SECTION_FOOTER = 0;
	private Context mContext;
	private List<HashMap<String, String>> mDatadateList;
	private LayoutInflater mInflater;

	public SeparatedListAdapter(Context context) {
		mContext = context;
		headers = new ArrayAdapter<String>(context,R.layout.mainlist_header_view);
		footers = new ArrayAdapter<String>(context,R.layout.main_list_footerview);
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addSection(String section, Adapter adapter,List<HashMap<String, String>> _mDataDateList) {
		mDatadateList = _mDataDateList;
		this.headers.add(section);
		this.footers.add(section);
		this.sections.put(section, adapter);
	}

	public Object getItem(int position) {
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 2;

			// check if position inside this section
			if (position == 0) {
				return section;
			}
			if (position < size - 1)
				return adapter.getItem(position - 1);
			if (position < size)
				return section;

			// otherwise jump into next section
			position -= size;
		}
		return null;
	}

	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for (Adapter adapter : this.sections.values())
			total += adapter.getCount() + 2;
		return total;
	}

	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = 2;
		for (Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}

	public int getItemViewType(int position) {
		int type = 1;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 2;

			// check if position inside this section
			if (position == 0)
				return TYPE_SECTION_HEADER;
			if (position < size - 1) {
				return type + adapter.getItemViewType(position - 1);
			}
			if (position < size)
				return TYPE_SECTION_FOOTER;
			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isEnabled(int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		ViewHolderHeader holderHeader;
		final ViewHolderBody holderBody;
		ViewHolderFooter holderFooter;

		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 2;

			// check if position inside this section
			if (position == 0) {
				holderHeader = new ViewHolderHeader();
				convertView = mInflater.inflate(R.layout.mainlist_header_view,null);
				holderHeader.expenses_listing_list_date_view = (TextView) convertView.findViewById(R.id.expenses_listing_list_date_view);
				holderHeader.expenses_listing_list_amount_view = (TextView) convertView.findViewById(R.id.expenses_listing_list_amount_view);
				holderHeader.expenses_listing_list_date_view.setText(mDatadateList.get(sectionnum).get(DatabaseAdapter.KEY_DATE_TIME));
				holderHeader.expenses_listing_list_amount_view.setText(mDatadateList.get(sectionnum).get(DatabaseAdapter.KEY_AMOUNT));
				return convertView;
			}
			if (position < size - 1) {
				if (convertView == null || position != 0) {
					holderBody = new ViewHolderBody();
					convertView = mInflater.inflate(R.layout.expense_listing_inflated_row, null);
					holderBody.expense_listing_inflated_row_location_time = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_location_time);
					holderBody.expense_listing_inflated_row_tag = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_tag);
					holderBody.expense_listing_inflated_row_amount = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_amount);
					holderBody.expense_listing_inflated_row_imageview = (ImageView) convertView.findViewById(R.id.expense_listing_inflated_row_imageview);
					holderBody.expense_listing_inflated_row_favorite_icon = (ImageView) convertView.findViewById(R.id.expense_listing_inflated_row_favorite_icon);
				} else {
					holderBody = (ViewHolderBody) convertView.getTag();
				}
				@SuppressWarnings("unchecked")
				final List<String> mlist = (List<String>) adapter.getItem(position - 1);
				
				if (mlist.get(5).equals(mContext.getString(R.string.camera))) {
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						try {
							File mFile = new File("/sdcard/ExpenseTracker/"+ mlist.get(0) + "_thumbnail.jpg");
							if (mFile.canRead()) {
								Drawable drawable = Drawable.createFromPath(mFile.getPath());
								holderBody.expense_listing_inflated_row_imageview.setImageDrawable(drawable);
							} else {
								holderBody.expense_listing_inflated_row_imageview.setImageResource(R.drawable.no_image_thumbnail);
							}
						} catch (Exception e) {
							// TODO if image not available on sdcard
							holderBody.expense_listing_inflated_row_imageview.setImageResource(R.drawable.no_image_thumbnail);
							e.printStackTrace();
						}
					} else {
						holderBody.expense_listing_inflated_row_imageview.setImageResource(R.drawable.no_image_thumbnail);
						// TODO if sdcard not available
					}
				} else if (mlist.get(5).equals(mContext.getString(R.string.text))) {

					if (!mlist.get(1).equals(mContext.getString(R.string.unfinished_textentry))) {
						holderBody.expense_listing_inflated_row_imageview.setImageResource(R.drawable.text_list_icon);
					} else {
						holderBody.expense_listing_inflated_row_imageview.setImageResource(R.drawable.text_list_icon_no_tag);
					}

				} else if (mlist.get(5).equals(mContext.getString(R.string.unknown))) {
					holderBody.expense_listing_inflated_row_imageview.setImageResource(R.drawable.unknown_list_icon);
				} else if (mlist.get(5).equals(mContext.getString(R.string.voice))) {
					File mFile = new File("/sdcard/ExpenseTracker/Audio/"+ mlist.get(0) + ".amr");
					if (mFile.canRead()) {
						holderBody.expense_listing_inflated_row_imageview.setImageResource(R.drawable.audio_play_list_icon);
					} else {
						holderBody.expense_listing_inflated_row_imageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					}

				} 
				
				
				if (mlist.get(4) != null) {
					
					if(!mlist.get(4).equals("")){
						try{
							if(isCurrentWeek(mDatadateList.get(sectionnum).get(DatabaseAdapter.KEY_DATE_TIME))){
								holderBody.expense_listing_inflated_row_favorite_icon.setVisibility(View.VISIBLE);
							}
						}catch(Exception e){
							
						}
					}
					// ///TODO if favorite entry
				}
				holderBody.expense_listing_inflated_row_imageview.setFocusable(false);
				holderBody.expense_listing_inflated_row_imageview.setOnClickListener(new MyClickListener(mlist));
				holderBody.expense_listing_inflated_row_location_time.setText(mlist.get(3));
				holderBody.expense_listing_inflated_row_tag.setText(mlist.get(1));
				holderBody.expense_listing_inflated_row_amount.setText(mlist.get(2));
				if ((mlist.get(5).equals(mContext
						.getString(R.string.sublist_daywise)))
						|| mlist.get(5).equals(
								mContext.getString(R.string.sublist_monthwise))
						|| mlist.get(5).equals(
								mContext.getString(R.string.sublist_yearwise))
						|| mlist.get(5).equals(
								mContext.getString(R.string.sublist_weekwise))) {
					holderBody.expense_listing_inflated_row_imageview
							.setVisibility(View.GONE);
					holderBody.expense_listing_inflated_row_location_time
							.setVisibility(View.GONE);
				}
				return convertView;
			}

			if (position < size) {
				if (convertView == null || position < size) {
					holderFooter = new ViewHolderFooter();
					convertView = mInflater.inflate(
							R.layout.main_list_footerview, null);
					holderFooter.expenses_listing_add_expenses_textview = (TextView) convertView
							.findViewById(R.id.expenses_listing_add_expenses_textview);
					holderFooter.expense_listing_list_add_expenses = (LinearLayout) convertView
							.findViewById(R.id.expense_listing_list_add_expenses);
				} else {
					holderFooter = (ViewHolderFooter) convertView.getTag();
				}

				holderFooter.expenses_listing_add_expenses_textview
						.setText("Add expenses to "
								+ mDatadateList.get(sectionnum).get(
										DatabaseAdapter.KEY_DATE_TIME));
				holderFooter.expense_listing_list_add_expenses
						.setFocusable(false);
				holderFooter.expense_listing_list_add_expenses
						.setOnClickListener(new MyClickListener(sectionnum));
				if (!isCurrentWeek(mDatadateList.get(sectionnum).get(
						DatabaseAdapter.KEY_DATE_TIME))) {
					holderFooter.expense_listing_list_add_expenses
							.setVisibility(View.GONE);
				}

				return convertView;
			}
			// otherwise jump into next section
			position -= size;
			sectionnum++;

		}
		return null;
	}

	private boolean isCurrentWeek(String dateViewString) {
		try {
			DateHelper mDateHelper = new DateHelper(dateViewString);
			mDateHelper.getTimeMillis();
			return true;
		} catch (Exception e) {
			e.printStackTrace();

		}

		return false;
	}

	private class ViewHolderBody {
		TextView expense_listing_inflated_row_location_time;
		TextView expense_listing_inflated_row_tag;
		TextView expense_listing_inflated_row_amount;
		ImageView expense_listing_inflated_row_imageview;
		ImageView expense_listing_inflated_row_favorite_icon;
	}

	private class ViewHolderHeader {
		TextView expenses_listing_list_date_view;
		TextView expenses_listing_list_amount_view;
	}

	private class ViewHolderFooter {
		TextView expenses_listing_add_expenses_textview;
		LinearLayout expense_listing_list_add_expenses;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class MyClickListener implements OnClickListener {

		List<String> mListenerList;
		int mPosition;

		public MyClickListener(List<String> mlist) {
			mListenerList = mlist;
		}

		public MyClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.expense_listing_inflated_row_imageview) {
				if (mListenerList != null)
					if (android.os.Environment.getExternalStorageState()
							.equals(android.os.Environment.MEDIA_MOUNTED)) {
						if (mListenerList.get(5).equals(
								mContext.getString(R.string.voice))) {
							File mFile = new File(
									"/sdcard/ExpenseTracker/Audio/"
											+ mListenerList.get(0) + ".amr");
							if (mFile.canRead()) {
								new AudioPlayDialog(mContext,
										mListenerList.get(0));
							} else {
								// TODO audio image change
							}
						} else if (mListenerList.get(5).equals(
								mContext.getString(R.string.camera))) {
							File mFile = new File("/sdcard/ExpenseTracker/"
									+ mListenerList.get(0) + ".jpg");
							if (mFile.canRead()) {
								
								new ImageViewDialog(mContext,
										Long.parseLong(mListenerList.get(0)));

							} else {
								// TODO if image not found
							}
						}
					}
				if (mListenerList.get(5).equals(
						mContext.getString(R.string.text))) {
					if (!mListenerList.get(1).equals(
							mContext.getString(R.string.unfinished_textentry))) {
						new DescriptionDialog(mContext, mListenerList.get(1));
					}
				}
			}

			if (v.getId() == R.id.expense_listing_list_add_expenses) {
				DateHelper mDateHelper = new DateHelper(mDatadateList.get(
						mPosition).get(DatabaseAdapter.KEY_DATE_TIME));
				Intent mMainIntent = new Intent(mContext, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong("timeInMillis", mDateHelper.getTimeMillis());
				mMainIntent.putExtra("mainBundle", bundle);
				mContext.startActivity(mMainIntent);
			}
		}
	}

}
