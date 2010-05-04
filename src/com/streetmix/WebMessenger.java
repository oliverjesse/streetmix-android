package com.streetmix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntity;

import org.json.*;


/**
 * Sends and receives messages from the game server.
 */
public class WebMessenger {
    private int myTeamNumber;
    private int playNumber;
    private String url;
    private String sendCluesURL;
    private String getGameInfoURL;
    
    public WebMessenger(int myTeamNumber, int playNumber) {
        this.myTeamNumber = myTeamNumber;
        this.playNumber = playNumber;
        this.url = "http://streetmix.seedbox.info";
        this.sendCluesURL = url + "/teams/" + myTeamNumber + "/clues";
        this.getGameInfoURL = url + "/plays/" + playNumber + ".json";
    }
    
    /**
     * Posts clue information to the server.
     */
    public boolean sendClue(GeoPolyPoint point, String caption) {
        // Create a new HttpClient and Post Header  
	    HttpClient httpclient = new DefaultHttpClient();  
	    HttpPost httppost = new HttpPost(sendCluesURL);  
	      
	    try {  
	        // Add your data  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	        nameValuePairs.add(new BasicNameValuePair("clue[lat]", "" + ((float) point.getLatitudeE6())/1000000.0));  
	        nameValuePairs.add(new BasicNameValuePair("clue[long]", "" + ((float) point.getLongitudeE6())/1000000.0));
	        nameValuePairs.add(new BasicNameValuePair("clue[caption]", caption));
	        //nameValuePairs.add(new BasicNameValuePair("clue[photo]", ???));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	      
	        // Execute HTTP Post Request  
	        HttpResponse response = httpclient.execute(httppost);  
	        
	    } catch (ClientProtocolException e) {  
	        return false;
	    } catch (IOException e) {  
	        return false;
	    }
	    
	    return true;
    }
    
    /**
     * Returns an array of team TEAMNUMBER's clues from the server.
     */
    public String getClueInfoString(int teamNumber) {
        //TODO: Return clues.
        return getFromServer(url + "/teams/" + teamNumber + "/clues.json");
    }
    
    /**
     * Returns general information about the game.
     */
    public String getGameInfoString() {
        return getFromServer(getGameInfoURL);
    }
    
    protected String getFromServer(String url) {
        HttpClient httpclient = new DefaultHttpClient();  
        HttpGet httpget = new HttpGet(url);  
        HttpResponse response = null;
        String result = null;
        
        try {
            response = httpclient.execute(httpget);
        } catch (ClientProtocolException e) {  
            // TODO Auto-generated catch block  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
        }
        
        try {
          result = convertStreamToString(response.getEntity().getContent());
        } catch (IllegalStateException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        return result;
    }
    
    private static String convertStreamToString(InputStream is) {  
        /* 
        * To convert the InputStream to String we use the BufferedReader.readLine() 
        * method. We iterate until the BufferedReader return null which means 
        * there's no more data to read. Each line will appended to a StringBuilder 
        * and returned as String. 
        */  
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));  
        StringBuilder sb = new StringBuilder();  
        
        String line = null;  
        try {  
            while ((line = reader.readLine()) != null) {  
                sb.append(line + "\n");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                is.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return sb.toString();  
    }
}
