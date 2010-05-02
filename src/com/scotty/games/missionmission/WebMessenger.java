package com.scotty.games.missionmission;

import java.io.IOException;
import java.io.File;
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


/**
 * Sends and receives messages from the game server.
 */
public class WebMessenger {
    private int teamNumber;
    private String url;
    
    public WebMessenger(int teamNumber) {
        this.teamNumber = teamNumber;
        this.url = "http://streetmix.seedbox.info/teams/" + 
            teamNumber + "/clues";
    }
    
    public boolean sendClue(GeoPolyPoint point, String caption) {
        // Create a new HttpClient and Post Header  
	    HttpClient httpclient = new DefaultHttpClient();  
	    HttpPost httppost = new HttpPost(url);  
	      
	    try {  
	        // Add your data  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	        nameValuePairs.add(new BasicNameValuePair("clue[lat]", "" + ((float) point.getLatitudeE6()) * 0.000001f));  
	        nameValuePairs.add(new BasicNameValuePair("clue[long]", "" + ((float) point.getLongitudeE6()) * 0.000001f));
	        nameValuePairs.add(new BasicNameValuePair("clue[caption]", caption));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	      
	        // Execute HTTP Post Request  
	        HttpResponse response = httpclient.execute(httppost);  
	        
	    } catch (ClientProtocolException e) {  
	        // TODO Auto-generated catch block  
	    } catch (IOException e) {  
	       // TODO Auto-generated catch block
	    }
	    
	    return true;
    }  
}
