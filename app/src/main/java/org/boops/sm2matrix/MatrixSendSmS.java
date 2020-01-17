package org.boops.sm2matrix;

import android.os.AsyncTask;
import android.telephony.SmsManager;

import org.json.JSONObject;

import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MatrixSendSmS extends AsyncTask<String, Void, Void> {

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

            AccessToken = new JSONObject(PostJSON("https://" + HomeURL + "/_matrix/client/r0/login", auth))
                    .getString("access_token");

        } catch(Exception e){
            System.out.println(e);
            return null;
        }

        String FromID = "";

        try {
            OkHttpClient client = new OkHttpClient();

            FromID = new JSONObject(GetReq("https://" + HomeURL + "/_matrix/client/r0/rooms/" + RoomID + "/messages?access_token=" + AccessToken))
                    .getString("end");


        } catch(Exception e){
            System.out.println(e);
            return null;
        }


        try {

            JSONObject res = new JSONObject(GetReq("https://" + HomeURL + "/_matrix/client/r0/rooms/" + RoomID + "/messages?from=" + FromID + "&dir=b&limit=1&filter=" +
                    "{\"not_senders\":[\"@" + UserID + ":" + HomeURL + "\"],\"types\":[\"m.room.message\"]}&access_token=" + AccessToken));

            if (res.getJSONArray("chunk").getJSONObject(0).getJSONObject("content").has("body")) {

                String EventID = res.getJSONArray("chunk").getJSONObject(0).getString("event_id");
                String old_body = res.getJSONArray("chunk").getJSONObject(0).getJSONObject("content").getString("body");
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), "{}");

                Request request = new Request.Builder()
                        .url("https://" + HomeURL + "/_matrix/client/r0/rooms/" + RoomID + "/redact/" + EventID + "/" + random() + "?access_token=" + AccessToken)
                        .put(body)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response resp = client.newCall(request).execute();

                if (old_body.charAt(0) == '!') {
                    String[] new_body = old_body.split(" ", 2);
                    SmsManager smgr = SmsManager.getDefault();
                    smgr.sendTextMessage(new_body[0].substring(1),null, new_body[1],null,null);

                    JSONObject auth = new JSONObject();
                    auth.put("msgtype", "m.text");
                    auth.put("body", "Message To: " + new_body[0].substring(1) + "\n" + new_body[1]);

                    PostJSON("https://" + HomeURL + "/_matrix/client/r0/rooms/" + RoomID + "/send/m.room.message?access_token=" + AccessToken, auth);
                }

            }
        } catch(Exception e){
            System.out.println(e);
            return null;
        }

        return null;
    }

    private String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(2);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
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

    private String GetReq(String URL) {

        String Ans = "";

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
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
