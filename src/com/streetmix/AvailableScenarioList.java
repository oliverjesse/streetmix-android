package com.streetmix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class AvailableScenarioList extends ListActivity {
    private ListAdapter mAdapter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String scenariosString = WebMessenger.getScenarioList();
        JSONArray scenarioArray = null;
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
				scenarioNames[i] = obj.getString("address");
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
    }
    
    /**
     * Keeps the device from reseting on a rotate.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
