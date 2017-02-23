package com.e2esp.dezynish.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e2esp.dezynish.R;
import com.e2esp.dezynish.expandablerecyclerview.ChildViewHolder;
import com.e2esp.dezynish.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.e2esp.dezynish.expandablerecyclerview.ParentViewHolder;
import com.e2esp.dezynish.interfaces.DrawerCallbacks;
import com.e2esp.dezynish.models.orders.DrawerItem;
import com.e2esp.dezynish.models.orders.DrawerSubItem;

import java.util.ArrayList;

/**
 * Created by Zain on 2/17/2017.
 */

public class DrawerAdapter extends ExpandableRecyclerAdapter<DrawerItem, DrawerSubItem, DrawerAdapter.DrawerItemVH, DrawerAdapter.DrawerSubItemVH> {

    private final int VIEW_TYPE_HEADER = 1001;
    private final int HEADER_SECTIONS = 1;

    private final ArrayList<DrawerItem> drawerItems;
    private final DrawerCallbacks drawerCallbacks;

    private final LayoutInflater layoutInflater;

    private String shopName;
    private String shopDescription;

    private int colorSelected;

    public DrawerAdapter(Context context, ArrayList<DrawerItem> drawerItems, DrawerCallbacks drawerCallbacks) {
        super(drawerItems);
        this.drawerItems = drawerItems;
        this.drawerCallbacks = drawerCallbacks;
        this.layoutInflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.colorSelected = context.getResources().getColor(R.color.colorPrimaryTransparent);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return super.getItemViewType(position - HEADER_SECTIONS);
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount() + HEADER_SECTIONS;
        return itemCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = layoutInflater.inflate(R.layout.fragment_navigation_drawer_header, parent, false);
            return new DrawerHeaderVH(headerView);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @NonNull
    @Override
    public DrawerItemVH onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.navigation_drawer_item, parentViewGroup, false);
        return new DrawerItemVH(itemView);
    }

    @NonNull
    @Override
    public DrawerSubItemVH onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View subItemView = layoutInflater.inflate(R.layout.navigation_drawer_sub_item, childViewGroup, false);
        return new DrawerSubItemVH(subItemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DrawerHeaderVH) {
            ((DrawerHeaderVH) holder).bind(position);
        } else {
            super.onBindViewHolder(holder, position - HEADER_SECTIONS);
        }
    }

    @Override
    public void onBindParentViewHolder(@NonNull DrawerItemVH parentViewHolder, int parentPosition, @NonNull DrawerItem parent) {
        parentViewHolder.bind(parentPosition);
    }

    @Override
    public void onBindChildViewHolder(@NonNull DrawerSubItemVH childViewHolder, int parentPosition, int childPosition, @NonNull DrawerSubItem child) {
        childViewHolder.bind(parentPosition, childPosition);
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        //this.selectedItemPosition = selectedItemPosition;
    }

    public void setSelectedSubItemPosition(int selectedItemPosition, int selectedSubItemPosition) {
        //this.selectedItemPosition = selectedItemPosition;
        //this.selectedSubItemPosition = selectedSubItemPosition;
    }

    public void setShopDetails(String shopName, String shopDescription) {
        this.shopName = shopName;
        this.shopDescription = shopDescription;
    }

    public int getHeaderSectionsCount() {
        return HEADER_SECTIONS;
    }

    private class DrawerHeaderVH extends RecyclerView.ViewHolder {
        private View topView;
        private TextView textViewName;
        private TextView textViewDescription;
        public DrawerHeaderVH(View headerView) {
            super(headerView);
            topView = headerView;
            textViewName = (TextView) headerView.findViewById(R.id.name);
            textViewDescription = (TextView) headerView.findViewById(R.id.resume);
        }
        public void bind(final int position) {
            if (shopName != null) {
                textViewName.setText(shopName);
            }
            if (shopDescription != null) {
                textViewDescription.setText(shopDescription);
            }
            topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerCallbacks.onItemSelected(position);
                }
            });
        }
    }

    class DrawerItemVH extends ParentViewHolder<DrawerItem, DrawerSubItem> {
        private View topView;
        private TextView textView;
        private TextView countView;
        private ImageView imageView;
        public DrawerItemVH(View itemView) {
            super(itemView);
            topView = itemView;
            textView = (TextView) itemView.findViewById(R.id.label);
            countView = (TextView) itemView.findViewById(R.id.count);
            imageView = (ImageView) itemView.findViewById(R.id.icon);
        }

        @Override
        public int getPositionFromAdapter() {
            return getAdapterPosition() - HEADER_SECTIONS;
        }

        public void bind(final int position) {
            DrawerItem drawerItem = drawerItems.get(position);
            textView.setText(drawerItem.getSection());
            if(drawerItem.getCount() > -1){
                countView.setText(String.valueOf(drawerItem.getCount()));
            }
            imageView.setImageResource(drawerItem.getIcon());
            if (isExpanded()) {
                topView.setBackgroundColor(colorSelected);
            } else {
                topView.setBackgroundColor(Color.TRANSPARENT);
            }
            /*topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerCallbacks.onItemSelected(position + HEADER_SECTIONS);
                }
            });*/
        }
    }

    class DrawerSubItemVH extends ChildViewHolder<DrawerSubItem> {
        private View topView;
        private TextView textView;
        private TextView countView;
        public DrawerSubItemVH(View itemView) {
            super(itemView);
            topView = itemView;
            textView = (TextView) itemView.findViewById(R.id.label);
            countView = (TextView) itemView.findViewById(R.id.count);
        }
        public void bind(final int parentPosition, final int childPosition) {
            DrawerSubItem drawerSubItem = drawerItems.get(parentPosition).getChildList().get(childPosition);
            textView.setText(drawerSubItem.getSection());
            if(drawerSubItem.getCount() > -1){
                countView.setText(String.valueOf(drawerSubItem.getCount()));
            }
            topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerCallbacks.onSubItemSelected(parentPosition + HEADER_SECTIONS, childPosition);
                }
            });
        }
    }

}
