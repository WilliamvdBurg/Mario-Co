package com.example.william.marioenco;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by William on 11-1-2015.
 */
public class HomeFragment extends Fragment {

    public static String serverIp ;
    public static int serverPort = 4444;
    public static ArrayList<String> services;
    public static ArrayList<JSONObject> beknopteLijst;
    public String beknopt = null;

    public static View rootview;
    public Spinner spinner;
    public static String servicenaam;
    public static int laatstepositie;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.home_layout, container, false);
        dataDownload();
        spinner.setSelection(laatstepositie);
        return rootview;

    }

    public void dataDownload() {
// in deze code
        services = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("servicelijst", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String response = null;
        try {
            try {
                // Het ip adres die ik hieronder aanmaakt moet het ip adres zijn van de server waaruit die alles moet ophalen
                response = new ServerCommunicator(serverIp,
                        serverPort, jsonObject.toString()).execute().get();

            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if (response == null) {

            Toast.makeText(rootview.getContext(), "Verbinding met de server niet mogelijk.", Toast.LENGTH_LONG).show();
        } else {
            // Haal de null naam weg van de JSONArray (Voorkomt error)
            String jsonFix = response.replace("null", "");

            JSONArray JArray = null;
            try {
                JArray = new JSONArray(jsonFix);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject jObject = null;
            String value = null;
            services = new ArrayList<String>();

            for (int i = 0; i < JArray.length(); i++) {
                try {
                    jObject = JArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    value = jObject.getString("naam");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                services.add(value);

            }
            // haaalt de beknopte informatie op.
            beknopteLijst = new ArrayList<JSONObject>();
            JSONObject beknoptjObject = new JSONObject();
            try {
                for (int i = 0; i < services.size(); i++) {
                    beknoptjObject.put("informatiebeknopt", services.get(i));
                    try {
                        try {
                            beknopt = new ServerCommunicator(serverIp,
                                    serverPort, beknoptjObject.toString()).execute().get();

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    String infoFix = beknopt.replace("null", "");
                    JSONObject fixedjObject = new JSONObject(infoFix);
                    beknopteLijst.add(fixedjObject);

                    Log.i("informatiebeknopt", infoFix);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        // Hieronder maak ik de spinner aan en geef ik aan waaaruit hij de informatie moet halen. Dit komt uit de server.
        spinner = (Spinner) rootview.findViewById(R.id.spinner);

        spinner
                .setAdapter(new ArrayAdapter<String>(rootview.getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        services));

        spinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub
                        // geeft aan waar de text moet komen in de homefragment. DIt komt te staan in de textview Textservice.
                        TextView beknopteinfo = (TextView) rootview.findViewById(R.id.Textservice);
                        laatstepositie = position;
                        try {
                            beknopteinfo.setText(beknopteLijst.get(position).getString("informatiebeknopt"));
                            servicenaam = services.get(position);

                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });





           //Hier koppel ik de button aan de pagina serviceinfo
        Button informatiebutton = (Button) rootview.findViewById(R.id.informatiebutton);
        informatiebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(rootview.getContext(), ServiceInfo.class);


                startActivity(i);
            }
        });

    }
}
