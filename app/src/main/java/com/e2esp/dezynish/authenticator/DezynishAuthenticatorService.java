package com.e2esp.dezynish.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Zain on 2/17/2017.
 */

public class DezynishAuthenticatorService  extends Service {

    private DezynishAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new DezynishAuthenticator(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
