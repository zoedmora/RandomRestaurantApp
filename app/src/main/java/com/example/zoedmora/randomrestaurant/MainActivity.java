package com.example.zoedmora.randomrestaurant;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myButton = (Button) findViewById(R.id.findButton);

        myButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v){

                    String consumerKey = "ILXyuZ1tUfXNEXmiko5eBg";
                    String consumerSecret = "8FwC9cN6tB4NOMfPfMkF8d4w9M0";
                    String token = "L_qovPrpu3UMHjspXgvKrcIiMjM69_l0";
                    String tokenSecret = "aUMm51QO2J1g5ohk5FeQc0nB7Hc";
                    String show = "Clicked It";
                    int totalCount = 0;
                    ArrayList<Business> business = null;
                    Random random = new Random();


                    int rNumber = (Math.abs(random.nextInt()) % 20);

                    YelpAPIFactory apiFactory = new YelpAPIFactory(consumerKey, consumerSecret, token, tokenSecret);
                    YelpAPI yelpAPI = apiFactory.createAPI();

                    Map<String, String> params = new HashMap<>();
                    params.put("term", "restaurants");
                    params.put("radius_filter", "2000");
                    params.put("limit", "20");


                    //This is to avoid Error
                    StrictMode.ThreadPolicy policy = new
                            StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    CoordinateOptions coordinate = CoordinateOptions.builder().latitude(37.7577).longitude(-122.4376).build();
                    Call<SearchResponse> call = yelpAPI.search(coordinate,params);
                    //Call<SearchResponse> call = yelpAPI.search("San Francisco",params);

                    try {
                        Response<SearchResponse> response = call.execute();
                        SearchResponse restaurant = response.body();

                        totalCount = restaurant.total();
                        business = restaurant.businesses();


                    }
                    catch( Exception e) {
                        show = e.toString();
                        //show = "Error Fool!";

                    }




                    TextView myTextView = (TextView)findViewById(R.id.myTextView);
                    if(business != null) {
                        myTextView.setText(business.get(rNumber).name() + " " + business.get(rNumber).rating() + " listing " + rNumber );
                    }
                    else{
                        myTextView.setText(show + " " + totalCount);
                    }




                }
        });



    }



}
