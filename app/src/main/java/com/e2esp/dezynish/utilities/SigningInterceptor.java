package com.e2esp.dezynish.utilities;

import android.util.Log;

import java.io.IOException;

import oauth.signpost.exception.OAuthException;
import oauth.signpost.http.HttpRequest;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Zain on 2/17/2017.
 */

public class SigningInterceptor implements Interceptor {

    public final String LOG_TAG = SigningInterceptor.class.getSimpleName();
    private final OkHttpOAuthConsumer consumer;

    public SigningInterceptor(OkHttpOAuthConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        try {
            HttpRequest httpRequest = consumer.sign(request);
            Request authenticateRequest = (Request) httpRequest.unwrap();
            return chain.proceed(authenticateRequest);
        } catch (OAuthException e) {
            Log.e(LOG_TAG, "Error " + e.getMessage());
            throw new IOException("Could not sign request", e);
        }
    }
}