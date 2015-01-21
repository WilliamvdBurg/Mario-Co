package com.example.william.marioenco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class ServiceInfo extends ActionBarActivity {
    public static View rootview;
    private String ip;
    private int port = 4444;
    private String informatiegedetailleerd;
    private String uitgebreideinformatie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_info);

        ip = HomeFragment.serverIp;
        dataophalen();

        TextView servicenaam = (TextView) findViewById(R.id.infoServiceNaam);
        servicenaam.setText(HomeFragment.servicenaam);

        TextView uitgebreideinformatie = (TextView) findViewById(R.id.infoVeld);
        uitgebreideinformatie.setText(this.uitgebreideinformatie);

// knop aanmaken om terug te gaan naar homefragment

        Button annuleerKnop = (Button) findViewById(R.id.annuleer);

        annuleerKnop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent home = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(home);
            }
        });
        // knop aanmaken om naar bestelling te gaan
        Button BestelKnop = (Button) findViewById(R.id.accepteer);


        BestelKnop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BestelService.class);

                startActivity(i);
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_info, menu);
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

    public void dataophalen() {
        JSONObject beknoptjObject = new JSONObject();
        try {
            beknoptjObject.put("informatie", HomeFragment.servicenaam);
            try {
                try {
                    informatiegedetailleerd = new ServerCommunicator(ip,
                            port, beknoptjObject.toString()).execute().get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            String infoFix = informatiegedetailleerd.replace("null", "");
            JSONObject fixedjObject = new JSONObject(infoFix);
            uitgebreideinformatie = fixedjObject.getString("informatie");

            Log.i("informatie", infoFix);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
