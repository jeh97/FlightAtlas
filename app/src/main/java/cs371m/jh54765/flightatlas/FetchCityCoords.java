package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchCityCoords {
    public final double RANGE = .75; // How many degrees away from the city's current LatLng to search for its correct one

    private ProgressBar progress;
    private TextView progressMessage;
    public ArrayList<String> progressMessages;

    public interface FetchCityCoordsCallback {
        void fetchStart();
        void fetchComplete();
        void fetchFailed();
    }

    protected FetchCityCoordsCallback fetchCallback;
    protected Context context = null;
    public FetchCityCoords(FetchCityCoordsCallback fetchCallback, Context context) {
        this.fetchCallback = fetchCallback;
        this.context = context;

        progressMessages = new ArrayList<>();
        progressMessages.add("Starting fetch...");
        progressMessages.add("Starting network loading...");
        progressMessages.add("Loading airports...");
        progressMessages.add("Loading airlines...");
        progressMessages.add("Loading routes...");
        progressMessages.add("Fixing city locations...");
        progressMessages.add("Finishing up...");

        MainActivity mainActivity = (MainActivity) context;

        progress = mainActivity.findViewById(R.id.progressBar_loading);
        progressMessage = mainActivity.findViewById(R.id.textView_loadingMessage);
        progress.setMax(1);
        progress.setProgress(0);
        progressMessage.setText("Starting fetch...");

        new FetchCityCoords.AsyncDownloader().execute();
    }
    public class AsyncDownloader extends AsyncTask<String,Integer,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            fetchCallback.fetchStart();

        }
        @Override
        protected void onPostExecute(Void nada) {
            super.onPostExecute(nada);
            fetchCallback.fetchComplete();
        }
        @Override
        protected void onCancelled(Void nada) {
            fetchCallback.fetchFailed();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progress.setMax(values[1]);
            progress.setProgress(values[0]);

        }

        protected Void doInBackground(String... urls) {
            try {
                updateCityCoords();
            } catch (Exception e) {

            }
            return null;
        }

        private Void updateCityCoords() {
            Map<String, String> states = new HashMap<>();
            states.put("Alabama","AL");
            states.put("Alaska","AK");
            states.put("Alberta","AB");
            states.put("American Samoa","AS");
            states.put("Arizona","AZ");
            states.put("Arkansas","AR");
            states.put("Armed Forces (AE)","AE");
            states.put("Armed Forces Americas","AA");
            states.put("Armed Forces Pacific","AP");
            states.put("British Columbia","BC");
            states.put("California","CA");
            states.put("Colorado","CO");
            states.put("Connecticut","CT");
            states.put("Delaware","DE");
            states.put("District Of Columbia","DC");
            states.put("Florida","FL");
            states.put("Georgia","GA");
            states.put("Guam","GU");
            states.put("Hawaii","HI");
            states.put("Idaho","ID");
            states.put("Illinois","IL");
            states.put("Indiana","IN");
            states.put("Iowa","IA");
            states.put("Kansas","KS");
            states.put("Kentucky","KY");
            states.put("Louisiana","LA");
            states.put("Maine","ME");
            states.put("Manitoba","MB");
            states.put("Maryland","MD");
            states.put("Massachusetts","MA");
            states.put("Michigan","MI");
            states.put("Minnesota","MN");
            states.put("Mississippi","MS");
            states.put("Missouri","MO");
            states.put("Montana","MT");
            states.put("Nebraska","NE");
            states.put("Nevada","NV");
            states.put("New Brunswick","NB");
            states.put("New Hampshire","NH");
            states.put("New Jersey","NJ");
            states.put("New Mexico","NM");
            states.put("New York","NY");
            states.put("Newfoundland","NF");
            states.put("North Carolina","NC");
            states.put("North Dakota","ND");
            states.put("Northwest Territories","NT");
            states.put("Nova Scotia","NS");
            states.put("Nunavut","NU");
            states.put("Ohio","OH");
            states.put("Oklahoma","OK");
            states.put("Ontario","ON");
            states.put("Oregon","OR");
            states.put("Pennsylvania","PA");
            states.put("Prince Edward Island","PE");
            states.put("Puerto Rico","PR");
            states.put("Quebec","PQ");
            states.put("Rhode Island","RI");
            states.put("Saskatchewan","SK");
            states.put("South Carolina","SC");
            states.put("South Dakota","SD");
            states.put("Tennessee","TN");
            states.put("Texas","TX");
            states.put("Utah","UT");
            states.put("Vermont","VT");
            states.put("Virgin Islands","VI");
            states.put("Virginia","VA");
            states.put("Washington","WA");
            states.put("West Virginia","WV");
            states.put("Wisconsin","WI");
            states.put("Wyoming","WY");
            states.put("Yukon Territory","YT");
            DBManager db = DBManager.getInstance(context);
            Cursor cursor = db.getCitiesAndAirport(null,true);
            Log.d("FetchCityCoords",String.format("%d cities",cursor.getCount()));

            int rows = cursor.getCount();
            int curRow = 0;
            Geocoder geo = new Geocoder(context);

            // For each city, get airports
            // if city's airports have no routes, delete city and airports from database
            // else, update city coords with geocoder



            while (cursor.moveToNext()) {
                publishProgress(curRow,rows);
                String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
                String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
                String searchTerm = String.format("%s, %s",city,country);
                Double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("cityLat"));
                Double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("cityLng"));

                double aLat = cursor.getDouble(cursor.getColumnIndexOrThrow("airLat"));
                double aLng = cursor.getDouble(cursor.getColumnIndexOrThrow("airLng"));

                Log.v("FetchCityCoords",String.format("Updating %s, %s",city,country));

                // get airport

                if (((aLat - lat) < (-1*RANGE)) || ((aLat-lat) > RANGE) || ((aLng - lng) < (-1*RANGE)) || ((aLng - lng) > RANGE) ) {
                    // use airports latitude
                    try {
                        double lLLat = aLat-RANGE;
                        double lLLng = aLng-RANGE;
                        double uRLat = aLat+RANGE;
                        double uRLng = aLng+RANGE;
                        if (lLLat < -180) {
                            lLLat = -180.0;
                        }
                        if (lLLng < -180) {
                            lLLng += 360;
                        }
                        if (uRLat > 180) {
                            uRLat = 180.0;
                        }
                        if (uRLng > 180) {
                            uRLng -= 360;
                        }
                        // get airport admin
                        List<Address> locations = geo.getFromLocation(aLat,aLng,1);
                        Address add = locations.get(0);
                        String admin = states.get(add.getAdminArea());

                        // if airport admin null
                        if (admin == null) {
                            // set city lat to airport lat
                            db.updateCityLatLng(city,country,new LatLng(aLat,aLng));
                        // else
                        } else {
                            int adminLen = admin.length();
                            int cityLen = city.length();
                            // set city name to city + admin
                            if (adminLen >= cityLen || !city.substring(cityLen-adminLen).equals(admin)) {
                                String newCity = String.format("%s, %s",city,admin);
                                String newSearchTerm = String.format("%s, %s",newCity,country);
                                Log.d("FetchCityCoords",String.format("New city name is %s",newSearchTerm));
                                // get latlng again
                                locations = geo.getFromLocationName(newSearchTerm,
                                        1, lLLat, lLLng, uRLat, uRLng);
                                if (locations == null || locations.size() == 0) {
                                    db.renameCity(city,country,newCity);
                                    continue;
                                }
                                add = locations.get(0);
                                double newLat = add.getLatitude();
                                double newLng = add.getLongitude();

                                // if out of range again
                                if ((aLat - newLat < -1*RANGE) || (aLat-newLat > RANGE) ||
                                        (aLng - newLng < -1*RANGE) || (aLng - newLng > RANGE)) {
                                    // set city lat to airport lat
                                    db.updateCityLatLng(city,country,new LatLng(aLat,aLng));
                                } else {
                                    db.updateCityLatLng(city,country,new LatLng(newLat,newLng));
                                    db.renameCity(city,country,newCity);
                                }

                            } else if (city.substring(cityLen-2-adminLen).equals(String.format(", %s",admin))) {
                                db.updateCityLatLng(city,country, new LatLng(aLat,aLng));
                            } else {
                                String newCity = String.format("%s, %s",city.substring(0,cityLen-1-adminLen),admin);
                                db.updateCityLatLng(city,country, new LatLng(aLat,aLng));
                                db.renameCity(city,country,newCity);
                            }


                        }
                    } catch (Exception e) {
                        Log.e("Geocoder",e.getMessage());
                        e.printStackTrace();
                        Log.e("Geocoder",String.format("City was %s",searchTerm));
                    }





                }
                //



                curRow++;

            }
            return null;
        }
    }



}
