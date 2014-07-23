package com.makingmagic.fairfare;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.maingmagic.fairfare.R;

public class FairFareEngine   {
	GoogleMap map;
	FragmentActivity activity;
	ArrayList<LatLng> markerPoints;
	float farePerKm, minFare, minDist,estDist,estFare;
	
	public FairFareEngine(FragmentActivity activity)
	{
		/*
		 * Initialize data members
		 */
		this.activity=activity;
		markerPoints=new ArrayList<LatLng>();
		loadFareData();
		map=((SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		/*
		 * Enable the myLocation layer on the map
		 */
		map.setMyLocationEnabled(true);
	}
	
	/*
	 ******************* Preferences Management Methods***************************
	 */
	private void loadFareData()
	{
		/*
		 * Get the initial values from the Preferences file, default values provided in case no preferences exist.
		 */
		SharedPreferences sharedPref = activity.getSharedPreferences(
		        activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		Resources res=activity.getResources();
		
		float defaultMinFare= Float.parseFloat(res.getString(R.string.saved_default_minFare));
		float defaultMinDist= Float.parseFloat(res.getString(R.string.saved_default_minDist));
		float defaultFarePerKm= Float.parseFloat(res.getString(R.string.saved_default_farePerKm));
		
		minFare=sharedPref.getFloat(res.getString(R.string.saved_minFare), defaultMinFare);
		minDist=sharedPref.getFloat(res.getString(R.string.saved_minDist), defaultMinDist);
		farePerKm=sharedPref.getFloat(res.getString(R.string.saved_farePerKm), defaultFarePerKm);
	}
	public void resetToDefaultFareData(FragmentActivity activity)
	{
		/*
		 * Update preferences and class values to defaults as defined in string.xml saved_default_* strings
		 */
		SharedPreferences sharedPref = activity.getSharedPreferences(
		        activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		Resources res=activity.getResources();
		
		minFare= Float.parseFloat(res.getString(R.string.saved_default_minFare));
		minDist= Float.parseFloat(res.getString(R.string.saved_default_minDist));
		farePerKm= Float.parseFloat(res.getString(R.string.saved_default_farePerKm));
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putFloat(res.getString(R.string.saved_minFare), minFare);
		editor.putFloat(res.getString(R.string.saved_minDist), minDist);
		editor.putFloat(res.getString(R.string.saved_farePerKm), farePerKm);
		editor.commit();
		
	}
	public void storeFareData(FragmentActivity activity ,float minFare, float minDist, float farePerKm)
	{
		/*
		 * Take the new values, update class and preferences values
		 */
		Context context =activity;
		SharedPreferences sharedPref = context.getSharedPreferences(
		        activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		Resources res=activity.getResources();
		
		this.minFare= minFare;
		this.minDist=minDist;
		this.farePerKm= farePerKm;
		
		
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putFloat(res.getString(R.string.saved_minFare), minFare);
		editor.putFloat(res.getString(R.string.saved_minDist), minDist);
		editor.putFloat(res.getString(R.string.saved_farePerKm), farePerKm);
		
		editor.commit();
	}
	
	/*
	 ******************* Preferences Management Methods***************************
	 */
	
	
	/*
	 * **************** Map Management Methods ********************************
	 */
	public void findMyLocation(Location myLocation)
	{
	/////----------------------------------Zooming camera to myLocation-----------------

       
        //myLatLng=convertLocationToLatLng(myLocation);
                    if (myLocation != null)
                    {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        		convertLocationToLatLng(myLocation), 13));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(convertLocationToLatLng(myLocation))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .build();                   // Creates a CameraPosition from the builder
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }

     /////----------------------------------Zooming camera to position mylocation-----------------
	}
	/*
	 * **************** Map Management Methods ********************************
	 */
	
	
	/*
	 * **************** Utility Methods **********************************
	 */
	public LatLng convertLocationToLatLng(Location location)
	{
		/*
		 * Takes a location and returns an LatLng value
		 */
		LatLng coord=new LatLng(0.0,0.0);
		if(location!=null)
		{
			coord=new LatLng(location.getLatitude(),location.getLongitude());
			
		}
		return coord;
	}
	public float getFare(float dist)
	{
		/*
		 * Returns Fare=minFare (For upto minDist km)
		 * Returns Fare=minFare + farePerKm *(dist-minDist)	(for journeys greater than minDist km
		 */
		float fare=minFare;
		if(dist>minDist)
			fare+=farePerKm*(dist-minDist);
		return fare;
	}
	/*
	 * **************** Utility Methods **********************************
	 */
}