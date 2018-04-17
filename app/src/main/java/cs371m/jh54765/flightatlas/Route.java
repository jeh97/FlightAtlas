package cs371m.jh54765.flightatlas;

/**
 * Created by Jacob on 3/29/18.
 */

import java.util.*;

public class Route {
    private Airport origin;
    private Airport destination;
    private ArrayList<Airline> operators;
    public Route(Airport orig, Airport dest) {
        origin = orig;
        destination = dest;
        operators = new ArrayList<Airline>();
    }
    public void addOperator(Airline airline) {
        operators.add(airline);
    }
    public String toString() {
        String str = "";
        str="Route: "+origin.getCodeIATA()+"-"+destination.getCodeIATA()+" operated by ";
        int count = 0;
        for (Airline i:operators) {
            if (count!=0) str = str+", ";
            str += i.getName();
        }
        return str;
    }
    public Airport getOrigin() {return origin;}
    public Airport getDestination() {return destination;}
    public ArrayList<Airline> getOperators() {return operators;}
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())) {
            Route other = (Route)obj;
            return origin.equals(other.getOrigin()) && destination.equals(other.getDestination()) && operators.equals(other.getOperators());
        }
        return false;
    }
    protected Route clone() {
        Route cpy = new Route(this.origin,this.destination);
        for (Airline op: this.operators) {
            cpy.addOperator(op);
        }
        return cpy;
    }
}
