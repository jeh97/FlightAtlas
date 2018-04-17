package cs371m.jh54765.flightatlas;

/**
 * Created by Jacob on 3/29/18.
 */

import java.util.*;
import java.io.*;
public class Metro {
    private String metro;
    private ArrayList<Airport> airports;
    private double xCoordFactor;
    private double yCoordFactor;
    private String country;
    private double timezone;
    private String DST;
    private String tz;


    public Metro(String name) {
        metro = name;
        airports = new ArrayList<Airport>();
        xCoordFactor = 0.0;
        yCoordFactor = 0.0;
    }
    public Metro(String name, double xFact, double yFact) {
        metro = name;
        airports = new ArrayList<Airport>();
        xCoordFactor = xFact;
        yCoordFactor = yFact;
    }

    public Metro(String name, String country, double timezone, String DST, String tz) {
        this.metro = name;
        this.country = country;
        this.timezone = timezone;
        this.DST = DST;
        this.tz = tz;
        airports = new ArrayList<Airport>();
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
        return metro;
    }
    public String getName() { return metro;	}
    // returns the position of the city on Map2 given the map size is 1x1
    public double getXFactor() {return xCoordFactor;}
    public double getYFactor() {return yCoordFactor;}
    public double setXFactor(double xFact) {return xCoordFactor = xFact;}
    public double setYFactor(double yFact) {return yCoordFactor = yFact;}
    public ArrayList<Airport> getAirports() {return airports;}
    public boolean equals(Metro obj) {
        return metro.equals(obj.getName()) && airports.equals(obj.getAirports()) &&
                (xCoordFactor == obj.getXFactor()) && (yCoordFactor == obj.getYFactor());
    }
    protected Metro clone() {
        Metro cpy = new Metro(this.metro,this.xCoordFactor,this.yCoordFactor);
        for (Airport apt: airports) {
            cpy.addAirport(apt);
        }
        return cpy;
    }
}
