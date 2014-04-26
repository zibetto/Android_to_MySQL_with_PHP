package com.melec.android_to_mysql_with_php.main;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector{

    private Context mcontext;

    /**constructor*/
    public ConnectionDetector (Context context){

        this.mcontext = context;
    }


    public boolean isConnectedToInternet(){

        ConnectivityManager connectivity = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null){

            NetworkInfo[] info =  connectivity.getAllNetworkInfo();

            if (info != null){

                for (NetworkInfo netInfo : info) { /**for each statement*/

                    if (netInfo.getState() == NetworkInfo.State.CONNECTED) {

                        return true;
                    }

                }

            }

        }
            return false;
    }

}