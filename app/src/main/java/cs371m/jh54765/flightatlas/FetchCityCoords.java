package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class FetchCityCoords {
    public final int RANGE = 2; // How many degrees away from the city's current LatLng to search for its correct one
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

        new FetchCityCoords.AsyncDownloader().execute();
    }
    public class AsyncDownloader extends AsyncTask<String,Double,Void> {
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

        @Override
        protected void onProgressUpdate(Double... values) {
        }

        protected Void doInBackground(String... urls) {
            try {

            } catch (Exception e) {

            }
            return null;
        }

        private Void updateCityCoords() {
            DBManager db = DBManager.getInstance(context);
            Cursor cursor = db.getCities(null,true);

            // For each city, get airports
            // if city's airports have no routes, delete city and airports from database
            // else, update city coords with geocoder
            while (cursor.moveToNext()) {
                String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
                String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
                String searchTerm = String.format("%s, %s",city,country);
                Double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                Double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                double lLLat = lat-RANGE;
                double lLLng = lng-RANGE;
                double uRLat = lat+RANGE;
                double uRLng = lng+RANGE;

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

                try {
                    LatLng pos = null;
                    Geocoder geo = new Geocoder(context);
                    List<Address> locations = geo.getFromLocationName(searchTerm,
                            1, lLLat, lLLng, uRLat, uRLng);
                    Address add = locations.get(0);

                    LatLng latlng = new LatLng(add.getLatitude(),add.getLongitude());
                    db.updateCityLatLng(city,country,latlng);

                } catch(Exception e) {
                    Log.e("Geocoder",e.getMessage());
                    e.printStackTrace();
                    Log.e("Geocoder",String.format("City was %s",searchTerm));
                }

            }
            return null;
        }
    }



}
