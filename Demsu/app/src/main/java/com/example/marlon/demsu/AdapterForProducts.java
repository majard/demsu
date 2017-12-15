package com.example.marlon.demsu;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static java.lang.Math.round;

/**
 * Created by Marlon on 18/06/2017.
 */

public class AdapterForProducts extends RecyclerView.Adapter {


    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private List<Product> productList;
    private double currentLatitude, currentLongitude;
    private Context context;
    private final OnItemClickListener listener;

    public AdapterForProducts(List<Product> productList, OnItemClickListener listener, double currentLatitude, double longitude, Context context){
        this.currentLatitude = currentLatitude;
        this.currentLongitude = longitude;
        this.productList = productList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        ViewHolderForProducts holder = new ViewHolderForProducts(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        ViewHolderForProducts holder = (ViewHolderForProducts) viewHolder;

        Product product = productList.get(position);

        Log.i("LOG", currentLatitude+" -- "+currentLongitude + "\nProduct: "
                + product.getLatitude() + " -- " + product.getLongitude());
        holder.bind(productList.get(position), listener, currentLatitude, currentLongitude);


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }



}
