package com.streetmix;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class JoinConfig extends Activity {// implements RadioGroup.OnCheckedChangeListener {
    
    private Intent myIntent;
    private EditText playIDField;
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
        setContentView(R.layout.join_layout);
        
        playIDField = (EditText) findViewById(R.id.joingame_playid_edittext);
        
        myIntent = getIntent();
        scenarioID = 1;
        //try {
		//	scenarioData = new JSONObject(myIntent.getStringExtra("scenarioData"));
		//    scenarioID = scenarioData.getInt("id");
		//} catch (JSONException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
       
        
        mRadioGroup = (RadioGroup) findViewById(R.id.team_affiliation_menu);
        //mRadioGroup.setOnCheckedChangeListener(this);
        
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
            	int playID = 0;
            	try {
            		playID = Integer.parseInt(playIDField.getText().toString());
            	} catch (NumberFormatException e) {
            	    AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            	    dialog.setTitle("Bad Play ID Number");
            	    dialog.setMessage("Please enter a valid Play ID number.");
            	    dialog.show();
            		return;
            	}
            	
                Intent returnIntent = new Intent(
                    v.getContext(), 
                    com.streetmix.AvailableScenarioList.class);
                returnIntent.putExtra("scenarioID", scenarioID);
                returnIntent.putExtra("duration", 20);
                returnIntent.putExtra("teamAffiliation", teamAffiliation);
                returnIntent.putExtra("playID", playID);
                
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
    
    //public void onCheckedChanged(RadioGroup group, int checkedId) {
    //    //teamAffiliation = checkedId;
    //}
    
    
}
