package com.example.michael.mygooglemapsexample;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //holds the request fine access code
    private final int REQUEST_FINE_ACCESS_CODE = 432092;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checks to see if the manifest has the locaiton permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request fine location permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_ACCESS_CODE);
        } else {
            //creates an intent and launches the test cases
            Intent testCasesIntent = new Intent(this, TestCases.class);
            startActivity(testCasesIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_ACCESS_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //creates an intent and launches the test cases
                    Intent testCasesIntent = new Intent(this, TestCases.class);
                    startActivity(testCasesIntent);

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Cannot continue because this is a google maps application and it needs to know your location!", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
