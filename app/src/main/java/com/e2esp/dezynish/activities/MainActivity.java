package com.e2esp.dezynish.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.e2esp.dezynish.data.DezynishContract;
import com.e2esp.dezynish.utilities.Utility;
import com.google.android.gms.actions.SearchIntents;

import com.e2esp.dezynish.R;
import com.e2esp.dezynish.fragments.NavigationDrawerFragment;
import com.e2esp.dezynish.fragments.OrdersFragment;
import com.e2esp.dezynish.fragments.ProductsFragment;
import com.e2esp.dezynish.fragments.ResumeFragment;
import com.e2esp.dezynish.interfaces.NavigationDrawerCallbacks;
import com.e2esp.dezynish.sync.DezynishSyncAdapter;

/**
 * Created by Zain on 2/17/2017.
 */

public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    private final String TAG = MainActivity.class.getName();

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class));

        DezynishSyncAdapter.initializeSyncAdapter(getApplicationContext());
        DezynishSyncAdapter.syncImmediately(getApplicationContext());

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.i(TAG, "Permission: " + permissionCheck);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.i(TAG, "Permission granted");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        10);
            }
        }

        processIntent(getIntent());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onNavigationDrawerSubItemSelected(position, null);
    }

    @Override
    public void onNavigationDrawerSubItemSelected(int position, final String subItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.d(TAG,"Position "+position);
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ResumeFragment.newInstance(position),"section")
                        .commit();
                break;
            case 1:
                final ProductsFragment productsFragment = ProductsFragment.newInstance(position);
                productsFragment.setCategory(subItem);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, productsFragment, "section")
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, OrdersFragment.newInstance(position),"section")
                        .commit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main_activity_menu, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean backPressed = false;
    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment != null && mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }
        if (backPressed) {
            super.onBackPressed();
            return;
        }
        backPressed = true;
        Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressed = false;
            }
        }, 2000);
    }

    private void processIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        //Log.i(TAG,"Process Call");
        if (action != null && (action.equals(Intent.ACTION_SEARCH) || action.equals(SearchIntents.ACTION_SEARCH))) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(query != null && query.length()>0){
                FragmentManager fragmentManager = getSupportFragmentManager();
                if(query.toLowerCase().contains(getString(R.string.product_voice_search).toLowerCase())){
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ProductsFragment.newInstance(2))
                            .commit();
                } else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, OrdersFragment.newInstance(1))
                            .commit();
                }
            }
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.app_name);
                break;
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    private void logout() {
        //Clear database
        getApplicationContext().getContentResolver().delete(DezynishContract.ShopEntry.CONTENT_URI, null, null);
        getApplicationContext().getContentResolver().delete(DezynishContract.ProductEntry.CONTENT_URI, null, null);
        getApplicationContext().getContentResolver().delete(DezynishContract.CategoryEntry.CONTENT_URI, null, null);
        getApplicationContext().getContentResolver().delete(DezynishContract.OrdersEntry.CONTENT_URI, null, null);
        getApplicationContext().getContentResolver().delete(DezynishContract.CustomerEntry.CONTENT_URI, null, null);

        //Remove Sync
        DezynishSyncAdapter.disablePeriodSync(getApplicationContext());
        DezynishSyncAdapter.removeAccount(getApplicationContext());

        //Remove Preferences
        Utility.setPreferredLastSync(getApplicationContext(), 0L);
        Utility.setPreferredShoppingCard(getApplicationContext(), null);
    }

}
