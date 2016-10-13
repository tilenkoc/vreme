package com.example.aljaztilen.myapplication;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


public class NapovedFragment extends ListFragment {
    //fragment ki se uporablja pri napovedi po urah
    //testni url
    private static final String VREME_URL = "http://api.openweathermap.org/data/2.5/forecast?q=london&mode=xml&appid=4246747296c49960a49a577a3022a1d5";
    private DownloadXMLHour dobiNapoved = new DownloadXMLHour(VREME_URL); //kličemo pridobitev podatkov za 3-urno napoved

    private ArrayList<Vreme_napoved> objects = new ArrayList<Vreme_napoved>();
    private MyArrayAdapterUra myArrayAdapterUra;
    private String dan;

    //setter za dan, da vemo kateri dan smo izbrali
    public void setDan(String dan) {this.dan = dan;}
    //getter za dan
    public String getDan(){return this.dan;}

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.myArrayAdapterUra = new MyArrayAdapterUra(getContext(), objects);
        this.setListAdapter(myArrayAdapterUra); //fragmentu nastavimo adapter
    }

    public void naloziXML(String url) {
        dobiNapoved= new DownloadXMLHour(url);
        dobiNapoved.fetchXML();
        while(dobiNapoved.parsingComplete);
        ArrayList<Vreme_napoved> newData = dobiNapoved.getObjects(); // dobi Vreme_napoved objekte
        myArrayAdapterUra.napoved().clear();
        myArrayAdapterUra.napoved().addAll(newData);
        myArrayAdapterUra.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.napoved_fragment, container, false);
        dobiNapoved.setDay(dan); //nastavimo dan, da vemo za kateri dan moramo prikazati podatke
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getActivity(), "Izbran " + dan, Toast.LENGTH_SHORT).show(); // toast za izpis izbranega dne
        myArrayAdapterUra.notifyDataSetChanged();
    }

}
