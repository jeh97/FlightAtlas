package cs371m.jh54765.flightatlas;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    public SQLiteDatabase flightDb;
    private DatabaseHelper dbHelper;
    private MainActivity mainActivity;
    private static DBManager instance;

    private DBManager(MainActivity mainActivity) {
        dbHelper = new DatabaseHelper(mainActivity);
        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            Log.e("DB","Fail to create database");
        }
        flightDb = dbHelper.getReadableDatabase();

    }

    public static DBManager getInstance(MainActivity mainActivity) {
        if (instance == null) {
            instance = new DBManager(mainActivity);
        }
        return instance;
    }

    public void addAirline(Airline airline) throws Exception{
        ContentValues args = new ContentValues();
        String table = "airlines";
        args.put("airlines.name",airline.getName());
        if (airline.getAlias().length()>0)
            args.put("alias",airline.getAlias());
        if (airline.getCodeIATA().length()>0)
            args.put("airlines.IATA",airline.getCodeIATA());
        else throw new Exception("Airline has no IATA code");
        if (airline.getCodeICAO().length()>0)
            args.put("airlines.ICAO",airline.getCodeICAO());
        if (airline.getCallsign().length()>0)
            args.put("callsign",airline.getCallsign());
        if (airline.getCountry().length()>0)
            args.put("airlines.country",airline.getCountry());

        flightDb.beginTransaction();
        flightDb.insert(table,null,args);
        flightDb.endTransaction();
    }

    public void addMetro(Metro metro) {
        String table = "metros";
        ContentValues args = new ContentValues();
        args.put("metros.city",metro.getCity());
        args.put("metros.country",metro.getCountry());
        args.put("metros.timezone",metro.getTimezone());
        args.put("metros.DST",metro.getDST());
        args.put("metros.tz",metro.getTz());
        args.put("metros.latitude",metro.getLatitude());
        args.put("metros.longitude",metro.getLongitude());
        flightDb.beginTransaction();
        flightDb.insert(table,null,args);
        flightDb.endTransaction();
    }

    public void addAirport(Airport airport) throws Exception {
        ContentValues args = new ContentValues();
        String table = "airports";
        if (airport.getName().length()>0)
            args.put("airports.name",airport.getName());
        if (airport.getCity() != null) {
            args.put("airports.city", airport.getCity().getCity());
            args.put("airports.country", airport.getCity().getCountry());
        }
        if (airport.getCodeIATA().length()>0)
            args.put("airports.IATA",airport.getCodeIATA());
        else throw new Exception("Airport has no IATA code");
        if (airport.getCodeICAO().length()>0)
            args.put("airports.ICAO",airport.getCodeICAO());
        args.put("airports.latitude",airport.getLatitude());
        args.put("airports.longitude",airport.getLongitude());
        args.put("airports.altitude",airport.getAltitude());
        args.put("airports.timezone",airport.getTimezone());
        args.put("airports.DST",airport.getDST());
        args.put("airports.tz",airport.getTz());
        flightDb.beginTransaction();
        flightDb.insert(table,null,args);
        flightDb.endTransaction();
    }

    public void addRoute(Route route) {
        for (int i = 0; i < route.getOperators().size(); i++) {
            ContentValues args = new ContentValues();
            String table = "routes";
            args.put("routes.origin",route.getOrigin().getCodeIATA());
            args.put("routes.destination",route.getDestination().getCodeIATA());
            args.put("routes.operator",route.getOperators().get(i).getCodeIATA());
            flightDb.beginTransaction();
            flightDb.insert(table,null,args);
            flightDb.endTransaction();
        }

    }




}
