package com.e2esp.dezynish.utilities;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import okhttp3.Request;
import se.akerfeldt.okhttp.signpost.OkHttpRequestAdapter;

/**
 * Created by Zain on 2/17/2017.
 */
public class OkHttpOAuthConsumer extends AbstractOAuthConsumer {

    public OkHttpOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof Request)) {
            throw new IllegalArgumentException("This consumer expects requests of type " + Request.class.getCanonicalName());
        }
        return new OkHttpRequestAdapter((Request) request);
    }
}