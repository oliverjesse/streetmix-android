package com.streetmix;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
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
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import org.json.*;


/**
 * Sends and receives messages from the game server.
 */
public class WebMessenger {
    private int myTeamNumber;
    private int playNumber;
    private String url;
    private String sendCluesURL;
    private String sendPhotosURL;
    private String getGameInfoURL;
    
    public WebMessenger(int myTeamNumber, int playNumber) {
        this.myTeamNumber = myTeamNumber;
        this.playNumber = playNumber;
        this.url = "http://streetmix.seedbox.info";
        this.sendCluesURL = url + "/teams/" + myTeamNumber + "/clues.json";
        this.sendPhotosURL = url + "/teams/" + myTeamNumber + "/clues/";
        this.getGameInfoURL = url + "/plays/" + playNumber + ".json";
    }
    
    /**
     * Posts clue information to the server.
     */
    public String sendClue(GeoPolyPoint point, String caption, String pictureFilename) {
        // Create a new HttpClient and Post Header  
	    HttpClient httpclient = new DefaultHttpClient();  
	    HttpPost httppost  = new HttpPost(sendCluesURL);
	      
	    try {  
	        // Send the clue.
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	        nameValuePairs.add(new BasicNameValuePair("clue[lat]", "" + ((float) point.getLatitudeE6())/1000000.0));  
	        nameValuePairs.add(new BasicNameValuePair("clue[long]", "" + ((float) point.getLongitudeE6())/1000000.0));
	        nameValuePairs.add(new BasicNameValuePair("clue[caption]", caption));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	        HttpResponse response = httpclient.execute(httppost);
	        
	        //Update the clue to include the most recently taken photo.
	        JSONObject clue = null;
	        JSONObject clueContents = null;
			try {
				clue = new JSONObject(convertInputStreamToString(response.getEntity().getContent()));
				clueContents = clue.getJSONObject("clue");				
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			HttpPut photoPost = null;
			try {
				photoPost = new HttpPut(sendPhotosURL + clueContents.getInt("id") + ".json");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			MultipartEntity nameValuePairsPhotos = new MultipartEntity();
            nameValuePairsPhotos.addPart("clue[photo]", new FileBody(new File(pictureFilename)));
	        photoPost.setEntity(nameValuePairsPhotos);
	        
	        // Execute HTTP Post Request  
	        HttpResponse responsePhoto = httpclient.execute(photoPost);  
	        return convertInputStreamToString(responsePhoto.getEntity().getContent());
	    } catch (ClientProtocolException e) { 
	    } catch (IOException e) {  
	    }
	    
	    return null;
    }
    
    /**
     * Returns an array of team TEAMNUMBER's clues from the server.
     */
    public String getClueInfoString(int teamNumber) {
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
          result = convertInputStreamToString(response.getEntity().getContent());
        } catch (IllegalStateException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        return result;
    }
    
    private static String convertInputStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return convertStreamToString(reader);
    }
    
    private static String convertFileReaderToString(FileReader fr) {
        BufferedReader reader = new BufferedReader(fr);
        return convertStreamToString(reader);
    }    
    
    private static String convertStreamToString(BufferedReader reader) {  
        /* 
        * To convert the InputStream to String we use the BufferedReader.readLine() 
        * method. We iterate until the BufferedReader return null which means 
        * there's no more data to read. Each line will appended to a StringBuilder 
        * and returned as String. 
        */  
        StringBuilder sb = new StringBuilder();  
        
        String line = null;  
        try {  
            while ((line = reader.readLine()) != null) {  
                sb.append(line + "\n");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            //try {  
            //    is.close();  
            //} catch (IOException e) {  
            //    e.printStackTrace();  
            //}  
        }  
        return sb.toString();  
    }
    
    /**
     * Returns a list of the possible scenarios from the website.
     */
    public static String getScenarioList() {
        String startGameURL = "http://streetmix.seedbox.info/scenarios.json";
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpGet httpget = new HttpGet(startGameURL);
	    HttpResponse response = null;
	    
	    try {
	        response = httpclient.execute(httpget);  
	        return convertInputStreamToString(response.getEntity().getContent());
	    } catch (ClientProtocolException e) {  
	    } catch (IOException e) {  
	    }
	    
	    return null;
    }
    
    /**
     * Tells the server to create a new instance of a given scenario
     * by its ID number.
     */
    public static String startNewGame(int scenarioID, int duration) {
        // Create a new HttpClient and Post Header
        String startGameURL = "http://streetmix.seedbox.info/plays.json";
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(startGameURL);
	    HttpResponse response = null;
	      
	    try {
	        // Add your data  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);  
	        nameValuePairs.add(new BasicNameValuePair("scenarioID", "" + scenarioID));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	      
	        // Execute HTTP Post Request  
	        response = httpclient.execute(httppost);  
	        return convertInputStreamToString(response.getEntity().getContent());
	    } catch (ClientProtocolException e) {  
	    } catch (IOException e) {  
	    }
	    
	    return null;
    }
    
    /**
     * Tells the server to create a new instance of a given scenario
     * by its ID number.
     */
    public static String joinGame(int playID) {
        // Create a new HttpClient and Post Header
        String joinGameURL = 
            "http://streetmix.seedbox.info/plays/" + playID + ".json";
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpGet httpget = new HttpGet(joinGameURL);
	    HttpResponse response = null;
	    
	    try {
	        // Add your data  
	        //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);  
	        //nameValuePairs.add(new BasicNameValuePair("scenarioID", "" + scenarioID));
	        //httpget.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	      
	        // Execute HTTP Post Request  
	        response = httpclient.execute(httpget);
	        return convertInputStreamToString(response.getEntity().getContent());
	    } catch (ClientProtocolException e) {  
	    } catch (IOException e) {  
	    }
	    
	    return null;
    }
}
