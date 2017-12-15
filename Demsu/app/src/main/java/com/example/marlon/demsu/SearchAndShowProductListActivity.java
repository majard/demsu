package com.example.marlon.demsu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchAndShowProductListActivity extends AppCompatActivity {

    String query;
    double latitude, longitude;
    AdapterForProducts adapterForProducts;
    List<Product> productList;
    EditText query_field;
    Button searchButton;
    RequestQueue requestQueue;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_and_show_product_list);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
        }
        query = "";
        query_field = (EditText) findViewById(R.id.query_field);
        searchButton = (Button) findViewById(R.id.searchProductButton);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        productList = new ArrayList<>();

        fillProductListByName();

        adapterForProducts = new AdapterForProducts(productList, new AdapterForProducts.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                showProductOnMap(product);
            }
        }, latitude, longitude, getApplication());

        recyclerView.setAdapter(adapterForProducts);
        adapterForProducts.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                query = query_field.getText().toString();
                productList.clear();
                fillProductListByName();
                adapterForProducts.notifyDataSetChanged();

            }
        });
    }


    private void fillProductListByName() {

        String showUrl = "http://demsu.000webhostapp.com/Demsu/showProduct.php";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                showUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray products = response.getJSONArray("products");

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject product = products.getJSONObject(i);

                        String nome = product.getString("nome");
                        if (nome.contains(query)) {
                            int id = product.getInt("id");
                            float valor = (float) product.getDouble("valor");
                            double latitude = product.getDouble("latitude");
                            double longitude = product.getDouble("longitude");

                            Product newProduct = new Product(nome, id, valor, latitude, longitude);
                            productList.add(newProduct);
                        }
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String err;

                if (error instanceof AuthFailureError) {
                    err = "Erro AuthFailureError: " + error;
                } else if (error instanceof ServerError) {
                    err = "Erro ServerError: " + error;
                } else if (error instanceof NetworkError) {
                    err = "Erro NetworkError: " + error;
                } else if (error instanceof NoConnectionError) {
                    err = "Cannot connect to Internet" + error;
                } else if (error instanceof ParseError) {
                    err = "Erro ParseError: " + error;
                } else {
                    err = "Erro Desconhecido: " + error;
                }

                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }



    public void showProductOnMap(Product product){
        Intent i = new Intent(getApplicationContext(), ShowProductOnMapActivity.class);
        i.putExtra("latitude", product.getLatitude());
        i.putExtra("longitude", product.getLongitude());
        i.putExtra("productName", product.getName());
        i.putExtra("value", product.getValue());
        startActivity(i);

    }

}


