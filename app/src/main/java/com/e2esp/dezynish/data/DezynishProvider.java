package com.e2esp.dezynish.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Zain on 2/17/2017.
 */

public class DezynishProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DezynishDbHelper mOpenHelper;

    private static final int SHOP = 100;
    private static final int SHOP_ID = 101;

    private static final int PRODUCT = 200;
    private static final int PRODUCT_ID = 201;

    private static final int CATEGORY = 300;
    private static final int CATEGORY_ID = 301;

    private static final int ORDER = 400;
    private static final int ORDER_ID = 401;

    private static final int CUSTOMER = 500;
    private static final int CUSTOMER_ID = 501;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DezynishDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "shop/#"
            case SHOP_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.ShopEntry.TABLE_NAME,
                        projection,
                        DezynishContract.ShopEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "shop"
            case SHOP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.ShopEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "product/#"
            case PRODUCT_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.ProductEntry.TABLE_NAME,
                        projection,
                        DezynishContract.ProductEntry.COLUMN_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "product"
            case PRODUCT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "category/#"
            case CATEGORY_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.CategoryEntry.TABLE_NAME,
                        projection,
                        DezynishContract.CategoryEntry.COLUMN_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "category"
            case CATEGORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "order/#"
            case ORDER_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.OrdersEntry.TABLE_NAME,
                        projection,
                        DezynishContract.OrdersEntry.COLUMN_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "order"
            case ORDER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.OrdersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "customer/#"
            case CUSTOMER_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.CustomerEntry.TABLE_NAME,
                        projection,
                        DezynishContract.CustomerEntry.COLUMN_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "customer"
            case CUSTOMER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DezynishContract.CustomerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHOP:
                return DezynishContract.ShopEntry.CONTENT_TYPE;
            case SHOP_ID:
                return DezynishContract.ShopEntry.CONTENT_ITEM_TYPE;
            case PRODUCT:
                return DezynishContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_ID:
                return DezynishContract.ProductEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return DezynishContract.CategoryEntry.CONTENT_TYPE;
            case CATEGORY_ID:
                return DezynishContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case ORDER:
                return DezynishContract.OrdersEntry.CONTENT_TYPE;
            case ORDER_ID:
                return DezynishContract.OrdersEntry.CONTENT_ITEM_TYPE;
            case CUSTOMER:
                return DezynishContract.CustomerEntry.CONTENT_TYPE;
            case CUSTOMER_ID:
                return DezynishContract.CustomerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SHOP: {
                db.beginTransaction();
                try {
                    long _id = db.insert(DezynishContract.ShopEntry.TABLE_NAME, null, contentValues);
                    if ( _id > 0 )
                        returnUri = DezynishContract.ShopEntry.buildShopUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case PRODUCT: {
                db.beginTransaction();
                try {
                    long _id = db.insertWithOnConflict(DezynishContract.ProductEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if ( _id > 0 )
                        returnUri = DezynishContract.ProductEntry.buildOrderUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case CATEGORY: {
                db.beginTransaction();
                try {
                    long _id = db.insertWithOnConflict(DezynishContract.CategoryEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if ( _id > 0 )
                        returnUri = DezynishContract.CategoryEntry.buildOrderUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case ORDER: {
                db.beginTransaction();
                try {
                    long _id = db.insertWithOnConflict(DezynishContract.OrdersEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if ( _id > 0 )
                        returnUri = DezynishContract.OrdersEntry.buildOrderUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case CUSTOMER: {
                db.beginTransaction();
                try {
                    long _id = db.insertWithOnConflict(DezynishContract.CustomerEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if ( _id > 0 )
                        returnUri = DezynishContract.CustomerEntry.buildOrderUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null, false);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case SHOP:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            DezynishContract.ShopEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case PRODUCT:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            DezynishContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CATEGORY:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            DezynishContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case ORDER:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            DezynishContract.OrdersEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CUSTOMER:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            DezynishContract.CustomerEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null, false);
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case SHOP:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(DezynishContract.ShopEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case PRODUCT:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(DezynishContract.ProductEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CATEGORY:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(DezynishContract.CategoryEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case ORDER:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(DezynishContract.OrdersEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CUSTOMER:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(DezynishContract.CustomerEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null, false);
        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DezynishContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DezynishContract.PATH_SHOP, SHOP);
        matcher.addURI(authority, DezynishContract.PATH_SHOP + "/#", SHOP_ID);

        matcher.addURI(authority, DezynishContract.PATH_PRODUCT, PRODUCT);
        matcher.addURI(authority, DezynishContract.PATH_PRODUCT + "/#", PRODUCT_ID);

        matcher.addURI(authority, DezynishContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, DezynishContract.PATH_CATEGORY + "/#", CATEGORY_ID);

        matcher.addURI(authority, DezynishContract.PATH_ORDER, ORDER);
        matcher.addURI(authority, DezynishContract.PATH_ORDER + "/#", ORDER_ID);

        matcher.addURI(authority, DezynishContract.PATH_CUSTOMER, CUSTOMER);
        matcher.addURI(authority, DezynishContract.PATH_CUSTOMER + "/#", CUSTOMER_ID);

        return matcher;
    }

}
