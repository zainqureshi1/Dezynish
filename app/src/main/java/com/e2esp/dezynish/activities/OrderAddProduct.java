package com.e2esp.dezynish.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e2esp.dezynish.interfaces.ObjectCallbacks;
import com.e2esp.dezynish.woocommerce.WooCommerce;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.e2esp.dezynish.R;
import com.e2esp.dezynish.data.DezynishContract;
import com.e2esp.dezynish.models.customers.BillingAddress;
import com.e2esp.dezynish.models.orders.Item;
import com.e2esp.dezynish.models.orders.Order;
import com.e2esp.dezynish.models.products.Product;
import com.e2esp.dezynish.models.products.Variation;
import com.e2esp.dezynish.utilities.Utility;

import retrofit.RetrofitError;

/**
 * Created by Zain on 2/18/2017.
 */

public class OrderAddProduct extends AppCompatActivity {

    private final String LOG_TAG = OrderAddProduct.class.getSimpleName();

    private int mProductId = -1;
    private int mQuantity = 1;
    private float mPrice = 0;

    private Product mProductSelected;
    private Order mOrder;
    private Gson mGson = new GsonBuilder().create();

    private static final String[] PRODUCT_PROJECTION = {
            DezynishContract.ProductEntry.COLUMN_ID,
            DezynishContract.ProductEntry.COLUMN_JSON,
    };
    private int COLUMN_PRODUCT_COLUMN_JSON = 1;

