package com.e2esp.dezynish.woocommerce;

import android.util.Log;

import com.e2esp.dezynish.models.orders.Order;
import com.e2esp.dezynish.models.products.Product;
import com.e2esp.dezynish.enums.RequestMethod;
import com.e2esp.dezynish.woocommerce.helpers.Endpoints;
import com.e2esp.dezynish.woocommerce.helpers.OAuthSigner;
import com.e2esp.dezynish.interfaces.ListCallbacks;
import com.e2esp.dezynish.interfaces.ObjectCallbacks;
import com.e2esp.dezynish.models.products.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import retrofit.mime.TypedByteArray;

/**
 * Created by Zain on 2/20/17.
 */
public class WooCommerce {
    private final String TAG = WooCommerce.class.getName();

    private static WooCommerce ourInstance = new WooCommerce();
    static volatile WooCommerce singleton = null;

    private WCBuilder wcBuilder;
    private OAuthSigner OAuthSigner;

    public static WooCommerce getInstance() {
        if (singleton == null) {
            synchronized (WooCommerce.class) {
                if (singleton == null) {
                    singleton = ourInstance;
                }
            }
        }
        return singleton;
    }

    private WooCommerce() {
        Log.d(TAG, "Instance created");
    }

    public void initialize(WCBuilder builder) {
        this.wcBuilder = builder;
        OAuthSigner = new OAuthSigner(wcBuilder);
        Log.i(TAG, "onCreate");
    }

    private interface ProductsInterface {
        @GET(Endpoints.PRODUCTS_ENDPOINT + "/{id}")
         void getProduct(@Path("id")String id,@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @GET(Endpoints.PRODUCTS_ENDPOINT)
         void getProducts(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @GET(Endpoints.CATEGORIES_ENDPOINT)
         void getCategories(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);
    }

    private interface OrdersInterface {
        @GET(Endpoints.ORDERS_ENDPOINT + "/{id}")
        void getOrder(@Path("id")String id,@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @GET(Endpoints.ORDERS_ENDPOINT)
        void getOrders(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);
    }

    public List<Product> getAllProducts(final ListCallbacks fetched) {
        return getProducts(null, fetched);
    }

    public List<Product> getProducts(int page, final ListCallbacks fetched) {
        return getProducts(page, 0, fetched);
    }

    public List<Product> getProducts(int page, int pageSize, final ListCallbacks fetched) {
        HashMap<String, String> params = new HashMap<>();
        if (page > 0) {
            params.put("page", String.valueOf(page));
        }
        if (pageSize > 0) {
            params.put("filter[limit]", String.valueOf(pageSize));
        }
        return getProducts(params, fetched);
    }

    public List<Product> getProducts(HashMap<String, String> params, final ListCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        ProductsInterface api = adapter.create(ProductsInterface.class);

        api.getProducts(OAuthSigner.getSignature(RequestMethod.GET, Endpoints.PRODUCTS_ENDPOINT, params), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "getProducts :: response:"+bodyString);
                try {
                    JSONObject jsonObject = new JSONObject(bodyString);
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    Gson gson = new Gson();
                    ArrayList<Product> products = gson.fromJson(jsonArray.toString(), new TypeToken<List<Product>>(){}.getType());
                    fetched.Callback(products, null);
                } catch (Exception error) {
                    error.printStackTrace();
                    fetched.Callback(null, error);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                fetched.Callback(null, error);
            }
        });

        return null;
    }

    public Product getProduct(String id, final ObjectCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        ProductsInterface api = adapter.create(ProductsInterface.class);
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("id",id);

        api.getProduct(id, OAuthSigner.getSignature(options,RequestMethod.GET, Endpoints.PRODUCTS_ENDPOINT), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    Gson gson = new Gson();
                    Product product = gson.fromJson(bodyString, Product.class);
                    System.out.println(product);
                    fetched.Callback(product, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.getUrl());
                fetched.Callback(null, error);
            }
        });

        return null;
    }

    public List<Product> getCategories(final ListCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        ProductsInterface api = adapter.create(ProductsInterface.class);

        api.getCategories(OAuthSigner.getSignature(RequestMethod.GET, Endpoints.CATEGORIES_ENDPOINT, null), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "getCategories :: response:"+bodyString);
                try {
                    JSONObject jsonObject = new JSONObject(bodyString);
                    JSONArray jsonArray = jsonObject.getJSONArray("product_categories");
                    Gson gson = new Gson();
                    ArrayList<Category> categories = gson.fromJson(jsonArray.toString(), new TypeToken<List<Category>>(){}.getType());
                    fetched.Callback(categories, null);
                } catch (Exception error) {
                    error.printStackTrace();
                    fetched.Callback(null, error);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                fetched.Callback(null, error);
            }
        });

        return null;
    }

    public List<Order> getAllOrders(final ListCallbacks fetched) {
        return getOrders(null, fetched);
    }

    public List<Order> getOrders(int page, final ListCallbacks fetched) {
        return getOrders(page, 0, fetched);
    }

    public List<Order> getOrders(int page, int pageSize, final ListCallbacks fetched) {
        HashMap<String, String> params = new HashMap<>();
        if (page > 0) {
            params.put("page", String.valueOf(page));
        }
        if (pageSize > 0) {
            params.put("filter[limit]", String.valueOf(pageSize));
        }
        return getOrders(params, fetched);
    }

    public List<Order> getOrders(HashMap<String, String> params, final ListCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        OrdersInterface api = adapter.create(OrdersInterface.class);

        api.getOrders(OAuthSigner.getSignature(RequestMethod.GET, Endpoints.ORDERS_ENDPOINT, params), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "getOrders :: response:"+bodyString);
                try {
                    JSONObject jsonObject = new JSONObject(bodyString);
                    JSONArray jsonArray = jsonObject.getJSONArray("orders");
                    Gson gson = new Gson();
                    ArrayList<Order> orders = gson.fromJson(jsonArray.toString(), new TypeToken<List<Order>>(){}.getType());
                    fetched.Callback(orders, null);
                } catch (Exception error) {
                    error.printStackTrace();
                    fetched.Callback(null, error);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                fetched.Callback(null, error);
            }
        });

        return null;
    }

}
