package cs371m.jh54765.flightatlas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    SQLiteDatabase flightDb;
    private DatabaseHelper dbHelper;
    private Context context;
    private static DBManager instance;

    private DBManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            Log.e("DB","Fail to create database");
        }
        flightDb = dbHelper.getWritableDatabase();

    }

    public static DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context);
        }
        return instance;
    }

    public void addAirline(Airline airline) throws Exception{
        ContentValues args = new ContentValues();
        String table = "airlines";
        args.put("name",airline.getName());
        if (airline.getAlias().length()>0 && !airline.getAlias().equals("\\N"))
            args.put("alias",airline.getAlias());
        if (airline.getCodeIATA().length()>0)
            args.put("IATA",airline.getCodeIATA());
        else throw new Exception("Airline has no IATA code");
        if (airline.getCodeICAO().length()>0)
            args.put("ICAO",airline.getCodeICAO());
        if (airline.getCallsign().length()>0)
            args.put("callsign",airline.getCallsign());
        if (airline.getCountry().length()>0)
            args.put("country",airline.getCountry());

        long rowID = flightDb.insert(table,null,args);
        Log.d("DBManager",String.format("addAirline got rowID %d",rowID));
    }

    public void addMetro(Metro metro) {
        String table = "metros";
        ContentValues args = new ContentValues();
        args.put("city",metro.getCity());
        args.put("country",metro.getCountry());
        args.put("timezone",metro.getTimezone());
        args.put("DST",metro.getDST());
        args.put("tz",metro.getTz());
        args.put("latitude",metro.getLatitude());
        args.put("longitude",metro.getLongitude());
        long rowID = flightDb.insert(table,null,args);
        Log.d("DBManager",String.format("addMetro got rowID %d",rowID));
    }

    public void addAirport(Airport airport) throws Exception {
        ContentValues args = new ContentValues();
        String table = "airports";
        if (airport.getName().length()>0)
            args.put("name",airport.getName());
        if (airport.getCity() != null) {
            args.put("city", airport.getCity().getCity());
            args.put("country", airport.getCity().getCountry());
        }
        if (airport.getCodeIATA().length()>0)
            args.put("IATA",airport.getCodeIATA());
        else throw new Exception("Airport has no IATA code");
        if (airport.getCodeICAO().length()>0)
            args.put("ICAO",airport.getCodeICAO());
        args.put("latitude",airport.getLatitude());
        args.put("longitude",airport.getLongitude());
        args.put("altitude",airport.getAltitude());
        args.put("timezone",airport.getTimezone());
        args.put("DST",airport.getDST());
        args.put("tz",airport.getTz());
        long rowID = flightDb.insert(table,null,args);
        Log.d("DBManager",String.format("addAirport got rowID %d",rowID));
    }

    public void addRoute(Route route) {
        for (int i = 0; i < route.getOperators().size(); i++) {
            ContentValues args = new ContentValues();
            String table = "routes";
            args.put("origin",route.getOrigin().getCodeIATA());
            args.put("destination",route.getDestination().getCodeIATA());
            args.put("operator",route.getOperators().get(i).getCodeIATA());
            long rowID = flightDb.insert(table,null,args);
            Log.d("DBManager",String.format("addRoute got rowID %d",rowID));
        }

    }

    // Return all airports
    public Cursor getAirports(String order, boolean asc) {
        String table = "airports";
        String[] columns = {"airports._id","name", "city", "country", "IATA",
                "airports.ICAO", "airports.latitude", "airports.longitude", "airports.altitude",
                "airports.timezone", "airports.DST", "airports.tz"};
        List<String> where = new ArrayList<>();
        List<String> args = new ArrayList<>();

        String ascDesc = (asc) ? "ASC":"DESC";

        String orderBy = String.format("%s %s",order,ascDesc);
        String groupBy = null;
        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table, columns, "", argsArr, groupBy, null, orderBy, null);
    }

    public Cursor getAirportRoutes(String IATA) {
        String table = "routes r\n" +
                "JOIN airports originAirport ON originAirport.IATA=r.origin \n" +
                "JOIN airports destAirport ON destAirport.IATA = r.destination";
        String[] columns = {"r._id","r.origin","r.destination","destAirport.city","destAirport.country"};
        List<String> args = new ArrayList<>();

        String queryString = "(originAirport.IATA = ?)";
        args.add(IATA);

//        String orderBy = "routes.destination ASC";

        String groupBy = "r.destination";
        String orderBy = "destAirport.city";

        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table,columns,queryString,argsArr,groupBy,null,orderBy,null);
    }

    public Cursor getAirportInfo(String IATA) {
        String table = "airports";
        String[] columns = {"name","city","country","IATA","ICAO","latitude","longitude"};
        List<String> args = new ArrayList<>();
        String queryString = "(IATA = ?)";
        args.add(IATA);


        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table,columns,queryString,argsArr,null,null,null,null);

    }

    public Cursor getOperators(String origin, String destination) {
        String table = "routes INNER JOIN airlines ON routes.operator=airlines.IATA";
        String[] columns = {"airlines.name","airlines.IATA"};
        List<String> args = new ArrayList<>();
        String queryString = "(routes.origin = ?) AND (routes.destination = ?)";
        args.add(origin);
        args.add(destination);


        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table,columns,queryString,argsArr,null,null,null,null);

    }

    public LatLng getAirportLatLng(String IATA) {
        String table = "airports";
        String[] columns = {"airports.name","airports.latitude","airports.longitude","airports.IATA"};
        List<String> args = new ArrayList<>();
        String queryString = "(airports.IATA = ?)";
        args.add(IATA);


        String[] argsArr = args.toArray(new String[args.size()]);
        Cursor cur = flightDb.query(table,columns,queryString,argsArr,null,null,null,null);

        cur.moveToFirst();
        double lat = cur.getDouble(cur.getColumnIndexOrThrow("latitude"));
        double lng = cur.getDouble(cur.getColumnIndexOrThrow("longitude"));

        return new LatLng(lat,lng);

    }

    public ArrayList<String> getAirportRoutesList(String IATA) {
        String table = "airports INNER JOIN routes ON airports.IATA = routes.origin INNER JOIN airlines ON routes.operator = airlines.IATA";
        String[] columns = {"airports.name","airports.latitude","airports.longitude","airports.IATA",
                "routes.origin","routes.destination","routes.operator","airlines.enabled"};
        List<String> args = new ArrayList<>();
        String queryString = "(airports.IATA = ?)";
        args.add(IATA);

        String orderBy = null;
        String groupBy = "routes.destination";

        String[] argsArr = args.toArray(new String[args.size()]);
        Cursor cur = flightDb.query(table,columns,queryString,argsArr,groupBy,null,orderBy,null);

        ArrayList<String> destinations = new ArrayList<>();
        while (cur.moveToNext()) {
            boolean enabled = (cur.getInt(cur.getColumnIndexOrThrow("enabled")) != 0)?true:false;
            if (enabled) {
                String dest = cur.getString(cur.getColumnIndexOrThrow("destination"));
                destinations.add(dest);
            }

        }
        return destinations;
    }

    public Cursor getAirlineRoutes(String IATA) {
        String table = "airlines JOIN routes ON airlines.IATA = routes.operator JOIN airports orig ON routes.origin = orig.IATA JOIN airports dest ON routes.destination = dest.IATA";
        String[] columns = {"routes.origin","routes.destination","routes.operator","airlines.enabled",
                "orig.name AS origName","orig.latitude AS origLat","orig.longitude AS origLng","orig.IATA AS origIATA","orig.city AS origCity","orig.country AS origCountry",
                "dest.name AS destName","dest.latitude AS destLat","dest.longitude AS destLng","dest.IATA AS destIATA","dest.city AS destCity","dest.country AS destCountry",
                "airlines.IATA"};
        List<String> args = new ArrayList<>();
        String queryString = "(airlines.IATA = ?)";
        args.add(IATA);

        String orderBy = null;
        String groupBy = null;

        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table,columns,queryString,argsArr,groupBy,null,orderBy,null);
    }

    public Cursor getAirlines(String order, boolean asc) {
        String table = "airlines";
        String[] columns = {"airlines._id","airlines.name", "airlines.country", "airlines.IATA",
                "airlines.ICAO", "airlines.callsign","enabled"};
        List<String> where = new ArrayList<>();
        List<String> args = new ArrayList<>();

        String ascDesc = (asc) ? "ASC":"DESC";

        String orderBy = String.format("%s %s",order,ascDesc);
        String groupBy = null;

        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table, columns, "", argsArr, groupBy, null, orderBy, null);
    }

    public Cursor getCities(String order, boolean asc) {
        String table = "metros";
        String[] columns = {"_id","city", "country", "timezone",
                "DST", "tz", "latitude","longitude"};
//        List<String> where = new ArrayList<>();
        String queryString = null;
        List<String> args = new ArrayList<>();

        String ascDesc = (asc) ? "ASC":"DESC";

        String orderBy = String.format("%s %s",order,ascDesc);
        String groupBy = null;

        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table, columns, queryString, argsArr, groupBy, null, orderBy, null);
    }

    public Cursor getCitiesAndAirport(String order, boolean asc) {
        String table = "metros JOIN (SELECT * FROM airports GROUP BY city,country) as airport ON metros.city = airport.city AND metros.country = airport.country";

        String[] columns = {"metros.city AS city", "metros.country AS country", "metros.latitude AS cityLat","metros.longitude AS cityLng",
        "airport.latitude AS airLat", "airport.longitude AS airLng","airport.name","airport.IATA"};
//        List<String> where = new ArrayList<>();
        String queryString = null;
        List<String> args = new ArrayList<>();

        String ascDesc = (asc) ? "ASC":"DESC";

        String orderBy = String.format("%s %s",order,ascDesc);
        String groupBy = null;

        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table, columns, queryString, argsArr, groupBy, null, orderBy, null);
    }

    public Cursor getCityAirports(String city, String country) {
        String table = "metros JOIN airports ON metros.city = airports.city AND metros.country = airports.country";
        String[] columns = {"metros._id","airports._id","metros.city", "metros.country","IATA","name"};
//        List<String> where = new ArrayList<>();
        List<String> args = new ArrayList<>();

        String queryString = "(metros.city = ?) AND (metros.country = ?)";

        args.add(city);
        args.add(country);

        String orderBy = null;
        String groupBy = null;

        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table, columns, queryString, argsArr, groupBy, null, orderBy, null);
    }
    public Cursor getCityAirportsAndDestCount(String city, String country) {
        String table = "airports JOIN "+
                    "(SELECT routes.origin AS origin, COUNT(DISTINCT routes.destination) AS dests FROM routes GROUP BY (origin))"+
                    "ON origin=airports.IATA";
        String[] columns = {"airports._id AS _id","airports.name AS name","airports.IATA as IATA","dests AS destinations",
                            "airports.city AS city","airports.country AS country"};
//        List<String> where = new ArrayList<>();
        List<String> args = new ArrayList<>();

        String queryString = "(city = ?) AND (country = ?)";

        args.add(city);
        args.add(country);

        String orderBy = null;
        String groupBy = null;

        String[] argsArr = args.toArray(new String[args.size()]);
        Cursor cursor =  flightDb.query(table, columns, queryString, argsArr, groupBy, null, orderBy, null);
        Log.d("DBManager",String.format("There are %d rows",cursor.getCount()));
        return cursor;
    }



    public void setAirlineEnabled(String IATA, boolean enabled) {
        String table = "airlines";
        ContentValues args = new ContentValues();
        args.put("enabled",(enabled)?1:0);

        String queryString = "(IATA = ?)";

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(IATA);

        String[] argsArr = whereArgs.toArray(new String[args.size()]);

        flightDb.update(table,args,queryString,argsArr);
    }


    public void enableAllAirlines() {
        String table = "airlines";
        ContentValues args = new ContentValues();
        args.put("enabled",1);

        String queryString = null;

        String[] argsArr = null;

        flightDb.update(table,args,queryString,argsArr);
    }

    public void disableAllAirlines() {
        String table = "airlines";
        ContentValues args = new ContentValues();
        args.put("enabled",0
        );

        String queryString = null;


        String[] argsArr = null;

        flightDb.update(table,args,queryString,argsArr);
    }

    public Cursor getCityInfo(String city, String country) {
        Log.d("DBManager",String.format("getCityInfo for %s, %s",city,country));
        String table = "metros";
        String[] columns = {"_id","city", "country","latitude","longitude"};
//        List<String> where = new ArrayList<>();
        List<String> args = new ArrayList<>();

        String queryString = "(metros.city = ?) AND (metros.country = ?)";

        args.add(city);
        args.add(country);

        String orderBy = null;
        String groupBy = null;

        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table, columns, queryString, argsArr, groupBy, null, orderBy, null);
    }

    public void updateCityLatLng(String city, String country, LatLng newLatLng) {
        String table = "metros";
        ContentValues args = new ContentValues();
        args.put("latitude",newLatLng.latitude);
        args.put("longitude",newLatLng.longitude);

        String queryString = "(city = ?) AND (country = ?)";

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(city);
        whereArgs.add(country);
        String[] whereArgsArr = whereArgs.toArray(new String[args.size()]);;

        Log.d("updateCityLatLng",String.format("%s, %s new LatLng % 08f,%+08f",city,country,newLatLng.latitude,newLatLng.longitude));

        flightDb.update(table,args,queryString,whereArgsArr);
    }


    public Cursor getCityRoutes(String city, String country) {
        String table = "routes r\n" +
                "JOIN airports origAirport ON origAirport.IATA = r.origin \n" +
                "JOIN airports destAirport ON destAirport.IATA = r.destination \n" +
                "JOIN metros origCity ON origAirport.city = origCity.city AND origAirport.country = origCity.country \n" +
                "JOIN metros destCity ON destAirport.city = destCity.city AND destAirport.country = destCity.country \n";
        String[] columns = {"r._id","r.origin","r.destination",
                "destCity.city AS destCityCity","destCity.country AS destCityCountry","destCity.latitude AS destCityLat","destCity.longitude AS destCityLng",
                "origCity.city AS origCityCity","origCity.country AS origCityCountry","origCity.latitude AS origCityLat","origCity.longitude AS origCityLng"};
        List<String> args = new ArrayList<>();

        String queryString = "(origCity.city = ?) AND (origCity.country = ?)";
        args.add(city);
        args.add(country);

//        String orderBy = "routes.destination ASC";

        String groupBy = "origCity.city, origCity.country, destCity.city, destCity.country";
        String orderBy = null;

        String[] argsArr = args.toArray(new String[args.size()]);
        return flightDb.query(table,columns,queryString,argsArr,groupBy,null,orderBy,null);
    }

    public void renameCity(String city, String country, String newCity) {
        String table = "metros";
        ContentValues args = new ContentValues();
        args.put("city",newCity);

        String queryString = "(city = ?) AND (country = ?)";

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(city);
        whereArgs.add(country);
        String[] whereArgsArr = whereArgs.toArray(new String[args.size()]);;

        flightDb.update(table,args,queryString,whereArgsArr);

//        table = "airports";


//        flightDb.update(table,args,queryString,whereArgsArr);


    }

}
