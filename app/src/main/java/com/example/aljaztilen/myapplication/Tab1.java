package com.example.aljaztilen.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class Tab1 extends Fragment {
    private static final String url1 = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String url2 = "&mode=xml&units=metric&appid=4246747296c49960a49a577a3022a1d5";
    private TextView naslov, temperatura;
    public static  Context context;
    public static Activity activity;
    private DownloadXMLToday asyncXML;
    public static boolean isLoaded = false;
    public static View v;
    private int count = 0;
    private long startMillis=0;
    private ImageView ikona;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);
        DownloadXMLToday.v = v;
        String mesto = PreferenceManager.getDefaultSharedPreferences(context).getString("mesto", "maribor");
        String finalUrl = url1 + mesto + url2; // tvori se url
        if(isNetworkAvailable()) {
            naloziXML(finalUrl);//posodobit podatke
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setTitle("Napaka"); // v primeru, da ni internetne povezave se izpiše dialog
            dialog.setMessage("Ni internetne povezave!");
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
        //definiraje se textview in imageview za izpis
        naslov = (TextView) v.findViewById(R.id.mesto);
        ikona = (ImageView)v.findViewById(R.id.ikona);
        temperatura = (TextView)v.findViewById(R.id.temperatura);

        temperatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                if (startMillis == 0 || (time - startMillis > 3000)) { //števec klikov
                    startMillis = time;
                    count = 1;
                } else { //  time-startMillis< 3000
                    count++;
                }

                if(count == 2){ // če dvakrat pritisnemo na temperaturo se zažene trend.class
                    Intent intentt = new Intent(Main.getContextOfApplication(),Trend.class);
                    intentt.setAction(Intent.ACTION_MAIN);
                    intentt.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(intentt);
                }
            }
        });
        return v; // vrne se view
    }

    public void naloziXML(String url) {
        isLoaded = false;
        asyncXML = new DownloadXMLToday(url);
        asyncXML.execute();
    }

    public  boolean isNetworkAvailable() {
        //preverimo internetno povezavo
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
