package com.vinsol.expensetracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CameraActivity extends Activity implements OnClickListener{

	private static final int PICTURE_RESULT = 35;
	private TextView text_voice_camera_header_title;
	private ImageView text_voice_camera_voice_details_separator;
	private LinearLayout text_voice_camera_camera_details;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		///////   ****** No Title Bar   ********* /////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.text_voice_camera);
        
        
        ////////   ********    Initializing and assigning memory to UI Items **********    /////////
        
        text_voice_camera_header_title = (TextView) findViewById(R.id.text_voice_camera_header_title);
        text_voice_camera_voice_details_separator = (ImageView) findViewById(R.id.text_voice_camera_voice_details_separator);
        text_voice_camera_camera_details = (LinearLayout) findViewById(R.id.text_voice_camera_camera_details);
        
        setGraphicsCamera();
        setClickListeners();
		startCamera();
	}
	
	private void startCamera() {
		
		///////   *******   Starting Camera to capture Image   ********    //////////
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
		File path = new File("/mnt/sdcard/ExpenseTracker");
        path.mkdirs();
        String name = "test1.jpg";
        File file = new File(path, name);
		camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(camera, PICTURE_RESULT);
        
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK){

//            File path = new File("/mnt/sdcard/ExpenseTracker");
//            path.mkdirs();
//            String name = "test1.jpg";
//            File file = new File(path, name);
//            try {
//                FileOutputStream out = new FileOutputStream(file);
//                
//                x.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                out.flush();
//                out.close();
//
//         } catch (Exception e) {
//                e.printStackTrace();
//         }
//    }
		
		int MAX_HEIGHT = 130;
		int MAX_WIDTH = 165;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		File path = new File("/mnt/sdcard/ExpenseTracker/test1.jpg");
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap bm = BitmapFactory.decodeStream(fileInputStream,null,options);
		int defaultHeight = bm.getHeight();
        int defaultWidth = bm.getWidth();
        int height = MAX_HEIGHT;
        int width = MAX_WIDTH;
        int i = 1;
        do{
     	   height = MAX_HEIGHT * i;
     	   width = MAX_WIDTH * i;
     	   Log.v("height calc", height+"");
     	  Log.v("width calc", width+"");
     	   i++;
        }while(height < (defaultHeight-MAX_HEIGHT) && width < (defaultWidth-MAX_WIDTH) );
        i--;
        int diffHeight = defaultHeight - height;
        int diffWidth = defaultWidth - width;
        int x = diffWidth / 2;
        int y = diffHeight / 2;
        int finalx;
        int finaly;
        if(diffWidth % 2 == 0)
        	   finalx = defaultWidth - x;
        else
     	   finalx = defaultWidth - x+1;
        if(diffHeight % 2 == 0)
        	   finaly = defaultHeight - y;
        else
     	   finaly = defaultHeight - y+1;
        
		Log.v("height", bm.getHeight()+" "+y+" "+finaly + " "+defaultHeight+ " "+defaultHeight + " "+height);
		Log.v("width", bm.getWidth()+" "+x+" "+finalx + " "+defaultWidth+ " "+defaultWidth+" "+width);
		try {
			fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Find the correct scale value. It should be the power of 2.
        int width_tmp=defaultWidth, height_tmp=defaultHeight;
        int scale=1;
        while(true){
            if(width_tmp/2<MAX_WIDTH || height_tmp/2<MAX_HEIGHT)
                break;
            width_tmp/=2;
            height_tmp/=2;
            scale*=2;
        }
     
        
        //Decode with inSampleSize
        File path1 = new File("/mnt/sdcard/ExpenseTracker");
        path.mkdirs();
        String name = "test1_small.jpg";
        File file = new File(path1, name);
        FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//  Bitmap.createBitmap(bm, x, y, finalx, finaly).compress(Bitmap.CompressFormat.JPEG, 20, out);
		
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        
        try {
        	   
               
               BitmapFactory.decodeStream(new FileInputStream(path), null, o2).compress(Bitmap.CompressFormat.JPEG, 100, out);
               out.flush();
               out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	private void setGraphicsCamera() {
		///////   ***** Sets Title Camera Entry *********///////
        text_voice_camera_header_title.setText("Camera Entry");
        
        ///////   ***** Sets Title Camera Entry *********///////
        text_voice_camera_voice_details_separator.setVisibility(View.VISIBLE);
        
        //////   ******  Shows Camera Details ********////////
        text_voice_camera_camera_details.setVisibility(View.VISIBLE);
	}
	


	private void setClickListeners() {
		////////    *******    Adding Click Listeners to UI Items ******** //////////
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}
