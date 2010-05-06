package com.streetmix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

    

public class GameMap extends MapActivity {    
    private MapView mapView;
    private List<Overlay> mapOverlays;
    
    private Drawable redMarker;
    private Drawable blueMarker;
    private Drawable myMan;
    private Drawable myMarker;
    private Drawable opponentMarker;
    
    private PlayerLocationOverlay playerLocationOverlay;
    private GameItemizedOverlay homeMarkerOverlay;
    private GameItemizedOverlay teamMarkerOverlay;
    private GameItemizedOverlay opponentMarkerOverlay;
    
    private CountDownTimer timer;
    private Thread networkThread;
    private Vibrator vibrate;
    private MapController mapController;
    private WebMessenger messenger;
    private Random random;

    private ImageButton cameraViewButton;
    private TextView gameData;
    private TextView debugText;
    private String time = "Calculating Time...";
    private String buffer = "\n\n\nDBG: ";
    private String debug = "DEBUG";

    //NOTE: Team IDs are indexed by the same order as their overlays 
    //      are listed in mapOverlays.
    private JSONObject playData = null;
    private JSONObject myTeamData = null;
    private JSONObject opponentTeamData = null;
    private JSONArray teamData = null;
    
    private int teamAffiliation = 1;
    private int opponentTeamAffiliation = 0;
    
    private int teamIDs[];
    private int teamNumber = 0;
    private int numberOfTeams = 0;
    private int myTeamColor = 0;
    private int opponentTeamColor = 0;    
    
    private int playNumber = 0;
    private int scenarioID = 0;
    private int evidenceFound = 0;
    private float area = 0;
    private long millisLeft = 0;
    private long minutes = 20;
    private long seconds = 0;
    private Intent gameOptionsIntent;
    private Intent myIntent = null;
    private GeoPoint playerLocation = null;
    private boolean updatePoints = false;
    
    private static final int MILLIS_PER_MINUTE = 60000;
    private static int AREA_COLOR_BLUETEAM = 0x4D0059E3;
    private static int AREA_COLOR_REDTEAM = 0x53FF0000;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        
        //Set up the printing for the game data.
        gameData  = (TextView) findViewById(R.id.gamedata);
        debugText = (TextView) findViewById(R.id.debugtext);
        debugText.setText(buffer + debug);
        
        gameOptionsIntent = getIntent();
        teamAffiliation = gameOptionsIntent.getIntExtra("teamAffiliation", 0);
        opponentTeamAffiliation = 
            (teamAffiliation == GlobalData.BLUE_TEAM) ? 
            GlobalData.RED_TEAM : GlobalData.BLUE_TEAM;
        try {
            String playDataString = gameOptionsIntent.getStringExtra("playInfo");
			playData = new JSONObject(playDataString);
			playData = playData.getJSONObject("play");
			teamData = playData.getJSONArray("teams");
			myTeamData = teamData.getJSONObject(teamAffiliation);
			opponentTeamData = teamData.getJSONObject(opponentTeamAffiliation);
			
			//Set up the messenger to send clues to the server
			numberOfTeams = 2;
			teamNumber = myTeamData.getInt("id");
			teamIDs = new int[numberOfTeams];
			teamIDs[0] = teamNumber;
			teamIDs[1] = opponentTeamData.getInt("id");
			playNumber = playData.getInt("id");
			
			debugText.setText(buffer + "Init OK, playID = " + playNumber);
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			//Set up the messenger to send clues to the server.
			random = new Random();
			numberOfTeams = 2;
			teamNumber = 44; //random.nextInt(1000);
			teamIDs = new int[numberOfTeams];
			teamIDs[0] = teamNumber;
			teamIDs[1] = 43;
			playNumber = 21;
			
			debugText.setText(buffer + "Init BAD :(");
		}
		
		redMarker = this.getResources().getDrawable(R.drawable.arrow_red);
		blueMarker = this.getResources().getDrawable(R.drawable.arrow_blue);
		
