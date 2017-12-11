package com.example.amank.locationtracker;

import android.app.Activity;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by amank on 06-12-2017.
 */

public class UploadService extends JobService {
    private JobParameters params;
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    private GoogleAccountCredential mCredential;
    private static final String PREF_ACCOUNT_NAME = "accName";
    private static final String PREF_LAST_PUSHED = "lastPushed";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.e("Upload Service", "------------->>>>>> Upload service started");
        this.params = jobParameters;
        if(Util.checkDataConnectivity(UploadService.this)) {
            WriteDataTask writeDataTask = new WriteDataTask();
            writeDataTask.execute();
        }  else{
            jobFinished(params, true);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private class WriteDataTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        WriteDataTask() {
            mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UploadService.this);
            String accountName = preferences.getString(PREF_ACCOUNT_NAME, null);
            mCredential.setSelectedAccountName(accountName);
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, mCredential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }


        @Override
        protected List<String> doInBackground(Void... par) {
            try {
                    writeData();
            } catch (Exception e) {
                Log.e("Main", e.getMessage());
                mLastError = e;
                jobFinished(params, true);
                return null;
            }
            return null;
        }

        private void writeData() throws IOException {

            String spreadsheetId = "1yjp-yeB22BUt8hxUlDCwAuG6Z1zhxJVElV90V9Fd-VY";
            String range = "A:C";

            List<List<Object>> finalList = new ArrayList<>();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UploadService.this);
            Long lst = preferences.getLong(PREF_LAST_PUSHED, 0);
            if(lst == 0){
                lst = System.currentTimeMillis();
            }
            List<Location> locations = new DatabaseHandler(UploadService.this).getAllLocations(lst);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(PREF_LAST_PUSHED, locations.get(0).getCreatedAt());
            editor.apply();

            for(Location location : locations){
                List<Object> list = new ArrayList<>();
                list.add(location.getDate());
                list.add(location.getTime());
                list.add(location.getAddress());
                finalList.add(list);
            }

            ValueRange body = new ValueRange()
                    .setValues(finalList);
            AppendValuesResponse result = mService.spreadsheets().values().append(spreadsheetId, range, body)
                            .setValueInputOption("RAW")
                            .execute();
            System.out.printf("%d cells updated.", result.getUpdates().getUpdatedRows());
            jobFinished(params, true);
        }
    }
}
