/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.ddpclient.spiovesan.ddpclient;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author openmobster@gmail.com
 */
public class LogService extends Service implements LocationListener
{
	private boolean isRunning = true;
	private LocationManager locationManager;
	private String provider = new String("No provider");
	Location location;
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		//Not implemented...this sample is only for starting and stopping services.
		//Service binding will be covered in another tutorial
		return null;
	}
	
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);

		// Get the location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(provider, 400, 1, this);
		
		//Announcement about starting
		Toast.makeText(this, "Starting the Log Service: " + provider, Toast.LENGTH_SHORT).show();
		  
		//Start a Background thread
		isRunning = true;
		Thread backgroundThread = new Thread(new BackgroundThread());
		backgroundThread.start();
		
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		
		//Stop the Background thread
		isRunning = false;
		
		//Announcement about stopping
		Toast.makeText(this, "Stopping the Log Service", Toast.LENGTH_SHORT).show();
	}
	
	  @Override
	  public void onLocationChanged(Location l) {
		location = l;
	    int lat = (int) (location.getLatitude());
	    int lng = (int) (location.getLongitude());
        Log.w("LogPositionApp", "Latitude: " + String.valueOf(lat));
        Log.w("LogPositionApp", "Longitude: " + String.valueOf(lng));
	  }

	  @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub

	  }

	  @Override
	  public void onProviderEnabled(String provider) {
	   // Toast.makeText(this, "Enabled new provider " + provider,
	    //    Toast.LENGTH_SHORT).show();

	  }

	  @Override
	  public void onProviderDisabled(String provider) {
	    //Toast.makeText(this, "Disabled provider " + provider,
		  //    Toast.LENGTH_SHORT).show();
	  }		

	    public void updateMeteorLocation() throws JSONException {

            JSONObject myObject = new JSONObject();
            Date myDate = new Date();
            try {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                myObject.put("time", myDate);
                myObject.put("lat", lat);
                myObject.put("lng", lng);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }


	   private class BackgroundThread implements Runnable
	   {
		int counter = 0;
		Location lastLocation = new Location(""); 
		Date lastUpdate = new Date();
		long minElapsedTime = 1; // min
		
		long elapsedTime(Date start, Date stop)
		{
			long l1 = start.getTime();
			long l2 = stop.getTime();
			long diff = l2 - l1;
	
			long secondInMillis = 1000;
			long minuteInMillis = secondInMillis * 60;
			long hourInMillis = minuteInMillis * 60;
			long dayInMillis = hourInMillis * 24;
			long yearInMillis = dayInMillis * 365;
	
			long elapsedYears = diff / yearInMillis;
			diff = diff % yearInMillis;
			long elapsedDays = diff / dayInMillis;
			diff = diff % dayInMillis;
			long elapsedHours = diff / hourInMillis;
			diff = diff % hourInMillis;
			long elapsedMinutes = diff / minuteInMillis;
			diff = diff % minuteInMillis;
			long elapsedSeconds = diff / secondInMillis;
			
			return elapsedMinutes;
		}
		
		public void run()
		{
		
			try
			{
				counter = 0;
				while(isRunning)
				{
					if (location != null)
					{
						//System.out.println(""+counter++ + location.toString());
						Date now = new Date();
						long elapsed = elapsedTime(lastUpdate, now);
						if (elapsed > minElapsedTime)
						{
							lastLocation = location;
							Log.i("mylogger", location.toString());
							lastLocation = location;
							updateMeteorLocation();
							lastUpdate = now;
						}
					}
					
					Thread.currentThread().sleep(5000);
				}
				
				System.out.println("Background Thread is finished.........");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