		if (teamAffiliation == GlobalData.BLUE_TEAM) {
			myTeamColor = AREA_COLOR_BLUETEAM;
			opponentTeamColor = AREA_COLOR_REDTEAM;
			myMarker = blueMarker;
			opponentMarker = redMarker;
		} else {
			myTeamColor = AREA_COLOR_REDTEAM;
			opponentTeamColor = AREA_COLOR_BLUETEAM;
			myMarker = redMarker;
			opponentMarker = blueMarker;
		}
        
        
        messenger = new WebMessenger(teamNumber, playNumber);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
        
        teamMarkerOverlay = new GameItemizedOverlay(
            myMarker, myTeamColor, this);
        mapOverlays.add(teamMarkerOverlay);
        
        opponentMarkerOverlay = new GameItemizedOverlay(
            opponentMarker, opponentTeamColor, this);
        mapOverlays.add(opponentMarkerOverlay);
        
        
        
        //Setup the map controller.
        mapController = mapView.getController();
        
        //Setup a player location listener.
        playerLocationOverlay = new PlayerLocationOverlay(this, mapView, teamAffiliation);
        mapOverlays.add(playerLocationOverlay);
        playerLocationOverlay.enableCompass();
        playerLocationOverlay.enableMyLocation();
        playerLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                //For debugging, Cory Hall's coordinates are:
                //Longitude = -122.257976, Latitude = 37.875344
                playerLocation = playerLocationOverlay.getMyLocation();
                mapController.animateTo(playerLocation);
                mapController.setZoom(18);
            }
        });
        
        //The Camera View swap button.
        cameraViewButton = (ImageButton) findViewById(R.id.cameraViewButton);
        cameraViewButton.setOnClickListener(new MapView.OnClickListener() {
        	@Override
			public void onClick(View v) {
			    if (millisLeft == 0) {
			        return;
			    }
			    
			    myIntent = new Intent(
			        mapView.getContext(), 
			        com.streetmix.CameraPreview.class);
			    myIntent.putExtra("teamNumber", teamNumber);
                myIntent.putExtra("clueNumber", evidenceFound);
                startActivityForResult(myIntent, GlobalData.CAMERA_ACTION);
			}
        });
        
        //Set up the game timer.
        long gameLengthInMinutes = 20;
        long gameLength = gameLengthInMinutes * MILLIS_PER_MINUTE;        
        timer = new CountDownTimer(gameLength, 1000) {
        	private int updatesPerServerCheck = 10;
        	private int updatesSoFar = 0;
        	
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished / 1000;
                minutes = millisLeft / 60;
                seconds = millisLeft % 60;
                time = "Time Left: " + minutes + ":" + 
                    ((seconds < 10) ? "0" + seconds : seconds);
                
                updateGameStats();
                
                if (updatesSoFar++ > updatesPerServerCheck) {
                	updatePoints = true;
                	updatesSoFar = 0;
                }
            }
            
            public void onFinish() {
                time = "Time's up!";
                millisLeft = 0;
                
                vibrate = (Vibrator) getSystemService(mapView.getContext().VIBRATOR_SERVICE);
                long rumblePattern[] = 
                    new long[] {250, 500, 250, 500, 250, 500, 250, 500, 250, 500};
                vibrate.vibrate(rumblePattern, -1);

                updatePoints = true;
                updateGameStats();
            }
        }.start();
        
        networkThread = new Thread() {
            public void run() {
                while (true) {
                    if (updatePoints) {
                        for (int i = 0; i < teamIDs.length; i++) {
                            checkCluesOnServer(i);
                        }
                        
                        updatePoints = false;
                    }
                }
            }
        };
        networkThread.start();
        
        //Plot some test points.
        //plotTestPoints();
    }
    
    /**
     * Called whenever the phone display has a cause to rotate.
     * Ex: Phone is tilted 90 degrees or more, keyboard pulled out, etc.
     * This does nothing except keep the phone from automatically
     * calling the onCreate() function everytime you shake the
     * device and resetting the whole bloody game.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    /**
     * Called when the map becomes the focus again after going off to
     * some other game feature like the camera.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == GlobalData.CAMERA_ACTION) {
             
             if (resultCode == RESULT_OK) {
                 //Acquire the player's current location, move the game 
                 //camera and set a marker there, and send a message off 
                 //to the server.
                 
                 playerLocation = playerLocationOverlay.getMyLocation();
                 if (playerLocation == null) {
                     debugText.setText(buffer + "ERROR: Couldn't locate player.");
                     return;
                 }
                 mapController.animateTo(playerLocation);
                 
                 GeoPolyPoint currentLocation = 
                    new GeoPolyPoint(
                        playerLocation.getLatitudeE6(), 
                        playerLocation.getLongitudeE6());
                 
                 //TODO: Make this retry until server acknoledges clue.
                 //TODO: Send the picture instead of the filename since 
                 //      the pictures are no longer stored locally on
                 //      the phones.
                 //placeMarker(currentLocation, teamMarkerOverlay, true);
                 messenger.sendClue(
                     currentLocation, 
                     "Clue #" + evidenceFound,
                     "/sdcard/dcim/Camera/streetmix_clue.jpg");
                 
                 debugText.setText(buffer + "Claimed!");
             } else {
                 debugText.setText(buffer + "Canceled");
             }
         }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    private void placeMarker(GeoPolyPoint point, 
        GameItemizedOverlay overlay, boolean pointForMyTeam) {
        
        OverlayItem marker = new OverlayItem(point, 
            "Clue ",// + evidenceFound, 
            "Longitude = " + 
            (((float)point.getLongitudeE6())/1000000.0) + 
            "\nLatitude = " + 
            (((float)point.getLatitudeE6())/1000000.0));
        overlay.addOverlay(marker);
        
        if (pointForMyTeam) {
            evidenceFound++;
            area = teamMarkerOverlay.getArea() * 1000000.0f;
        }
    }
    
    public MapView getView() {
        return mapView;
    }
    
    public void updateGameStats() {
    	gameData.setText(time + "\nClues: " + evidenceFound +
    	    " pieces\nArea: " + area + " points");
    }
    
    
    /**
     * Checks that the clues on the server and the phone match.
     * Clues missing on the phone are added locally.
     */
    public void checkCluesOnServer(int teamIDsIndex) {
        int teamIDNumber = teamIDs[teamIDsIndex];
        String clueInfo = messenger.getClueInfoString(teamIDNumber);
        //Log.d("Opponent Clues", clueInfo);
        //debugText.setText(buffer + clueInfo);
        
        JSONArray clueArray;
		try {
			clueArray = new JSONArray(clueInfo);
		} catch (JSONException e1) {
			Log.e("checkCluesOnServer", "ERROR: JSONException thrown.");
			e1.printStackTrace();
			return;
		}
		
		int cluesIKnowAbout = 
			((GameItemizedOverlay) mapOverlays.get(teamIDsIndex)).size();
		if (cluesIKnowAbout < clueArray.length()) {
            for (int i = cluesIKnowAbout; i < clueArray.length(); i++) {
                JSONObject clue, clueContents;
                try {
                    clue = clueArray.getJSONObject(i);
                    Log.d("clue " + i, clue.toString());
                    
                    clueContents = clue.getJSONObject("clue");
                    Log.d("clueContents " + i, clueContents.toString());
                    
                    GeoPolyPoint point = new GeoPolyPoint(
                        (int) (clueContents.getDouble("lat")  * 1000000),
                        (int) (clueContents.getDouble("long") * 1000000));
                    placeMarker(point, 
                        (GameItemizedOverlay) mapOverlays.get(teamIDsIndex), 
                        ((teamIDNumber == teamNumber) ? true : false)
                        );
                } catch (JSONException e) {
                    Log.e("clue " + i, "ERROR: JSONException thrown.");
                    e.printStackTrace();
                }
                
            }
        }
    }
    
    
    /**
     * Plots some test markers down in the Mission District
     * of San Francisco.
     */
    public void plotTestPoints() {
        placeMarker(new GeoPolyPoint(37761432, -122417135), teamMarkerOverlay, true);
        placeMarker(new GeoPolyPoint(37761788, -122419260), teamMarkerOverlay, true);
        placeMarker(new GeoPolyPoint(37758565, -122419002), teamMarkerOverlay, true);
        placeMarker(new GeoPolyPoint(37759000, -122418000), opponentMarkerOverlay, false);
        placeMarker(new GeoPolyPoint(37760000, -122418000), opponentMarkerOverlay, false);
        placeMarker(new GeoPolyPoint(37760000, -122421000), opponentMarkerOverlay, false);
    }
    
}