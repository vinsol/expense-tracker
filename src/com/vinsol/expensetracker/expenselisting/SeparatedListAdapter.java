/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.expenselisting;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.expenselisting.dialog.AudioPlayDialog;
import com.vinsol.expensetracker.expenselisting.dialog.DescriptionDialog;
import com.vinsol.expensetracker.expenselisting.dialog.GroupedIconDialogClickListener;
import com.vinsol.expensetracker.expenselisting.dialog.UnknownEntryDialog;
import com.vinsol.expensetracker.helpers.CheckEntryComplete;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.ListDatetimeAmount;
import com.vinsol.expensetracker.utils.ImagePreview;
import com.vinsol.expensetracker.utils.Log;

class SeparatedListAdapter extends BaseAdapter {

	public final Map<String, ArrayAdapter<Entry>> sections = new LinkedHashMap<String, ArrayAdapter<Entry>>();
	public final ArrayAdapter<String> headers;
	public final ArrayAdapter<String> footers;
	public final static int TYPE_SECTION_HEADER = 0;
	public final static int TYPE_SECTION_FOOTER = 0;
	private Context mContext;
	private List<ListDatetimeAmount> mDataDateList;
	private LayoutInflater mInflater;
	private UnknownEntryDialog unknownEntryDialog;
	private View viewHeader = null;
	private View viewFooter = null;
	private String highlightID;
	private FileHelper fileHelper;
	
	public SeparatedListAdapter(Context context,String highlightID) {
		mContext = context;
		this.highlightID = highlightID;
		if(this.highlightID == null) {
			highlightID = "";
		}
		headers = new ArrayAdapter<String>(context,R.layout.mainlist_header_view);
		footers = new ArrayAdapter<String>(context,R.layout.main_list_footerview);
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fileHelper = new FileHelper();
	}

	public void addSection(String section, ArrayAdapter<Entry> adapter,List<ListDatetimeAmount> mDataDateList) {
		this.mDataDateList = mDataDateList;
		Log.d("*********** Amount *************");
		Log.d(" amon "+mDataDateList.get(0).amount);
		this.headers.add(section);
		this.footers.add(section);
		this.sections.put(section, adapter);
	}

	@Override
	public Object getItem(int position) {
		for (Object section : sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 2;
			// check if position inside this section
			
			if (position == 0) {
				return section;
			}
			
			if (position < size - 1) {
				return adapter.getItem(position - 1);
			}
			
			if (position < size) {
				return section;
			}

			// otherwise jump into next section
			position -= size;
		}
		return null;
	}

