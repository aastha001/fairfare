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
	
	private void loadFareData()
	{
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
	
	public FairFareEngine(FragmentActivity activity)
	{
		this.activity=activity;
		markerPoints=new ArrayList<LatLng>();
		loadFareData();
		map=((SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
	}
	
	public void findMyLocation(Location myLocation)
	{
	/////----------------------------------Zooming camera to position user-----------------

       
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

/////----------------------------------Zooming camera to position user-----------------
	}
	
	
	public LatLng convertLocationToLatLng(Location location)
	{
		LatLng coord=new LatLng(0.0,0.0);
		if(location!=null)
		{
			coord=new LatLng(location.getLatitude(),location.getLongitude());
			
		}
		return coord;
	}
	
}