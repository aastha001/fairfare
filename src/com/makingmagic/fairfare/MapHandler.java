package com.maingmagic.fairfare;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MapHandler {
	GoogleMap map;
	
	public MapHandler(FragmentActivity activity)
	{
		map=((SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
	}
}
