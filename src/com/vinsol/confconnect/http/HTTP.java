package com.vinsol.confconnect.http;

import static org.apache.http.protocol.HTTP.UTF_8;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Build;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.utils.Log;
import com.vinsol.expensetracker.utils.Strings;
import com.vinsol.expensetracker.utils.Utils;


public class HTTP {

	// Requirements
	private String baseUrl = "http://192.168.0.26:3000/";
	private String userId;
	private String sync = "sync";
	private String verification = "?token=";
	private String timestamp = "&&timestamp=";
	private String expenses = "expenses";
	private String favorites = "favorites";
	private String upload = "upload";
	private String download = "download";
	private String json = ".json";
	private String update = "update";
	private String delete = "delete";
	private String signup = "signup";
	private String signin = "signin";
	private Context mContext;
	private FileHelper fileHelper;
	private int responseCode;
	
	public HTTP(Context context) {
		fileHelper = new FileHelper();
		mContext = context;
		verification = "?token="+SharedPreferencesHelper.getSharedPreferences().getString(context.getString(R.string.pref_key_token), "");
		userId = SharedPreferencesHelper.getSharedPreferences().getString(context.getString(R.string.pref_key_sync_user_id), "");
	}
	
	public String getSyncData() throws IOException{
		return get(baseUrl+userId+"/"+sync+json+verification+timestamp+SharedPreferencesHelper.getSharedPreferences().getString(mContext.getString(R.string.pref_key_sync_timestamp), ""));
	}
	
	public String get(String url) throws IOException{
		return execute(url, null, "GET");
	}
	
	public String addMultipleExpenses(String postData) throws IOException {
		return post(baseUrl+userId+"/"+expenses+json+verification, postData);
	}
	
	public String updateMultipleExpenses(String postData) throws IOException {
		return post(baseUrl+userId+"/"+expenses+"/"+update+json+verification, postData);
	}
	
	public String updateMultipleFavorites(String postData) throws IOException {
		return post(baseUrl+userId+"/"+favorites+"/"+update+json+verification, postData);
	}
	
	public String deleteMultipleExpenses(String postData) throws IOException {
		return post(baseUrl+userId+"/"+expenses+"/"+delete+json+verification, postData);
	}
	
	public String deleteMultipleFavorites(String postData) throws IOException {
		return post(baseUrl+userId+"/"+favorites+"/"+delete+json+verification, postData);
	}
	
	public String addMultipleFavorites(String postData) throws IOException {
		return post(baseUrl+userId+"/"+favorites+json+verification, postData);
	}
	
	public String signup(String postData) throws IOException {
		return post(baseUrl+"/"+signup+json, postData);
	}
	
	public String signin(String postData) throws IOException {
		return post(baseUrl+"/"+signin+json, postData);
	}

	public String post(Object url, List<NameValuePair> nvps) throws IOException {
        return execute(url.toString(), new UrlEncodedFormEntity(nvps, UTF_8), "POST");
    }
	
	public String put(Object url, List<NameValuePair> nvps) throws IOException {
        return execute(url.toString(), new UrlEncodedFormEntity(nvps, UTF_8), "PUT");
    }
	
	public String post(Object url, String postData) throws IOException {
		StringEntity entity = new StringEntity(postData);
        return execute(url.toString(), entity, "POST");
    }
	
	public String put(Object url, String postData) throws IOException {
		StringEntity entity = new StringEntity(postData);
        return execute(url.toString(), entity, "PUT");
    }
	
	public String delete(Object url, String postData) throws IOException {
		StringEntity entity = new StringEntity(postData);
        return execute(url.toString(), entity, "DELETE");
    }
	
	public String uploadExpenseFile(File file,String idFromServer, boolean isAudio) throws IOException {
		return uploadFile(baseUrl+userId+"/"+expenses+"/"+upload+"/"+idFromServer+json+verification, file, isAudio);
	}
	
	public String uploadFavoriteFile(File file,String idFromServer, boolean isAudio) throws IOException {
		return uploadFile(baseUrl+userId+"/"+favorites+"/"+upload+"/"+idFromServer+json+verification, file, isAudio);
	}
	
	public boolean downloadExpenseFile(String id,String idFromServer, boolean isAudio) throws IOException {
		String extension;
		File file;
		if(isAudio) { 
			extension = ".amr";
			file = fileHelper.getAudioFileEntry(id);
		} else {
			extension = ".jpg";
			file = fileHelper.getCameraFileLargeEntry(id);
		}
		
		return downloadFile(baseUrl+userId+"/"+expenses+"/"+download+"/"+idFromServer+extension+verification, file);
	}
	
	public boolean downloadFavoriteFile(String id,String idFromServer, boolean isAudio) throws IOException {
		String extension;
		File file;
		if(isAudio) { 
			extension = ".amr";
			file = fileHelper.getAudioFileFavorite(id);
		} else {
			extension = ".jpg";
			file = fileHelper.getCameraFileLargeFavorite(id);
		}
		
		return downloadFile(baseUrl+userId+"/"+favorites+"/"+download+"/"+idFromServer+extension+verification, file);
	}
 	