	@Override
	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for (Adapter adapter : this.sections.values())
			total += adapter.getCount() + 2;
		return total;
	}

	@Override
	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = 2;
		for (Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}

	@Override
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

	@Override
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
			ArrayAdapter<Entry> adapter = sections.get(section);
			int size = adapter.getCount() + 2;
			// check if position inside this section
			if (position == 0) {
				holderHeader = new ViewHolderHeader();
				viewHeader = mInflater.inflate(R.layout.mainlist_header_view,null);
				holderHeader.listDateView = (TextView) viewHeader.findViewById(R.id.expenses_listing_list_date_view);
				holderHeader.listAmountView = (TextView) viewHeader.findViewById(R.id.expenses_listing_list_amount_view);
				holderHeader.listDateView.setText(mDataDateList.get(sectionnum).dateTime);
				holderHeader.listAmountView.setText(new StringProcessing().getStringDoubleDecimal(mDataDateList.get(sectionnum).amount));
				return viewHeader;
			}

			if (position == size-1) {
				holderFooter = new ViewHolderFooter();
				viewFooter = mInflater.inflate(R.layout.main_list_footerview, null);
				holderFooter.addExpensesButton = (Button) viewFooter.findViewById(R.id.expenses_listing_add_expenses_button);
				holderFooter.addExpenses = (LinearLayout) viewFooter.findViewById(R.id.expense_listing_list_add_expenses);
				if (!isCurrentWeek(mDataDateList.get(sectionnum).dateTime)) {
					holderFooter.addExpenses.setBackgroundResource(0);
					holderFooter.addExpenses.setVisibility(View.GONE);
					holderFooter.addExpensesButton.setVisibility(View.GONE);
				} else {
					holderFooter.addExpensesButton.setText("Add expenses to "+ mDataDateList.get(sectionnum).dateTime);
					holderFooter.addExpensesButton.setFocusable(false);
					DateHelper mDateHelper = new DateHelper(mDataDateList.get(sectionnum).dateTime);
					holderFooter.addExpensesButton.setOnClickListener(new GroupedIconDialogClickListener(unknownEntryDialog, (Activity)mContext, null, mDateHelper.getTimeMillis()));
				}
				return viewFooter;
			}
			
			if (position > 0 && position < size - 1) {
				if (convertView == null) {
					holderBody = new ViewHolderBody();
					convertView = mInflater.inflate(R.layout.expense_listing_inflated_row, null);
					holderBody.rowLocationTime = (TextView) convertView.findViewById(R.id.row_location_time);
					holderBody.rowTag = (TextView) convertView.findViewById(R.id.row_tag);
					holderBody.rowAmount = (TextView) convertView.findViewById(R.id.row_amount);
					holderBody.rowImageview = (ImageView) convertView.findViewById(R.id.row_imageview);
					holderBody.dividerImageView = (ImageView) convertView.findViewById(R.id.row_imageview_divider);
					holderBody.rowFavoriteIcon = (ImageView) convertView.findViewById(R.id.row_favorite_icon);
					holderBody.rowListview = (RelativeLayout) convertView.findViewById(R.id.row_listview);
					convertView.setTag(holderBody);
				} else {
					holderBody = (ViewHolderBody) convertView.getTag();
				}
				
				if(holderBody.bitmap != null) {holderBody.bitmap.recycle();}
				
				holderBody.rowImageview.setScaleType(ScaleType.CENTER_INSIDE);
				Entry mlist = (Entry) adapter.getItem(position - 1);
				CheckEntryComplete mCheckEntryComplete = new CheckEntryComplete();
				if (mlist.type.equals(mContext.getString(R.string.camera))) {
					setBackGround(holderBody,mCheckEntryComplete,mlist);
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						try {
							File mFile = fileHelper.getCameraFileThumbnailEntry(mlist.id);
							if (mFile.canRead()) {
								holderBody.bitmap = BitmapFactory.decodeFile(mFile.getPath());
								holderBody.rowImageview.setImageBitmap(holderBody.bitmap);
								holderBody.rowImageview.setScaleType(ScaleType.FIT_CENTER);
							} else {
								holderBody.rowImageview.setScaleType(ScaleType.CENTER_INSIDE);
								holderBody.rowImageview.setImageResource(R.drawable.no_image_small);
							}
						} catch (Exception e) {
							holderBody.rowImageview.setImageResource(R.drawable.no_image_small);
						}
					} else {
						holderBody.rowImageview.setImageResource(R.drawable.no_image_small);
					}
				} else if (mlist.type.equals(mContext.getString(R.string.text))) {
					setBackGround(holderBody, mCheckEntryComplete, mlist);
					
					if (!mlist.description.equals(mContext.getString(R.string.unfinished_textentry)) && !mlist.description.equals(mContext.getString(R.string.finished_textentry))) {
						holderBody.rowImageview.setImageResource(R.drawable.listing_text_entry_icon);
					} else {
						holderBody.rowImageview.setImageResource(R.drawable.text_list_icon_no_tag);
					}
					
				} else if (mlist.type.equals(mContext.getString(R.string.unknown))) {
					holderBody.rowImageview.setImageResource(R.drawable.listing_reminder_icon);
					holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_unknown_states);
				} else if (mlist.type.equals(mContext.getString(R.string.voice))) {
					setBackGround(holderBody, mCheckEntryComplete, mlist);
					File mFile = fileHelper.getAudioFileEntry(mlist.id);
					if (mFile.canRead()) {
						holderBody.rowImageview.setImageResource(R.drawable.listing_voice_entry_icon);
					} else {
						holderBody.rowImageview.setImageResource(R.drawable.no_voice_file_small);
					}
				} 
				if (mlist.favorite != null) {
					if(!mlist.favorite.equals("")) {
						try{
							if(isCurrentWeek(mDataDateList.get(sectionnum).dateTime)) {
								holderBody.rowFavoriteIcon.setVisibility(View.VISIBLE);
							} else {
								holderBody.rowFavoriteIcon.setVisibility(View.INVISIBLE);
							}
						}catch(Exception e) { 
							
						}
					}else {
						holderBody.rowFavoriteIcon.setVisibility(View.INVISIBLE);
					}
				}else {
					holderBody.rowFavoriteIcon.setVisibility(View.INVISIBLE);
				}
				
				holderBody.rowImageview.setFocusable(false);
				holderBody.rowImageview.setOnClickListener(new MyClickListener(mlist));
				if (mlist.timeInMillis != null  && !mlist.timeInMillis.equals("")) {
					holderBody.rowLocationTime.setText(new DisplayDate().getLocationDate(mlist.timeInMillis, mlist.location));
				} else if ((mlist.timeInMillis == null || mlist.timeInMillis.equals(""))&& mlist.location != null&& !mlist.location.equals("")) {
					holderBody.rowLocationTime.setText("Unknown time at "+ mlist.location);
				} else {
					holderBody.rowLocationTime.setText("Unknown time at Unknown Location");
				}
				holderBody.rowTag.setText(mlist.description);
				holderBody.rowAmount.setText(new StringProcessing().getStringDoubleDecimal(mlist.amount));
				if (mlist.type.equals(mContext.getString(R.string.sublist_thisyear)) || mlist.type.equals(mContext.getString(R.string.sublist_all))|| mlist.type.equals(mContext.getString(R.string.sublist_thismonth))) {
					holderBody.rowImageview.setVisibility(View.GONE);
					holderBody.rowLocationTime.setVisibility(View.GONE);
					holderBody.dividerImageView.setVisibility(View.GONE);
				}
				return convertView;
			}
			position -= size;
			sectionnum++;
		}
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public int getListPosition(int position) {
		int listPosition = 0;
		for (Object section : sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 2;
			if(position <= size) {
				return listPosition;
			}
			listPosition+=adapter.getCount();
			position-=size;
		}
		return -1;
	}

	public boolean remove(int toRemove) {
		String sectionNumber = getSectionNumber(toRemove);
		Entry prevEntry = (Entry) getItem(toRemove);
		if(sections.containsKey(sectionNumber)) {
			sections.get(sectionNumber).remove((Entry)getItem(toRemove));
			if(sections.get(sectionNumber).getCount() == 0) {
				removeSection(sectionNumber);
			} else {
				updateAmount(sectionNumber, prevEntry, null);
			}
			notifyDataSetChanged();
			return true;
		}
		return false;
	}
	
	public void update(Entry updatedEntry,int toUpdate, String sectionNumber, Entry prevEntry) {
		sections.get(sectionNumber).insert(updatedEntry, toUpdate);
		sections.get(sectionNumber).remove(prevEntry);
		updateAmount(sectionNumber, prevEntry, updatedEntry);
		notifyDataSetChanged();
	}
	
	public String  getSectionNumber(int position) {
		int sectionNumber = 0;
		for (Object section : sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 2;
			if(position <= size) {
				return sectionNumber+"";
			}
			sectionNumber++;
			position-=size;
		}
		return "";
	}
	
	public List<ListDatetimeAmount> getDataDateList() {
		return mDataDateList;
	}
	
	/////////////////**********************Private Classes **********************////////////////////////
	private class MyClickListener implements OnClickListener {

		Entry mListenerList;
		public MyClickListener(Entry mlist) {
			mListenerList = mlist;
		}
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.row_imageview:
				if (mListenerList != null)
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						if (mListenerList.type.equals(mContext.getString(R.string.voice))) {
							File mFile = fileHelper.getAudioFileEntry(mListenerList.id);
							if (mFile.canRead()) {
								new AudioPlayDialog(mContext, mListenerList.id);
							}
						} else if (mListenerList.type.equals(mContext.getString(R.string.camera))) {
							File mFile = fileHelper.getCameraFileLargeEntry(mListenerList.id);
							if (mFile.canRead()) {
								Intent intent = new Intent(mContext, ImagePreview.class);
								intent.putExtra(Constants.KEY_ID, mListenerList.id);
								mContext.startActivity(intent);
							}
						}
					}
				if (mListenerList.type.equals(mContext.getString(R.string.text))) {
					if (!mListenerList.description.equals(mContext.getString(R.string.unfinished_textentry))) {
						new DescriptionDialog(mContext, mListenerList.description);
					}
				}
				break;

			default:
				break;
			}
		}
	}

	private void removeSection(String sectionKey) {
		sections.remove(sectionKey);
		mDataDateList.remove(Integer.parseInt(sectionKey));
	}

	private static class ViewHolderBody {
		TextView rowLocationTime;
		TextView rowTag;
		TextView rowAmount;
		ImageView rowImageview;
		ImageView rowFavoriteIcon;
		RelativeLayout rowListview;
		ImageView dividerImageView;
		Bitmap bitmap;
	}

	private class ViewHolderHeader {
		TextView listDateView;
		TextView listAmountView;
	}

	private class ViewHolderFooter {
		Button addExpensesButton;
		LinearLayout addExpenses;
	}
	
	private void setBackGround(ViewHolderBody holderBody,CheckEntryComplete mCheckEntryComplete,Entry mlist) {
//		if (mlist.id.equals(highlightID)) {
//			holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_unfinished_states);
//			//TODO I dont like the background
////			holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_highlighted_entry_states);
//		} else 
		
		if(!mCheckEntryComplete.isEntryComplete(mlist,mContext)) {
			holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_unfinished_states);
		} else {
			holderBody.rowListview.setBackgroundResource(R.drawable.listing_row_states);
		}
	}

	private boolean isCurrentWeek(String dateViewString) {
		if(dateViewString != null) {
			if(!dateViewString.equals("")) {
				return new DateHelper(dateViewString).isCurrentWeek();
			}
		}
		return false;
	}

	private void updateAmount(String sectionNumber, Entry prevEntry, Entry updatedEntry) {
		StringProcessing mStringProcessing = new StringProcessing();
		Double amountDouble = mStringProcessing.getAmount(mDataDateList.get(Integer.parseInt(sectionNumber)).amount);
		amountDouble -= mStringProcessing.getAmount(prevEntry.amount);
		if(updatedEntry != null && !updatedEntry.amount.contains("?")) { 
			updatedEntry.amount = mStringProcessing.getStringDoubleDecimal(updatedEntry.amount);
			amountDouble += mStringProcessing.getAmount(updatedEntry.amount);
		}
		if(amountDouble != 0.0 && mDataDateList.get(Integer.parseInt(sectionNumber)).amount.contains("?")) {
			mDataDateList.get(Integer.parseInt(sectionNumber)).amount = mStringProcessing.getStringDoubleDecimal(amountDouble+"?");
		} else if(amountDouble == 0.0 && mDataDateList.get(Integer.parseInt(sectionNumber)).amount.contains("?")) {
			mDataDateList.get(Integer.parseInt(sectionNumber)).amount = mStringProcessing.getStringDoubleDecimal("?");
		} else {
			mDataDateList.get(Integer.parseInt(sectionNumber)).amount = mStringProcessing.getStringDoubleDecimal(amountDouble+"");
		}
	}
	
}
