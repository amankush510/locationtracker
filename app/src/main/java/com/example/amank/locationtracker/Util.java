package com.example.amank.locationtracker;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

/**
 * Created by amank on 04-12-2017.
 */

public class Util {
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, LocationService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000);
        builder.setOverrideDeadline(3 * 1000);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setPeriodic(60 * 60 * 1000);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
