package com.streetmix;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GameConfig extends Activity {
    private Intent myIntent;
    private TextView scenarioDescriptionText;
    private Button startButton;
    private RadioGroup mRadioGroup;
    private RadioButton redButton;
    private RadioButton blueButton;
    private JSONObject scenarioData;
    private int teamAffiliation = GlobalData.BLUE_TEAM;
    private int scenarioID = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);
        
        myIntent = getIntent();
	    scenarioDescriptionText = (TextView) findViewById(R.id.scenario_description_text);
        try {
			scenarioData = new JSONObject(myIntent.getStringExtra("scenarioData"));
		    scenarioID = scenarioData.getInt("id");
	        scenarioDescriptionText.setText(scenarioData.getString("description"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        mRadioGroup = (RadioGroup) findViewById(R.id.team_affiliation_menu);
        
        blueButton = (RadioButton) findViewById(R.id.blue_team);
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamAffiliation = GlobalData.BLUE_TEAM;
            }
        });
        
        redButton = (RadioButton) findViewById(R.id.red_team);
        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamAffiliation = GlobalData.RED_TEAM; 
            }
        });
        
        startButton = (Button) findViewById(R.id.config_startgamebutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent(
                    v.getContext(), 
                    com.streetmix.AvailableScenarioList.class);
                returnIntent.putExtra("scenarioID", scenarioID);
                returnIntent.putExtra("duration", 20);
                returnIntent.putExtra("teamAffiliation", teamAffiliation);
                returnIntent.putExtra("instanceName", "");
                
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
    
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        teamAffiliation = 1;
    }
    
    
}
