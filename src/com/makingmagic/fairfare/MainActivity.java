package com.maingmagic.fairfare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity {

	MapHandler mMapHandle;
	InputFragment fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMapHandle=new MapHandler(this);
		fragment=new InputFragment();
		
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, fragment).commit();
			
		}
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class InputFragment extends Fragment {
		
		public InputFragment() {
			
			
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_input, container,
					false);
			return rootView;
		}
	}
	
	public static class FareCardFragment extends Fragment {
		
		public FareCardFragment() {
			
			
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_fare_card, container,
					false);
			return rootView;
		}
	}
}
