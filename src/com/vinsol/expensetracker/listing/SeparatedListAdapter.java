package com.vinsol.expensetracker.listing;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vinsol.expensetracker.GroupedIconDialogClickListener;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.models.DisplayList;
import com.vinsol.expensetracker.models.ListDatetimeAmount;
import com.vinsol.expensetracker.utils.ImagePreview;

class SeparatedListAdapter extends BaseAdapter {

	public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	public final ArrayAdapter<String> headers;
	public final ArrayAdapter<String> footers;
	public final static int TYPE_SECTION_HEADER = 0;
	public final static int TYPE_SECTION_FOOTER = 0;
	private Context mContext;
	private List<ListDatetimeAmount> mDatadateList;
	private LayoutInflater mInflater;
	private UnknownEntryDialog unknownEntryDialog;
	private View viewHeader = null;
	private View viewFooter = null;
	
	public SeparatedListAdapter(Context context) {
		mContext = context;
		headers = new ArrayAdapter<String>(context,R.layout.mainlist_header_view);
		footers = new ArrayAdapter<String>(context,R.layout.main_list_footerview);
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addSection(String section, Adapter adapter,List<ListDatetimeAmount> mDataDateList2) {
		
		this.mDatadateList = mDataDateList2;
		notifyDataSetChanged();
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
		ViewHolderBody holderBody;
		ViewHolderFooter holderFooter;
		
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 2;

			// check if position inside this section
			if (position == 0) {
				holderHeader = new ViewHolderHeader();
				viewHeader = mInflater.inflate(R.layout.mainlist_header_view,null);
				holderHeader.listDateView = (TextView) viewHeader.findViewById(R.id.expenses_listing_list_date_view);
				holderHeader.listAmountView = (TextView) viewHeader.findViewById(R.id.expenses_listing_list_amount_view);
				holderHeader.listDateView.setText(mDatadateList.get(sectionnum).dateTime);
				holderHeader.listAmountView.setText(mDatadateList.get(sectionnum).amount);
				return viewHeader;
			}
			

			if (position == size-1) {
				holderFooter = new ViewHolderFooter();
				viewFooter = mInflater.inflate(R.layout.main_list_footerview, null);
				holderFooter.addExpensesButton = (Button) viewFooter.findViewById(R.id.expenses_listing_add_expenses_button);
				holderFooter.addExpenses = (LinearLayout) viewFooter.findViewById(R.id.expense_listing_list_add_expenses);

				if (!isCurrentWeek(mDatadateList.get(sectionnum).dateTime)) {
					holderFooter.addExpenses.setBackgroundResource(0);
					holderFooter.addExpenses.setVisibility(View.GONE);
					holderFooter.addExpensesButton.setVisibility(View.GONE);
				} else {
					holderFooter.addExpensesButton.setText("Add expenses to "+ mDatadateList.get(sectionnum).dateTime);
					holderFooter.addExpensesButton.setFocusable(false);
					DateHelper mDateHelper = new DateHelper(mDatadateList.get(sectionnum).dateTime);
					holderFooter.addExpensesButton.setOnClickListener(new GroupedIconDialogClickListener(unknownEntryDialog, mContext, null, mDateHelper.getTimeMillis()));
				}

				return viewFooter;
			}
			
			if (position > 0 && position < size - 1) {
				if (convertView == null) {
					holderBody = new ViewHolderBody();
					convertView = mInflater.inflate(R.layout.expense_listing_inflated_row, null);
					holderBody.rowLocationTime = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_location_time);
					holderBody.rowTag = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_tag);
					holderBody.rowAmount = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_amount);
					holderBody.rowImageview = (ImageView) convertView.findViewById(R.id.expense_listing_inflated_row_imageview);
					holderBody.rowFavoriteIcon = (ImageView) convertView.findViewById(R.id.expense_listing_inflated_row_favorite_icon);
					holderBody.rowListview = (RelativeLayout) convertView.findViewById(R.id.expense_listing_inflated_row_listview);
					convertView.setTag(holderBody);
				} else {
					holderBody = (ViewHolderBody) convertView.getTag();
				}
				
				
				DisplayList mlist = (DisplayList) adapter.getItem(position - 1);
				if (mlist.type.equals(mContext.getString(R.string.camera))) {
					
					if(!isEntryComplete(mlist)){
						holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_unfinished_states);
					} else {
						holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_states);
					}
					
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						try {
							File mFile = new File("/sdcard/ExpenseTracker/"+ mlist.userId + "_thumbnail.jpg");
							if (mFile.canRead()) {
								Drawable drawable = Drawable.createFromPath(mFile.getPath());
								holderBody.rowImageview.setImageDrawable(drawable);
							} else {
								holderBody.rowImageview.setImageResource(R.drawable.no_image_thumbnail);
							}
						} catch (Exception e) {
							holderBody.rowImageview.setImageResource(R.drawable.no_image_thumbnail);
							e.printStackTrace();
						}
					} else {
						holderBody.rowImageview.setImageResource(R.drawable.no_image_thumbnail);
					}
				} else if (mlist.type.equals(mContext.getString(R.string.text))) {

					if(!isEntryComplete(mlist)){
						holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_unfinished_states);
					} else {
						holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_states);
					}
					
					if (!mlist.description.equals(mContext.getString(R.string.unfinished_textentry)) && !mlist.description.equals(mContext.getString(R.string.finished_textentry))) {
						holderBody.rowImageview.setImageResource(R.drawable.listing_text_entry_icon);
					} else {
						holderBody.rowImageview.setImageResource(R.drawable.text_list_icon_no_tag);
					}
					
				} else if (mlist.type.equals(mContext.getString(R.string.unknown))) {
					holderBody.rowImageview.setImageResource(R.drawable.listing_reminder_icon);
					holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_unknown_states);
				} else if (mlist.type.equals(mContext.getString(R.string.voice))) {

					if(!isEntryComplete(mlist)){
						holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_unfinished_states);
					} else {
						holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_states);
					}
					File mFile = new File("/sdcard/ExpenseTracker/Audio/"+ mlist.userId + ".amr");
					if (mFile.canRead()) {
						holderBody.rowImageview.setImageResource(R.drawable.listing_voice_entry_icon);
					} else {
						holderBody.rowImageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					}
				} 
				if (mlist.favorite != null) {
					if(!mlist.favorite.equals("")){
						try{
							if(isCurrentWeek(mDatadateList.get(sectionnum).dateTime)){
								holderBody.rowFavoriteIcon.setVisibility(View.VISIBLE);
							} else {
								holderBody.rowFavoriteIcon.setVisibility(View.INVISIBLE);
							}
						}catch(Exception e){
							
						}
					}
				}
				
				holderBody.rowImageview.setFocusable(false);
				holderBody.rowImageview.setOnClickListener(new MyClickListener(mlist));
				holderBody.rowLocationTime.setText(mlist.timeLocation);
				holderBody.rowTag.setText(mlist.description);
				holderBody.rowAmount.setText(mlist.amount);
				if ((mlist.type.equals(mContext.getString(R.string.sublist_daywise))) || mlist.type.equals(mContext.getString(R.string.sublist_monthwise)) || mlist.type.equals(mContext.getString(R.string.sublist_yearwise))|| mlist.type.equals(mContext.getString(R.string.sublist_weekwise))) {
					holderBody.rowImageview.setVisibility(View.GONE);
					holderBody.rowLocationTime.setVisibility(View.GONE);
				}
				return convertView;
			}
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
		}

		return false;
	}

	private static class ViewHolderBody {
		TextView rowLocationTime;
		TextView rowTag;
		TextView rowAmount;
		ImageView rowImageview;
		ImageView rowFavoriteIcon;
		RelativeLayout rowListview;
	}

	private class ViewHolderHeader {
		TextView listDateView;
		TextView listAmountView;
	}

	private class ViewHolderFooter {
		Button addExpensesButton;
		LinearLayout addExpenses;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class MyClickListener implements OnClickListener {

		DisplayList mListenerList;

		public MyClickListener(DisplayList mlist) {
			mListenerList = mlist;
		}
		
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.expense_listing_inflated_row_imageview) {
				if (mListenerList != null)
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						if (mListenerList.type.equals(mContext.getString(R.string.voice))) {
							File mFile = new File("/sdcard/ExpenseTracker/Audio/" + mListenerList.userId + ".amr");
							if (mFile.canRead()) {
								new AudioPlayDialog(mContext, mListenerList.userId);
							}
						} else if (mListenerList.type.equals(mContext.getString(R.string.camera))) {
							File mFile = new File("/sdcard/ExpenseTracker/" + mListenerList.userId + ".jpg");
							if (mFile.canRead()) {
								
								Intent intent = new Intent(mContext, ImagePreview.class);
								intent.putExtra("id", mListenerList.userId);
								mContext.startActivity(intent);

							}
						}
					}
				if (mListenerList.type.equals(mContext.getString(R.string.text))) {
					if (!mListenerList.description.equals(mContext.getString(R.string.unfinished_textentry))) {
						new DescriptionDialog(mContext, mListenerList.description);
					}
				}
			}
		}
	}
	
	private boolean isEntryComplete(DisplayList mlist) {
		if (mlist.type.equals(mContext.getString(R.string.camera))) {
			if(mlist.amount != null){
				if (mlist.amount.contains("?")) {
					return false;
				}
			} else {
				return false;
			}
			File mFileSmall = new File("/sdcard/ExpenseTracker/" + mlist.userId + "_small.jpg");
			File mFile = new File("/sdcard/ExpenseTracker/" + mlist.userId + ".jpg");
			File mFileThumbnail = new File("/sdcard/ExpenseTracker/" + mlist.userId + "_thumbnail.jpg");
			if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
				return true;
			} else {
				return false;
			}
		} else if (mlist.type.equals(mContext.getString(R.string.voice))) {
			if(mlist.amount != null){
				if (mlist.amount.contains("?")) {
					return false;
				}
			} else {
				return false;
			}
			File mFile = new File("/sdcard/ExpenseTracker/Audio/" + mlist.userId + ".amr");
			if (mFile.canRead()) {
				return true;
			} else {
				return false;
			}
		} else if (mlist.type.equals(mContext.getString(R.string.text))) {
			if(mlist.amount != null){
				if (mlist.amount.contains("?")) {
					return false;
				}
			} else {
				return false;
			}
			if(mlist.description != null){
				if (mlist.description.equals(mContext.getString(R.string.unfinished_textentry)) || mlist.description.equals(mContext.getString(R.string.finished_textentry))) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

}
