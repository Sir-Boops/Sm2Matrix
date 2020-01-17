package org.boops.sm2matrix;

import android.os.AsyncTask;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MatrixReceiveSmS extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... args) {

        String UserID = MainActivity.arr[0];
        String Password = MainActivity.arr[1];
        String HomeURL = MainActivity.arr[2];
        String RoomID = MainActivity.arr[3];

        String AccessToken = "";

        try {

            JSONObject auth = new JSONObject();
            auth.put("type", "m.login.password");
            auth.put("user", UserID);
            auth.put("password", Password);

            AccessToken = new JSONObject(PostJSON("https://" + HomeURL + "/_matrix/client/r0/login", auth)).getString("access_token");

        } catch(Exception e){
            System.out.println(e);
            return null;
        }

        try {

            JSONObject auth = new JSONObject();
            auth.put("msgtype", "m.text");
            auth.put("body", "Message From: " + args[0] + "\n" + args[1]);

            PostJSON("https://" + HomeURL + "/_matrix/client/r0/rooms/" + RoomID + "/send/m.room.message?access_token=" + AccessToken, auth);

        } catch(Exception e){
            System.out.println(e);
            return null;
        }

        return null;
    }

    private String PostJSON(String URL, JSONObject PostBody) {

        String Ans = "";

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), PostBody.toString());
            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            Response resp = client.newCall(request).execute();
            if (resp.code() == 200) {
                Ans =  resp.body().string();
            }

        } catch(Exception e){
            System.out.println(e);
            return null;
        }

        return Ans;
    }
}
