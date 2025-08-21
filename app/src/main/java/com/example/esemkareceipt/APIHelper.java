package com.example.esemkareceipt;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class APIHelper extends AsyncTask <String, Void, String>{

    String url;
    String method;
    HttpURLConnection connection;

    public APIHelper(String url, String method) {
        this.url = url;
        this.method = method;
        try {
            this.connection = (HttpURLConnection) new URL(this.url).openConnection();
            this.connection.setRequestMethod(this.method);
            this.connection.setRequestProperty("Content-Type", "application/json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String doInBackground(String... strings) {

        if (strings.length > 0){
            try {
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(strings[0]);
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

       String result = "";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            result = reader.readLine();
        } catch (IOException e) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            try {
                result = reader.readLine();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }


        return result;
    }
}
