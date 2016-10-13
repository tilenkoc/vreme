package com.example.aljaztilen.myapplication;


import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DownloadXMLForecast extends AsyncTask<String, Void, String> {

    //spremenljivke za izpis vremena
    private String opisVreme;
    private String city;
    private String day;
    private String datum;
    private String temperatureMin;
    private String temperatureMax;
    private String urlString;
    private String error = null;
    public static String dayMin = null;
    public static String dayMax = null;
    public static int noRainDays = 0;
    private String dezPreveriString;
    private char dezPreveriChar;
    private double maxDan = 0;
    private double minDan = 0;
    private String day1,night,eve,morn;

    // spremenljivke za računanje trenda
    public static double day11,nightt,evee,mornn;
    public static double sum1,sum2,sum3,sum4;
    public static double pr1,pr2,pr3;
    public static double sumpr1, sumpr2, sumpr3;
    public static double kvadratn,kvadratd,kvadrate,kvadratm;
    public static double sumkvn,sumkvd,sumkve;

    //spremenljivke za obvestila
    public static double maxWeekTemp = 0;
    public static double minWeekTemp = 0;
    public static Context context;
    private XmlPullParserFactory xmlFactoryObject;
    private MyArrayAdapter myArrayAdapter;
    private ArrayList<Vreme_napoved> objects = new ArrayList<Vreme_napoved>();//tukaj se zapisujejo vrednosti za posamezni dan
    private ArrayList<Vreme_napoved> objects1= new ArrayList<Vreme_napoved>();
    private ArrayList<Vreme_napoved> tempObjects;
    public static View v;

    //konstruktor , kjer pridobimo url
    public DownloadXMLForecast(String url){
        this.urlString = url;
    }

    //prazen konstruktor
    public DownloadXMLForecast(){}

    //getterji da lahko dobimo določene vrednosti iz tega razreda v trugih
    public double getMaxWeekTemp() {
        return maxWeekTemp;
    }

    public double getMinWeekTemp() {
        return minWeekTemp;
    }

    public String getDayMin() {
        return dayMin;
    }

    public String getDayMax() {
        return dayMax;
    }

    public MyArrayAdapter getMyArrayAdapter(){
        return myArrayAdapter;
    }//getter za adapter za 5-dnevno napoved

    //setterji za spremelnjivke oz. vrednosti , ki se pridobijo v tem razredu

    public void setMyArrayAdapter(MyArrayAdapter myArrayAdapter) {
        this.myArrayAdapter = myArrayAdapter;
    }
    public void setTemperatureMin(String temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public void setTemperatureMax(String temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public void setOpisVreme(String opisVreme) {
        this.opisVreme = opisVreme;
    }




    @Override
    protected String doInBackground(String... params) {
        //incializacija za obvestilo o št dnevih brez dežja in max/ min temperaturi
        maxWeekTemp = 0;
        minWeekTemp = 0;
        noRainDays = 0;

        error = null;
        try {
            //vzpostavitev povezave
            URL url = new URL(urlString); //url pridobimo iz main
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();// povezava se vzpostavi

            InputStream stream = conn.getInputStream(); //ustvari se tok vhodnih podatkov
            //implementira se Xml Pull parser, branje xml
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();//novi pullparser
            myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);//kliče se generalno vedenje parserja, kot je npr. procesiranje namespace-ov
            myparser.setInput(stream, null); //nastavimo vhod
            parseXMLAndStoreIt(myparser);
            stream.close();//zapremo stream
        }
        catch (IOException | XmlPullParserException e ) {
            error = "napaka";
        }
        return null;
    }

    protected void onPostExecute(String u){
        final ListView lv1 = (ListView) v.findViewById(android.R.id.list);
        //nastavimo listview v katerem se izpiše 5-dnevna napoved
        if(error == null) {
            //če ni napak se kreaira novi arrayadapter v katerega vstavimo objekte tipa Vreme_napoved
            myArrayAdapter = new MyArrayAdapter(context, objects);
            tempObjects = objects;
            lv1.setAdapter(myArrayAdapter); //nastavimo adapter
            this.setMyArrayAdapter(myArrayAdapter);
            myArrayAdapter.notifyDataSetChanged();

            //ob prikazu se pokaže obvestilo o št deževnih dneh , min/ max dneh
            Main.makeToast("Max temp je v " + this.getDayMax() + ": " + this.getMaxWeekTemp()
                    + "\nMin temp je v " + this.getDayMin()+  ": "  + this.getMinWeekTemp() +
                    "\n Deževnih dni : " + noRainDays, context);
        }
    }


    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)//dokler ne pridemo do konca xml
            {
                String name=myParser.getName(); //pridobi ime značke

                switch (event){
                    case XmlPullParser.START_TAG:
                        switch (name) {
                            case "time":  //kličemo ime značke in beremo izbrane vrednosti
                                //pridobi vrednost atributa value značke time
                                day = myParser.getAttributeValue(null, "day");
                                this.setDay(day);
                                datum = myParser.getAttributeValue(null, "day");

                                break;
                            case "temperature":
                                //pridobi vrednost atributa value značke temperature
                                temperatureMin = myParser.getAttributeValue(null, "min");
                                temperatureMax = myParser.getAttributeValue(null, "max");
                                day1 = myParser.getAttributeValue(null, "day");
                                morn = myParser.getAttributeValue(null, "morn");
                                eve = myParser.getAttributeValue(null, "eve");
                                night = myParser.getAttributeValue(null, "night");

                                //išče se maksimalna in minimalna temperatura v tednu
                                maxDan = Double.parseDouble(temperatureMax);
                                minDan = Double.parseDouble(temperatureMin);
                                this.setTemperatureMax(temperatureMax);
                                this.setTemperatureMin(temperatureMin);
                                //preverimo, če je temperatura tega dneva največja v tem tednu
                                if (maxDan > maxWeekTemp || maxWeekTemp == 0) {
                                    maxWeekTemp = maxDan;
                                    dayMax = this.day;
                                }
                                //preverimo, če je temperatura tega dneva najmanjša v tem tednu
                                if (minDan < minWeekTemp || minWeekTemp == 0) {
                                    minWeekTemp = minDan;
                                    dayMin = this.day;
                                }
                                break;
                            case "symbol":
                                //pridobi vrednost atributa value značke symbol
                                opisVreme = myParser.getAttributeValue(null, "number");
                                this.setOpisVreme(opisVreme);
                                break;
                            case "name":
                                //pridobi vrednost atributa value značke name
                                String query = myParser.nextText();
                                city = query.replaceAll("\\s", "");
                                break;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("time")) {
                            //ko pridemo do končne značke se izvede to
                            //seštevanje vrednosti za trend temperature
                            System.out.println("noc " + night);
                            nightt=Double.parseDouble(night);
                            kvadratn = nightt*nightt;
                            sumkvn +=kvadratn;
                            sum1+=nightt;

                            System.out.println("sum1" + sum1);

                            System.out.println("dan " + day1);
                            day11=Double.parseDouble(day1);
                            kvadratd = day11*day11;
                            sumkvd=kvadratd*kvadratd;
                            sum2+=day11;

                            System.out.println("sum2" + sum2);

                            pr1 = day11*nightt;
                            sumpr1+=pr1;

                            System.out.println("morn " + morn);
                            mornn=Double.parseDouble(morn);
                            kvadratm = mornn*mornn;
                            sumkvd+=kvadratd;
                            sum3+=mornn;
                            System.out.println("sum3" + sum3);

                            pr2 = mornn*day11;
                            sumpr2+=pr2;

                            System.out.println("eve" + eve);
                            evee=Double.parseDouble(eve);
                            kvadrate=evee*evee;
                            sumkve+=kvadrate;
                            sum4+=evee;
                            System.out.println("sum4" + sum4);

                            pr3 = evee*mornn;
                            sumpr3+=pr3;

                            //štetje dni brez  dežja, če je se id ikone začne na 5
                            dezPreveriChar =opisVreme.charAt(0);
                            dezPreveriString = String.valueOf(dezPreveriChar);
                            if(dezPreveriString.equals("5")){
                                noRainDays++;
                            }
                            //vrednosti dodam v arraylist objects, ki je tipa Vreme_napoved
                            objects.add(new Vreme_napoved(day, day1, night, eve, morn, temperatureMin, temperatureMax, opisVreme, datum, city));
                        }
                }
                event = myParser.next();
            }
        }
        catch (Exception e){
            error = "napaka";
        }
    }

    //metoda za dan
    public void setDay(String day) throws ParseException {
        //datum se pretvori v obliko dd.MM.yyyy
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
        Date dt1=format1.parse(day);
        DateFormat format2=new SimpleDateFormat("EEEE");
        String finalDay=format2.format(dt1);

        //dnevi se pretvorijo v kratice
        switch(finalDay) {
            case "ponedeljek":
                finalDay = "PON";
                break;
            case "torek":
                finalDay="TOR";
                break;
            case "sreda":
                finalDay="SRE";
                break;
            case "četrtek":
                finalDay="ČET";
                break;
            case "petek":
                finalDay ="PET";
                break;
            case "sobota":
                finalDay="SOB";
                break;
            case "nedelja":
                finalDay  ="NED";
                break;
        }
        this.day = finalDay;
    }
}
