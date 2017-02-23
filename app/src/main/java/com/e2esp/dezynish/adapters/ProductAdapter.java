package com.e2esp.dezynish.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import com.e2esp.dezynish.R;
import com.e2esp.dezynish.data.DezynishContract;
import com.e2esp.dezynish.models.products.Product;

/**
 * Created by Zain on 2/18/2017.
 */

public class ProductAdapter extends CursorRecyclerViewAdapter<ProductAdapter.ViewHolder>  {
    private static final String LOG_TAG = ProductAdapter.class.getName();

    private Context mContext;
    private int mLayoutResourceId;
    private View.OnClickListener mOnClickListener;
    private Gson mGson = new GsonBuilder().create();

    public ProductAdapter(Context context, int layoutResourceId, Cursor cursor, View.OnClickListener onClickListener){
        super(context,cursor);
        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mOnClickListener = onClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView txtName;
        private TextView txtPrice;
        private TextView txtStock;
        private TextView txtSku;
        private TextView txtDescription;

        public ViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.name);
            imageView = (ImageView) view.findViewById(R.id.image);
            txtPrice = (TextView) view.findViewById(R.id.price);
            txtStock = (TextView) view.findViewById(R.id.stock);
            txtSku = (TextView) view.findViewById(R.id.sku);
            txtDescription = (TextView) view.findViewById(R.id.description);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutResourceId, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        //int id = cursor.getInt(cursor.getColumnIndexOrThrow(DezynishContract.ProductEntry.COLUMN_ID));
        String json = cursor.getString(cursor.getColumnIndexOrThrow(DezynishContract.ProductEntry.COLUMN_JSON));
        if(json!=null) {
            Product product = mGson.fromJson(json, Product.class);

            if(product.getStockQuantity() > 0){
                holder.txtName.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                holder.txtStock.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.txtName.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                holder.txtStock.setTextColor(mContext.getResources().getColor(R.color.red));
            }

            if(product.getFeaturedSrc() != null && product.getFeaturedSrc().length() > 0) {
                Log.d(LOG_TAG, "Image: " + product.getFeaturedSrc());
                Picasso.with(mContext)
                        .load(product.getFeaturedSrc())
                        .resize(300, 300)
                        .centerCrop()
                        .placeholder(android.R.color.transparent)
                        .error(R.drawable.ic_action_cancel)
                        .into(holder.imageView);
            }

            holder.txtName.setText(product.getTitle());
            holder.txtPrice.setText(mContext.getString(R.string.price, product.getPrice()));
            holder.txtStock.setText(mContext.getString(R.string.stock, String.valueOf(product.getStockQuantity())));
            holder.txtSku.setText(product.getSku());

            String description = product.getShortDescription().replaceAll("\\<.*?>","");
            description = description.replaceAll("[\\t\\n\\r]"," ");
            description = description.replaceAll("&nbsp;"," ");

            holder.txtDescription.setText(description);
        }
    }

}
