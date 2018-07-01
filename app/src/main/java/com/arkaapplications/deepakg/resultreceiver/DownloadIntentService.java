package com.arkaapplications.deepakg.resultreceiver;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadIntentService extends IntentService {

    public static final int SERVICE_CALL_STARTED = 0;

    public static final int SERVICE_CALL_FINISHED = 1;

    public static final int SERVICE_CALL_ERROR = 2;

    private static final String TAG = "DownloadService";

    public DownloadIntentService() {
        super(DownloadIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Service Started!");
        final ResultReceiver mResultResceiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");

        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(url)) {
            mResultResceiver.send(SERVICE_CALL_STARTED, Bundle.EMPTY);
            try {
                String[] results = downloadwebservice(url);

                /* Sending result back to activity */
                if (null != results && results.length > 0) {
                    Log.d(TAG, "Sent data!");
                    bundle.putStringArray("result", results);
                    mResultResceiver.send(SERVICE_CALL_FINISHED, bundle);
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception");
                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                mResultResceiver.send(SERVICE_CALL_ERROR, bundle);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private String[] downloadwebservice(String url) throws IOException, MyException {

        InputStream inputStream = null;

        HttpURLConnection urlConnection = null;

        /* forming th java.net.URL object */
        URL myurl = new URL(url);

        urlConnection = (HttpURLConnection) myurl.openConnection();

        /* optional request header */
        urlConnection.setRequestProperty("Content-Type", "application/json");

        /* optional request header */
        urlConnection.setRequestProperty("Accept", "application/json");

        /* for Get request */
        urlConnection.setRequestMethod("GET");

        int statusCode = urlConnection.getResponseCode();

        /* 200 represents HTTP OK */
        if (statusCode == 200) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            Log.d(TAG, "200");
            String response = convertInputStreamToString(inputStream);

            String[] results = parseResult(response);
            Log.d(TAG, results.toString());
            return results;
        } else {
            throw new MyException("Failed to fetch data yo!!");
        }


    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }

        return result;
    }

    private String[] parseResult(String result) {

        String[] blogTitles = null;
        try {
            JSONObject response = new JSONObject(result);

            JSONArray posts = response.optJSONArray("posts");

            blogTitles = new String[posts.length()];

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");

                blogTitles[i] = title;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return blogTitles;
    }

    public class MyException extends Exception {

        public MyException(String str) {
            super(str);
        }

        public MyException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }
}
