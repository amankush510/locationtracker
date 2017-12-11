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
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by amank on 04-12-2017.
 */

public class LocationService extends JobService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Location location;
    private String address;
    private JobParameters params;
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private LocationListener listener;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.e("Job", " ----------------------------- Location Job Started");
        this.params = jobParameters;
        listener = this;
        buildGoogleApiClient();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();
            createLocationRequest();
        }
        else if (mGoogleApiClient.isConnected()) {
            getLocation();
        } else {
            mGoogleApiClient.connect();
            createLocationRequest();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getLocation() {
        if (checkPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (location == null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, listener);
                    jobFinished(params, true);
                }
            }
        }, 20000);


    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        getAddress();
    }

    private void getAddress() {
        Geocoder gc = new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            StringBuilder sb = new StringBuilder();

            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                for(int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                    sb.append(address.getAddressLine(i));
                }

            }
            address = sb.toString();
            Log.e("Job", "-------------------" + address.toLowerCase());
        } catch (IOException e) {
            Log.e("Job", "-------------------" + e.getMessage());
        }
        if (address != null && !address.isEmpty()) {
            com.example.amank.locationtracker.Location location = new com.example.amank.locationtracker.Location();
            location.setAddress(address);
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
                timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                String timeNow = timeFormat.format(new Date());
                String dateNow = dateFormat.format(new Date());
                location.setTime(timeNow);
                location.setDate(dateNow);

                DatabaseHandler databaseHandler = new DatabaseHandler(this);
                databaseHandler.addLocation(location);

            } catch (Exception e) {

            }

        }
        jobFinished(params, true);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
