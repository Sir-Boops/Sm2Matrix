package org.boops.sm2matrix;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.security.Security;


// Requires
// username
// password
// matrix homeserver url
// roomid

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();

    private static String UserID = "";
    private static String Password = "";
    private static String MatrixURL = "";
    private static String RoomID = "";
    public static String[] arr = {"", "", "", ""};

    public static boolean Run = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(new org.conscrypt.OpenSSLProvider(), 1);
        setContentView(R.layout.activity_main);
        handler.postDelayed(SendSmS, 0);

        final SharedPreferences prefs = this.getSharedPreferences(
                "org.boops.Sm2Matrix", Context.MODE_PRIVATE);

        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final EditText url = findViewById(R.id.url);
        final EditText roomid = findViewById(R.id.roomid);

        final Button save_button = findViewById(R.id.save);

        username.setText(prefs.getString("username", ""));
        password.setText(prefs.getString("password", ""));
        url.setText(prefs.getString("url", ""));
        roomid.setText(prefs.getString("roomid", ""));

        MainActivity.UserID = username.getText().toString();
        MainActivity.Password = password.getText().toString();
        MainActivity.MatrixURL = url.getText().toString();
        MainActivity.RoomID = roomid.getText().toString();

        arr = new String[]{MainActivity.UserID, MainActivity.Password, MainActivity.MatrixURL, MainActivity.RoomID};

        final Switch onoff_switch = findViewById(R.id.onoff);
        onoff_switch.setChecked(false);
        onoff_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.Run = true;

                    username.setEnabled(false);
                    password.setEnabled(false);
                    url.setEnabled(false);
                    roomid.setEnabled(false);

                    save_button.setEnabled(false);
                } else {
                    MainActivity.Run = false;

                    username.setEnabled(true);
                    password.setEnabled(true);
                    url.setEnabled(true);
                    roomid.setEnabled(true);

                    save_button.setEnabled(true);
                }
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putString("username", username.getText().toString()).apply();
                prefs.edit().putString("password", password.getText().toString()).apply();
                prefs.edit().putString("url", url.getText().toString()).apply();
                prefs.edit().putString("roomid", roomid.getText().toString()).apply();

                MainActivity.UserID = username.getText().toString();
                MainActivity.Password = password.getText().toString();
                MainActivity.MatrixURL = url.getText().toString();
                MainActivity.RoomID = roomid.getText().toString();

                arr = new String[]{MainActivity.UserID, MainActivity.Password, MainActivity.MatrixURL, MainActivity.RoomID};
            }
        });
    }

    public Runnable SendSmS = new Runnable() {
        @Override
        public void run() {
            if (MainActivity.Run) {
                new MatrixSendSmS().execute();
            }
            handler.postDelayed(SendSmS, 5000);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
