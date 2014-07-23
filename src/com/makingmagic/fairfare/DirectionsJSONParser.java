package com.makingmagic.fairfare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import com.google.android.gms.maps.model.LatLng;
 
public class DirectionsJSONParser
{
	ArrayList<LatLng> neBound,swBound;
	ArrayList<ArrayList<LatLng>> source,destination;
	ArrayList<ArrayList<String>> distanceString,durationString,startAddress,endAddress;
	ArrayList<ArrayList<Long>> distanceValue,durationValue;
	List<List<HashMap<String, String>>> routes;
	
	
	public DirectionsJSONParser(JSONObject jObject)
	{
		parse(jObject);
	}
	
	public LatLng getLatLngJSON(JSONObject jLatLng)
	{
		try{
			double lat=Double.parseDouble(jLatLng.getString("lat"));
			double lng=Double.parseDouble(jLatLng.getString("lng"));

			return new LatLng(lat,lng);
		}
		catch(JSONException je)
		{
			return new LatLng(0,0);
		}
	}
 
	


	public void parse(JSONObject jObject)
	{
	
		routes = new ArrayList<List<HashMap<String, String>>>();
		neBound=new ArrayList<LatLng>();
		swBound=new ArrayList<LatLng>();
		source=new ArrayList<ArrayList<LatLng>>();
		destination=new ArrayList<ArrayList<LatLng>>();
		distanceString=new ArrayList<ArrayList<String>>();
		durationString=new ArrayList<ArrayList<String>>();
		startAddress=new ArrayList<ArrayList<String>>();
		endAddress=new ArrayList<ArrayList<String>>();
		distanceValue=new ArrayList<ArrayList<Long>>();
		durationValue=new ArrayList<ArrayList<Long>>();


		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;
		JSONObject jDistance = null;
		JSONObject jDuration = null;
		JSONObject jBounds=null;
		JSONObject jLatLng=null;
 
		try
		{
			jRoutes = jObject.getJSONArray("routes");

			//Initialization Block


			/*
			 * * Traversing every route 
			 */
			for (int i = 0; i < jRoutes.length(); i++)
			{
				jBounds=((JSONObject)jRoutes.get(i)).getJSONObject("bounds");
				//Get NorthEast Bound
				jLatLng=jBounds.getJSONObject("northeast");
				neBound.add(getLatLngJSON(jLatLng));
	
				//Get SouthWest Bound
				jLatLng=jBounds.getJSONObject("southwest");
				swBound.add(getLatLngJSON(jLatLng));
	
	
				//Traverse all legs
				jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
				List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
				
				ArrayList<String> distString=new ArrayList<String>();
				ArrayList<String> duraString=new ArrayList<String>();
				ArrayList<Long> distValue=new ArrayList<Long>();
				ArrayList<Long> duraValue=new ArrayList<Long>();
				ArrayList<LatLng> src=new ArrayList<LatLng>();
				ArrayList<LatLng> dest=new ArrayList<LatLng>();
				ArrayList<String> stAdd= new ArrayList<String>();
				ArrayList<String> enAdd= new ArrayList<String>();
				/** Traversing all legs */
				for (int j = 0; j < jLegs.length(); j++)
				{
					/** Getting Start and End Locations of this leg */
					jLatLng=((JSONObject)jLegs.get(j)).getJSONObject("start_location");
					src.add(getLatLngJSON(jLatLng));
					jLatLng=((JSONObject)jLegs.get(j)).getJSONObject("end_location");
					dest.add(getLatLngJSON(jLatLng));
					
					/** Start and End Address */
					stAdd.add(((JSONObject)jLegs.get(j)).getString("start_address"));
					enAdd.add(((JSONObject)jLegs.get(j)).getString("end_address"));
					
					/** Getting distance from the json data */
					jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
					HashMap<String, String> hmDistance = new HashMap<String, String>();
					hmDistance.put("distance", jDistance.getString("text"));
					distString.add(jDistance.getString("text"));
					

					/** Getting distance value from the json data */
					//jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
					HashMap<String, String> hmDistanceval = new HashMap<String, String>();
					hmDistanceval.put("distanceval", jDistance.getString("value"));
					distValue.add(Long.parseLong(jDistance.getString("value")));
 
					/** Getting duration from the json data */
					jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
					HashMap<String, String> hmDuration = new HashMap<String, String>();
					hmDuration.put("duration", jDuration.getString("text"));
					duraString.add(jDuration.getString("text"));
					duraValue.add(Long.parseLong(jDuration.getString("value")));
 
					/** Adding distance object to the path */
					path.add(hmDistance);

					/** Adding distanceval object to the path */
					path.add(hmDistanceval);
					/** Adding duration object to the path */
					/*path.add(hmDuration);*/
 
					jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
 
					/** Traversing all steps */
					for (int k = 0; k < jSteps.length(); k++)
					{
						String polyline = "";
						polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
						List<LatLng> list = this.decodePoly(polyline);
 
						/** Traversing all points */
						for (int l = 0; l < list.size(); l++)
						{
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("lat", Double.toString((list.get(l)).latitude));
							hm.put("lng", Double.toString((list.get(l)).longitude));
							path.add(hm);
						}
					}
				}
				source.add(src);
				destination.add(dest);
				distanceString.add(distString);
				durationString.add(duraString);
				distanceValue.add(distValue);
				durationValue.add(duraValue);
				startAddress.add(stAdd);
				endAddress.add(enAdd);
				routes.add(path);
			}
 
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		} 
		catch (Exception e)
		{
	
		}
 

	}
 
	/**
	 * Method to decode polyline points
	 * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
	 * */
	private List<LatLng> decodePoly(String encoded)
	{
 
		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
 
		while (index < len)
		{
			int b, shift = 0, result = 0;
			do
			{
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
 
			shift = 0;
			result = 0;
			do
			{
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;
 
			LatLng p = new LatLng(((lat / 1E5)), ((lng / 1E5)));
			poly.add(p);
		}
 
		return poly;
	}

	public ArrayList<LatLng> getNeBound() {
		return neBound;
	}

	public ArrayList<LatLng> getSwBound() {
		return swBound;
	}

	public ArrayList<ArrayList<LatLng>> getSource() {
		return source;
	}

	public ArrayList<ArrayList<LatLng>> getDestination() {
		return destination;
	}

	public ArrayList<ArrayList<String>> getDistanceString() {
		return distanceString;
	}

	public ArrayList<ArrayList<String>> getDurationString() {
		return durationString;
	}

	public ArrayList<ArrayList<String>> getStartAddress() {
		return startAddress;
	}

	public ArrayList<ArrayList<String>> getEndAddress() {
		return endAddress;
	}

	public ArrayList<ArrayList<Long>> getDistanceValue() {
		return distanceValue;
	}

	public ArrayList<ArrayList<Long>> getDurationValue() {
		return durationValue;
	}

	public List<List<HashMap<String, String>>> getRoutes() {
		return routes;
	}


		
}