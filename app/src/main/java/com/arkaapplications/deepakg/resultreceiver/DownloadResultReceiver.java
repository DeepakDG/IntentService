package com.arkaapplications.deepakg.resultreceiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DownloadResultReceiver extends ResultReceiver {

    private onReceiver mReceiver;

    public void setReceiver(onReceiver mIReceiver) {
        mReceiver = mIReceiver;
    }

    public DownloadResultReceiver(Handler handler) {
        super(handler);
    }

    public interface onReceiver {
        void onResultReceiver(int resultcode, Bundle resultdata);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onResultReceiver(resultCode, resultData);
        }
    }
}