	private String execute(String url, HttpEntity postData, String requestMethod) throws IOException {
		if(!Utils.isOnline(mContext)) {return null;}
		
		Log.d("***************************** Sending HTTP request *****************************" +url);
    	HttpURLConnection connection = null;

    	try {
    		connection = (HttpURLConnection) new URL(url).openConnection();
    		connection.setConnectTimeout(90 * 1000);
    		connection.setReadTimeout(90 * 1000);
    		connection.setRequestProperty("User-Agent", String.format("ExpenseTracker"+mContext.getString(R.string.version)+" Android(%s/%s)", Build.VERSION.RELEASE, Build.VERSION.INCREMENTAL));
			connection.setRequestMethod(requestMethod);
			
			// print request in log
			StringBuilder requestBuilder = new StringBuilder(connection.getURL().toString() + "\n");
    		for (Entry<String, List<String>> header : connection.getRequestProperties().entrySet()) { requestBuilder.append(header.getKey() + " = " + header.getValue()); } 
    		Log.d(requestBuilder);
    		
    		// post request
			if (postData != null) {
	    		String postParams = Strings.InputStreamToString(postData.getContent());
	    		Log.d(requestMethod+" Data: " + postParams);
	    		connection.setDoOutput(true);
	    		OutputStream outputStream = connection.getOutputStream();
	    		outputStream.write(postParams.getBytes("UTF-8"));
				outputStream.flush();
				outputStream.close();
			}
            
    		// get response
			Log.d("getting response with status " +connection.getResponseCode() );
			responseCode = connection.getResponseCode();
    		
        	if(connection.getURL().toString().equals(url) && responseCode == 200) {
        		String response = Strings.InputStreamToString(connection.getInputStream()); 
        		// print response
        		Log.d("response "+response);	
	        	// return response
	        	return response;
        	}

        	if(connection.getURL().toString().equals(url) && responseCode == 422) {
        		String response = Strings.InputStreamToString(connection.getErrorStream()); 
        		// print response
        		Log.d("response error "+response);	
	        	// return response
	        	return response;
        	}
        	
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}finally {
			connection.disconnect();
		}
    	// not propogating 404 FileNotFoundException - NR
		return null;
	}
	
	public boolean downloadFile(String urlString, File file) throws IOException {
		Log.d("******************** Downloading File *********************"+file.toString());
		HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);

            Log.d("download begining");
            Log.d("download url:" + url);
            Log.d("downloaded file name:" + file.toString());
            
            connection = (HttpURLConnection) url.openConnection();

            InputStream is = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            responseCode = connection.getResponseCode();

            if(responseCode == 200) {
            	FileOutputStream fos = new FileOutputStream(file);
            	fos.write(baf.toByteArray());
            	fos.close();
            	return true;
            }
            
        } catch (MalformedURLException e) {
        	Log.d("Error: ");
        	e.printStackTrace();
        } finally {
        	connection.disconnect();
        }
        return false;
	}
	
	private String uploadFile(String url, File file, boolean isAudio) throws IOException {
		if(!Utils.isOnline(mContext)) {return null;}
		Log.d("********************** Starting File Upload ******************");
		
		Log.d("***************************** Sending HTTP request *****************************");
		HttpClient httpclient = new DefaultHttpClient();
		try {
	
		    HttpPost httppost = new HttpPost(url);
	
		    MultipartEntity mpEntity = new MultipartEntity();
		    ContentBody cbFile;
		    if(isAudio) {
		    	cbFile = new FileBody(file, "audio/basic");
		    } else {
		    	cbFile = new FileBody(file, "image/jpeg");
		    }
		    mpEntity.addPart("file", cbFile);
		    httppost.setEntity(mpEntity);
		    HttpResponse response = httpclient.execute(httppost);
		    HttpEntity resEntity = response.getEntity();
		    
		    Log.d("getting response with status " +response.getStatusLine().getStatusCode() );
			responseCode = response.getStatusLine().getStatusCode();
//			String location = response.getLastHeader("Location").getValue();
//			if (resEntity != null) {
//				Log.d(EntityUtils.toString(resEntity));
//		    }
//			if (resEntity != null) {
//				resEntity.consumeContent();
//			}
			
			String responseString = EntityUtils.toString(resEntity);
			if(responseCode == 200) {
        		// print response
        		Log.d("response "+ responseString);	
	        	// return response
	        	return responseString;
        	}

			return null;
			
//        	if(Strings.equal(location, url) && responseCode == 422) { 
//        		// print response
//        		Log.d("response error "+responseString);	
//	        	// return response
//	        	return responseString;
//        	}
        	
//		    Log.d("Response "+response.getStatusLine().getStatusCode());
//		    Log.d(response.getStatusLine());
//		    if (resEntity != null) {
//		      Log.d(EntityUtils.toString(resEntity));
//		    }
//		    if (resEntity != null) {
//		      resEntity.consumeContent();
//		    }
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	    Log.d("********************** File Uploaded ******************");
	    return null;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

}