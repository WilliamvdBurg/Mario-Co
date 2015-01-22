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
    public static ArrayList<String> serviceLijst;
    public static ArrayList<JSONObject> beknopteInformatielijst;
    public String informatiebeknopt = null;

    private Spinner service_spinner;
    public static View rootview;
    public Spinner spinner;
    public static String servicenaam;
    public static int selectedPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.home_layout, container, false);
        dataOphalen();
        return rootview;

    }

    public void dataOphalen() {
// in deze code
        serviceLijst = new ArrayList<String>();
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
            serviceLijst = new ArrayList<String>();

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
                serviceLijst.add(value);

            }
            // haaalt de beknopte informatie op.
            beknopteInformatielijst = new ArrayList<JSONObject>();
            JSONObject beknoptjObject = new JSONObject();
            try {
                for (int i = 0; i < serviceLijst.size(); i++) {
                    beknoptjObject.put("informatiebeknopt", serviceLijst.get(i));
                    try {
                        try {
                            informatiebeknopt = new ServerCommunicator(serverIp,
                                    serverPort, beknoptjObject.toString()).execute().get();

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    String infoFix = informatiebeknopt.replace("null", "");
                    JSONObject fixedjObject = new JSONObject(infoFix);
                    beknopteInformatielijst.add(fixedjObject);

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
                        serviceLijst));

        spinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub
                        // geeft aan waar de text moet komen in de homefragment. DIt komt te staan in de textview Textservice.
                        TextView beknopteinfo = (TextView) rootview.findViewById(R.id.Textservice);

                        try {
                            beknopteinfo.setText(beknopteInformatielijst.get(position).getString("informatiebeknopt"));
                            servicenaam = serviceLijst.get(position);

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
