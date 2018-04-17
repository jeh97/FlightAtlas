package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Created by Jacob on 4/2/18.
 */

public class Fetch {
    public interface FetchCallback {
        void fetchStart();
        void fetchComplete(TreeMap<String,Metro> cities,
                           TreeMap<String,Airport> airports,
                           TreeMap<String,Route> routes,
                           TreeMap<String,Airline> airlines,
                           ArrayList<String> routeCodes);
        void fetchFailed();
    }
    protected FetchCallback fetchCallback = null;
    protected Context context = null;
    public TreeMap<String,String> airportToState;
    public Fetch(FetchCallback fetchCallback, Context context) {
        this.fetchCallback = fetchCallback;
        this.context = context;

        airportToState = new TreeMap<>();
        airportToState.put("ALB",", NY");
        airportToState.put("ABY",", GA");
        airportToState.put("BTV",", VT");
        airportToState.put("BRL",", IA");
        airportToState.put("CNM",", NM");
        airportToState.put("CLD",", CA");
        airportToState.put("CHS",", SC");
        airportToState.put("CRW",", WV");
        airportToState.put("CAE",", SC");
        airportToState.put("COU",", MO");
        airportToState.put("LCK",", OH");
        airportToState.put("CMH",", OH");
        airportToState.put("CSG",", GA");
        airportToState.put("GSP",", SC");
        airportToState.put("GLH",", MS");
        airportToState.put("PGV",", NC");
        airportToState.put("JAN",", MS");
        airportToState.put("MKL",", TN");
        airportToState.put("PDX",", OR");
        airportToState.put("PWM",", ME");
        airportToState.put("ROC",", NY");
        airportToState.put("RST",", MN");
        airportToState.put("ART",", NY");
        airportToState.put("ATY",", SD");
        airportToState.put("ILM",", NC");
        airportToState.put("ILG",", DE");

        new AsyncDownloader().execute();
    }
    public class AsyncDownloader extends AsyncTask<String,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fetchCallback.fetchStart();

        }
        @Override
        protected void onPostExecute(Void nada) {
            super.onPostExecute(nada);
        }
        @Override
        protected void onCancelled(Void nada) {
            fetchCallback.fetchFailed();

        }

        protected Void doInBackground(String... urls) {
            // XXX Write this
            try {
                loadXmlFromNetwork();
            } catch (Exception e) {

            }
            return null;
        }
        private Void loadXmlFromNetwork() {
            Log.d("Fetch","LOAD XML");
            TreeMap<String,Metro> cities = new TreeMap<String,Metro>();
            TreeMap<String,Airport> airports = new TreeMap<String,Airport>();
            TreeMap<String,Route> routes = new TreeMap<String,Route>();
            TreeMap<String,Airline> airlines = new TreeMap<String,Airline>();
            ArrayList<String> routeCodes = new ArrayList<String>();
            URL routesURL,airportsURL,airlinesURL;

            double xFact,yFact;
            int lineLen;
            Scanner cityList;
            try {
                Log.v("Fetch","Test 1");
                routesURL = new URL(context.getString(R.string.routes_url));
                airportsURL = new URL(context.getString(R.string.airports_url));
                airlinesURL = new URL(context.getString(R.string.airlines_url));

                Log.v("Fetch","Test 2");
                Scanner airportsScanner = new Scanner(airportsURL.openStream());
                Log.v("Fetch","Test 3");
                Scanner airlinesScanner = new Scanner(airlinesURL.openStream());
                Log.v("Fetch","Test 4");
                Scanner routesScanner = new Scanner(routesURL.openStream());
                Log.v("Fetch","Test 5");

                String current,airportID,name,city,
                        country,codeIATA,codeICAO,DST,Tz,
                        nextLn,fileName,xFactString,yFactString;
                double latitude,longitude,altitude,timezone;
                while(airportsScanner.hasNextLine()) {
                    current = airportsScanner.nextLine();
                    if (current.indexOf("\\N") < 0) {
                        List<String> data = Arrays.asList(current.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                        airportID = data.get(0);
                        name = data.get(1).replaceAll("\"","");
                        city = data.get(2).replaceAll("\"","");
                        country = data.get(3).replaceAll("\"","");
                        codeIATA = data.get(4).replaceAll("\"","");
                        codeICAO = data.get(5).replaceAll("\"","");
                        latitude = Double.parseDouble(data.get(6));
                        longitude = Double.parseDouble(data.get(7));
                        altitude = Double.parseDouble(data.get(8));
                        timezone = Double.parseDouble(data.get(9));
                        DST = data.get(10).replaceAll("\"","");
                        Tz = data.get(11).replaceAll("\"","");

                        city = city + modifiedCityName(codeIATA);

                        // see if city is in cities
                        Metro metro = cities.get(city+", "+country);
                        if (metro == null) {
                            // if not, create
                            metro = new Metro(city,country,timezone,DST,Tz);
                            cities.put(city+", "+country,metro);
                        }
                        Airport airport = new Airport(metro,name,country,codeIATA,codeICAO,latitude,longitude,altitude,timezone,DST,Tz);
                        metro.addAirport(airport);
                        airports.put(codeIATA, airport);
                    }


                }
                airportsScanner.close();
                Log.v("Fetch","Test 6");

                String airlineID,airlineName,airlineAlias,IATAcode,ICAOcode,callsign,active;
                while(airlinesScanner.hasNextLine()) {
                    current = airlinesScanner.nextLine();
                    List<String> data = Arrays.asList(current.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                    airlineID = data.get(0);
                    airlineName = data.get(1).replaceAll("\"","");
                    airlineAlias = data.get(2).replaceAll("\"","");
                    IATAcode = data.get(3).replaceAll("\"","");
                    ICAOcode = data.get(4).replaceAll("\"","");
                    callsign = data.get(5).replaceAll("\"","");
                    country = data.get(6).replaceAll("\"","");
                    active = data.get(7).replaceAll("\"","");
                    if (active.equals("Y")) {

                        Airline airline = new Airline(airlineName,airlineAlias,IATAcode,ICAOcode,callsign,country);
                        airlines.put(IATAcode, airline);
                    }

                }
                airlinesScanner.close();
                String sourceAirport,sourceID,destAirport,destID,codeshare,stops,equipment;
                while(routesScanner.hasNextLine()) {
                    current = routesScanner.nextLine();
                    List<String> data = Arrays.asList(current.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                    IATAcode = data.get(0);
                    airlineID = data.get(1);
                    sourceAirport = data.get(2);
                    sourceID = data.get(3);
                    destAirport = data.get(4);
                    destID = data.get(5);
                    codeshare = data.get(6);
                    stops = data.get(7);
                    Airport source = airports.get(sourceAirport);
                    Airport dest = airports.get(destAirport);
                    Airline arln = airlines.get(IATAcode);
                    Route thisRoute = routes.get(sourceAirport+destAirport);

                    if(source!=null && dest!=null && arln != null) {
                        if (thisRoute==null) {
                            thisRoute = new Route(source,dest);
                            source.addRoute(thisRoute);
                            routes.put(sourceAirport+destAirport, thisRoute);
                        }
                        thisRoute.addOperator(arln);

                    }
                }
                routesScanner.close();
                fetchCallback.fetchComplete(cities,airports,routes,airlines,routeCodes);


            } catch (Exception e) {
                Log.e("Fetch",e.getMessage());

            }
            return null;
        }

    }
    public String modifiedCityName(String code) {


        if (airportToState.containsKey(code)) {
            return airportToState.get(code);
        }
        return "";


    }

}
