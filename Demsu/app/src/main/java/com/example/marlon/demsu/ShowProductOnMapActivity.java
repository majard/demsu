package com.example.marlon.demsu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class ShowProductOnMapActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String productName;
    private float productValue;
    LatLng productLocation;

    public Circle circle;

    private double latitude, longitude, productLatitude, productLongitude;
    Button startSearchButton, registerProductButton, like_button;
    TextView result;
    String insertUrl = "http://demsu.000webhostapp.com/Demsu/insertProduct.php";
    RequestQueue requestQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        startSearchButton = (Button) findViewById(R.id.startSearchProductActivityButton);
        registerProductButton = (Button) findViewById(R.id.RegisterProductButton);
        like_button = (Button) findViewById(R.id.like_product_button);
        result = (TextView) findViewById(R.id.product_info);


        result.setText("");

        requestQueue = Volley.newRequestQueue(getApplicationContext());




        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            productName = extras.getString("productName");
            productValue = extras.getFloat("value");
            productLatitude = extras.getDouble("latitude");
            productLongitude = extras.getDouble("longitude");
            productLocation = new LatLng(productLatitude, productLongitude);
            result.append(productName + "\nPrice: "+ productValue);
        }

        startSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchAndShowProductListActivity.class);

                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                startActivity(i);
            }
        });

        registerProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterProductActivity.class);
                startActivity(i);
            }
        });

        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeProduct();
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        callMap();

    }

    public void likeProduct(){

        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Toast.makeText(getApplicationContext(), "Liked!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                String err;

                if (error instanceof AuthFailureError) {
                    err = "Erro AuthFailureError: " + error;
                } else if (error instanceof ServerError) {
                    err = "Erro ServerError: " + error;
                } else if (error instanceof NetworkError) {
                    err = "Erro NetworkError: " + error;
                } else if (error instanceof NoConnectionError) {
                    err = "Cannot connect to Internet" + error;
                }else if (error instanceof ParseError) {
                    err = "Erro ParseError: " + error;
                }
                else
                {
                    err = "Erro Desconhecido: " + error;
                }

                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parameters  = new HashMap<String, String>();
                parameters.put("nome", productName);
                parameters.put("valor", Float.toString(productValue));
                parameters.put("latitude", Double.toString(latitude));
                parameters.put("longitude", Double.toString(longitude));

                return parameters;
            }
        };
        requestQueue.add(request);
    }


    public void callMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (productLocation != null) {
            googleMap.addMarker(new MarkerOptions().position(productLocation)
                    .title(productName));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(productLocation, 12));
        }

        if(circle==null) {
            circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(100).strokeWidth(2)
                    .strokeColor(Color.BLUE).fillColor(Color.TRANSPARENT));
        }else{
            circle.remove();
            circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(100).strokeWidth(2)
                    .strokeColor(Color.BLUE).fillColor(Color.TRANSPARENT));
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},2);

        }
        else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


            }
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude=location.getLatitude();
        longitude=location.getLongitude();

    }
    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }
}