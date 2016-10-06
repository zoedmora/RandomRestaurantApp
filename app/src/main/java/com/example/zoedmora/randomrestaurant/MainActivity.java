package com.example.zoedmora.randomrestaurant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myButton = (Button) findViewById(R.id.findButton);

        myButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                //This is to avoid Error from Output Streams
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                String consumerKey = "ILXyuZ1tUfXNEXmiko5eBg";
                String consumerSecret = "8FwC9cN6tB4NOMfPfMkF8d4w9M0";
                String token = "L_qovPrpu3UMHjspXgvKrcIiMjM69_l0";
                String tokenSecret = "aUMm51QO2J1g5ohk5FeQc0nB7Hc";
                String show = "Clicked It";                             //for testing only
                int totalCount = 0;                                     //count of restaurants from search
                ArrayList<Business> business = null;                    //List of all businesses returned from searhc
                Random random = new Random();                           //used to create a random number
                int rNumber = (Math.abs(random.nextInt()) % 20);        //the random number used to pick from the list of businesses

                //Signing in to Yelp API
                YelpAPIFactory apiFactory = new YelpAPIFactory(consumerKey, consumerSecret, token, tokenSecret);
                YelpAPI yelpAPI = apiFactory.createAPI();

                //Parameters for Yelp Search
                Map<String, String> params = new HashMap<>();
                params.put("term", "restaurants");
                params.put("radius_filter", "2000");
                params.put("limit", "20");


                //Coordinates for Yelp Search
                CoordinateOptions coordinate = CoordinateOptions.builder().latitude(37.7577).longitude(-122.4376).build();

                //Building the Yelp Search
                Call<SearchResponse> call = yelpAPI.search(coordinate, params);
                //Call<SearchResponse> call = yelpAPI.search("San Francisco",params);

                //Excecuting Yelp Search
                try {
                    Response<SearchResponse> response = call.execute();
                    SearchResponse restaurant = response.body();

                    totalCount = restaurant.total();        //total count of restaurants returned from Search
                    business = restaurant.businesses();     //get the list of all Businesses

                } catch (Exception e) {
                    show = e.toString();
                    //show = "Error Fool!";

                }

                //Putting text information onto Screen
                TextView myTextView = (TextView) findViewById(R.id.myTextView);
                if (business != null) {
                    myTextView.setText(business.get(rNumber).name() + " " + business.get(rNumber).rating() + " listing " + rNumber);
                } else {
                    myTextView.setText(show + " " + totalCount);
                }


                //Putting the picture of the random restaurant onto Screen
                //Yelp returns a picture via URL therefore we must decode it using BitmapFactory
                ImageView iv = (ImageView) findViewById(R.id.photo);
                try {
                    URL yelpPicture = new URL(business.get(rNumber).imageUrl());
                    Bitmap urlStream = BitmapFactory.decodeStream(yelpPicture.openConnection().getInputStream());
                    iv.setImageBitmap(urlStream);
                }
                catch (Exception e) {

                }



                //Getting the address of the random restaurant
                /*String*/ address = business.get(rNumber).location().address().get(0);
                address += business.get(rNumber).location().city();
                address += business.get(rNumber).location().stateCode();


/*
                //This is so that the google app opens up
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + address );
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
*/


            }
        });

        ImageView myImage = (ImageView) findViewById(R.id.photo);
        myImage.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                //This is so that the google map app opens up
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + address );
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }


        });

    }
}
