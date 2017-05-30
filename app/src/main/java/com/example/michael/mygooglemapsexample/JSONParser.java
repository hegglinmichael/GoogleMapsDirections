package com.example.michael.mygooglemapsexample;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Michael on 12/4/2015.
 */
public class JSONParser {

    //creates a Debugging tag for this class
    private static final String TAG = "JSONParser";

    //gets a JSON object and returns a list of potential routes
    public List<List<HashMap<String, String>>> parse(JSONObject jsonObject)  {
        //????????
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        //creates a JSONArray to hold routes received
        JSONArray jRoutes = null;
        //creates a JSONArray to hold all legs held within a received route
        JSONArray jLegs = null;
        //creates a JSONArray to hold all steps held in a received leg
        JSONArray jSteps = null;

        try{
            //gets the array named routes held in the jsonObject
            //and sets the jRoutes JSONArray equal to the aforementioned array
            jRoutes = jsonObject.getJSONArray("routes");

            //creates a for loop to loop through all the routes in the array
            for(int i = 0; i < jRoutes.length(); i++)  {
                //sets the jLegs JSONArray equal to a single route's
                //JSONArray for the legs in that route
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                //creates an arrayList to hold all hashmaps
                List path = new ArrayList<HashMap<String, String>>();

                //creates a forloop to loop through the legs in a specific route
                for(int j = 0; j < jLegs.length(); j++)  {
                    //sets the jStep JSONArray equal to the array of steps held
                    //in a single leg of the route
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    //loopes through all the steps in a leg of a route
                    for(int k = 0; k < jSteps.length(); k++)  {
                        //holds all the steps in the route to make a polyline
                        //that represents a polyline on a google map
                        String polyline = "";
                        //gets a step in the route and gets a polyLine from that step
                        //it is then cast to a JSON object and points of that JSON object are
                        //then extracted and cast to a string
                        polyline = (String)((JSONObject)((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        //creates a list to hold LatLng from the polyline string
                        //(which holds points) after its decoded???
                        List<LatLng> list = decodePoly(polyline);

                        //goes through the list of LatLng
                        for(int r = 0; r < list.size(); r++)  {
                            //holds the lng and lat values of a polyline
                            HashMap<String, String> hm = new HashMap<String, String>();
                            //declaring keys called lat and lng
                            //putting in string values of doubles that come from the list of LngLat
                            hm.put("lat", Double.toString(((LatLng) list.get(r)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(r)).longitude));
                            //adds the hm Hashmap to an index in the path arrayList
                            path.add(hm);
                        }
                    }
                    //adds the path arrayList as an object in the
                    //routes arrayList
                    routes.add(path);
                }
            }
        }catch(Exception e) {
            //tells me there is an exception
            Log.i(TAG, "parse method - Exception caught no routes found");
            e.printStackTrace();
        }

        //returns the routes calculated or null if no routes are found
        return routes;
    }

    //returns a list of LatLng taken from the encoded string received
    private List<LatLng> decodePoly(String encoded)  {
        //arrayList to hold all the LatLng values
        List<LatLng> poly = new ArrayList<LatLng>();
        //
        int index = 0;
        //gets the length of the parameter String
        int len = encoded.length();
        //
        int lat = 0;
        //
        int lng = 0;

        //
        while(index < len)  {
            //
            int b = 0;
            //
            int shift = 0;
            //
            int result = 0;
            //
            do  {
                //gets the char at 1 plus the undex number
                //it then subtracts 63
                b = encoded.charAt(index++) - 63;
                //bitwise operators that turn the #s into binary
                //values, do the operation then give back the decimal #s
                //gets the value of b&0x1f, then shifts all the numbers left
                //(the spaces left depends on how big shift is (if shift
                // is 2 it will be shifted 2)) and zeros are added on the end
                //finally gets the value of result|(number calculated above)
                result |= (b & 0x1f) << shift;
                //adds 5 to the shift value
                shift += 5;
                //continues to do this while b is greater than the
                //0x20(which is a hexadecimal number) which is 32 in decimal
            } while (b >= 0x20);
            //
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            //adds dlat to lat
            lat += dlat;

            //sets shift and result equal to 0
            shift = 0;
            result = 0;
            //
            do  {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat/1E5)), ((((double) lng/1E5))));
            poly.add(p);
        }

        //returns a list of lng and lat
        return poly;
    }
}
