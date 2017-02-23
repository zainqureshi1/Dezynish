package com.e2esp.dezynish.applications;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.e2esp.dezynish.utilities.Consts;
import com.e2esp.dezynish.enums.SigningMethod;
import com.e2esp.dezynish.woocommerce.WCBuilder;
import com.e2esp.dezynish.woocommerce.WooCommerce;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Zain on 2/17/2017.
 */

public class Dezynish extends MultiDexApplication {

    public final String LOG_TAG = Dezynish.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Picasso.Builder picassoBuilder = new Picasso.Builder(getApplicationContext());
        Picasso picasso = picassoBuilder.build();
        //picasso.setIndicatorsEnabled(true);
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored) {
            Log.e(LOG_TAG, "Picasso instance already used");
        }

        WCBuilder builder = new WCBuilder();
        builder.setIsHttps(false);
        builder.setBaseUrl(Consts.WC_API_LINK);
        builder.setSigning_method(SigningMethod.HMACSHA1);
        builder.setWc_key(Consts.WC_API_KEY);
        builder.setWc_secret(Consts.WC_API_SECRET);

        WooCommerce.getInstance().initialize(builder);
    }

}
