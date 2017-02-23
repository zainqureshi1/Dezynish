package com.e2esp.dezynish.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zain on 2/17/2017
 */

public class DezynishDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "dezynish.db";

    public DezynishDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_SHOP_TABLE = "CREATE TABLE " + DezynishContract.ShopEntry.TABLE_NAME + " (" +
                DezynishContract.ShopEntry._ID + " INTEGER PRIMARY KEY, " +
                DezynishContract.ShopEntry.COLUMN_NAME + " TEXT, " +
                DezynishContract.ShopEntry.COLUMN_DESCRIPTION + " TEXT, " +
                DezynishContract.ShopEntry.COLUMN_URL + " TEXT, " +
                DezynishContract.ShopEntry.COLUMN_WC_VERSION + " TEXT, " +
                DezynishContract.ShopEntry.COLUMN_META_TIMEZONE + " TEXT, " +
                DezynishContract.ShopEntry.COLUMN_META_CURRENCY + " TEXT, " +
                DezynishContract.ShopEntry.COLUMN_META_CURRENCY_FORMAT + " TEXT, " +
                DezynishContract.ShopEntry.COLUMN_META_TAXI_INCLUDE + " INTEGER DEFAULT 0 NOT NULL, " +
                DezynishContract.ShopEntry.COLUMN_META_WEIGHT_UNIT + " TEXT, " +
                DezynishContract.ShopEntry.COLUMN_META_DIMENSION_UNIT + " TEXT);";

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + DezynishContract.ProductEntry.TABLE_NAME + " (" +
                DezynishContract.ProductEntry._ID + " INTEGER PRIMARY KEY, " +
                DezynishContract.ProductEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                DezynishContract.ProductEntry.COLUMN_TITLE + " TEXT, " +
                DezynishContract.ProductEntry.COLUMN_SKU + " TEXT, " +
                DezynishContract.ProductEntry.COLUMN_PRICE + " TEXT, " +
                DezynishContract.ProductEntry.COLUMN_STOCK + " INTEGER, " +
                DezynishContract.ProductEntry.COLUMN_CATEGORIES + " STRING, " +
                DezynishContract.ProductEntry.COLUMN_JSON + " TEXT, " +
                DezynishContract.ProductEntry.COLUMN_ENABLE + " INTEGER);";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + DezynishContract.CategoryEntry.TABLE_NAME + " (" +
                DezynishContract.CategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                DezynishContract.CategoryEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                DezynishContract.CategoryEntry.COLUMN_NAME + " TEXT, " +
                DezynishContract.CategoryEntry.COLUMN_JSON + " TEXT);";

        final String SQL_CREATE_ORDER_TABLE = "CREATE TABLE " + DezynishContract.OrdersEntry.TABLE_NAME + " (" +
                DezynishContract.OrdersEntry._ID + " INTEGER PRIMARY KEY, " +
                DezynishContract.OrdersEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                DezynishContract.OrdersEntry.COLUMN_ORDER_NUMBER + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                DezynishContract.OrdersEntry.COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                DezynishContract.OrdersEntry.COLUMN_COMPLETED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                DezynishContract.OrdersEntry.COLUMN_STATUS + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CURRENCY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_TOTAL + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SUBTOTAL + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_TOTAL_LINE_ITEMS_QUANTITY + " INTEGER, " +
                DezynishContract.OrdersEntry.COLUMN_TOTAL_TAX + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_TOTAL_SHIPPING + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CART_TAX + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_TAX + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_TOTAL_DISCOUNT + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CART_DISCOUNT + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_ORDER_DISCOUNT + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_METHODS + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_NOTE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_VIEW_ORDER_URL + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_ID + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_TITLE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_PAID + " INTEGER DEFAULT 0 NOT NULL, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_LAST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_COMPANY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_ADDRESS_1 + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_ADDRESS_2 + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_CITY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_STATE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_POSTCODE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_COUNTRY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_EMAIL + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_BILLING_PHONE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_FIRST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_LAST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_COMPANY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_1 + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_2 + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_CITY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_STATE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_POSTCODE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_SHIPPING_COUNTRY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_ID + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_EMAIL + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_USERNAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_ID + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_ORDERS_COUNT + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_TOTAL_SPEND + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_AVATAR_URL + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_FIRST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_LAST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COMPANY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_1 + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_2 + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_CITY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_STATE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_POSTCODE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COUNTRY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_EMAIL + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_PHONE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_FIRST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_LAST_NAME + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COMPANY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_1 + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_2 + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_CITY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_STATE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_POSTCODE + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COUNTRY + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_JSON + " TEXT, " +
                DezynishContract.OrdersEntry.COLUMN_ENABLE + " INTEGER);";

        final String SQL_CREATE_CONSUMER_TABLE = "CREATE TABLE " + DezynishContract.CustomerEntry.TABLE_NAME + " (" +
                DezynishContract.CustomerEntry._ID + " INTEGER PRIMARY KEY, " +
                DezynishContract.CustomerEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                DezynishContract.CustomerEntry.COLUMN_EMAIL + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_FIRST_NAME + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_LAST_NAME + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_SHIPPING_FIRST_NAME + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_SHIPPING_LAST_NAME + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_SHIPPING_PHONE + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_BILLING_FIRST_NAME + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_BILLING_LAST_NAME + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_BILLING_PHONE + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_USERNAME + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_LAST_ORDER_ID + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_JSON + " TEXT, " +
                DezynishContract.CustomerEntry.COLUMN_ENABLE + " INTEGER);";

        sqLiteDatabase.execSQL(SQL_CREATE_SHOP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CONSUMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DezynishContract.ShopEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DezynishContract.ProductEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DezynishContract.CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DezynishContract.OrdersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DezynishContract.CustomerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
