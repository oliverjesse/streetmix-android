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
import android.widget.RadioGroup;

public class JoinConfig extends Activity {
    private Intent myIntent;
    private EditText playIDField;
    private Button startButton;
    private RadioGroup mRadioGroup;
    private JSONObject scenarioData;
    private int teamAffiliation = 1;
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
       
        
        //mRadioGroup = (RadioGroup) findViewById(R.id.config_layout);
        //mRadioGroup.setOnCheckedChangeListener(this);
        
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
                returnIntent.putExtra("teamAffiliation", GlobalData.RED_TEAM);
                returnIntent.putExtra("playID", playID);
                
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
    
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        teamAffiliation = 1;
    }
    
    
}
