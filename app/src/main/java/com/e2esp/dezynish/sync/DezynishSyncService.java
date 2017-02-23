package com.e2esp.dezynish.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Zain on 2/17/2017.
 */

public class DezynishSyncService extends Service {

    public final String LOG_TAG = DezynishSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static DezynishSyncAdapter sDezynishSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (sDezynishSyncAdapter == null) {
                sDezynishSyncAdapter = new DezynishSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sDezynishSyncAdapter.getSyncAdapterBinder();
    }

}
