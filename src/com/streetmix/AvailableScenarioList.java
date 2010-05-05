package com.streetmix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AvailableScenarioList extends ListActivity {
    private ListAdapter mAdapter;
    private JSONArray scenarioArray;
    private Intent myIntent;
    private Intent returnIntent;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //setProgressBarVisibility(true);
        
        String scenariosString = WebMessenger.getScenarioList();
        scenarioArray = null;
		try {
			scenarioArray = new JSONArray(scenariosString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int numOfScenarios = scenarioArray.length();
        String[] scenarioNames = new String[numOfScenarios];
        String[] scenarioAddresses = new String[numOfScenarios];
        
        for (int i = 0; i < scenarioArray.length(); i++) {
            JSONObject obj;
			try {
				obj = scenarioArray.getJSONObject(i);
				obj = obj.getJSONObject("scenario");
				scenarioNames[i] = obj.getString("title");
				scenarioAddresses[i] = obj.getString("address");
				scenarioNames[i] = scenarioNames[i] + "\n" + scenarioAddresses[i];
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        String[] scenarioLists = new String[] {};
        int[] mapTo = new int[] {android.R.id.text1};
        
        setListAdapter(new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, scenarioNames));
        getListView().setTextFilterEnabled(true);
        
        //setProgressBarVisibility(false);
    }
    
    public void onListItemClick(ListView parent, View v, int position, long id) {
        //AlertDialog.Builder dialog = new AlertDialog.Builder(parent.getContext());
	    //dialog.setTitle("" + id);
	    //dialog.setMessage("Position = " + position);
	    //dialog.show();
	    
	    myIntent = new Intent(
	        v.getContext(), 
	        com.streetmix.GameConfig.class);
	    returnIntent = new Intent(
	        v.getContext(),
	        com.streetmix.TitleScreen.class);
	    
	    try {
			myIntent.putExtra("scenarioData", 
			    scenarioArray.getJSONObject(position).getString("scenario"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //setResult(RESULT_OK, myIntent);
	    //finish()
		startActivityForResult(myIntent, GlobalData.CONFIG_ACTION);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalData.CONFIG_ACTION) {
            if (resultCode == RESULT_OK) {
                returnIntent.putExtra("scenarioID",
                    data.getIntExtra("scenarioID", 0));
                returnIntent.putExtra("duration",
                    data.getIntExtra("duration", 0));
                returnIntent.putExtra("teamAffiliation",
                    data.getIntExtra("teamAffiliation", 0));
                
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }
    }
    
    /**
     * Keeps the device from reseting on a rotate.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
