package com.example.michael.mygooglemapsexample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "aa";

    //creates a google maps object
    private static GoogleMap mMap;
    //creates a location manager
    protected LocationManager locationManager = null;
    //creates a location
    protected Location location = null;

    private LatLng userLatLng = null;
    private LatLng desinationLatLng = null;
    private static DrawPolyline drawPolyline = null;

    //message to hold which method the intent comes from
    String message = null;
    //intent that holds intent info from the intent that calls this activity
    Intent infoIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //gets the user's location
        getLocation();

        //gets the intent info from the method that launches this
        infoIntent = getIntent();
        message = infoIntent.getStringExtra(TestCases.SEARCH);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady called");

        mMap = googleMap;
        //enables user location
        mMap.setMyLocationEnabled(true);

        //test to see if the intent is the search address intent
        if(message.equals("search_address"))
        {
            //calls the search address method
            try {
                searchAddress(infoIntent.getStringExtra(TestCases.ADDRESSIN));
                Log.i(TAG, "call to search address");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.i(TAG, "could not call search address");
        }
    }

    //searches an address the user enters
    public void searchAddress(String address) throws IOException, JSONException {
        Log.i(TAG, "search address called");
        //creates a latlng object with the user's lat lng
        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        //gets the address's latlng
        desinationLatLng = addressToLngLat(address);

        //moves the camera to the correct location
        if(mMap != null) {
            Log.i(TAG, "map is not null");
            //zooms to the correct user location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));

            //addressToLngLat(address);
            //if(desinationLatLng != null) {
            //adds a marker at the searched location
            mMap.addMarker(new MarkerOptions().position(desinationLatLng).title("Destination")).showInfoWindow();

            Log.i(TAG, "about to make ppolyLineOptions");
            //creates a drawPolyline object
            drawPolyline = new DrawPolyline(userLatLng, desinationLatLng);
            Log.i(TAG, "DrawPolylineObject created");

            //get a url string
            String url = drawPolyline.createUrl();
            Log.i(TAG, "url string created");

            //gets the json data from the url
            drawPolyline.downloadUrl(url);
            Log.i(TAG, "method downloaded data");

            //THIS WORKS FOR A STRAIGHT LINE--------------------------------------------------------------
            //creates a polyLine options object and adds the paramters I want
            //PolylineOptions polylineOptions = new PolylineOptions();
            //polylineOptions.add(userLatLng).add(desinationLatLng).color(Color.BLUE).geodesic(true);
            //adds the polyLine to the map
            //mMap.addPolyline(polylineOptions);
            //--------------------------------------------------------------------------------------------
        }else{
            Log.i(TAG, "mMap is equal to null");
        }
    }

    public void finishDrawingPolyline() throws JSONException {
        //EXECUTES BEFORE THE DATA IS FINISHED DOWNLOADING AND THE VARIABLES ARE SET
        String data = drawPolyline.getJSONData();
        Log.i(TAG, "json file downloaded and received.  Data received : " + data);

        //gets the polyline options
        PolylineOptions polylineOptions = drawPolyline.getPolylineOptions(data);
        Log.i(TAG, "PolyLineOptions are : " + polylineOptions.toString());

        //draws it on the map
        mMap.addPolyline(polylineOptions);
        Log.i(TAG, "polLineOptions added");
    }

    //gets the user's location
    public void getLocation()
    {
        //sets up the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //checks to make sure there is the correct locations
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //gets the location
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    //turns an address into a LatLng
    public LatLng addressToLngLat(String address) throws IOException {
        LatLng addressLatLng = null;
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addresses = null;
        addresses = geocoder.getFromLocationName(address, 1);
        if(addresses.size() > 0) {
            double latitude= addresses.get(0).getLatitude();
            double longitude= addresses.get(0).getLongitude();
            addressLatLng = new LatLng(latitude, longitude);
        }

        return addressLatLng;
    }
}