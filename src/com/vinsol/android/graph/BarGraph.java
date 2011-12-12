package com.vinsol.android.graph;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class BarGraph extends View {

	private Paint paint;
	private Double max;
	private ArrayList<String> values;
	private int height;
	private int width;
	private int verDiff;
	private int horDiff;
	private ArrayList<String> horLabels;
	
	public BarGraph(Context context,ArrayList<String> valueList,ArrayList<String> _horLabels) {
		super(context);
		values = valueList;
		paint = new Paint();
		horLabels = _horLabels;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		max = getMax();
		height = getHeight();
		width = getWidth();
		paint.setColor(Color.parseColor("#000000"));
		drawVerticalLine(canvas);
		drawHorinzontalLine(canvas);
		drawVerticalLabels(canvas);
		drawGraph(canvas);
	}
	
	private void drawVerticalLabels(Canvas canvas) {
		int originX = (int) ((15.83/100)*width);
		int topY = (int) ((Double)(85.76/100)*height);
		int interval = (int) (max/5);
		int value = 0;
		paint.setTextAlign(Align.RIGHT);
		TextView mTextView = new TextView(getContext());
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
		
		paint.setTextSize(mTextView.getTextSize());
		TextView mTextViewTemp = new TextView(getContext());
		mTextViewTemp.setTextSize(TypedValue.COMPLEX_UNIT_DIP,5);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		for(int i=0;i<6;i++){
			canvas.drawText(value+"", originX-mTextViewTemp.getTextSize(), topY+mTextViewTemp.getTextSize(), paint);
			value = value + interval;
			topY = (int) (topY-(verDiff/5));
		}
		paint = new Paint();
		paint.setColor(Color.parseColor("#000000"));
	}

	private void drawGraph(Canvas canvas) {
		int barWidth = (int) ((6.04/100)*width);
		int topY = (int) ((Double)(85.76/100)*height);
		int originX = (int) ((15.83/100)*width);
		int interval = (int) (horDiff/7);
		int value = 0;
		int finalValue;
		Paint textPaint = new Paint();
		textPaint.setTextAlign(Align.RIGHT);
		TextView mTextView = new TextView(getContext());
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
		textPaint.setTextSize(mTextView.getTextSize());
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		for(int i = 0 ;i<values.size();i++){
			finalValue = value+originX+barWidth;
			value = value + interval;
			Double tempDouble = getDouble(i);
			TextView mTextViewTemp = new TextView(getContext());
			mTextViewTemp.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
			RectF mRectF = new RectF(originX+value, topY-(int)((tempDouble/max)*verDiff), finalValue, topY);
			canvas.drawRect(mRectF,paint);
			canvas.drawText(horLabels.get(i), originX+value, topY+mTextViewTemp.getTextSize(), textPaint);
			if(values.get(i) != null) {
				if(values.get(i).contains("?")){
					textPaint.setTextAlign(Align.LEFT);
					canvas.drawText("?", originX+value-(barWidth/2), topY-(int)((tempDouble/max)*verDiff)-5, textPaint);
					textPaint.setTextAlign(Align.RIGHT);
				}
			}
		}
	}

	private void drawHorinzontalLine(Canvas canvas) {
		int originX = (int) ((15.83/100)*width);
		int originY = (int) ((Double)(85.76/100)*height);
		int rightX = (int) ((93.75/100)*width);
		int rightY = originY;
		horDiff = rightX - originX;
		canvas.drawLine(originX, originY, rightX, rightY, paint);
	}

	private void drawVerticalLine(Canvas canvas){
		int originX = (int) ((15.83/100)*width);
		int originY = (int) ((9.0/100)*height);
		int topX = originX;
		int topY = (int) ((Double)(85.76/100)*height);
		verDiff = topY - originY;
		canvas.drawLine(originX, originY, topX, topY, paint);
	}
	
	private Double getMax() {
		Double largest = 0.0;
		for (int i = 0; i < values.size(); i++){
			Double tempDouble = getDouble(i);
			if (tempDouble > largest)
				largest = tempDouble;
		}
		if(largest != 0.0)
			return largest;
		else 
			return 100.0;
	}
	
	private Double getDouble(int i){
		Double tempDouble = null;
		if(values.get(i) != null) {
			if(values.get(i).equals("?")){
				return 0.0;
			}
			if(values.get(i).contains("?")){
				try{
					if(values.get(i).length() > 1){
						String tempString = (String) values.get(i).subSequence(0, values.get(i).length()-1);
					return Double.parseDouble(tempString);
					}
				} catch (Exception e){
					e.printStackTrace();
					return 0.00;
				}
			} else {
				return Double.parseDouble(values.get(i));
			}
			
			return tempDouble;
		} else {
			return 0.0;
		}
	}
}
