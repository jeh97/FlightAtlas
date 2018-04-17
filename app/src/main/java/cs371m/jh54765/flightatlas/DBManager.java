package cs371m.jh54765.flightatlas;

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

    public void addAirline(String name, String alias, String codeIATA, String codeICAO, String callsign, String country) {
        List<String> where = new ArrayList<String>();
        List<String> args = new ArrayList<String>();
        String table = "airlines";
        String[] columns = {"airlines.name","alias","airlines.IATA","airlines.ICAO","callsign","airlines.country"};


    }

}
