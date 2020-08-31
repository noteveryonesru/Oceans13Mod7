package com.example.usaid_app;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.common.util.JsonUtils;

import java.util.HashMap;

//inner class to perform network request extending an AsyncTask
public class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    //the url where we need to send the request
    String url;
    String result;
    private Context mContext;

    //the parameters
    HashMap<String, String> params;

    //the request code to define whether it is a GET or POST
    int requestCode;

    //constructor to initialize values
    PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode, Context context) {
        this.url = url;
        this.params = params;
        this.requestCode = requestCode;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(mContext, "Connecting to the database...", Toast.LENGTH_SHORT);
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

    //the network operation will be performed in background
    @Override
    protected String doInBackground(Void... voids) {
            String result;
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST) {


                return requestHandler.sendPostRequest(url, params);

            }

            if (requestCode == CODE_GET_REQUEST) {

                return requestHandler.sendGetRequest(url);

            }

            return null;

    }

}