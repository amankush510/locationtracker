package com.example.amank.locationtracker;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.PersistableBundle;

/**
 * Created by amank on 04-12-2017.
 */

public class Util {
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, LocationService.class);
        JobInfo.Builder builder = new JobInfo.Builder(115, serviceComponent);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setPeriodic(30000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public static void scheduleUploadJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, UploadService.class);
        JobInfo.Builder builder = new JobInfo.Builder(116, serviceComponent);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setPeriodic(2 * 60 * 1000L);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public static boolean checkDataConnectivity(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
