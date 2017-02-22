package com.e2esp.dezynish.interfaces;

import com.e2esp.dezynish.models.products.Product;

import retrofit.RetrofitError;

/**
 * Created by Zain on 2/20/17.
 */
public interface ObjectCallbacks {
    void Callback(Product content, RetrofitError error);
}
