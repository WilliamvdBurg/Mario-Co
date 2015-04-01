package com.example.william.marioenco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class BestelService extends Activity {
    private String servicenaam;
    private String ip;
    private int port = 4444;

    String responseFix;
    Button annuleerknop;
    Button bestelknop;
    private static String naam;
    private static String adres;
    private static String telefoon;
    private static String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // word gekoppeld aan zijn layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestel_service);
        servicenaam = ServiceInfo.servicenaam;
        ip = HomeFragment.serverIp;
        setTitle("Service aanvragen");
        final TextView koperNaam = (TextView) findViewById(R.id.naamVeld);
        final TextView koperAdres = (TextView) findViewById(R.id.adresVeld);
        final TextView koperTelefoon = (TextView) findViewById(R.id.telefoonVeld);
        final TextView koperEmail = (TextView) findViewById(R.id.emailVeld);

        koperNaam.setText(naam);
        koperAdres.setText(adres);
        koperTelefoon.setText(telefoon);
        koperEmail.setText(email);

        final TextView serviceNaam = (TextView) findViewById(R.id.serviceNaam);
        serviceNaam.setText("Bestel: " + servicenaam);
        final TextView serviceBeknopteinformatie = (TextView) findViewById(R.id.aanvraagBeknopteinformatie);
        try{
            serviceBeknopteinformatie.setText(HomeFragment.beknopteLijst.get(HomeFragment.laatstepositie).getString("informatiebeknopt"));
        }
        catch(JSONException e)
        {

        }

        bestelknop = (Button) findViewById(R.id.bestelBevestigen);
        bestelknop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                plaatsBestelling();
            }
        });
        annuleerknop = (Button) findViewById(R.id.bestelAnnuleer);
        annuleerknop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                naam = koperNaam.getText().toString();
                adres = koperAdres.getText().toString();
                telefoon = koperTelefoon.getText().toString();
                email = koperEmail.getText().toString();

                Intent i = new Intent(BestelService.this, ServiceInfo.class);

                startActivity(i);
            }
        });

    }
    // hieronder krijgen de velden een id en worden ze koper.... genoemd om zo naar de server te brengen
    private void plaatsBestelling() {
        final TextView koperNaam = (TextView) findViewById(R.id.naamVeld);
        final TextView koperAdres = (TextView) findViewById(R.id.adresVeld);
        final TextView koperTelefoon = (TextView) findViewById(R.id.telefoonVeld);
        final TextView koperEmail = (TextView) findViewById(R.id.emailVeld);

        naam = koperNaam.getText().toString();
        adres = koperAdres.getText().toString();
        telefoon = koperTelefoon.getText().toString();
        email = koperEmail.getText().toString();

        JSONObject bestelling = new JSONObject();
        JSONObject service = new JSONObject();
        JSONObject gegevens = new JSONObject();
        JSONArray bestelArray = new JSONArray();

        try {
            service.put("slotnaam", servicenaam);
            gegevens.put("kopernaam", naam);
            gegevens.put("koperadres", adres);
            gegevens.put("kopertelnr", telefoon);
            gegevens.put("koperemail", email);

            bestelArray.put(service);
            bestelArray.put(gegevens);

            bestelling.put("aanvraag", bestelArray);

        } catch (JSONException e) {

        }
        String response = null;

        try {
            try {
                // Dit IP adres moet IP adres van server zijn.
                response = new ServerCommunicator(ip,
                        port, bestelling.toString()).execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if(response == null)
        {
            Toast.makeText(BestelService.this, "Server is momenteel niet bereikbaar", Toast.LENGTH_LONG).show();
        }
        else{
            responseFix = response.replace("null", "");

            Toast.makeText(BestelService.this, responseFix, Toast.LENGTH_LONG).show();
            bestelknop.setVisibility(View.GONE);

        }
        naam = koperNaam.getText().toString();
        adres = koperAdres.getText().toString();
        telefoon = koperTelefoon.getText().toString();
        email = koperEmail.getText().toString();

        annuleerknop.setText("terug");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bestel, menu);
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