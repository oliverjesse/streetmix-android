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
    private TextView debugText;
    private Intent myIntent;
    
    private static final int MAP_ACTION = 1;
    private static final int NEWGAME_ACTION = 2;
    
    
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
			    //myIntent.putExtra("teamNumber", teamNumber);
                //myIntent.putExtra("clueNumber", evidenceFound);
                startActivityForResult(myIntent, MAP_ACTION);
			}
        });
        
        joinGameButton = (Button) findViewById(R.id.joingamebutton);
        joinGameButton.setOnClickListener(new ImageView.OnClickListener() {
        	@Override
			public void onClick(View v) {
			    debugText.setText("Not yet implemented :(");
			}
        });
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEWGAME_ACTION) {
            if (resultCode == RESULT_OK) {
                myIntent = new Intent(
                myImageView.getContext(), 
                    com.streetmix.GameMap.class);
                //myIntent.putExtra("clueNumber", evidenceFound);
                startActivityForResult(myIntent, MAP_ACTION);
            }
        }
    }
    
}
