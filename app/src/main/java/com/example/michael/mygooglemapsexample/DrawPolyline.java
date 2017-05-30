package com.example.michael.mygooglemapsexample;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 12/13/2015.
 */
public class DrawPolyline {

    private static final String TAG = "aa";
    private String JSONData = "";

    //creates the latlngs
    private LatLng userLatLng = null;
    private LatLng destinationLatLng = null;

    //creates a MapsActivity Object
    private MapsActivity mapsActivity = new MapsActivity();

    //creates a constructor with no parameters
    public DrawPolyline() {}

    //constructor to set userlatlng and destintationlatlng
    public DrawPolyline(LatLng start, LatLng finish)
    {
        userLatLng = start;
        destinationLatLng = finish;
    }

    //creates the polyLine options
    public PolylineOptions getPolylineOptions(String jsonData) throws JSONException {
        //creates JSON object
        JSONObject json = new JSONObject(jsonData);
        Log.i(TAG, "JSONObject : "+json);

        //creates routeArray
        JSONArray routeArray = json.getJSONArray("routes");
        Log.i(TAG, "routeArray : "+routeArray);

        //creates a route JSON object
        JSONObject routes = routeArray.getJSONObject(0);
        Log.i(TAG, "JSON route object : "+routes);

        //creates a polyline object for the polyline in the route
        JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
        Log.i(TAG, "overviewPolylines : "+overviewPolylines);

        //holds the encoded string called points
        String encodedString = overviewPolylines.getString("points");
        Log.i(TAG, "encoded string : "+encodedString);

        List<LatLng> list = decodePoly(encodedString);

        //creates a polyline options object
        PolylineOptions pOptions = new PolylineOptions();
        //adds the two latlng to the pOptions
        for(int i = 0; i < list.size(); i++)
        {
            LatLng point = list.get(i);
            pOptions.add(point);
        }
        //pOptions.add(userLatLng).add(destinationLatLng);
        //sets the color and makes it geodesic
        pOptions.color(Color.parseColor("#FF8300")).geodesic(true);
        Log.i(TAG, pOptions.toString());
        //resturns the pOptions
        return pOptions;
    }

    //decodes the polyline
    private List<LatLng> decodePoly(String encoded)
    {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    //creates a url
    public String createUrl()
    {
        String origin = "origin="+userLatLng.latitude+","+userLatLng.longitude;
        String dest = "destination="+destinationLatLng.latitude+","+destinationLatLng.longitude;
        String sensor = "sensor=false";
        String parameters = origin+"&"+dest+"&"+sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    public void downloadUrl(String strUrl) throws IOException {
        new DownloadTask().execute(strUrl);
    }

    public void setJSONData(String data) {
        JSONData = "";
        Log.i(TAG, "set Json data");
        JSONData = data;

        //MapsActivity mapsActivity = new MapsActivity();
        //mapsActivity.finishDrawingPolyline();
        //Log.i(TAG, "finish drawing polyline method called");
    }

    public String getJSONData()
    {
        Log.i(TAG, "getJSONData return method : "+JSONData);
        return JSONData;
    }

    //gets the json file from the url line
    private class DownloadTask extends AsyncTask<String, Void, String> {

        String data = "";
        //String json_data = "";

        //downloading data in a non-ui thread
        @Override
        protected String doInBackground(String... url)  {
            //stores the data from the webservice

            try  {
                Log.i(TAG, "downloadUrlCalled");
                //String data = "";
                InputStream iStream = null;
                HttpURLConnection urlConnection = null;
                try{
                    Log.i(TAG, "trying");
                    URL url_str = new URL(url[0]);
                    Log.i(TAG, "url created");
                    // Creating an http connection to communicate with url
                    urlConnection = (HttpURLConnection) url_str.openConnection();
                    Log.i(TAG, "url connection object");
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000); // millis
                    urlConnection.setConnectTimeout(15000); // millis
                    urlConnection.setDoOutput(true);
                    Log.i(TAG, "urlConnection parameters set");
                    // Connecting to url
                    urlConnection.connect();
                    Log.i(TAG, "tried to connect using urlConnection object");
                    // Reading data from url
                    iStream = urlConnection.getInputStream();
                    Log.i(TAG, "set up the iStream");
                    BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                    Log.i(TAG, "set up bufferedReader");
                    StringBuffer sb = new StringBuffer();
                    Log.i(TAG, "set up string buffer");
                    String line = "";
                    while( ( line = br.readLine()) != null){
                        sb.append(line);
                    }
                    Log.i(TAG, "added all the info to a string");
                    data = sb.toString();
                    Log.i(TAG, "data was set to variable data");
                    br.close();
                    Log.i(TAG, "buffer reader closed");
                }catch(Exception e){
                    Log.i(TAG, "caught something"+e.toString());
                    //Log.d("Exception while downloading url", e.toString());
                }finally{
                    Log.i(TAG, "closed the string and urlconnection");
                    iStream.close();
                    urlConnection.disconnect();
                }
                Log.i(TAG, "data to be returned : "+data);
            }catch (Exception e)  {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            Log.i(TAG, "onpostexecutecalled || data contains : "+data);
            setJSONData(data);
            try {
                mapsActivity.finishDrawingPolyline();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "finishdrawing call failed.  Exception : "+e.toString());
            }
        }

    }
}
