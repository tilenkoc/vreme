package com.example.aljaztilen.myapplication;


public class Vreme_napoved {
    //vrednosti , ki so v posamezni vremenski napovedi
    private String datum;
    private String temperatura;
    private String vreme;
    private String dan;
    private String ura;
    private String mesto;
    private String temperaturamin;
    private String temperaturamax;
    private String night;
    private String morn;
    private String eve;
    private String day1;


    public Vreme_napoved(String dan,String day1,String night,String eve,String morn, String temperaturamin,String temperaturamax, String vreme, String datum, String mesto) {
        this.dan = dan; //konstruktor za 5 dnevno napoved
        this.temperaturamin = temperaturamin;
        this.temperaturamax = temperaturamax;
        this.vreme = vreme;
        this.datum = datum;
        this.mesto = mesto;
        this.day1 =day1;
        this.eve=eve;
        this.night=night;
        this.morn=morn;
    }

    public Vreme_napoved(String temperatura, String vreme, String ura) { // konstruktor za trenutno stanje
        this.ura=ura;
        this.temperatura = temperatura;
        this.vreme = vreme;
    }

    public String getDatum() {
        return datum;
    } //getter za datum
    public String getMesto() {return mesto;} // getter za mesto
    public String getDay() {
    return dan;}

    public String getTemperatura() {
        return temperatura;
    } //getter za temperaturo

    public String getTemperaturamin(){return temperaturamin;} // getter za minimalno temperaturo

    public String getTemperaturamax (){return temperaturamax;}// getter za maksimalno temperaturo


    public String getVreme() {
        return vreme;
    } //getter za stanje vremena


    public String getUra(){
        return ura;
    } //getter za uro
}
