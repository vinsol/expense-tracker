package com.vinsol.confconnect.http;

import static org.apache.http.protocol.HTTP.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Build;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.utils.Log;
import com.vinsol.expensetracker.utils.Strings;
import com.vinsol.expensetracker.utils.Utils;


public class HTTP {

	// Requirements
	private String baseUrl = "http://192.168.0.15:3000/";
	private String sync = "sync.json";
	private String verification = "?email=hiteshsondhi88@gmail.com";
	private String confConnect = "railsconf-2012/";
	private String events = "events/";
	private String timestamp = "&&timestamp=";
	private String attendees = "attendees.json";
	private String comments = "comments.json";
	private String expenses = "expenses.json";
	private Context mContext;
	
	public HTTP(Context context) {
		mContext = context;
	}
	
//	public String addExpenses(com.vinsol.expensetracker.models.Entry expenseEntry, String email, String token) throws IOException {
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		nameValuePairs.add(new BasicNameValuePair("username", username));
//		nameValuePairs.add(new BasicNameValuePair("token", token));
//		nameValuePairs.add(new BasicNameValuePair("stars", rating));
//		return post(baseUrl+confConnect+events+eventPermalink+"/"+rate, nameValuePairs);
//	}
	
	public String addComment(String eventPermalink, String username, String token, String description) throws IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("token", token));
		nameValuePairs.add(new BasicNameValuePair("comment[description]", description));
		return post(baseUrl+confConnect+events+eventPermalink+"/"+comments, nameValuePairs);
	}
	
	public String deleteAttendeeFromEvent(String eventPermalink, String username, String token) throws IOException {
		return delete(baseUrl+confConnect+"attendees"+"/"+eventPermalink+".json"+"?username="+username+"&token="+token, null);
	}
	
	private String delete(String url, List<NameValuePair> nameValuePairs) throws IOException {
		return execute(url.toString(), null, "DELETE");
	}
	
	public String getSyncData() throws IOException{
		return get(baseUrl+sync+verification+timestamp+SharedPreferencesHelper.getSharedPreferences().getString(mContext.getString(R.string.pref_key_sync_timestamp), ""));
	}
	
	public String get(String url) throws IOException{
		return execute(url, null, "GET");
	}
	
	public String addToIsAttending(String eventPermalink,String username,String token) throws IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("token", token));
		return post(baseUrl+confConnect+events+eventPermalink+"/"+attendees, nameValuePairs);
	}
	
	public String addMultipleExpenses(String postData) throws IOException {
		return post(baseUrl+expenses+verification, postData);
	}

	public String post(Object url, List<NameValuePair> nvps) throws IOException {
        return execute(url.toString(), new UrlEncodedFormEntity(nvps, UTF_8), "POST");
    }
	
	public String post(Object url, String postData) throws IOException {
		StringEntity entity = new StringEntity(postData);
        return execute(url.toString(), entity, "POST");
    }
	
	private String execute(String url, HttpEntity postData, String requestMethod) throws IOException{
		if(!Utils.isOnline(mContext)) {return null;}
		
		Log.d("***************************** Sending HTTP request *****************************");
    	HttpURLConnection connection = null;

    	try {
    		connection = (HttpURLConnection) new URL(url).openConnection();
    		connection.setConnectTimeout(90 * 1000);
    		connection.setReadTimeout(90 * 1000);
    		connection.setRequestProperty("User-Agent", String.format("ConfConnect(1.0) Android(%s/%s)", Build.VERSION.RELEASE, Build.VERSION.INCREMENTAL));
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
			int responseCode = connection.getResponseCode();
    		
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
	
}
