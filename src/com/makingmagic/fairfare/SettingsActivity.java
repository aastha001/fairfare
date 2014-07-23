package com.makingmagic.fairfare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maingmagic.fairfare.R;

public class SettingsActivity extends ActionBarActivity {
	
	static EditText etMinFare;
	static EditText etMinDist;
	static EditText etFarePerKm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		if (savedInstanceState == null) {
			/*
			 * Load the two Fare card and edit fare card fragments into their containers
			 */
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container_curr_fare_card, new CurrentFareCardFragment())
					.add(R.id.container_set_fare_card, new SetFareCardFragment()).commit();
			
			
		}
		
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static class CurrentFareCardFragment extends Fragment {
		
		public CurrentFareCardFragment() {
			
			
			
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_curr_fare_card, container,
					false);
			/*
			 * Update the Fare card TextViews to reflect latest values as indicated by the preferences.
			 */
			TextView tvMinFare=(TextView)v.findViewById(R.id.tv_curr_minFare);
			TextView tvMinDist=(TextView)v.findViewById(R.id.tv_curr_minDist);
			TextView tvFarePerKm=(TextView)v.findViewById(R.id.tv_curr_farePerKm);
			
			SharedPreferences prefs = getActivity().getSharedPreferences(
					getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

			tvMinFare.setText("Rs. "+prefs.getFloat(getString(R.string.saved_minFare), Float.parseFloat(getString(R.string.saved_default_minFare))));
			tvMinDist.setText(prefs.getFloat(getString(R.string.saved_minDist), Float.parseFloat(getString(R.string.saved_default_minDist)))+" km");
			tvFarePerKm.setText("Rs. "+prefs.getFloat(getString(R.string.saved_farePerKm), Float.parseFloat(getString(R.string.saved_default_farePerKm))));
			return v;
		}
		
		
	}
	
	public static class SetFareCardFragment extends Fragment {
		
		public SetFareCardFragment() {
			
			
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_set_fare_card, container,
					false);
			/*
			 * Locate and remember the EditTexts for setting the values
			 */
			etMinFare=(EditText)v.findViewById(R.id.et_set_minFare);
			etMinDist=(EditText)v.findViewById(R.id.et_set_minDist);
			etFarePerKm=(EditText)v.findViewById(R.id.et_set_farePerKm);
			return v;
		}
	}
	
	public void onUpdateClick(View v)
	{
		/*
		 * Obtain values from EditTexts,ensure they're formatted okay and update the values through the engine
		 */
		try{
			float minFare=Float.parseFloat(etMinFare.getText().toString());
			float minDist=Float.parseFloat(etMinDist.getText().toString());
			float farePerKm=Float.parseFloat(etFarePerKm.getText().toString());
			
			if(minFare<=0||minDist<=0||farePerKm<=0)
				throw new NumberFormatException();
			
			MainActivity.mEngine.storeFareData(this,minFare, minDist, farePerKm);
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.container_curr_fare_card, new CurrentFareCardFragment()).commit();
		}
		catch(NumberFormatException nfe)
		{
			Toast.makeText(this, "Invalid parameters. Enter positive real numbers.", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public void onDefaultsClick(View v)
	{
		/*
		 * Restore to defaults as defined by the strings.xml files through the engine
		 */
		MainActivity.mEngine.resetToDefaultFareData(this);
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.container_curr_fare_card, new CurrentFareCardFragment()).commit();
	}

}