    private Button buttonRemove;
    private Button buttonAdd;
    private Button buttonCancel;
    private Button buttonOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add_product);

        if(mProductId < 0 ) {
            mProductId = getIntent().getIntExtra("product", -1);
        }

        String query = DezynishContract.ProductEntry.COLUMN_ID + " == ?" ;
        String[] parametersOrder = new String[]{ String.valueOf(mProductId) };
        Cursor cursor = getContentResolver().query(DezynishContract.ProductEntry.CONTENT_URI,
                PRODUCT_PROJECTION,
                query,
                parametersOrder,
                null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(COLUMN_PRODUCT_COLUMN_JSON);
                    if(json!=null){
                        mProductSelected = mGson.fromJson(json, Product.class);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        String json = Utility.getPreferredShoppingCard(getApplicationContext());
        if(json != null) {
            mOrder = mGson.fromJson(json, Order.class);
        } else {
            mOrder = new Order();

            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setCompany(getString(R.string.default_company));
            billingAddress.setFirstName(getString(R.string.default_first_name));
            billingAddress.setLastName(getString(R.string.default_last_name));
            billingAddress.setAddressOne(getString(R.string.default_line_one));
            billingAddress.setAddressTwo(getString(R.string.default_line_two));
            billingAddress.setCity(getString(R.string.default_city));
            billingAddress.setState(getString(R.string.default_state));
            billingAddress.setPostcode(getString(R.string.default_postal_code));
            billingAddress.setCountry(getString(R.string.default_country));
            billingAddress.setEmail(getString(R.string.default_email));
            billingAddress.setPhone(getString(R.string.default_phone));

            mOrder.setBillingAddress(billingAddress);

            Utility.setPreferredShoppingCard(getApplicationContext(), mGson.toJson(mOrder));
        }

        buttonRemove = (Button)findViewById(R.id.less);
        buttonAdd = (Button)findViewById(R.id.more);
        buttonCancel = (Button)findViewById(R.id.cancel);
        buttonOk = (Button)findViewById(R.id.ok);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.product_updated_wip), Toast.LENGTH_LONG).show();
            }
        };

        buttonRemove.setOnClickListener(onClickListener);
        buttonAdd.setOnClickListener(onClickListener);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonOk.setOnClickListener(onClickListener);

        fillView(false);
        getProduct();
    }

    private void updateProduct() {

        mProductSelected.setStockQuantity(mProductSelected.getStockQuantity() - mQuantity);

        ArrayList<ContentValues> productsValues = new ArrayList<>();

        ContentValues productValues = new ContentValues();
        productValues.put(DezynishContract.ProductEntry.COLUMN_ID, mProductSelected.getId());
        productValues.put(DezynishContract.ProductEntry.COLUMN_TITLE, mProductSelected.getTitle());
        productValues.put(DezynishContract.ProductEntry.COLUMN_SKU, mProductSelected.getSku());
        productValues.put(DezynishContract.ProductEntry.COLUMN_PRICE, mProductSelected.getPrice());
        productValues.put(DezynishContract.ProductEntry.COLUMN_STOCK, mProductSelected.getStockQuantity());
        productValues.put(DezynishContract.ProductEntry.COLUMN_CATEGORIES, mProductSelected.getCategoryNames());
        productValues.put(DezynishContract.ProductEntry.COLUMN_JSON, mGson.toJson(mProductSelected));
        productValues.put(DezynishContract.ProductEntry.COLUMN_ENABLE, 1);

        productsValues.add(productValues);

        for(Variation variation:mProductSelected.getVariations()) {

            //TODO, CHANGE THIS APPROACH
            mProductSelected.setSku(variation.getSku());
            mProductSelected.setPrice(variation.getPrice());
            mProductSelected.setStockQuantity(variation.getStockQuantity() - mQuantity);

            ContentValues variationValues = new ContentValues();
            variationValues.put(DezynishContract.ProductEntry.COLUMN_ID, variation.getId());
            variationValues.put(DezynishContract.ProductEntry.COLUMN_TITLE, mProductSelected.getTitle());
            variationValues.put(DezynishContract.ProductEntry.COLUMN_SKU, mProductSelected.getSku());
            variationValues.put(DezynishContract.ProductEntry.COLUMN_PRICE, mProductSelected.getPrice());
            variationValues.put(DezynishContract.ProductEntry.COLUMN_STOCK, mProductSelected.getStockQuantity());
            variationValues.put(DezynishContract.ProductEntry.COLUMN_CATEGORIES, mProductSelected.getCategoryNames());
            variationValues.put(DezynishContract.ProductEntry.COLUMN_JSON, mGson.toJson(mProductSelected));
            variationValues.put(DezynishContract.ProductEntry.COLUMN_ENABLE, 1);

            productsValues.add(variationValues);

        }

        ContentValues[] productsValuesArray = new ContentValues[productsValues.size()];
        productsValuesArray = productsValues.toArray(productsValuesArray);
        int ordersRowsUpdated = getContentResolver().bulkInsert(DezynishContract.ProductEntry.CONTENT_URI, productsValuesArray);
        Log.v(LOG_TAG, "Products " + ordersRowsUpdated + " updated");

        getContentResolver().notifyChange(DezynishContract.ProductEntry.CONTENT_URI, null, false);

    }

    private void getProduct() {
        if(mProductSelected != null) {
            Log.v(LOG_TAG, "Get product " +  mProductSelected.getId());

            WooCommerce.getInstance().getProduct(mProductSelected.getId()+"", new ObjectCallbacks() {
                @Override
                public void Callback(Object object, RetrofitError error) {
                    if (error != null) {
                        Log.v(LOG_TAG, "onFailure error " + error);
                        fillView(false);
                    }
                    if (object != null) {
                        Product product = (Product) object;
                        Log.v(LOG_TAG, "onSuccess product " + product);
                        ArrayList<ContentValues> productsValues = new ArrayList<>();

                        ContentValues productValues = new ContentValues();
                        productValues.put(DezynishContract.ProductEntry.COLUMN_ID, product.getId());
                        productValues.put(DezynishContract.ProductEntry.COLUMN_TITLE, product.getTitle());
                        productValues.put(DezynishContract.ProductEntry.COLUMN_SKU, product.getSku());
                        productValues.put(DezynishContract.ProductEntry.COLUMN_PRICE, product.getPrice());
                        productValues.put(DezynishContract.ProductEntry.COLUMN_STOCK, product.getStockQuantity());
                        productValues.put(DezynishContract.ProductEntry.COLUMN_CATEGORIES, product.getCategoryNames());
                        productValues.put(DezynishContract.ProductEntry.COLUMN_JSON, mGson.toJson(product));
                        productValues.put(DezynishContract.ProductEntry.COLUMN_ENABLE, 1);

                        productsValues.add(productValues);

                        for(Variation variation:product.getVariations()) {
                            //TODO, CHANGE THIS APPROACH
                            product.setSku(variation.getSku());
                            product.setPrice(variation.getPrice());
                            product.setStockQuantity(variation.getStockQuantity());

                            ContentValues variationValues = new ContentValues();
                            variationValues.put(DezynishContract.ProductEntry.COLUMN_ID, variation.getId());
                            variationValues.put(DezynishContract.ProductEntry.COLUMN_TITLE, product.getTitle());
                            variationValues.put(DezynishContract.ProductEntry.COLUMN_SKU, product.getSku());
                            variationValues.put(DezynishContract.ProductEntry.COLUMN_PRICE, product.getPrice());
                            variationValues.put(DezynishContract.ProductEntry.COLUMN_STOCK, product.getStockQuantity());
                            variationValues.put(DezynishContract.ProductEntry.COLUMN_CATEGORIES, product.getCategoryNames());
                            variationValues.put(DezynishContract.ProductEntry.COLUMN_JSON, mGson.toJson(product));
                            variationValues.put(DezynishContract.ProductEntry.COLUMN_ENABLE, 1);

                            productsValues.add(variationValues);
                        }

                        ContentValues[] productsValuesArray = new ContentValues[productsValues.size()];
                        productsValuesArray = productsValues.toArray(productsValuesArray);
                        int ordersRowsUpdated = getContentResolver().bulkInsert(DezynishContract.ProductEntry.CONTENT_URI, productsValuesArray);
                        Log.v(LOG_TAG, "Products " + ordersRowsUpdated + " updated");

                        getContentResolver().notifyChange(DezynishContract.ProductEntry.CONTENT_URI, null, false);

                        Toast.makeText(getApplicationContext(), getString(R.string.product_updated), Toast.LENGTH_LONG).show();
                        mProductSelected = product;
                        fillView(true);
                    }
                }
            });
        }
    }

    private void fillView(boolean active) {
        LinearLayout header = (LinearLayout)findViewById(R.id.header);
        TextView sku = (TextView)findViewById(R.id.sku);
        TextView title = (TextView)findViewById(R.id.title);
        TextView price = (TextView)findViewById(R.id.price);
        EditText quantity = (EditText)findViewById(R.id.quantity);
        TextView stock = (TextView) findViewById(R.id.stock);

        ImageView image = (ImageView) findViewById(R.id.item_image_card);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mProductSelected.getPermalink()));
                startActivity(browserIntent);
            }
        });

        mPrice = Float.valueOf(mProductSelected.getPrice());

        if(mProductSelected.getStockQuantity() > 0){
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            header.setBackgroundColor(getResources().getColor(R.color.red));
        }

        sku.setText(mProductSelected.getSku());
        price.setText(getString(R.string.price, String.valueOf(mPrice)));
        title.setText(mProductSelected.getTitle());
        quantity.setText(String.valueOf(mQuantity));
        stock.setText(getString(R.string.stock, String.valueOf(mProductSelected.getStockQuantity() - mQuantity)));

        for(Item itemOder :mOrder.getItems()) {
            if(itemOder.getProductId() == mProductId) {
                quantity.setText(String.valueOf(itemOder.getQuantity()));
                mProductSelected.setStockQuantity(mProductSelected.getStockQuantity() + itemOder.getQuantity());
                break;
            }
        }

        Picasso.with(getApplicationContext())
                .load(mProductSelected.getFeaturedSrc())
                .resize(300, 300)
                .centerCrop()
                .placeholder(android.R.color.transparent)
                .error(R.drawable.ic_action_cancel)
                .into(image);

        if(active) {
            buttonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView price = (TextView)findViewById(R.id.price);
                    EditText quantity = (EditText)findViewById(R.id.quantity);
                    TextView stock = (TextView)findViewById(R.id.stock);
                    try {
                        mQuantity = Integer.valueOf(quantity.getText().toString());
                    } catch (Exception ex){
                        Log.e(LOG_TAG, "Error getting quantity");
                    }
                    if (mQuantity > 0) {
                        mQuantity--;
                        String quantityString = String.valueOf(mQuantity);
                        String priceString = getString(R.string.price, String.valueOf(mQuantity * mPrice));
                        String stockString = getString(R.string.stock, String.valueOf(mProductSelected.getStockQuantity() - mQuantity));
                        quantity.setText(quantityString);
                        price.setText(priceString);
                        stock.setText(stockString);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_add_quantity), Toast.LENGTH_LONG).show();
                    }
                }
            });

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView price = (TextView)findViewById(R.id.price);
                    TextView stock = (TextView)findViewById(R.id.stock);
                    EditText quantity = (EditText)findViewById(R.id.quantity);
                    try {
                        mQuantity = Integer.valueOf(quantity.getText().toString());
                    } catch (Exception ex){
                        Log.e(LOG_TAG, "Error getting quantity");
                    }
                    if (mQuantity < mProductSelected.getStockQuantity()) {
                        mQuantity++;
                        String quantitiyString = String.valueOf(mQuantity);
                        String priceString = getString(R.string.price, String.valueOf(mQuantity * mPrice));
                        String stockString = getString(R.string.stock, String.valueOf(mProductSelected.getStockQuantity() - mQuantity));
                        quantity.setText(quantitiyString);
                        price.setText(priceString);
                        stock.setText(stockString);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_add_stock_quantity), Toast.LENGTH_LONG).show();
                    }
                }
            });

            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText quantity = (EditText)findViewById(R.id.quantity);
                    try {
                        mQuantity = Integer.valueOf(quantity.getText().toString());
                    } catch (Exception ex){
                        Log.e(LOG_TAG, "Error getting quantity");
                    }
                    if (mQuantity <= mProductSelected.getStockQuantity()) {

                        if(mQuantity >= 0) {

                            Item item = new Item();
                            for(Item itemOder :mOrder.getItems()) {
                                if(itemOder.getProductId() == mProductId) {
                                    if(mQuantity == 0) {
                                        item.setProductId(itemOder.getId());
                                        mOrder.getItems().remove(itemOder);
                                    } else {
                                        itemOder.setQuantity(mQuantity);
                                        itemOder.setTotal(String.valueOf(mQuantity * mPrice));
                                        item = itemOder;
                                    }
                                    break;
                                }
                            }
                            if(item.getProductId() < 0 && mQuantity > 0) {
                                item.setName(mProductSelected.getTitle());
                                item.setSku(mProductSelected.getSku());
                                item.setPrice(mProductSelected.getPrice());
                                item.setTotal(String.valueOf(mQuantity * mPrice));

                                item.setProductId(mProductId);
                                item.setQuantity(mQuantity);
                                mOrder.getItems().add(item);
                            }

                            updateProduct();

                            Utility.setPreferredShoppingCard(getApplicationContext(), mGson.toJson(mOrder));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_add_quantity), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_add_stock_quantity), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

}
