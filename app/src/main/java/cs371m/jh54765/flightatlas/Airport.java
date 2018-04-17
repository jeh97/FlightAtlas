package cs371m.jh54765.flightatlas;

/**
 * Created by Jacob on 3/29/18.
 */

import java.util.*;

public class Airport {
    private String name;
    private String code;
    private ArrayList<Route> routes;
    private Metro city;
    private String country;
    private String codeIATA;
    private String codeICAO;
    private double latitude;
    private double longitude;
    private double altitude;
    private double timezone;
    private String DST;
    private String tz;
    public Airport(String codeIATA, Metro cty) {
        code = codeIATA;
        name = "";
        routes = new ArrayList<Route>();
        city = cty;
    }
    public Airport(Metro metro,
                   String name,
                   String country,
                   String IATA,
                   String ICAO,
                   double latitude,
                   double longitude,
                   double altitude,
                   double timezone,
                   String DST,
                   String tz) {
        this.name = name;
        this.country = country;
        this.codeIATA = IATA;
        this.codeICAO = ICAO;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timezone = timezone;
        this.DST = DST;
        this.tz = tz;
        routes = new ArrayList<Route>();
        city = metro;
    }
    /**
     * method to get the airport's IATA code
     * @return IATA code
     */
    public String getCodeIATA() { return codeIATA; }
    /**
     * method to get airport's name. Returns blank string if no name is assigned
     * @return Airport name or blank string if no name is assigned
     */
    public String getName() { return name; }
    /**
     * method to set the airport's name
     * @param nm
     */
    public void setName(String nm) { name = nm; }
    /**
     * method to get the airport's routes
     * @return the airport's routes
     */

    public String getCodeICAO() { return codeICAO; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getAltitude() { return altitude; }
    public double getTimezone() { return timezone; }
    public String getDST() { return DST; }
    public String getTz() { return tz; }
    public ArrayList<Route> getRoutes() { return routes; }
    public Metro getCity() { return city;}
    public void addRoute(Route rt) { routes.add(rt); }

    public boolean hasRoutes() { return routes.size() > 0; }
    public String toString() {
        return "#IATA Code: <"+code+"> Metro Area: "+city+"#";
    }
    public boolean equals(Airport obj) {
        return name.equals(obj.getName()) && code.equals(obj.getCodeIATA()) && routes.equals(obj.getRoutes()) && city.equals(obj.getCity());
    }
    protected Airport clone() {
        Airport cpy = new Airport(this.code,this.city);
        cpy.setName(this.name);
        for (Route rt:routes) {
            cpy.addRoute(rt);
        }
        return cpy;
    }
}
