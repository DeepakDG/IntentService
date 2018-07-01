package com.arkaapplications.deepakg.resultreceiver;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DownloadResultReceiver.onReceiver {

    private ListView listView = null;

    private ArrayAdapter arrayAdapter = null;

    private DownloadResultReceiver mReceiver;

    private static final String TAG = "IntentService";

    final String url = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Allow activity to show indeterminate progressbar */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        /* Initialize listView */
        listView = (ListView) findViewById(R.id.listView);

        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intnt = new Intent(Intent.ACTION_SYNC, null, this, DownloadIntentService.class);
        intnt.putExtra("url", url);
        intnt.putExtra("receiver", mReceiver);
        intnt.putExtra("requestedID", 101);
        startService(intnt);
    }

    @Override
    public void onResultReceiver(int resultcode, Bundle resultdata) {
        switch (resultcode) {
            case DownloadIntentService.SERVICE_CALL_STARTED:
                Log.d(TAG, "results"+"SERVICE_CALL_STARTED");
                setProgressBarIndeterminateVisibility(true);
                break;
            case DownloadIntentService.SERVICE_CALL_FINISHED:
                Log.d(TAG, "results"+"SERVICE_CALL_FINISHED");
                /* Hide progress & extract result from bundle */
                setProgressBarIndeterminateVisibility(false);

                String[] results = resultdata.getStringArray("result");
                Log.d("Deepak ", "results"+results.length);
                /* Update ListView with result */
                arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.listitem, R.id.textview, results);
                listView.setAdapter(arrayAdapter);

                break;
            case DownloadIntentService.SERVICE_CALL_ERROR:
                Log.d(TAG, "results"+"SERVICE_CALL_ERROR");
                /* Handle the error */
                String error = resultdata.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }

    }
}
