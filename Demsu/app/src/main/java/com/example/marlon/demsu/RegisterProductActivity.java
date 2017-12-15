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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
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

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegisterProductActivity extends FragmentActivity  implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public Circle circle;
    public double latitude,longitude;

    EditText name, value;
    Button btn_register, show_product_list_btn;

    RequestQueue requestQueue;
    String insertUrl = "http://demsu.000webhostapp.com/Demsu/insertProduct.php";

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_product);

        System.setProperty("http.keepAlive", "false");



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        callMap();

        latitude = 0;
        longitude = 0;

        name = (EditText) findViewById(R.id.text_Product_Name);
        value = (EditText) findViewById(R.id.text_Product_value);
        btn_register = (Button) findViewById(R.id.register_product_button);
        show_product_list_btn = (Button) findViewById(R.id.show_product_list_button);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(name.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(value.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Value cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(getApplicationContext(), "Sent!", Toast.LENGTH_SHORT).show();
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
                        parameters.put("nome", name.getText().toString());
                        parameters.put("valor", value.getText().toString());
                        parameters.put("latitude", Double.toString(getLatitude()));
                        parameters.put("longitude",Double.toString(getLongitude()));

                        return parameters;
                    }
                };
                requestQueue.add(request);
            }

        });

        show_product_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), SearchAndShowProductListActivity.class);
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                startActivity(i);

            }
        });




     }
    public void callMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(circle == null) {
            circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(100).fillColor(Color.BLUE));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));

        }else{
            circle.remove();
            circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(100)
                    .fillColor(Color.BLUE));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),12));
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
        Log.i("LOG", latitude+" -- "+longitude);
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }
}
