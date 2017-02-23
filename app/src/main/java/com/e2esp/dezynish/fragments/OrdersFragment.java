package com.e2esp.dezynish.fragments;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.actions.SearchIntents;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.e2esp.dezynish.R;
import com.e2esp.dezynish.applications.Dezynish;
import com.e2esp.dezynish.activities.MainActivity;
import com.e2esp.dezynish.activities.OrderNew;
import com.e2esp.dezynish.activities.OrderDetail;
import com.e2esp.dezynish.adapters.OrderAdapter;
import com.e2esp.dezynish.data.DezynishContract;
import com.e2esp.dezynish.models.orders.Order;
import com.e2esp.dezynish.models.orders.Orders;

/**
 * Created by Zain on 2/17/2017.
 */

public class OrdersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

    private final String LOG_TAG = OrdersFragment.class.getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";
    private OrderAdapter mAdapter;

    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;

    private static final int ORDER_LOADER = 100;
    private static final String[] ORDER_PROJECTION = {
            DezynishContract.OrdersEntry._ID,
            DezynishContract.OrdersEntry.COLUMN_ID,
            DezynishContract.OrdersEntry.COLUMN_JSON,
            DezynishContract.OrdersEntry.COLUMN_CREATED_AT
    };

    private String mQuery;
    private int mPage = 0;
    private int mSize = 50;

    public static OrdersFragment newInstance(int sectionNumber) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public OrdersFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        onNewIntent(getActivity().getIntent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(final View view) {
                int position = mRecyclerView.getChildAdapterPosition(view);
                mAdapter.getCursor().moveToPosition(position);
                int idSelected = mAdapter.getCursor().getInt(mAdapter.getCursor().getColumnIndex(DezynishContract.OrdersEntry.COLUMN_ID));

                Intent intent = new Intent(getActivity(), OrderDetail.class);
                intent.putExtra("order", idSelected);
                startActivity(intent);
            }
        };

        mAdapter = new OrderAdapter(getActivity().getApplicationContext(), R.layout.fragment_order_list_item, null, onClickListener);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.list_orders);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(ORDER_LOADER, null, this);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                getPageOrders();
            }
        });

        mSwipeLayout.setColorSchemeResources(R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light,
                R.color.holo_red_light);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                boolean enable = false;
                if (view != null && view.getChildCount() > 0) {
                    enable = view.getChildAt(0).getTop() == 0;
                }
                mSwipeLayout.setEnabled(enable);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent orderIntent = new Intent(getActivity(), OrderNew.class);
                    startActivity(orderIntent);

                }
            });
        }
        getPageOrders();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.order_fragment_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchView != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
            SearchableInfo info = searchManager.getSearchableInfo(getActivity().getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            searchView.setSearchableInfo(info);
            searchView.setOnQueryTextListener(this);
            searchView.setQueryHint(getActivity().getString(R.string.order_title_search));

            if(mQuery != null && mQuery.length() > 0) {
                searchView.setQuery(mQuery, true);
                searchView.setIconifiedByDefault(false);
                searchView.performClick();
                searchView.requestFocus();
            } else {
                searchView.setIconifiedByDefault(true);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");

        String sortOrder = DezynishContract.OrdersEntry.COLUMN_ID + " DESC";
        CursorLoader cursorLoader;
        Uri ordersUri = DezynishContract.OrdersEntry.CONTENT_URI;
        switch (id) {
            case ORDER_LOADER:
                if(mQuery != null && mQuery.length()>0) {
                    if(mQuery.toLowerCase().equals("ship")) {

                        Date now = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(now);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE,0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date startDay = calendar.getTime();

                        String query = DezynishContract.OrdersEntry.COLUMN_STATUS + " = ? AND " + DezynishContract.OrdersEntry.COLUMN_UPDATED_AT + " BETWEEN ? AND ?";
                        String[] parameters = new String[]{"completed", DezynishContract.getDbDateString(startDay), DezynishContract.getDbDateString(now)};
                        cursorLoader = new CursorLoader(
                                getActivity().getApplicationContext(),
                                ordersUri,
                                ORDER_PROJECTION,
                                query,
                                parameters,
                                sortOrder);

                        Toast.makeText(getContext(), getString(R.string.toast_search_ship, DezynishContract.getDbDateString(startDay)), Toast.LENGTH_LONG).show();

                    } else {
                        String query = DezynishContract.OrdersEntry.COLUMN_ORDER_NUMBER + " LIKE ? OR  " +
                                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME + " LIKE ? OR  " +
                                DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME + " LIKE ? OR  " +
                                DezynishContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME + " LIKE ? OR  " +
                                DezynishContract.OrdersEntry.COLUMN_BILLING_LAST_NAME + " LIKE ? OR  " +
                                DezynishContract.OrdersEntry.COLUMN_TOTAL + " LIKE ?" ;
                        String[] parameters = new String[]{
                                "%" + mQuery + "%",
                                "%" + mQuery + "%",
                                "%" + mQuery + "%",
                                "%" + mQuery + "%",
                                "%" + mQuery + "%" };
                        cursorLoader = new CursorLoader(
                                getActivity().getApplicationContext(),
                                ordersUri,
                                ORDER_PROJECTION,
                                query,
                                parameters,
                                sortOrder);
                    }
                } else {

                    Date today = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(today);
                    calendar.add(Calendar.MONTH, -1);
                    Date oneMonthsBack = calendar.getTime();

                    //String query = DezynishContract.OrdersEntry.COLUMN_ENABLE + " = ? AND " + DezynishContract.OrdersEntry.COLUMN_CREATED_AT + " BETWEEN ? AND ?";
                    //String[] parameters = new String[]{ String.valueOf("1"), startDate, endDate };
                    String query = DezynishContract.OrdersEntry.COLUMN_CREATED_AT + " BETWEEN ? AND ?";
                    String[] parameters = new String[]{DezynishContract.getDbDateString(oneMonthsBack), DezynishContract.getDbDateString(today)};
                    cursorLoader = new CursorLoader(
                            getActivity().getApplicationContext(),
                            ordersUri,
                            ORDER_PROJECTION,
                            query,
                            parameters,
                            sortOrder);
                }
                break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case ORDER_LOADER:
                if(mSwipeLayout != null){
                    mSwipeLayout.setRefreshing(false);
                }
                Log.d(LOG_TAG, "Orders " + cursor.getCount());
                mAdapter.changeCursor(cursor);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(LOG_TAG, "onLoaderReset");
        switch (cursorLoader.getId()) {
            case ORDER_LOADER:
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mQuery = query;
        doSearch();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuery = newText;
        doSearch();
        return true;
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null && (action.equals(Intent.ACTION_SEARCH) || action.equals(SearchIntents.ACTION_SEARCH))) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            mQuery = mQuery.replace(getString(R.string.order_voice_search) + " ","");
        }
    }

    private void doSearch() {
        getActivity().getSupportLoaderManager().restartLoader(ORDER_LOADER, null, this);
        getActivity().getSupportLoaderManager().getLoader(ORDER_LOADER).forceLoad();
    }

    private void getPageOrders() {
        mSwipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
            }
        }, 2000);

        Log.v(LOG_TAG,"Orders Read Total:" + mAdapter.getItemCount() + " Page : " + mPage);

        HashMap<String, String> options = new HashMap<>();
        options.put("status", "any");
        options.put("filter[limit]", String.valueOf(mSize));
        /*
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fortmated = dateFormat.format(date);
            options.put("filter[updated_at_min]", fortmated);
            Log.v(LOG_TAG, "Date limit: " + fortmated);
        }
        if(mQuery != null) {
            Log.v(LOG_TAG, "Searching: " + mQuery);
            options.put("filter[q]", mQuery);
        }
        */
        options.put("page", String.valueOf(mPage));

        // TODO:Z
        /*Call<Orders> call = ((Dezynish)getActivity().getApplication()).getWoocommerceApiHandler().getOrders(options);
        call.enqueue(new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {
                mSwipeLayout.setRefreshing(false);
                int statusCode = response.code();
                if (statusCode == 200) {
                    final List<Order> orders = response.body().getOrders();
                    Log.v(LOG_TAG,"Success Order page " + mPage + " orders " + orders.size());
                    new Thread(new Runnable() {
                        public void run() {
                            Gson gson = new Gson();
                            ArrayList<ContentValues> ordersValues = new ArrayList<>();
                            for(Order order:orders) {

                                ContentValues orderValues = new ContentValues();
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_ID, order.getId());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_ORDER_NUMBER, order.getOrderNumber());
                                if(order.getCreatedAt() != null) {
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CREATED_AT, DezynishContract.getDbDateString(order.getCreatedAt()));
                                }
                                if(order.getUpdatedAt() != null) {
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_UPDATED_AT, DezynishContract.getDbDateString(order.getUpdatedAt()));
                                }
                                if(order.getCompletedAt() != null) {
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_COMPLETED_AT, DezynishContract.getDbDateString(order.getCompletedAt()));
                                }
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_STATUS, order.getStatus());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CURRENCY, order.getCurrency());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL, order.getTotal());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SUBTOTAL, order.getSubtotal());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_LINE_ITEMS_QUANTITY, order.getTotalLineItemsQuantity());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_TAX, order.getTotalTax());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_SHIPPING, order.getTotalShipping());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CART_TAX, order.getCartTax());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_TAX, order.getShippingTax());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_DISCOUNT, order.getTotalDiscount());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CART_DISCOUNT, order.getCartDiscount());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_ORDER_DISCOUNT, order.getOrderDiscount());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_METHODS, order.getShippingMethods());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_NOTE, order.getNote());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_VIEW_ORDER_URL, order.getViewOrderUrl());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_ID, order.getPaymentDetails().getMethodId());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_TITLE, order.getPaymentDetails().getMethodTitle());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_PAID, order.getPaymentDetails().isPaid() ? "1" : "0");
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME, order.getBillingAddress().getFirstName());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_LAST_NAME , order.getBillingAddress().getLastName());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_COMPANY, order.getBillingAddress().getCompany());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_ADDRESS_1, order.getBillingAddress().getAddressOne());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_ADDRESS_2, order.getBillingAddress().getAddressTwo());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_CITY, order.getBillingAddress().getCity());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_STATE, order.getBillingAddress().getState());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_POSTCODE, order.getBillingAddress().getPostcode());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_COUNTRY, order.getBillingAddress().getCountry());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_EMAIL, order.getBillingAddress().getEmail());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_PHONE, order.getBillingAddress().getPhone());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_FIRST_NAME, order.getShippingAddress().getFirstName());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_LAST_NAME, order.getShippingAddress().getLastName());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_COMPANY, order.getShippingAddress().getCompany());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_1, order.getShippingAddress().getAddressOne());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_2, order.getShippingAddress().getAddressTwo());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_CITY, order.getShippingAddress().getCity());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_STATE, order.getShippingAddress().getState());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_POSTCODE, order.getShippingAddress().getPostcode());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_COUNTRY, order.getShippingAddress().getCountry());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_ID, order.getCustomerId());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_EMAIL, order.getCustomer().getEmail());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME, order.getCustomer().getFirstName());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME, order.getCustomer().getLastName());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_USERNAME, order.getCustomer().getUsername());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_ID, order.getCustomer().getLastOrderId());
                                if(order.getCustomer().getLastOrderDate() != null) {
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_DATE, DezynishContract.getDbDateString(order.getCustomer().getLastOrderDate()));
                                }
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_ORDERS_COUNT, order.getCustomer().getOrdersCount());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_TOTAL_SPEND, order.getCustomer().getTotalSpent());
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_AVATAR_URL, order.getCustomer().getAvatarUrl());
                                if(order.getCustomer().getBillingAddress()!= null){
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_FIRST_NAME, order.getCustomer().getBillingAddress().getFirstName());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_LAST_NAME, order.getCustomer().getBillingAddress().getLastName());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COMPANY, order.getCustomer().getBillingAddress().getCompany());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_1, order.getCustomer().getBillingAddress().getAddressOne());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_2, order.getCustomer().getBillingAddress().getAddressTwo());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_CITY, order.getCustomer().getBillingAddress().getCity());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_STATE, order.getCustomer().getBillingAddress().getState());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_POSTCODE, order.getCustomer().getBillingAddress().getPostcode());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COUNTRY, order.getCustomer().getBillingAddress().getCountry());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_EMAIL, order.getCustomer().getBillingAddress().getEmail());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_PHONE, order.getCustomer().getBillingAddress().getPhone());
                                }
                                if(order.getCustomer().getShippingAddress() != null){
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_FIRST_NAME, order.getCustomer().getShippingAddress().getFirstName());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_LAST_NAME , order.getCustomer().getShippingAddress().getLastName());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COMPANY, order.getCustomer().getShippingAddress().getCompany());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_1, order.getCustomer().getShippingAddress().getAddressOne());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_2, order.getCustomer().getShippingAddress().getAddressTwo());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_CITY, order.getCustomer().getShippingAddress().getCity());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_STATE, order.getCustomer().getShippingAddress().getState());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_POSTCODE, order.getCustomer().getShippingAddress().getPostcode());
                                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COUNTRY, order.getCustomer().getShippingAddress().getCountry());
                                }
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_JSON, gson.toJson(order));
                                orderValues.put(DezynishContract.OrdersEntry.COLUMN_ENABLE, 1);
                                ordersValues.add(orderValues);

                            }

                            if(getContext() != null) {
                                ContentValues[] ordersValuesArray = new ContentValues[ordersValues.size()];
                                ordersValuesArray = ordersValues.toArray(ordersValuesArray);
                                int ordersRowsUpdated = getContext().getContentResolver().bulkInsert(DezynishContract.OrdersEntry.CONTENT_URI, ordersValuesArray);
                                Log.v(LOG_TAG,"Orders " + ordersRowsUpdated + " updated");
                                getContext().getContentResolver().notifyChange(DezynishContract.OrdersEntry.CONTENT_URI, null, false);
                            }
                        }
                    }).start();
                    if(orders.size() == mSize) {
                        //getPageOrders();
                    }
                }
                mPage++;
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {
                Log.v(LOG_TAG, "onFailure " + mPage + " error " + t.getMessage());
                mSwipeLayout.setRefreshing(false);
            }
        });*/
    }

}