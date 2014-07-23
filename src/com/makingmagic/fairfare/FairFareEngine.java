package com.makingmagic.fairfare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.maingmagic.fairfare.R;
import com.makingmagic.fairfare.MainActivity.FareCardFragment;
import com.makingmagic.fairfare.MainActivity.ErrorFragment;

public class FairFareEngine   {
	GoogleMap map;
	FragmentActivity activity;
	ArrayList<LatLng> markerPoints;
	DirectionsJSONParser parsedResult;
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
		map.setOnMapClickListener(new OnMapClickListener() {
			
			
			public void onMapClick(LatLng point)
		    {
				addPoint(point);
				 // Checks, whether start and end locations are captured
		        if (markerPoints.size() >= 2)
		        {
		            LatLng origin = markerPoints.get(0);
		            LatLng dest = markerPoints.get(1);

		            // Getting URL to the Google Directions API
		            String url = getDirectionsUrl(origin, dest);
		            startDownload(url);

		            
		        }
		        
		    }
		});
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
	
	void addPoint(LatLng point)
	{
		// Already two locations
        if (markerPoints.size() > 1)
        {
            markerPoints.clear();
            map.clear();
        }

        // Adding new item to the ArrayList
        markerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (markerPoints.size() == 1)
        {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            
        } else if (markerPoints.size() == 2)
        {
        	
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        map.addMarker(options);

       
	}
	void addPoint(MarkerOptions options)
	{
		// Already two locations
        if (markerPoints.size() > 1)
        {
            markerPoints.clear();
            map.clear();
        }

        // Adding new item to the ArrayList
        markerPoints.add(options.getPosition());
        // Add new marker to the Google Map Android API V2
        map.addMarker(options);

       
	}
	void setMarkers(DirectionsJSONParser parser)
	{
		
		markerPoints.clear();
        map.clear();
        MarkerOptions srcOpt=new MarkerOptions();
        MarkerOptions destOpt=new MarkerOptions();
        LatLng src=parser.getSource().get(0).get(0);
        LatLng dest=parser.getDestination().get(0).get(0);
        
        srcOpt.title("Start");
        srcOpt.snippet(parser.getStartAddress().get(0).get(0));
        srcOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        srcOpt.visible(true);
        srcOpt.position(src);
        
        destOpt.title("End");
        destOpt.snippet(parser.getEndAddress().get(0).get(0));
        destOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        destOpt.visible(true);
        destOpt.position(dest);
        
        addPoint(srcOpt);
        addPoint(destOpt);
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
		dist=dist/(float)1000;
		float fare=minFare;
		if(dist>minDist)
			fare+=farePerKm*(dist-minDist);
		return fare;
	}
	/*
	 * **************** Utility Methods **********************************
	 */
	
	/*
	 * ************* Data and Network Management Methods********************
	 */
	public void startDownload(String url)
	{
		DownloadTask downloadTask = new DownloadTask();
		 
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
	}
	
	 public String getDirectionsUrl(LatLng origin, LatLng dest)
	 {
	        // Origin of route
	        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
	 
	        // Destination of route
	        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
	 
	        // Sensor enabled
	        String sensor = "sensor=false";
	 
	        // Building the parameters to the web service
	        String parameters = str_origin + "&" + str_dest + "&" + sensor;
	 
	        // Output format
	        String output = "json";
	 
	        // Building the url to the web service
	        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
	 
	        return url;
	 }
	
	 
	 public void getDataAndProcess(String origin, String dest)
	 {
		 if(origin!=null && dest != null)
		 {
				String url=getDirectionsUrl(origin,dest);
				startDownload(url);
		 }
	 }
	 
	 private String getDirectionsUrl(String origin, String dest)
	    {
		 try{
	        	origin=URLEncoder.encode(origin, "utf-8");
	        	dest=URLEncoder.encode(dest, "utf-8");
	        	
	        }
	        catch(UnsupportedEncodingException e)
	        {
	        	e.printStackTrace();
	        	
	        }
	        // Origin of route
	        String str_origin = "origin=" + origin/*addressDecoder(origin)*/;
	 
	        // Destination of route
	        String str_dest = "destination=" + dest /*addressDecoder(dest)*/;
	 
	        // Sensor enabled
	        String sensor = "sensor=false";
	 
	        // Building the parameters to the web service
	        String parameters = str_origin + "&" + str_dest + "&" + sensor;
	 
	        // Output format
	        String output = "json";
	 
	        // Building the url to the web service
	        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
	        
	       
	        return url;
	    }


	 /** A method to download json data from url */
	 private String downloadUrl(String strUrl) throws IOException
	    {
	        String data = "";
	        InputStream iStream = null;
	        HttpURLConnection urlConnection = null;
	        try
	        {
	            URL url = new URL(strUrl);
	 
	            // Creating an http connection to communicate with url
	            urlConnection = (HttpURLConnection) url.openConnection();
	 
	            // Connecting to url
	            urlConnection.connect();
	 
	            // Reading data from url
	            iStream = urlConnection.getInputStream();
	 
	            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
	 
	            StringBuffer sb = new StringBuffer();
	 
	            String line = "";
	            while ((line = br.readLine()) != null)
	            {
	                sb.append(line);
	            }
	 
	            data = sb.toString();
	 
	            br.close();
	 
	        } catch (Exception e)
	        {
	            Log.d("Exception while downloading url", e.toString());
	        } finally
	        {
	            iStream.close();
	            urlConnection.disconnect();
	        }
	        return data;
	    }	 

	// Fetches data from url passed
	 private class DownloadTask extends AsyncTask<String, Void, String>
	 {
	        // Downloading data in non-ui thread
	        @Override
	        protected String doInBackground(String... url)
	        {
	 
	            // For storing data from web service
	            String data = "";
	 
	            try
	            {
	                // Fetching the data from web service
	                data = downloadUrl(url[0]);
	            } catch (Exception e)
	            {
	                Log.d("Background Task", e.toString());
	            }
	            return data;
	        }
	 
	        // Executes in UI thread, after the execution of
	        // doInBackground()
	        @Override
	        protected void onPostExecute(String result)
	        {
	            super.onPostExecute(result);
	 
	            ParserTask parserTask = new ParserTask();
	 
	            // Invokes the thread for parsing the JSON data
	            parserTask.execute(result);
	 
	        }
	    }

	 private class ParserTask extends AsyncTask<String, Integer, DirectionsJSONParser>
	 {
		 
	 
	      // Parsing the data in non-ui thread
	      @Override
	      protected DirectionsJSONParser doInBackground(String... jsonData)
	      {
	    	  DirectionsJSONParser mParser = null;
	    	  JSONObject jObject;
	          
	          try
	          {
	        	  jObject = new JSONObject(jsonData[0]);
	                /*DirectionsJSONParser parser = new DirectionsJSONParser();*/
	                mParser=new DirectionsJSONParser(jObject);
	 
	                
	          } catch (Exception e)
	          {
	                e.printStackTrace();
	          }
	          return mParser;
	       }
	 
	        // Executes in UI thread, after the parsing process
	        @Override
	      protected void onPostExecute(DirectionsJSONParser parsedResult)
	      {
	        	FairFareEngine.this.parsedResult=parsedResult;
	        	if(parsedResult==null)
	        	{
	        		String cause=activity.getString(R.string.str_noObj_cause);
	            	String help=activity.getString(R.string.str_noObj_help);
	       		 	ErrorFragment newFragment=ErrorFragment.newInstance(cause,help);
	       		 	//replace fragment in container with new fragment
	       		 	activity.getSupportFragmentManager().popBackStack();
	       		 	activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).addToBackStack(null).commit();
	       		 	return;
	        	}
	            ArrayList<LatLng> points = null;
	            PolylineOptions lineOptions = null;
	            List<List<HashMap<String, String>>> routes=new ArrayList<List<HashMap<String, String>>>();
	            routes=parsedResult.getRoutes();
	            if (routes==null||routes.size() < 1)
	            {
	            	
	            	//Create a fragment with the loaded error details
	            	String cause=activity.getString(R.string.str_noPoints_cause);
	            	String help=activity.getString(R.string.str_noPoints_help);
	       		 	ErrorFragment newFragment=ErrorFragment.newInstance(cause,help);
	       		 	//replace fragment in container with new fragment
	       		 	activity.getSupportFragmentManager().popBackStack();
	       		 	activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).addToBackStack(null).commit();
	                Toast.makeText(activity.getBaseContext(), "No Points. Try Changing your query..", Toast.LENGTH_SHORT).show();
	                return;
	            }
	 
	            // Traversing through all the routes
	            for (int i = 0; i < routes.size(); i++)
	            {
	                points = new ArrayList<LatLng>();
	                lineOptions = new PolylineOptions();
	 
	                // Fetching i-th route
	                List<HashMap<String, String>> path = routes.get(i);
	 
	                // Fetching all the points in i-th route
	                for (int j = 0; j < path.size(); j++)
	                {
	                    HashMap<String, String> point = path.get(j);
	 
	                    if (j == 0)
	                    { // Get distance from the list
	                        continue;
	                    } else if (j == 1)
	                    { // Get distanceval from the list
	                        continue;
	                    }
	                    
	                    double lat = Double.parseDouble(point.get("lat"));
	                    double lng = Double.parseDouble(point.get("lng"));
	                    LatLng position = new LatLng(lat, lng);
	                    points.add(position);
	                }
	 
	                // Adding all the points in the route to LineOptions
	                lineOptions.addAll(points);
	                lineOptions.width(4);
	                lineOptions.color(Color.BLACK);
	            }
	 
	            
	            postProcess(parsedResult,lineOptions);
	            
	       
	 
	            
	           
	        }
	 }
	 
	 void postProcess(DirectionsJSONParser parsedResult, PolylineOptions lineOptions)
	 {
		 //Set the endpoint markers
		 setMarkers(parsedResult);
		 // Drawing polyline in the Google Map for the i-th route
         map.addPolyline(lineOptions);
         //Zooming to view
		 map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(parsedResult.getSwBound().get(0),parsedResult.getNeBound().get(0)), 20));
		 //Create a fragment with the loaded details
		 FareCardFragment newFragment=new FareCardFragment();
		 //replace fragment in container with new fragment
		 activity.getSupportFragmentManager().popBackStack();
		 activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).addToBackStack(null).commit();
		 
	 }
	 
	 
	/*
	 * ************* Data and Network Management Methods********************
	 */
}