package com.example.tv_remote;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class InternetConnection extends AsyncTask<String, Void, Void> {
    private static final String TAG = ".InternetConnection";
    private static boolean switchSocketWriter = false;
    private static boolean callback = false;

    protected Void doInBackground(String... params) {
        DataOutputStream message;
        String frequency = params[0];
        String ipAddress = params[1];
        char port = 80;

        try {
            Socket s;
            s = new Socket(ipAddress, port);
            message = new DataOutputStream(s.getOutputStream());
            if(switchSocketWriter) {    //send writeBytes for java-server on pc
                message.writeBytes(frequency);
            }
            else{   //send UTF for java-server on esp module
                message.writeUTF(frequency);
                TimeUnit.MILLISECONDS.sleep(10);
            }
            message.flush();
            if(callback){
                RoomLightSeekerBar.sensorData = readMessage(s);
            }
            message.close();
            s.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void changeBooleanTrue(){  //change boolean from external activity to true
        switchSocketWriter = true;
        Log.d(TAG,"switch socket writer true");
    }

    public static void changeBooleanFalse(){ //change boolean from external activity to false
        switchSocketWriter = false;
        Log.d(TAG,"switch socket writer false");
    }

    public static void changeCallbackBooleanTrue(){
        callback = true;
        Log.d(TAG,"switch callback true");
    }

    public static void changeCallbackBooleanFalse(){
        callback = false;
        Log.d(TAG,"switch callback false");
    }

    String readMessage(java.net.Socket socket) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        char[] buffer = new char[200];
        int numberOfChars = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
        return new String(buffer, 0, numberOfChars);
    }
}
