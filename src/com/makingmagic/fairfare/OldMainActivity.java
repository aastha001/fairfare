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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.maingmagic.fairfare.R;

public class OldMainActivity extends ActionBarActivity implements android.location.LocationListener{
	
	
	public static FairFareEngine mEngine;
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
		
		mEngine=new FairFareEngine(this);
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
	        	locationManager.requestLocationUpdates(provider, 0, 0, this);
	        	
	        }
	       
	        
	        if (savedInstanceState == null) {
	        	
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, fragment).commit();
				
				mEngine.findMyLocation(myLocation);
				
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
		case R.id.collapse_clear:
			clear();
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
	
	public void clear()
	{
		mEngine.map.clear();
		mEngine.findMyLocation(myLocation);
		EditText et= (EditText)findViewById(R.id.et_src);
		if(et!=null)et.setText("");
		et=(EditText)findViewById(R.id.et_dest);
		if(et!=null)et.setText("");
	}
	
	public void calculateClickHandler(View v)
	{
		 
		/*getSupportFragmentManager().beginTransaction().replace(R.id.container,new LoadingFragment()).addToBackStack(null).commit();*/
		String origin=etSrc.getText().toString();
		String dest=etDest.getText().toString();
		etSrc.setText("");
		etDest.setText("");
		mEngine.getDataAndProcess(origin, dest);
		
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
	public static class LoadingFragment extends Fragment {
		
		public LoadingFragment() {
			
			
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_loading, container,
					false);
			return rootView;
		}
		
		
	}
public static class ErrorFragment extends Fragment {
	private static final String EXTRA_HELP = "com.makingmagic.fairfare.EXTRA_HELP";
	public static final String EXTRA_CAUSE = "com.makingmagic.fairfare.EXTRA_CAUSE";
		String cause,help;
		public ErrorFragment(/*String cause, String help*/) {
			
			/*this.cause=cause;
			this.help=help;*/
			
		}
		public static final ErrorFragment newInstance(String cause, String help) {
			ErrorFragment fragment = new ErrorFragment();

	        final Bundle args = new Bundle(1);
	        args.putString(EXTRA_CAUSE, cause);
	        args.putString(EXTRA_HELP, help);
	        fragment.setArguments(args);

	        return fragment;
	    }
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        cause = getArguments().getString(EXTRA_CAUSE);
	        help=getArguments().getString(EXTRA_HELP);
	    }
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_error, container,
					false);
			
			return rootView;
		}
		@Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {
	        super.onViewCreated(view, savedInstanceState);
	        TextView tvCause=(TextView)view.findViewById(R.id.error_cause);
			TextView tvHelp=(TextView)view.findViewById(R.id.error_help);
			tvCause.setText(cause);
			tvHelp.setText(help);
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
			TextView tvStartAdd=(TextView)rootView.findViewById(R.id.tv_src);
			TextView tvDestAdd=(TextView)rootView.findViewById(R.id.tv_dest);
			TextView tvEstFare=(TextView)rootView.findViewById(R.id.tv_est_fare);
			TextView tvEstDist=(TextView)rootView.findViewById(R.id.tv_est_dist);
			TextView tvEstTime=(TextView)rootView.findViewById(R.id.tv_est_time);
			float dist=(float)FairFareEngine.parsedResult.getDistanceValue().get(0).get(0);
			float fare=mEngine.getFare(dist);
			
			tvStartAdd.setText(FairFareEngine.parsedResult.getStartAddress().get(0).get(0));
			tvDestAdd.setText(FairFareEngine.parsedResult.getEndAddress().get(0).get(0));
			tvEstFare.setText(Float.toString(fare));
			tvEstDist.setText(FairFareEngine.parsedResult.getDistanceString().get(0).get(0));
			tvEstTime.setText(FairFareEngine.parsedResult.getDurationString().get(0).get(0));
			
			return rootView;
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		myLocation=arg0;
		myLatLng=mEngine.convertLocationToLatLng(myLocation);
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
