package cs371m.jh54765.flightatlas;

/**
 * Created by Jacob on 3/29/18.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.*;
import java.io.*;
public class Metro {
    private String city;
    private ArrayList<Airport> airports;
    private double xCoordFactor;
    private double yCoordFactor;
    private String country;
    private double timezone;
    private String DST;
    private String tz;
    private double latitude;
    private double longitude;


    public Metro(String name) {
        city = name;
        airports = new ArrayList<Airport>();
        xCoordFactor = 0.0;
        yCoordFactor = 0.0;
    }
    public Metro(String name, double xFact, double yFact) {
        city = name;
        airports = new ArrayList<Airport>();
        xCoordFactor = xFact;
        yCoordFactor = yFact;
    }

    public Metro(String name, String country, double timezone, String DST, String tz) {
        this.city = name;
        this.country = country;
        this.timezone = timezone;
        this.DST = DST;
        this.tz = tz;
        airports = new ArrayList<Airport>();
        this.latitude = Double.NaN;
        this.longitude = Double.NaN;
    }
    public Metro(String name, String country, double timezone, String DST, String tz, double latitude, double longitude) {
        this.city = name;
        this.country = country;
        this.timezone = timezone;
        this.DST = DST;
        this.tz = tz;
        airports = new ArrayList<Airport>();
        this.latitude = latitude;
        this.longitude = longitude;
    }



    public boolean hasCommercialAirport() {
        for (int i = 0; i < airports.size(); i++) {
            if (airports.get(i).hasRoutes()) {
                return true;
            }
        }
        return false;
    }

    public String getCountry() { return country; }
    public double getTimezone() { return timezone; }
    public String getDST() { return DST; }
    public String getTz() {return tz; }
    public void addAirport(Airport airport) { airports.add(airport); }
    public String toString() {
        return city;
    }
    public String getCity() { return city;	}
    public double getLatitude() {
        return this.latitude;
    }
    public double getLongitude() {
        return this.longitude;
    }
    // returns the position of the city on Map2 given the map size is 1x1
    public double getXFactor() {return xCoordFactor;}
    public double getYFactor() {return yCoordFactor;}
    public double setXFactor(double xFact) {return xCoordFactor = xFact;}
    public double setYFactor(double yFact) {return yCoordFactor = yFact;}
    public ArrayList<Airport> getAirports() {return airports;}
    public boolean equals(Metro obj) {
        return city.equals(obj.getCity()) && airports.equals(obj.getAirports()) &&
                (xCoordFactor == obj.getXFactor()) && (yCoordFactor == obj.getYFactor());
    }
    public void setLatLng(LatLng pos) {
        this.latitude = pos.latitude;
        this.longitude = pos.longitude;
    }
    protected Metro clone() {
        Metro cpy = new Metro(this.city,this.xCoordFactor,this.yCoordFactor);
        for (Airport apt: airports) {
            cpy.addAirport(apt);
        }
        return cpy;
    }

    public void removeAirport(Airport airport) {
        airports.remove(airport);
    }
}
