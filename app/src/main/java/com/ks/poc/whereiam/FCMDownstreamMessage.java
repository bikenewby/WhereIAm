package com.ks.poc.whereiam;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Krit on 7/14/2016.
 */
public class FCMDownstreamMessage extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String server_key = "key=AIzaSyAp0Izmtmtt20hqhCb1SYdixSrpKB5gtGM";
        String client_key;
        String content;
        String content_json_string;
        int responseCode;

        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            client_key = "cdqFiSFRtGs:APA91bF9Rh8f8Kg-93Hphp4TqXLzhAALktskRKN_jNfF4jJFthTuB5LpsGs3tDyrdTUPpbrOvxvEg6YitFN4-sv7i7SFHUBqb1I5tunqrV5BTbdhMteSws8MdtbGxaWwFvkQHTvxRTJM";

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Authorization", server_key);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            // params[0] = Date & time
            // params[1] = Device model
            // params[2] = Device's Latitude
            // params[3] = Device's Longitude

            JSONObject content_json_object = new JSONObject();
            JSONObject data_json_object;
            try {
                data_json_object = new JSONObject(params[1]);
                content_json_object.put("to", params[0]);
                // Only send dat if need the receiver callback to triggered although the app is not running.
                content_json_object.put("data", data_json_object);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            content_json_string = content_json_object.toString();

            OutputStream output = httpURLConnection.getOutputStream();
            output.write(content_json_string.getBytes());
            output.flush();
            output.close();

            responseCode = httpURLConnection.getResponseCode();

            return Integer.toString(responseCode);
        } catch (ProtocolException e) {
            return "Error: " + e.getMessage();

        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
