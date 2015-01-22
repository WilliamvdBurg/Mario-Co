package com.example.william.marioenco;

/**
 * Created by William on 20-1-2015.
 */
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class ServerCommunicator extends AsyncTask<Void, Void, String> {

    private String message;
    private String ip;
    public static int port = 4444;
    private String response = null;

    public ServerCommunicator(String ip, int port, String message ){
        super();
        //Hier maak je de gegevsna aan die er zijn om verbinden ye gaan maken met de server een dan een message kunen sturen//
        this.message = message;
        this.ip = ip;
        this.port = port;
    }



    @Override
    protected String doInBackground(Void... params) {
        try {
            Socket serverSocket = new Socket();
            serverSocket.connect(new InetSocketAddress(this.ip, this.port), 4444);

            // Hierin word datbericht gestuurd naar de server. Je stuurt een bericht om een antwoord terug te krijgen
            this.sendMessage(message, serverSocket);

            InputStream input;

            try {
                input = serverSocket.getInputStream();
                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(input));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                responseStreamReader.close();

                this.response = stringBuilder.toString();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            System.out.println("Response: " + response);

            //Hieronder worden twee methoden gebruikt van de activity
            //Deze zijn er om informatie naar de UI thread te sturen
                    } catch (UnknownHostException e) {
            Log.d("debug", "can't find host");
        } catch (SocketTimeoutException e) {
            Log.d("debug", "time-out");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    //ook deze methoden kunnen niet naar de UI direct communiceren, hou hier rekening mee
    private void sendMessage(String message, Socket serverSocket) {
        OutputStreamWriter outputStreamWriter = null;

        try {
            outputStreamWriter = new OutputStreamWriter(serverSocket.getOutputStream());
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        if (outputStreamWriter != null) {
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            PrintWriter writer = new PrintWriter(bufferedWriter, true);

            writer.println(message);
        }
    }
}