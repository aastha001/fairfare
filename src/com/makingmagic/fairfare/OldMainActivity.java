package com.makingmagic.fairfare;

import android.app.Dialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.maingmagic.fairfare.R;

public class MainActivity extends ActionBarActivity implements android.location.LocationListener{

	MapHandler mMapHandle;
	InputFragment fragment;
	static EditText etSrc;
	static EditText etDest;
	private static LocationManager locationManager;
	private String provider;
	static Location myLocation;
	static LatLng myLatLng;
	boolean located;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMapHandle=new MapHandler(this);
		fragment=new InputFragment();
		
		// Getting Google Play availability status
	    int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

	    // Showing status
	    if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

	        int requestCode = 10;
	        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
	        dialog.show();

	    }else { // Google Play Services are available

	    	

	        // Getting LocationManager object from System Service LOCATION_SERVICE
	        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	       if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
	        {
	        	provider=LocationManager.GPS_PROVIDER;
	        	
	        }
	        else
	        {
	        	Toast.makeText(this, "Put on your GPS for an enhanced experience!",Toast.LENGTH_SHORT).show();
	        	// Creating a criteria object to retrieve provider
		        Criteria criteria = new Criteria();

		        // Getting the name of the best provider
		        provider = locationManager.getBestProvider(criteria, true);
	        }
	        locationManager.requestLocationUpdates(provider, 400, 5, this);
	        // Getting Current Location
	        myLocation = locationManager.getLastKnownLocation(provider);

	        if(myLocation!=null){
	            onLocationChanged(myLocation);
	            
	        }
	        else
	        {
	        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	        	
	        }
	       
	        
	        if (savedInstanceState == null) {
	        	
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, fragment).commit();
				
				mMapHandle.findMyLocation(myLocation);
				
	    }
	
		
		
			
		}
	}
	 /* Request updates at startup */
	 @Override
	protected void onResume() {
	    super.onResume();
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	  }
	 
	 @Override
	protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch(id)
		{
		case R.id.action_settings:
		case R.id.collapse_settings:
			openSettingsActivity();
			return true;
		case R.id.action_about:
		case R.id.collapse_about:
			openAboutActivity();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	public void openAboutActivity()
	{
		Intent intent = new Intent(this, AboutActivity.class);
	    startActivity(intent);
	}

	public void openSettingsActivity()
	{
		Intent intent = new Intent(this, SettingsActivity.class);
	    startActivity(intent);
	}
	
	public void calculateClickHandler(View v)
	{
		 
		getSupportFragmentManager().beginTransaction().replace(R.id.container,new FareCardFragment()).addToBackStack(null).commit();
	}
	
	/*
	 * Sets the Source EditText to the current Latitude and Longitude is available
	 */
	public void onMyLocClick(View v)	
	{
		
		if(myLocation!=null)
		{
			etSrc.setText(myLatLng.latitude+","+myLatLng.longitude);
		}
		else
		{
			Toast.makeText(this, "Your location is currently unavailable.. Try again later!", Toast.LENGTH_SHORT).show();
		}
		
	}
	/*
	 * Swaps the contents of the source and destination textboxes
	 */
	
	public void onSwapClick(View v)
	{
		
		Editable srcString=etSrc.getText();
		etSrc.setText(etDest.getText());
		etDest.setText(srcString);
		
	}

	/**
	 * An InputFragment Containing the fragment_input view.
	 */
	public static class InputFragment extends Fragment {
		
		public InputFragment() {
			
			
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_input, container,
					false);
			etSrc=(EditText)rootView.findViewById(R.id.et_src);
			etDest=(EditText)rootView.findViewById(R.id.et_dest);
			return rootView;
		}
		
		
	}
	
	public static class FareCardFragment extends Fragment {
		
		public FareCardFragment() {
			
			
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_result_card, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		myLocation=arg0;
		myLatLng=mMapHandle.convertLocationToLatLng(myLocation);
		//Toast.makeText(this, myLatLng.toString(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Provider " + arg0+" disabled.",Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Enabled new provider " + arg0,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
}
