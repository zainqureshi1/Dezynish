package com.e2esp.dezynish.fragments;

import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e2esp.dezynish.R;
import com.e2esp.dezynish.adapters.DrawerAdapter;
import com.e2esp.dezynish.data.DezynishContract;
import com.e2esp.dezynish.interfaces.DrawerCallbacks;
import com.e2esp.dezynish.interfaces.ListCallbacks;
import com.e2esp.dezynish.interfaces.NavigationDrawerCallbacks;
import com.e2esp.dezynish.models.orders.DrawerItem;
import com.e2esp.dezynish.models.orders.DrawerSubItem;
import com.e2esp.dezynish.models.products.Category;
import com.e2esp.dezynish.woocommerce.WooCommerce;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zain on 2/17/2017.
 */

public class NavigationDrawerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = NavigationDrawerFragment.class.getSimpleName();

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String STATE_SELECTED_SUB_POSITION = "selected_navigation_drawer_sub_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerRecyclerView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private int mCurrentSelectedSubPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private ArrayList<DrawerItem> drawerItems;
    private DrawerAdapter mAdapter;

    private static final int SHOP_LOADER = 0;
    private static final String[] SHOP_PROJECTION = {
            DezynishContract.ShopEntry.COLUMN_NAME,
            DezynishContract.ShopEntry.COLUMN_DESCRIPTION
    };
    private int SHOP_COLUMN_NAME = 0;
    private int SHOP_COLUMN_DESCRIPTION = 1;

    private static final int CATEGORY_LOADER = 1;
    private static final String[] CATEGORY_PROJECTION = {
            DezynishContract.CategoryEntry.COLUMN_NAME
    };
    private int CATEGORY_COLUMN_NAME = 0;

    private static final String[] COUNT_PROJECTION = {
            "COUNT(*)"
    };
    private int COLUMN_COUNT = 0;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDrawerRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        drawerItems = new ArrayList<>();
        drawerItems.add(new DrawerItem(getString(R.string.title_section1), R.drawable.products, 0, null));

        mAdapter = new DrawerAdapter(getActionBar().getThemedContext(), drawerItems, new DrawerCallbacks() {
            @Override
            public void onItemSelected(int position) {
                selectItem(position);
            }
            @Override
            public void onSubItemSelected(int parentPosition, int childPosition) {
                selectSubItem(parentPosition, childPosition);
            }
        });
        mAdapter.setSelectedItemPosition(mCurrentSelectedPosition);
        mDrawerRecyclerView.setAdapter(mAdapter);
        mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        getActivity().getSupportLoaderManager().initLoader(SHOP_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);

        return mDrawerRecyclerView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void closeDrawer() {
        if (mDrawerLayout != null && mFragmentContainerView != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle( getActivity(),
                mDrawerLayout, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                Cursor cursor = getActivity().getContentResolver().query(DezynishContract.ProductEntry.CONTENT_URI,
                        COUNT_PROJECTION,
                        null,
                        null,
                        null);
                if(cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            int count = cursor.getInt(COLUMN_COUNT);
                            drawerItems.get(0).setCount(count);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                mAdapter.notifyDataSetChanged();

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mAdapter != null) {
            mAdapter.setSelectedItemPosition(mCurrentSelectedPosition);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    private void selectSubItem(int parentPosition, int childPosition) {
        mCurrentSelectedPosition = parentPosition;
        mCurrentSelectedSubPosition = childPosition;
        if (mAdapter != null) {
            mAdapter.setSelectedSubItemPosition(mCurrentSelectedPosition, mCurrentSelectedSubPosition);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            String subItem = null;
            int headersCount = mAdapter.getHeaderSectionsCount();
            if (parentPosition >= headersCount && childPosition > 0) {
                try {
                    subItem = drawerItems.get(parentPosition - headersCount).getChildList().get(childPosition).getSection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            mCallbacks.onNavigationDrawerSubItemSelected(parentPosition, subItem);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (NavigationDrawerCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.drawer_fragment_menu, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");

        CursorLoader cursorLoader;
        switch (id) {
            case SHOP_LOADER: {
                Uri shopUri = DezynishContract.ShopEntry.CONTENT_URI;
                String sortOrder = DezynishContract.ShopEntry._ID + " ASC";
                cursorLoader = new CursorLoader(
                        getActivity().getApplicationContext(),
                        shopUri,
                        SHOP_PROJECTION,
                        null,
                        null,
                        sortOrder);
                }
                break;
            case CATEGORY_LOADER: {
                Uri categoryUri = DezynishContract.CategoryEntry.CONTENT_URI;
                String sortOrder = DezynishContract.CategoryEntry._ID + " ASC";
                cursorLoader = new CursorLoader(
                        getActivity().getApplicationContext(),
                        categoryUri,
                        CATEGORY_PROJECTION,
                        null,
                        null,
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
        Log.d(LOG_TAG, "onLoadFinished");
        switch (cursorLoader.getId()) {
            case SHOP_LOADER: {
                    if (cursor.moveToFirst()) {
                        do {
                            String name = cursor.getString(SHOP_COLUMN_NAME);
                            String description = cursor.getString(SHOP_COLUMN_DESCRIPTION);
                            mAdapter.setShopDetails(name, description);
                        } while (cursor.moveToNext());
                        mAdapter.notifyDataSetChanged();
                    }
                    cursor.close();
                }
                break;
            case CATEGORY_LOADER: {
                if (cursor.moveToFirst()) {
                    ArrayList<DrawerSubItem> categories = new ArrayList<>();
                    categories.add(new DrawerSubItem("ALL", 0));
                    do {
                        String name = cursor.getString(CATEGORY_COLUMN_NAME);
                        categories.add(new DrawerSubItem(name.toUpperCase(Locale.getDefault()), 0));
                    } while (cursor.moveToNext());
                    drawerItems.get(0).addSubItems(categories);
                    mAdapter.notifyParentDataSetChanged(true);
                }
                cursor.close();
            }
            break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(LOG_TAG, "onLoaderReset");
        switch (cursorLoader.getId()) {
            case SHOP_LOADER: {
                    TextView shopName = (TextView) mDrawerRecyclerView.findViewById(R.id.name);
                    shopName.setText("");
                    TextView resume = (TextView) mDrawerRecyclerView.findViewById(R.id.resume);
                    resume.setText("");
                }
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

}
