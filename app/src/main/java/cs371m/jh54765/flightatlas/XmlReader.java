package cs371m.jh54765.flightatlas;

/**
 * Created by Jacob on 3/29/18.
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.*;
import java.util.*;
import java.net.URL;
import android.content.Context;

public class XmlReader implements Fetch.FetchCallback {
    public TreeMap<String,Metro> cities;
    public TreeMap<String,Airport> airports;
    public TreeMap<String,Route> routes;
    public TreeMap<String,Airline> airlines;
    public ArrayList<String> routeCodes;
    private Context context;

    public Boolean loaded;
    public XmlReader(Context context) {
        loaded = false;
        cities = new TreeMap<String,Metro>();
        airports = new TreeMap<String,Airport>();
        routes = new TreeMap<String,Route>();
        airlines = new TreeMap<String,Airline>();
        routeCodes = new ArrayList<String>();
        this.context = context;

        new Fetch(XmlReader.this,context);
//        readCitiesFromWeb();

    }

    public void fetchStart() {
        System.out.println("Fetch Started");

    }
    public void fetchComplete(TreeMap<String,Metro> cities,
                              TreeMap<String,Airport> airports,
                              TreeMap<String,Route> routes,
                              TreeMap<String,Airline> airlines,
                              ArrayList<String> routeCodes) {
        System.out.println("Fetch complete");
        this.cities = cities;
        this.airports = airports;
        this.routes = routes;
        this.airlines = airlines;
        this.loaded = true;
        System.out.printf("There are %d cities and %d airports\n",this.cities.size(),this.airports.size());
    }
    public void fetchFailed() {

    }
    public TreeMap<String,Metro> getCities() {return cities;}
    public TreeMap<String,Airport> getAirports() {return airports;}
    public TreeMap<String,Route> getRoutes() {return routes;}
    public TreeMap<String,Airline> getAirlines() {return airlines;}
    //public TreeMap<String,Airport> getCodes() {return codes;}
    private boolean readCities() {
        Scanner cityList;
        String fileName,nextLn,xFactString,yFactString;
        int lineLen;
        double xFact,yFact;
        try {
            cityList = new Scanner(new FileReader("Airports/Airports.txt"));
        } catch (Exception e) {
            return false;
        }
        for (nextLn = "",fileName = "",lineLen = 0;cityList.hasNextLine();)
        {
            nextLn = cityList.nextLine();
            lineLen = nextLn.length();
            fileName = nextLn.substring(0,lineLen-13);
            System.out.printf("Looking at %s\n", fileName);
            xFactString = nextLn.substring(lineLen-12,lineLen-7);
            yFactString = nextLn.substring(lineLen-6);
            xFact = Double.parseDouble(xFactString);
            yFact = Double.parseDouble(yFactString);

            try {
                File xmlFile = new File("Airports/"+fileName+".txt");
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                System.out.println("Testing 1");
                doc.getDocumentElement().normalize();
                String metroName = doc.getElementsByTagName("metro").item(0).getTextContent();

                NodeList airportsList = doc.getElementsByTagName("Airport");

                Metro thisCity = new Metro(metroName,xFact,yFact);

                cities.put("metroName", thisCity);
                System.out.println("Testing 2");

                for (int temp = 0; temp < airportsList.getLength(); temp++) {
                    Element airport = (Element) airportsList.item(temp);
                    String code = airport.getElementsByTagName("code").item(0).getTextContent();
                    String name = airport.getElementsByTagName("name").item(0).getTextContent();

                    System.out.println("Testing 4");
                    Element destinations = (Element) airport.getElementsByTagName("Destinations").item(0);
                    System.out.println("Testing 5");

                    NodeList airlinesList = destinations.getElementsByTagName("Airline");
                    System.out.println("Testing 3");

                    Airport t = new Airport(code,thisCity);
                    t.setName(name);
                    airports.put(code, t);
                    thisCity.addAirport(t);

                    for (int j = 0; j < airlinesList.getLength(); j++) {
                        Element airline = (Element) airlinesList.item(j);
                        String airlineName = airline.getElementsByTagName("Name").item(0).getTextContent();
                        System.out.printf("  Examining airline: %s\n", airlineName);
                        NodeList dests = airline.getElementsByTagName("Dest");

                        Airline current = airlines.get(airlineName);
                        if (current == null) {
                            System.out.printf("   Creating new airline object for %s\n", airlineName);
                            current = new Airline(airlineName);
                            airlines.put(airlineName, current);
                        }

                        System.out.printf("  dests.getLength() = %d\n",dests.getLength());
                        for (int k = 0; k < dests.getLength(); k++) {
                            Element dest = (Element) dests.item(k);
                            String destCode = dest.getTextContent();
                            System.out.printf("    destCode = %s\n", destCode);

                            routeCodes.add(code+destCode+" "+airlineName);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.printf("Failed to read xml file for <%s>\n",fileName);
            }
        }
        cityList.close();
        System.out.println("Testing 8");
        for (String m:routeCodes) {
            String orig = m.substring(0,3);
            String dest = m.substring(3,6);
            String airline = m.substring(7);
            System.out.printf("Origin: %s Destination: %s Airline: %s", orig,dest,airline);
            Airport origin = airports.get(orig);
            Airport destination = airports.get(dest);
            Airline thisAirline = airlines.get(airline);
            if (origin != null && destination != null) {
                Route newRoute = routes.get(orig+dest);
                if (newRoute==null) {
                    newRoute = new Route(origin,destination);
                    routes.put(orig+dest,newRoute);
                }
                newRoute.addOperator(thisAirline);
                thisAirline.addRoute(newRoute);
                origin.addRoute(newRoute);

            }
        }
        return true;
    }

    private boolean readCitiesFromWeb() {
        URL routesURL,airportsURL,airlinesURL;
        String current,airportID,name,city,
                country,codeIATA,codeICAO,latitude,
                longitude,altitude,timezone,DST,Tz,
                nextLn,fileName,xFactString,yFactString;
        double xFact,yFact;
        int lineLen;
        Scanner cityList;

        try {
            InputStream xmlStream = this.context.getResources().openRawResource(R.raw.metrocodes);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlStream);
            System.out.println("Testing 1");
            doc.getDocumentElement().normalize();
            NodeList citiesList = doc.getElementsByTagName("city");

            for (int temp = 0; temp < citiesList.getLength();temp++) {
                Element cityEle = (Element) citiesList.item(temp);
                double xf = Double.parseDouble(cityEle.getElementsByTagName("xFact").item(0).getTextContent());
                double yf = Double.parseDouble(cityEle.getElementsByTagName("yFact").item(0).getTextContent());
                String cityName = cityEle.getElementsByTagName("name").item(0).getTextContent();

                Metro currentCity = new Metro(cityName,xf,yf);
                cities.put(cityName,currentCity);

                NodeList cityAirports = cityEle.getElementsByTagName("airport");
                for (int i = 0; i < cityAirports.getLength();i++) {
                    String airCode = cityAirports.item(i).getTextContent();
                    Airport airpt = new Airport(airCode,currentCity);
                    currentCity.addAirport(airpt);
                    airports.put(airCode,airpt);
                }
            }

        } catch (Exception e) {
            return false;
        }

        try {
            routesURL = new URL("https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat");
            airportsURL = new URL("https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat");
            airlinesURL = new URL("https://raw.githubusercontent.com/jpatokal/openflights/master/data/airlines.dat");



            Scanner routesScanner = new Scanner(routesURL.openStream());
            Scanner airportsScanner = new Scanner(airportsURL.openStream());
            Scanner airlinesScanner = new Scanner(airlinesURL.openStream());
            //System.out.println("Test web 1");
			/*while(airportsScanner.hasNextLine()) {
				current = airportsScanner.nextLine();
				List<String> data = Arrays.asList(current.split(","));
				airportID = data.get(0);	name = data.get(1);
				city = data.get(2);			country = data.get(3);
				codeIATA = data.get(4);		codeICAO = data.get(5);
				latitude = data.get(6);		longitude = data.get(7);
				altitude = data.get(8);		timezone = data.get(9);
				DST = data.get(10);			Tz = data.get(11);
				if (country.equals("United States")) {

				}
			}*/
            //System.out.println("Test web 2");
            String airlineID,airlineName,airlineAlias,IATAcode,ICAOcode,callsign,active;
            while(airlinesScanner.hasNextLine()) {
                current = airlinesScanner.nextLine();
                List<String> data = Arrays.asList(current.split(","));
                //System.out.println(data);
                airlineID = data.get(0);
                airlineName = data.get(1);
                airlineAlias = data.get(2);
                IATAcode = data.get(3);
                ICAOcode = data.get(4);
                callsign = data.get(5);
                country = data.get(6);
                active = data.get(7);
                if (active.equals("\"Y\"") && (IATAcode.length()==4) && country.equals("\"United States\"")) {
                    int lenName = airlineName.length();
                    int lenCode = IATAcode.length();
                    String shortenedName = airlineName.substring(1,lenName-1);
                    String shortenedCode = IATAcode.substring(1,lenCode-1);
                    //System.out.printf("Length = %d    %s    %s/n",lenName,shortenedName,shortenedCode);
                    Airline arln = new Airline(shortenedName);
                    airlines.put(shortenedCode, arln);
                }

            }
            //System.out.println(airlines);
            //System.out.println("Test web 3");
            airlinesScanner.close();
            String sourceAirport,sourceID,destAirport,destID,codeshare,stops,equipment;
            while(routesScanner.hasNextLine()) {
                current = routesScanner.nextLine();
                List<String> data = Arrays.asList(current.split(","));
                IATAcode = data.get(0);
                airlineID = data.get(1);
                sourceAirport = data.get(2);
                sourceID = data.get(3);
                destAirport = data.get(4);
                destID = data.get(5);
                codeshare = data.get(6);
                stops = data.get(7);
                //equipment = data.get(8);
				/*System.out.printf("Input\n");
				System.out.printf("IATAcode = %s     ",IATAcode);
				System.out.printf("airlineID = %s\n",airlineID);
				System.out.printf("sourceAirport = %s     ",sourceAirport);
				System.out.printf("sourceID = %s\n",sourceID);
				System.out.printf("destAirport = %s      ",destAirport);
				System.out.printf("destID = %s\n",destID);
				System.out.printf("codeshare = %s     ",codeshare);
				System.out.printf("stops = %s\n",stops);*/
                //System.out.printf("equipment = %s\n",equipment);
                //System.out.println();
                //System.out.printf("Source: <%s>   Dest: <%s>   Airline: <%s>\n",sourceAirport,destAirport,IATAcode);
                Airport source = airports.get(sourceAirport);
                Airport dest = airports.get(destAirport);
                //System.out.println(IATAcode);
                Airline arln = airlines.get(IATAcode);
                Route thisRoute = routes.get(sourceAirport+destAirport);
/*
				System.out.println("Test web 4");
				try {
					System.out.printf("Source: %s\n", source.toString());
				} catch (Exception e) { System.out.println("Broke at this point"); }
				try {
					System.out.printf("Dest: %s\n", dest.toString());
				} catch (Exception e) { System.out.println("Broke during dest print"); }
				try {
					System.out.printf("Airline: %s\n", arln.toString());
				} catch (Exception e) { System.out.println("Broke during airline print"); }
				*/
                if(source!=null && dest!=null && arln != null) {
                    //System.out.printf("IATAcode = %s sourceAirport = %s destAirport = %s\n", IATAcode,sourceAirport,destAirport);
                    //System.out.println("Test web 5");
                    if (thisRoute==null) {
                        thisRoute = new Route(source,dest);
                        //System.out.println("Test web 6");
                        source.addRoute(thisRoute);
                        //System.out.println("Test web 7");
                        routes.put(sourceAirport+destAirport, thisRoute);
                    }
                    thisRoute.addOperator(arln);

                }
                //System.out.println("Test web 8");
            }
            routesScanner.close();
        } catch (IOException e) {
            System.out.println("Broke here ");
            System.out.println(e);
            return false;
        }

        return true;
    }


	/*public static void main(String argv[]) {

	}*/
}
