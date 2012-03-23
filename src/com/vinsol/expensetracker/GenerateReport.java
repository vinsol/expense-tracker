/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/ 

package com.vinsol.expensetracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.CustomDatePickerDialog;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.utils.Log;

public class GenerateReport extends BaseActivity implements OnClickListener {
	
	private Spinner period;
	private Spinner typeSpinner;
	
	private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    
    private int mEndYear;
    private int mEndMonth;
    private int mEndDay;
    
    private TextView customStartDateTextView;
    private TextView customEndDateTextView;
    
    private Calendar endCalendar;
    private Calendar startCalendar;
    	
    private AsyncTask<Void, Void, Void> exportPDF;
    private AsyncTask<Void, Void, Void> exportCSV;
    
    private File fileLocation;
    
    private String dateRange;
	private List<Entry> mEntryList;
	
	private final int REQUEST_CODE = 1055;
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_key));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generate_report);
		((Button)findViewById(R.id.export_button)).setOnClickListener(this);
		customStartDateTextView = (TextView)findViewById(R.id.custom_start_date);
		customStartDateTextView.setOnClickListener(this);
		customEndDateTextView = (TextView)findViewById(R.id.custom_end_date);
		customEndDateTextView.setOnClickListener(this);
		period = (Spinner) findViewById(R.id.period_spinner);
		typeSpinner = (Spinner) findViewById(R.id.type_spinner);
		typeSpinner.setOnItemSelectedListener(typeSpinnerListener);
		period.setOnItemSelectedListener(periodListener);
		FlurryAgent.onEvent(getString(R.string.generate_report_activity));
		//set default end day values
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		mEndYear = calendar.get(Calendar.YEAR);
		mEndMonth = calendar.get(Calendar.MONTH);
		mEndDay = calendar.get(Calendar.DAY_OF_MONTH);
		mEntryList = new ConvertCursorToListString(GenerateReport.this).getEntryList(true, "");
		if(mEntryList.size() == 0) {
			new AlertDialog.Builder(this)
			.setTitle("Error")
			.setCancelable(false)
			.setMessage("No Record to Generate Report, Please add some")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.export_button:
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				if(mEntryList.size() <= 5000) {
					if(setStartEndDate()) {
						Log.d("**************Exporting Range****************");
						Log.d("Start Date "+mStartDay+" "+(mStartMonth+1)+" "+mStartYear);
						Log.d("End Date "+mEndDay+" "+(mEndMonth+1)+" "+mEndYear);
						Log.d("******************************");
						switch ((int)typeSpinner.getSelectedItemId()) {
						//case if Exporting to PDF
						case 0:
							exportToPDF();
							break;
						//case if Exporting to CSV
						case 1:
							exportToCSV();
							break;
			
						default:
							break;
						}
					}

				} else {
					new AlertDialog.Builder(this)
					.setTitle("Error")
					.setMessage("Too many Records, Please select fewer")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(getString(R.string.ok), (DialogInterface.OnClickListener)null)
					.show();
				}
			} else {
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
			}
			break;

		case R.id.custom_start_date:
			new CustomDatePickerDialog(this, mStartDateSetListener, customStartDateTextView).show();
			break;
			
		case R.id.custom_end_date:
			new CustomDatePickerDialog(this, mEndDateSetListener, customEndDateTextView).show();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE) {
			finish();
		}
	}
	
	private void exportToCSV() {
		exportCSV = new ExportToCSV().execute();
	}
	
	private abstract class Export extends AsyncTask<Void, Void, Void> {

		protected ProgressDialog progressDialog;
		protected Double totalAmount = 0.0;
		protected boolean isAmountNotEntered = false;
		protected boolean isRecordAdded = false;
		protected int totalNumberOfRecordsAdded;
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(GenerateReport.this);
			progressDialog.setCancelable(false);
			progressDialog.setTitle("Exporting Report");
			progressDialog.setMessage("Please Wait...");
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.cancel();
			if(!isRecordAdded) {
				fileLocation.delete();
				new AlertDialog.Builder(GenerateReport.this)
				.setTitle("Error")
				.setMessage("No Record within range to generate report")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(getString(R.string.ok), (DialogInterface.OnClickListener)null)
				.show();
			} else {
				addFlurryEvent(totalNumberOfRecordsAdded);
				final PackageManager packageManager = getPackageManager();
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(fileLocation));
				List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
				if (resolveInfo.size() > 0) {
					Toast.makeText(GenerateReport.this, "Report Exported to - "+getShowLocation(), Toast.LENGTH_LONG).show();
					startActivityForResult(intent, REQUEST_CODE);
			    } else {
			    	new AlertDialog.Builder(GenerateReport.this)
			    	.setMessage(getType()+" Viewer not found, Generated report saved at "+getShowLocation())
			    	.setTitle("Report Generated")
			    	.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					})
			    	.setIcon(android.R.drawable.ic_dialog_info)
			    	.show();
			    }
			}
		}
		
		protected void addFlurryEvent(int srNo) {
			Map<String, String> recordType = new HashMap<String, String>();
			recordType.put("Total Records", +srNo+"");
			recordType.put("Type Spinner", getType());
			recordType.put("Date Range",dateRange);
			recordType.put("Period Spinner", period.getSelectedItem()+"");
			FlurryAgent.onEvent(getString(R.string.generate_report), recordType);
		}

		protected String getShowLocation(){
			return fileLocation.toString().replaceAll("/mnt", "");
		} 
		
		protected abstract String getType();

		protected void setFile() {
			File dir = new File(Environment.getExternalStorageDirectory()+"/ExpenseTracker");
	        if(!dir.exists()) {dir.mkdirs();}
	        fileLocation = new File(dir, getFileName());
		}
		
		protected String getFileName() {
			return (dateRange+"("+Calendar.getInstance().getTimeInMillis()+")").replaceAll(" ", "");
		}
		
		protected boolean isDateValid(Long timeInMillis) {
			Calendar mCalendar = Calendar.getInstance();
	 		mCalendar.setTimeInMillis(timeInMillis);
	 		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
	 		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.set(mStartYear, mStartMonth, mStartDay, 0, 0, 0);
			startCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.set(mEndYear, mEndMonth, mEndDay, 0, 0, 0);
			endCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			if(mStartDay == mCalendar.get(Calendar.DAY_OF_MONTH) && mStartMonth == mCalendar.get(Calendar.MONTH) && mStartYear == mCalendar.get(Calendar.YEAR)) {return true;}
			if(mEndDay == mCalendar.get(Calendar.DAY_OF_MONTH) && mEndMonth == mCalendar.get(Calendar.MONTH) && mEndYear == mCalendar.get(Calendar.YEAR)) {return true;}
			if(mCalendar.after(startCalendar) && mCalendar.before(endCalendar)) {return true;}
			return false;
		}
		
		protected String getDescriptionIfNotPresent(String type) {
			if(type.equals(getString(R.string.unknown))) {
				return getString(R.string.unknown_entry);
			} else if(type.equals(getString(R.string.text))) {
				return getString(R.string.finished_textentry);
			} else if(type.equals(getString(R.string.voice))) {
				return getString(R.string.finished_voiceentry);
			} else if(type.equals(getString(R.string.camera))) {
				return getString(R.string.finished_cameraentry);
			}
			
			return "";
		}
	} 
	
	private class ExportToCSV extends Export {
		
		private Writer writer;
		
		@Override
		protected Void doInBackground(Void... params) {
	        setFile();
	        try {
				writer = new BufferedWriter(new FileWriter(fileLocation));
				addContent();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected String getFileName() {
			return super.getFileName()+".csv";
		}
	    
		private void addContent() throws IOException {
			writer.write("Sr. No.,");
	    	writer.write("Date,");
	    	writer.write("Location,");
	    	writer.write("Description,");
	    	writer.write("Amount\n");
			addDataToTable();
		}

		private void addDataToTable() throws IOException{
			int srNo = 0;
			for(int i=0 ; i < mEntryList.size() ; i++) {
				Entry entry = mEntryList.get(i);
				if(!isDateValid(entry.timeInMillis)) {continue;}
				
				// Adding Serial Number
				srNo++;
				writer.write((srNo)+",");
				// Adding date
				writer.write(new DisplayDate().getDisplayDateReport(entry.timeInMillis).replaceAll(",", " ")+",");
				
				// Adding location
				if(entry.location != null && !entry.location.equals("")) {
					writer.write(entry.location.replaceAll(",", " ")+",");
				} else {
					writer.write(getString(R.string.unknown_location).replaceAll(",", " ")+",");
				}
				
				// Adding description
				if(entry.description != null && !entry.description.equals("")) {
					writer.write(entry.description.replaceAll(",", " ")+",");
				} else {
					writer.write(getDescriptionIfNotPresent(entry.type).replaceAll(",", " ")+",");
				}
				
				// Adding Amount
				if(entry.amount != null && !entry.amount.equals("") && !entry.amount.contains("?")) {
					totalAmount = totalAmount + Double.parseDouble(entry.amount);
					writer.write(new StringProcessing().getStringDoubleDecimal(entry.amount).replaceAll(",", " ")+"\n");
				} else {
					isAmountNotEntered = true;
					writer.write("?"+"\n");
				}
				
				isRecordAdded = true;
			}
			totalNumberOfRecordsAdded = srNo;
			addTotalAmountRow();
		}

		private void addTotalAmountRow() throws IOException{
			// Adding Serial Number
			writer.write(",");
			
			// Adding date
			writer.write(",");
			
			// Adding location
			writer.write(",");
			
			// Adding description
			writer.write("Total Amount,");
			
			// Adding Amount
			if(isAmountNotEntered) {
				writer.write(new StringProcessing().getStringDoubleDecimal(totalAmount+"").replaceAll(",", " ")+" ?\n");
			} else {
				writer.write(new StringProcessing().getStringDoubleDecimal(totalAmount+"").replaceAll(",", " ")+"\n");
			}
		}

		@Override
		protected String getType() {
			return "CSV";
		}
		
	}

	private void exportToPDF() {
		exportPDF = new ExportPDF().execute();
	}
	
	private class ExportPDF extends Export {
		
		private Font catFont;
		private Font subFont;
		private Font tableHeader;
		private Font small;
		private PdfWriter writer;
		
		@Override
		protected Void doInBackground(Void... params) {
			catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
			subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);
	        tableHeader = new Font();
	        tableHeader.setStyle(Font.BOLD);
	        small = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
	        Document document = new Document();
	        setFile();
	        try {
				writer = PdfWriter.getInstance(document, new FileOutputStream(fileLocation));
				writer.setPageEvent(new HeaderAndFooter());
				document.open();
				addMetaData(document);
				addContent(document);
				document.close();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected String getFileName() {
			return super.getFileName()+".pdf";
		}
	    
	    private void addTable(Document document) throws DocumentException {
	 		PdfPTable table = new PdfPTable(5);
	 		table.setWidthPercentage(90);
	 		table.getDefaultCell().setPadding(5.0F);
			float totalWidth = ((table.getWidthPercentage()*writer.getPageSize().getWidth())/100);
			float widths[] = {totalWidth/10,totalWidth/5,totalWidth/5,(3*totalWidth)/10,totalWidth/5};
			table.setWidths(widths);
			
			PdfPCell c1 = new PdfPCell(new Phrase("Sr. No.",tableHeader));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Date",tableHeader));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Location",tableHeader));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("Description",tableHeader));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("Amount",tableHeader));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			table.setHeaderRows(1);
			table.setSplitRows(true);

			addDataToTable(table,document);
		}

		private void addTitle(Document document) throws DocumentException{
	 		Paragraph preface = new Paragraph();
			// We add one empty line
			addEmptyLine(preface, 1);
			// Lets write a big header
			preface.setAlignment(Element.ALIGN_CENTER);
			preface.add(new Paragraph("Expenses Report", catFont));
			addEmptyLine(preface, 1);
			document.add(preface);
		}
	    
		private void addContent(Document document) throws DocumentException {
	 		addTitle(document);
	 		addDateRange(document);
			addTable(document);
		}

		private void addDateRange(Document document) throws DocumentException {
			Paragraph preface = new Paragraph();
			preface.setAlignment(Element.ALIGN_CENTER);
			preface.add(new Paragraph(dateRange, subFont));
			addEmptyLine(preface, 2);
			document.add(preface);
		}

		// add metadata to the PDF which can be viewed in your Adobe Reader
	 	// under File -> Properties
	 	private void addMetaData(Document document) {
	 		document.addTitle("Expenses Report using Expense Tracker (Vinsol)");
	 		document.addSubject("PDF created using android app \"Expense Tracker (Vinsol)\"");
	 		document.addKeywords("Android, PDF, Vinsol, Expense, Tracker, Expense Tracker");
	 		document.addAuthor("Vinsol");
	 		document.addCreator("Vinsol");
	 	}

		private void addDataToTable(PdfPTable table, Document document) throws DocumentException{
			int srNo = 0;
			for(int i=0 ; i < mEntryList.size() ; i++) {
				Entry entry = mEntryList.get(i);
				if(!isDateValid(entry.timeInMillis)) {continue;}
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
				
				// Adding Serial Number
				srNo++;
				table.addCell((srNo)+"");
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
				
				// Adding date
				table.addCell(new DisplayDate().getDisplayDateReport(entry.timeInMillis));
				
				// Adding location
				if(entry.location != null && !entry.location.equals("")) {
					table.addCell(entry.location);
				} else {
					table.addCell(getString(R.string.unknown_location));
				}
				
				// Adding description
				if(entry.description != null && !entry.description.equals("")) {
					table.addCell(entry.description);
				} else {
					table.addCell(getDescriptionIfNotPresent(entry.type));
				}
				
				// Adding Amount
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
				if(entry.amount != null && !entry.amount.equals("") && !entry.amount.contains("?")) {
					totalAmount = totalAmount + Double.parseDouble(entry.amount);
					table.addCell(new StringProcessing().getStringDoubleDecimal(entry.amount));
				} else {
					isAmountNotEntered = true;
					table.addCell("?");
				}
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
				
				isRecordAdded = true;
				
				if((i+1) % 500 == 0) {
					document.add(table);
					table.flushContent();
				} 
			}
			totalNumberOfRecordsAdded = srNo;
			addTotalAmountRow(table);
			document.add(table);
			table.flushContent();
		}

		private void addTotalAmountRow(PdfPTable table) {
			// Adding Serial Number
			table.addCell("");
			
			// Adding date
			table.addCell("");
			
			// Adding location
			table.addCell("");
			
			// Adding description
			table.addCell("Total Amount");
			
			// Adding Amount
			if(isAmountNotEntered) {
				table.addCell(new StringProcessing().getStringDoubleDecimal(totalAmount+"")+" ?");
			} else {
				table.addCell(new StringProcessing().getStringDoubleDecimal(totalAmount+"")+"");
			}
		}
		
		private void addEmptyLine(Paragraph paragraph, int number) {
			for (int i = 0; i < number; i++) {
				paragraph.add(new Paragraph(" "));
			}
		}
	 	
	 	public class HeaderAndFooter extends PdfPageEventHelper {

	 		protected PdfPTable footer;

	 		public HeaderAndFooter() {
	 			footer = new PdfPTable(1);
	 			footer.setTotalWidth(220);
	 			footer.getDefaultCell().setBorderWidth(0);
	 			footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	 			Chunk chunk = new Chunk("Report Generated Using - Expense Tracker (Vinsol)");
	 			chunk.setAction(new PdfAction(PdfAction.FIRSTPAGE));
	 			chunk.setFont(small);
	 			footer.addCell(new Phrase(chunk));
	 		}
	 		
	 		public void onEndPage(PdfWriter writer, Document document) {
	 	    	PdfContentByte cb = writer.getDirectContent();
	 	    	footer.writeSelectedRows(0, -1,(document.right() - document.left() - 200)+ document.leftMargin(), document.bottom() - 10, cb);
	 	    }

	 	}
	 	
	 	@Override
		protected String getType() {
			return "PDF";
		}
		
	}
	
	private boolean setStartEndDate() {
		endCalendar = Calendar.getInstance();
		endCalendar.set(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		endCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	
		startCalendar = (Calendar) endCalendar.clone();
		switch ((int)period.getSelectedItemId()) {
		//case if period is 1 Month
		case 0:
			startCalendar.add(Calendar.MONTH, -1);
			startCalendar.add(Calendar.DAY_OF_MONTH, +1);
			setDateParameters(startCalendar,endCalendar);
			return true;

		//case if period is 1 Quarter
		case 1:
			startCalendar.add(Calendar.MONTH, -3);
			startCalendar.add(Calendar.DAY_OF_MONTH, +1);
			setDateParameters(startCalendar,endCalendar);
			return true;

		//case if period is Half Year
		case 2:
			startCalendar.add(Calendar.MONTH, -6);
			startCalendar.add(Calendar.DAY_OF_MONTH, +1);
			setDateParameters(startCalendar,endCalendar);
			return true;
			
		//case if period is 1 Year
		case 3:
			startCalendar.add(Calendar.YEAR, -1);
			startCalendar.add(Calendar.DAY_OF_MONTH, +1);
			setDateParameters(startCalendar,endCalendar);
			return true;
			
		//case if period is Custom
		case 4:
			return checkStartEndDate(true);
		default:
			return false;
		}
	}

	private void setDateParameters(Calendar startCalendar, Calendar endCalendar) {
		mEndYear = endCalendar.get(Calendar.YEAR);
		mEndMonth = endCalendar.get(Calendar.MONTH);
		mEndDay = endCalendar.get(Calendar.DAY_OF_MONTH);
		mStartYear = startCalendar.get(Calendar.YEAR);
		mStartMonth = startCalendar.get(Calendar.MONTH);
		mStartDay = startCalendar.get(Calendar.DAY_OF_MONTH);
		dateRange = new DisplayDate().getDisplayDateReport(startCalendar)+" - "+new DisplayDate().getDisplayDateReport(endCalendar);
	}
	
	private OnItemSelectedListener periodListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
			if(id == 4) {
				((TextView)findViewById(R.id.daterange_textview)).setVisibility(View.GONE);
				((LinearLayout)findViewById(R.id.custom_date_layout)).setVisibility(View.VISIBLE);
			} else {
				setStartEndDate();
				TextView dateRangeView = (TextView) findViewById(R.id.daterange_textview);
				dateRangeView.setVisibility(View.VISIBLE);
				dateRangeView.setText(dateRange);
				((LinearLayout)findViewById(R.id.custom_date_layout)).setVisibility(View.GONE);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			//do nothing
		}
	};
	
	private OnItemSelectedListener typeSpinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
			if(id == 0) {
				findViewById(R.id.type_message_textview).setVisibility(View.GONE);
			} else {
				findViewById(R.id.type_message_textview).setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			//do nothing
		}
	};
	
	private CustomDatePickerDialog.OnDateSetListener mStartDateSetListener = new CustomDatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			mStartYear = year;
			mStartMonth = monthOfYear;
			mStartDay = dayOfMonth;
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			((TextView)findViewById(R.id.custom_start_date)).setText(new DisplayDate(mCalendar).getDisplayDate());
			checkStartEndDate(false);
		}
	};
	
	private CustomDatePickerDialog.OnDateSetListener mEndDateSetListener = new CustomDatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			mEndYear = year;
			mEndMonth = monthOfYear;
			mEndDay = dayOfMonth;
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			((TextView)findViewById(R.id.custom_end_date)).setText(new DisplayDate(mCalendar).getDisplayDate());
            checkStartEndDate(false);
		}
	};
	
	private boolean checkStartEndDate(boolean isToShowToast) {
		if(customEndDateTextView.getText().toString().equals("") || customStartDateTextView.getText().toString().equals("")) {
			if(isToShowToast) {
				new AlertDialog.Builder(GenerateReport.this)
				.setTitle("Error")
				.setMessage("Set Start Date and End Date before exporting")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(getString(R.string.ok), (DialogInterface.OnClickListener)null)
				.show();
			}
			return false;
		}
		if(!isCombinationCorrect()) {
			new AlertDialog.Builder(GenerateReport.this)
			.setTitle("Error")
			.setMessage("End Date must be greater than Start Date")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(getString(R.string.ok), (DialogInterface.OnClickListener)null)
			.show();
			return false;
		}
		dateRange = new DisplayDate().getReportDateRange(mStartDay, mStartMonth, mStartYear, mEndDay, mEndMonth, mEndYear);
		return true;
	}
	
	private boolean isCombinationCorrect() {
		if(mStartDay == mEndDay && mStartMonth == mEndMonth && mEndYear == mStartYear) {return true;}
		if(mStartYear > mEndYear) {return false;}
		if(mStartYear == mEndYear && mStartMonth > mEndMonth) {return false;}
		if(mStartYear == mEndYear && mStartMonth == mEndMonth && mStartDay > mEndDay) {return false;}
		return true;
	}
	
	
	@Override
	protected void onPause() {
		if(exportPDF != null && (exportPDF.getStatus().equals(AsyncTask.Status.RUNNING) || exportPDF.getStatus().equals(AsyncTask.Status.PENDING))) {
			exportPDF.cancel(true);
			Toast.makeText(this, "PDF Report Exporting Cancelled", Toast.LENGTH_LONG).show();
		}
		if(exportCSV != null && (exportCSV.getStatus().equals(AsyncTask.Status.RUNNING) || exportCSV.getStatus().equals(AsyncTask.Status.PENDING))) {
			exportCSV.cancel(true);
			Toast.makeText(this, "CSV Report Exporting Cancelled", Toast.LENGTH_LONG).show();
		}
		super.onPause();
	}
}
