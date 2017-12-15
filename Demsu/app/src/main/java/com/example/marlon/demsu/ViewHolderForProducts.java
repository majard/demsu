package com.example.marlon.demsu;

import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.round;

/**
 * Created by Marlon on 18/06/2017.
 */

public class ViewHolderForProducts extends RecyclerView.ViewHolder{
    CardView cardView;
    TextView name;
    TextView value;
    TextView distance;


    public ViewHolderForProducts(View view){
        super(view);
        cardView = (CardView) itemView.findViewById(R.id.cardView);
        name = (TextView) view.findViewById(R.id.product_name);
        value = (TextView) view.findViewById(R.id.product_value);
        distance = (TextView) view.findViewById(R.id.product_distance);
    }



    public void bind(final Product product, final AdapterForProducts.OnItemClickListener listener,
                     double currentLatitude, double currentLongitude) {


        name.setText(product.getName());
        value.setText("Preço: " + product.getValue());

        Location locationA = new Location("Current Location");
        locationA.setLatitude(currentLatitude);
        locationA.setLongitude(currentLongitude);
        Location locationB = new Location("Product Location");
        locationB.setLatitude(product.getLatitude());
        locationB.setLongitude(product.getLongitude());

        String distanceText = "Distance: " + round(locationA.distanceTo(locationB)) + " meters";

        distance.setText(distanceText);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(product);
            }
        });

    }
}
