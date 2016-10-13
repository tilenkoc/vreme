package com.example.aljaztilen.myapplication;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends AppCompatActivity{

    //spremenljivke za oblikovanje url
    private static final String VREME_URL = "http://api.openweathermap.org/data/2.5/weather?q=London,uk&mode=xml&appid=4246747296c49960a49a577a3022a1d5";
    private static final String url1 = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String url2 = "&mode=xml&units=metric&appid=4246747296c49960a49a577a3022a1d5";
    private static final String url1Napoved = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
    private static final String url2Napoved = "&mode=xml&cnt=5&units=metric&appid=4246747296c49960a49a577a3022a1d5";

    //definitanje orodne vrstica
    private Toolbar toolbar;

    //definira se adapter za prehod med tab1 in tab2
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private Tab1 tb1;
    private Tab2 tb2;

    //naslova za tab1 in tab2
    CharSequence Titles[]={"Danes","Napoved"};
    //kontekst maina
    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //definira se orodna vrstica
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //nastavimo statične spremenljivke, da lahko iz razredov kličemo ta aktivity in context
        DownloadXMLToday.activity = this;
        Tab2.activity = this;
        Tab1.activity = this;
        Tab1.context = getBaseContext();

        // ustvari se vieepager in passing fragment manager, naslovi in število tabov
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Titles.length);

        // definira se viewpager in nastavimo adapter Assigning
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // izberemo slidin tab layout view
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // da, so tabi fiksirane in enakomerno tazdeljeni

        // custom barva
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // view pager za SlidingTabsLayout
        tabs.setViewPager(pager);
        contextOfApplication = getApplicationContext();
        Tab2.context = contextOfApplication;
    }

    // getter za kontekst aplikacije
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        tb1 = (Tab1) adapter.getRegisteredFragment(0); // iniciliziramo tab1 in tab 2
        tb2 = (Tab2) adapter.getRegisteredFragment(1);

        //z klikom na ikono se prikaže toast z podatki o tednu
        ImageView imageView = (ImageView)findViewById(R.id.ikona);
        imageView.setOnClickListener(new View.OnClickListener() {
            //obvestilo za max/min temperaturo v teno in št dni brez dežja
            @Override
            public void onClick(View v) {
                Main.makeToast("Max temp je v " + DownloadXMLForecast.dayMax + ": " + DownloadXMLForecast.maxWeekTemp
                        + "\nMin temp je v " + DownloadXMLForecast.dayMin + ": " + DownloadXMLForecast.minWeekTemp
                        + "\nDeževnih dni: " + DownloadXMLForecast.noRainDays, getApplicationContext());
            }
        });

        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView(); // koda za standardni searchview
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() { //listener za vpis
            @Override
            public boolean onQueryTextSubmit(String query) {
                String finalQuery = query.replaceAll("\\s", ""); // dobimo vpisano vrednost
                String mesto = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("mesto", "maribor");
                DownloadXMLToday.okMesto = mesto; //prejšnjo mesto nastavimo kot okMesto v slučaju, če vpisan query ne vrne pravega mesta

                if (isNetworkAvailable()) {
                    String finalUrl = url1 + finalQuery + url2; //url za danes
                    String finalNapovedUrl = url1Napoved + finalQuery + url2Napoved; // url za napoved

                    tb1.naloziXML(finalUrl);
                    tb2.naloziXML(finalNapovedUrl);//poda se url za tab1 in tab2, trenutno stanje vremena in 5 dnevna napoved

                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("mesto", finalQuery).commit(); //v preference vpišemo mesto
                    Intent intent = new Intent(Main.this, Widget.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE); //updejtamo widget

                    int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), Widget.class));
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids); //če je slučajno več widgetov
                    sendBroadcast(intent); //pošljemo broadcast, prejme ga widget in se updejta
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
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
                //izbrišemo vpisano mesto v searchviewu
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                searchView.setQuery("", false);
                updateWidget();
                return true;
                }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            } //če je vpisan tekst se vrne true
        };
        searchView.setOnQueryTextListener(queryTextListener); //listner , če se vpisuje v searchview
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // orodna vrstica bo avtomatsko zaznala klike in se na njih odzavala , starši so specificirani v AndroidManifest.xml
        switch(item.getItemId()) {
            case R.id.search:
                return true;
            case R.id.nastavitve:
                startActivity(new Intent(this, Preferences.class));//odpre se razred Preferences.class oz. zažene se activity Poreferences.class
                return true;
            case R.id.pomoc:
                startActivity(new Intent(this,Pomoc.class)); // odpre se razred Pomoc.class oz. zažene se activity Pomoc.class
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void makeToast(String message, Context context) {
        if(message==null) {
            message = "Mesto ne obstaja ali internet ni na voljo!";
        }
        Toast.makeText(context, message , Toast.LENGTH_LONG).show();
    }

    //metoda za update widget-a
    public void updateWidget(){
        TextView a = (TextView) findViewById(R.id.mesto);
        String mesto = a.getText().toString();
        Intent updateWidget = new Intent(Main.this, Widget.class);
        updateWidget.setAction(Widget.ACTION_UPDATE_CITY);
        updateWidget.putExtra("mesto", mesto);
        PendingIntent pending = PendingIntent.getBroadcast(Main.this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pending.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public  boolean isNetworkAvailable() {
        //preverimo internetno povezavo
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
