package com.example.aljaztilen.myapplication;

import android.app.Activity;
import android.content.res.AssetManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;


public class DownloadXMLHour {
    //spremenljivke za izpis vremena
    public static String day;
    private String dayCheck;
    private String temperature;
    private String from;
    private String to;
    private String time;
    private String opisVreme;
    private String urlString;
    public static Activity activity;
    //xmlparser
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    private ArrayList<Vreme_napoved> objects = new ArrayList<Vreme_napoved>(); //tukaj se zapisujejo vrednosti za posamezni dan
    public volatile boolean isXML=true;
    public DownloadXMLHour(String url){
        this.urlString = url;
    }

    //getter za objekte Vreme_napoved , vrednosti za posamezen dan
    public ArrayList<Vreme_napoved> getObjects() {
        return this.objects;
    }

    //setter za temperaturo
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    //setter za opis vremena
    public void setOpisVreme(String opisVreme) {
        this.opisVreme = opisVreme;
    }

    //setter za dan
    public void setDay(String day){
        this.day = day;
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) throws ParseException {
        int event;
        int cnt = 0;
        for(int i= 0; i<objects.size();i++) {
            objects.remove(i);
        }
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)//dokler ne pridemo do konca xml
            {
                String name=myParser.getName(); //pridobi ime značke
                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("time")){ //kličemo ime značke in beremo izbrane vrednosti
                            from =  myParser.getAttributeValue(null,"from"); //dobimo čas od-do (18-21)
                            to = myParser.getAttributeValue(null, "to");
                            String[] st = from.split("T");
                            String[] st2 = to.split("T");
                            dayCheck = st[0];
                            from = st[1];
                            to = st2[1];
                            st = from.split(":");
                            st2 = to.split(":");
                            from = st[0] + ":" + st[1];
                            to = st2[0] + ":" + st2[1];
                            time = from + "-" + to;

                        } else if(name.equals("temperature")) {
                            temperature = myParser.getAttributeValue(null, "value");
                            temperature = String.valueOf(temperature).split("\\.")[0];
                            this.setTemperature(temperature);
                        } else if(name.equals("symbol")) {
                            //pridobi vrednost atributa value značke symbol
                            opisVreme = myParser.getAttributeValue(null, "number");
                            this.setOpisVreme(opisVreme);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("time")) {
                            //dodamo samo objekt, ki ima isti dan kot tisti ki smo ga izbrali v listview
                            if(dayCheck.equals(day))
                                 objects.add(new Vreme_napoved(temperature, opisVreme, time));
                        }

                }
                event = myParser.next();
            }
            parsingComplete = false;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    AssetManager assetManager = activity.getAssets();
                    InputStream stream;
                    //vzpostavitev povezave
                    URL url = new URL(urlString);//url pridobimo iz main
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();// povezava se vzpostavi
                    stream = conn.getInputStream(); //ustvari se tok vhodnih podatkov

                    //implementira se Xml Pull parser, branje xml
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();//novi pullparser
                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);//kliče se generalno vedenje parserja, kot je npr. procesiranje namespace-ov
                    myparser.setInput(stream, null);//nastavimo vhod

                    parseXMLAndStoreIt(myparser);
                    stream.close();//zapremo stream
                } catch (IOException e ) {
                    e.printStackTrace();
                } catch (XmlPullParserException e ){
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start(); //nit za branje se zažene
    }
}
