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
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


public class Tab2 extends ListFragment {
    //spremenljivke za generiranje url naslova
    private static final String VREME_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Maribor&mode=xml&cnt=5&units=metric&appid=4246747296c49960a49a577a3022a1d5";
    private final String url1 = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
    private final String url2 = "&mode=xml&cnt=5&units=metric&appid=4246747296c49960a49a577a3022a1d5";
    Main main;

    //arraylist tipa Vreme_napoved, 5-dnevna napoved
    private ArrayList<Vreme_napoved> objects = new ArrayList<Vreme_napoved>();
    private MyArrayAdapter myArrayAdapter;
    private DownloadXMLForecast xmlNapoved = new DownloadXMLForecast(VREME_URL);// razred za pridobitev podatkov v normalnem ražimu
    public static Context context;
    public static Activity activity;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String mesto = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("mesto", "maribor");// da se spremenijo enote
        DownloadXMLForecast.context = getContext();
        main = new Main();
        naloziXML(url1+mesto+url2); //ponovno naloži xml
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_2, container, false);
        DownloadXMLForecast.v = v; //view za DownloadXMLForecast
        return v;
    }

    public void naloziXML(String url) {
        xmlNapoved = new DownloadXMLForecast(url); // v primeru normalnega se izvede branje tukaj, tja pošljemo url
        xmlNapoved.execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        myArrayAdapter = xmlNapoved.getMyArrayAdapter();//v primeru normalnega režima pridobi adapter razred za pridobitev podatkov v nromalnem režimu
        if(isNetworkAvailable()) {
            //če hočemo prikazati 3-urno napoved
            Intent intent = new Intent(getActivity().getApplicationContext(), NapovedActivity.class);
            intent.putExtra("DAN", myArrayAdapter.getDatum(position));
            intent.putExtra("MESTO", myArrayAdapter.getCity(position));
            startActivity(intent);
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
    }

    public  boolean isNetworkAvailable() {
        //preverimo internetno povezavo
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
