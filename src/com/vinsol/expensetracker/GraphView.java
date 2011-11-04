package com.vinsol.expensetracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.View;

public class GraphView extends View {

	private Paint paint;
	private float[] values;
	private String[] horlabels;
	private String title;

	public GraphView(Context context, float[] values, String title, String[] horlabels) {
		super(context);
		if (values == null)
			values = new float[0];
		else
			this.values = values;
		if (title == null)
			title = "";
		else
			this.title = title;
		if (horlabels == null)
			this.horlabels = new String[0];
		else
			this.horlabels = horlabels;
	
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float border = 0.042f*getWidth();
		float horstart = border * 2;
		float height = getHeight()*0.6f;
		float width = getWidth();
		float max = getMax();
		float min = getMin();
		float diff = max - min;
		float graphheight = height - (2 * border);
		float graphwidth = width - (2 * border);
		

		paint.setTextAlign(Align.LEFT);
		for (int i = 0; i < horlabels.length; i++) {
			paint.setColor(Color.DKGRAY);
			Log.v("graph", ""+graphwidth);
			float x = ((graphwidth / horlabels.length) * i)+(0.0625f*graphwidth)+2*border;
			paint.setTextAlign(Align.CENTER);
			paint.setColor(Color.WHITE);
			paint.setTextScaleX(1.5f);
			canvas.drawText(horlabels[i], x, height - 4, paint);
		}

		paint.setTextAlign(Align.CENTER);
		canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);

		if (max != min) {
			paint.setColor(Color.LTGRAY);
			float datalength = values.length;
			float colwidth = (width - (2 * border)) / datalength;
			for (int i = 0; i < values.length; i++) {
				float val = values[i] - min;
				float rat = val / diff;
				float h = graphheight * rat;
				canvas.drawRect((i * colwidth) + horstart, (20 - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), (height) - (border - 1), paint);
			} 
		}
	}

	private float getMax() {
		float largest = Integer.MIN_VALUE;
		for (int i = 0; i < values.length; i++)
			if (values[i] > largest)
				largest = values[i];
		return largest;
	}

	private float getMin() {
		float smallest = Integer.MAX_VALUE;
		for (int i = 0; i < values.length; i++)
			if (values[i] < smallest)
				smallest = values[i];
		return smallest;
	}

}
