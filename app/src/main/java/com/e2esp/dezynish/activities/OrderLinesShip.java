package com.e2esp.dezynish.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.e2esp.dezynish.R;
import com.e2esp.dezynish.data.DezynishContract;
import com.e2esp.dezynish.models.orders.Item;
import com.e2esp.dezynish.models.orders.Order;
import com.e2esp.dezynish.models.products.Product;
import com.e2esp.dezynish.models.products.Variation;

/**
 * Created by Zain on 2/18/2017.
 */

public class OrderLinesShip extends AppCompatActivity {

    private final String LOG_TAG = OrderLinesShip.class.getSimpleName();

    private int mIndexProducts = 0;
    private int mCounterPerItem = 1;
    private Item mItemProcessing = null;
    private Product mProductProcessing = null;

    private List<Product> mProducts = new ArrayList<>();
    private Order mOrderSelected;
    private Gson mGson = new GsonBuilder().create();

    private static final String[] ORDER_PROJECTION = {
            DezynishContract.OrdersEntry.COLUMN_JSON,
    };
    private int COLUMN_ORDER_COLUMN_JSON = 0;

    private static final String[] PRODUCT_PROJECTION = {
            DezynishContract.ProductEntry.COLUMN_ID,
            DezynishContract.ProductEntry.COLUMN_JSON,
    };
    private int COLUMN_PRODUCT_COLUMN_JSON = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orderId = getIntent().getIntExtra("order", -1);
        setContentView(R.layout.activity_order_lines_ship);

        String query = DezynishContract.OrdersEntry.COLUMN_ID + " == ?" ;
        String[] parametersOrder = new String[]{ String.valueOf(orderId) };
        Cursor cursor = getContentResolver().query(DezynishContract.OrdersEntry.CONTENT_URI,
                ORDER_PROJECTION,
                query,
                parametersOrder,
                null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(COLUMN_ORDER_COLUMN_JSON);
                    if(json!=null){
                        mOrderSelected = mGson.fromJson(json, Order.class);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        List<String> ids = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        for(Item item:mOrderSelected.getItems()) {
            ids.add(String.valueOf(item.getProductId()));
            parameters.add("?");
        }

        query = DezynishContract.ProductEntry.COLUMN_ID + " IN (" + TextUtils.join(", ", parameters) + ")";
        cursor = getContentResolver().query(DezynishContract.ProductEntry.CONTENT_URI,
                PRODUCT_PROJECTION,
                query,
                ids.toArray(new String[ids.size()]),
                null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(COLUMN_PRODUCT_COLUMN_JSON);
                    if(json!=null) {
                        Product product = mGson.fromJson(json, Product.class);
                        mProducts.add(product);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.order, String.valueOf(orderId)));
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextItem(view);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchItem();
        refreshDataCurrentItem();

        EditText scan = (EditText) findViewById(R.id.scan);
        scan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sentence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sentence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String code = editable.toString();
                Log.d(LOG_TAG, "afterTextChanged " + code);
                if(code.toLowerCase().startsWith(String.valueOf(mProductProcessing.getId()))) {
                    goToNextItem(null);
                }
            }
        });

    }

    private void searchItem() {
        for(Item item: mOrderSelected.getItems()) {
            if(mProducts.get(mIndexProducts).getId() == item.getProductId()) {
                mItemProcessing = item;
                mProductProcessing = mProducts.get(mIndexProducts);
                return;
            }
            for(Variation variation : mProducts.get(mIndexProducts).getVariations()) {
                if(variation.getId() == item.getProductId()) {
                    mItemProcessing = item;
                    mProductProcessing = mProducts.get(mIndexProducts);
                    return;
                }
            }
        }
    }

    private void refreshDataCurrentItem() {

        if(mCounterPerItem > 1) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderLinesShip.this)
                .setTitle(mProductProcessing.getTitle())
                .setMessage(getString(R.string.warning_more_items))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            alertDialogBuilder.create().show();

        }

        LinearLayout header = (LinearLayout) findViewById(R.id.header);

        TextView txtOrder = (TextView) findViewById(R.id.order);
        TextView txtTitle = (TextView) findViewById(R.id.title);
        TextView txtDescription = (TextView) findViewById(R.id.description);
        TextView txtSku = (TextView) findViewById(R.id.sku);
        TextView txtQuantity = (TextView) findViewById(R.id.quantity);
        TextView txtStock = (TextView) findViewById(R.id.stock);
        TextView txtPrice = (TextView) findViewById(R.id.price);

        ImageView imageView = (ImageView) findViewById(R.id.item_image);

        Picasso.with(getApplicationContext())
                .load(mProductProcessing.getFeaturedSrc())
                .resize(500, 500)
                .centerCrop()
                .placeholder(android.R.color.transparent)
                .error(android.R.color.transparent)
                .into(imageView);

        if(mOrderSelected.getStatus().toUpperCase().equals("COMPLETED")){
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if(mOrderSelected.getStatus().toUpperCase().equals("CANCELLED") || mOrderSelected.getStatus().toUpperCase().equals("REFUNDED")){
            header.setBackgroundColor(getResources().getColor(R.color.red));
        } else {
            header.setBackgroundColor(getResources().getColor(R.color.orange));
        }
        txtSku.setText(mProductProcessing.getSku());
        txtOrder.setText(mOrderSelected.getOrderNumber());
        txtTitle.setText("(" + mProductProcessing.getId() + ") " + mProductProcessing.getTitle());
        txtPrice.setText("$" + mProductProcessing.getPrice());
        txtStock.setText(String.valueOf(mProductProcessing.getStockQuantity()));
        txtDescription.setText(Html.fromHtml(mProductProcessing.getDescription()).toString());

        txtQuantity.setText(getString(R.string.counter_process, mCounterPerItem, mItemProcessing.getQuantity()));

    }

    private void goToNextItem(View view) {
        EditText scan = (EditText) findViewById(R.id.scan);
        scan.setText("");
        mCounterPerItem ++;
        if(mItemProcessing.getQuantity() >= mCounterPerItem) {

            if(view != null) {
                Snackbar.make(view, mProductProcessing.getTitle() + " " + getString(R.string.counter_process, mCounterPerItem, mItemProcessing.getQuantity()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            refreshDataCurrentItem();

        } else {

            mIndexProducts++;
            if(mIndexProducts < mProducts.size()) {
                mCounterPerItem = 1;
                searchItem();
                if(view != null) {
                    Snackbar.make(view, mProductProcessing.getTitle() + " " + getString(R.string.counter_process, mCounterPerItem, mItemProcessing.getQuantity()), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                refreshDataCurrentItem();
            } else {
                finish();
            }
        }
    }
}
