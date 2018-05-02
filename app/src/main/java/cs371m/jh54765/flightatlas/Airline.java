package cs371m.jh54765.flightatlas;

/**
 * Created by Jacob on 3/29/18.
 */

import java.util.*;

public class Airline {
    private String name;
    private String alias;
    private String codeIATA;
    private String codeICAO;
    private String callsign;
    private String country;
    private ArrayList<Route> routes;
    public Airline(String nm) {
        name = nm;
        routes = new ArrayList<Route>();
    }
    public Airline(String name,
                   String alias,
                   String codeIATA,
                   String codeICAO,
                   String callsign,
                   String country) {
        this.name = name;
        this.alias = alias;
        this.codeIATA = codeIATA;
        this.codeICAO = codeICAO;
        this.callsign = callsign;
        this.country = country;
        routes = new ArrayList<Route>();
    }
    /**
     * method to add a new destination to the airline,
     * @param
     */
    public void addRoute(Route newRoute) { routes.add(newRoute); }
    public ArrayList<Route> getRoutes() { return routes; }
    public String getName() { return name; }
    public String getAlias() { return alias; }
    public String getCodeIATA() { return codeIATA; }
    public String getCodeICAO() { return codeICAO; }
    public String getCallsign() { return callsign; }
    public String getCountry() { return country; }
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())) {
            Airline other = (Airline) obj;
            return name.equals(other.getName()) && routes.equals(other.getRoutes());
        }
        return false;
    }

    public String toString() {
        return name;
    }
}
