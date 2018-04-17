package cs371m.jh54765.flightatlas;

import android.content.Context;

import java.util.TreeMap;

/**
 * Created by Jacob on 3/29/18.
 */

public class AirportInfo {

    private static AirportInfo instance;

    private XmlReader xml;
    private TreeMap<String,Airport> airports;
    private Context context;

    private AirportInfo(Context context) {
        this.context = context;
        xml = new XmlReader(context);

        airports = xml.getAirports();
    }
    public static AirportInfo getInstance(Context context) {
        if (instance == null) {
            instance = new AirportInfo(context);
        }
        return instance;
    }

    public TreeMap<String,Airport> getAirports() {
        return airports;
    }



}
