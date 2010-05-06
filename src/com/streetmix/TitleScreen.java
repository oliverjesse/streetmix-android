package com.streetmix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class TitleScreen extends Activity {
    private ImageView myImageView;
    private Button startGameButton;
    private Button joinGameButton;
    private Button continueGameButton;
    private TextView debugText;
    private Intent myIntent;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.titlescreen_layout);
        
        myImageView = (ImageView) findViewById(R.id.titlescreen_image);
        debugText = (TextView) findViewById(R.id.titlescreen_debugtext);
        
        startGameButton = (Button) findViewById(R.id.startgamebutton);
        startGameButton.setOnClickListener(new ImageView.OnClickListener() {
        	@Override
			public void onClick(View v) {
			    myIntent = new Intent(
			        myImageView.getContext(), 
			        com.streetmix.AvailableScenarioList.class);
                startActivityForResult(myIntent, GlobalData.NEWGAME_ACTION);
			}
        });
        
        joinGameButton = (Button) findViewById(R.id.joingamebutton);
        joinGameButton.setOnClickListener(new ImageView.OnClickListener() {
        	@Override
			public void onClick(View v) {
			    //debugText.setText("Not yet implemented :(");
			    myIntent = new Intent(
			        myImageView.getContext(), 
			        com.streetmix.JoinConfig.class);
                startActivityForResult(myIntent, GlobalData.JOINGAME_ACTION);
			}
        });
        
        continueGameButton = (Button) findViewById(R.id.continuegamebutton);
        continueGameButton.setOnClickListener(new ImageView.OnClickListener() {
        	@Override
			public void onClick(View v) {
			}
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case GlobalData.NEWGAME_ACTION:
            if (resultCode == RESULT_OK) {
                myIntent = new Intent(
                    myImageView.getContext(), 
                    com.streetmix.GameMap.class);
                
                int scenarioID = data.getIntExtra("scenarioID", 0);
                int duration = data.getIntExtra("duration", 0);
                int teamAffiliation = data.getIntExtra("teamAffiliation", 0);
                String instanceName = data.getStringExtra("instanceName");
                
                String playInfo = WebMessenger.startNewGame(
                    scenarioID, duration, instanceName);
                
                myIntent.putExtra("scenarioID", scenarioID);
                myIntent.putExtra("duration", duration);
                myIntent.putExtra("teamAffiliation", teamAffiliation);
                myIntent.putExtra("playInfo", playInfo);
                
                startActivityForResult(myIntent, GlobalData.MAP_ACTION);
            }
            break;
        case GlobalData.JOINGAME_ACTION:
            if (resultCode == RESULT_OK) {
                myIntent = new Intent(
                    myImageView.getContext(), 
                    com.streetmix.GameMap.class);
                
                int scenarioID = data.getIntExtra("scenarioID", 0);
                int duration = data.getIntExtra("duration", 0);
                int teamAffiliation = data.getIntExtra("teamAffiliation", 0);
                int playID = data.getIntExtra("playID", 0);

                String playInfo = WebMessenger.joinGame(playID);
                
                myIntent.putExtra("scenarioID", scenarioID);
                myIntent.putExtra("duration", duration);
                myIntent.putExtra("teamAffiliation", teamAffiliation);
                myIntent.putExtra("playInfo", playInfo);
                
                startActivityForResult(myIntent, GlobalData.MAP_ACTION);
            }
            break;
        default:
            break;
        }
    }
    
}
