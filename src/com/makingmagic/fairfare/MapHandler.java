package com.makingmagic.fairfare;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.maingmagic.fairfare.R;

public class MapHandler   {
	GoogleMap map;
	FragmentActivity activity;
	
	public MapHandler(FragmentActivity activity)
	{
		this.activity=activity;
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
                                new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 13));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
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