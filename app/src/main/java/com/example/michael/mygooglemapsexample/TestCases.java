package com.example.michael.mygooglemapsexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class TestCases extends AppCompatActivity {

    //creates a google maps activity object
    MapsActivity mapsActivity = new MapsActivity();

    //string that holds the address for the address search scenario
    private String address = "3131 Stone Valley Road, Danville, California";
    //string that holds the search address extra
    protected static final String SEARCH = "search_address";
    protected static final String ADDRESSIN = "3131 Stone Valley Road, Danville, California";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_cases);

    }

    //opens the search an address scenario
    public void searchAddressButton(View view)
    {
        //creates a maps intent
        Intent mapsIntent = new Intent(this, MapsActivity.class);
        //puts a string into the intent
        mapsIntent.putExtra(SEARCH, "search_address");
        mapsIntent.putExtra(ADDRESSIN, address);
        //starts the activity
        startActivity(mapsIntent);

        //calls the Maps Activity method to search an address
        //mapsActivity.searchAddress(address);
    }

}
