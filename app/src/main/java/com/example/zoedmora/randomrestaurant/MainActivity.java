package com.example.zoedmora.randomrestaurant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
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


public class MainActivity extends AppCompatActivity implements LocationListener {

    public final static String EXTRA_MESSAGE = "com.example.zoedmora.randomrestaurant.MESSAGE";
    private static final String MYTAG = "MYTAG";
    private LatLng latLng;
    private double latitude, longitude;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Finds users location
        askPermissionsAndShowMyLocation();

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

                    CoordinateOptions coordinate = CoordinateOptions.builder().latitude(latitude).longitude(longitude).build();
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

    private void askPermissionsAndShowMyLocation(){
        //With API>=23, you have to ask the user for permission to view their location.
        if(Build.VERSION.SDK_INT >= 23){
            int accessCoarsePermisiion
                    = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermisiion
                    = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

            if(accessCoarsePermisiion != PackageManager.PERMISSION_GRANTED
                    || accessFinePermisiion !=PackageManager.PERMISSION_GRANTED){

                //The Permissions to ask user.
                String[] permissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION};
                //Show a dialog askign the user to allow the above permissions.
                ActivityCompat.requestPermissions(this, permissions, REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
                return;
            }
        }
        this.showMyLocation();
    }

    private String getEnabledLocationProvider(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Criteria to find location provider.
        Criteria criteria = new Criteria();

        // Returns the name of the provider that best meets the given criteria.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled((bestProvider));

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }

        return bestProvider;
    }


    // Call this method only when you have the permissions to view a user's location.
    private void showMyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = this.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation = null;
        try {
            // This code need permissions (Asked above ***)
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
            // Getting location.
            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);
        }
        // With Android API >= 23, need to catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (myLocation != null) {
            latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            latitude = myLocation.getLatitude();
            longitude = myLocation.getLongitude();

            //Writes latitude and longitude to a SharedPrefrence
            setDefaults("Latitude", myLocation.getLatitude(), getApplicationContext());
            setDefaults("Longitude", myLocation.getLongitude(), getApplicationContext());

            //Lets the user know that their location was found
            Toast.makeText(this, "Location found!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location found");


        } else {
            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location not found");
        }


    }

    /***
     * Sets the Shared preferences
     * @param key   The key name to determine it.
     * @param value The Value that is being saved
     * @param context   The context whether it is gonna be private or not, etc
     */
    public static void setDefaults(String key, Double value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.commit();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
