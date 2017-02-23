package com.e2esp.dezynish.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.e2esp.dezynish.services.HeadInfoService;

/**
 * Created by Zain on 2/17/2017.
 */

public class CallerDetector extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(incomingNumber != null && !incomingNumber.isEmpty()) {
                // Service to search caller's number in customers db.
                // If found, show a view with customers details, shipping, billing and orders etc.
                Intent intentHeader = new Intent(context, HeadInfoService.class);
                intentHeader.putExtra("search", incomingNumber);
                context.startService(intentHeader);

            }
        }
    }
}
