package com.example.amank.locationtracker;

import android.*;
import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by amank on 04-12-2017.
 */

public class LocationService extends JobService implements LocationListener {
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private String address;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        getLocation();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private void getLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if(checkPermission()) {
            locationManager.requestLocationUpdates(provider, 0, 0, this);
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        locationManager.removeUpdates(this);
        getAddress();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void getAddress(){
        Geocoder gc = new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            StringBuilder sb = new StringBuilder();

            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    sb.append(address.getAddressLine(i)).append("\n");

                sb.append(address.getCountryName());
            }
            address = sb.toString();
        } catch (IOException e) {
        }
        if(address != null && !address.isEmpty()){
            com.example.amank.locationtracker.Location location = new com.example.amank.locationtracker.Location();
            location.setAddress(address);
try {
    SimpleDateFormat isoFormat = new SimpleDateFormat("HH:mm:ss");
    isoFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
    Date date = isoFormat.parse(new Date(System.currentTimeMillis()).toString());
    location.setTime(date.toString());

    DatabaseHandler databaseHandler = new DatabaseHandler(this);
    databaseHandler.addLocation(location);

} catch (Exception e){

}

        }
        jobFinished(null, true);
    }

    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
}
