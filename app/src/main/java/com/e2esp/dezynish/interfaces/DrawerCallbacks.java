package com.e2esp.dezynish.interfaces;

/**
 * Created by Zain on 2/17/2017.
 */

public interface DrawerCallbacks {
    void onItemSelected(int position);
    void onSubItemSelected(int parentPosition, int childPosition);
}
