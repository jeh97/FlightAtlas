package cs371m.jh54765.flightatlas;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by thunt on 10/25/16.
 * Holds Maps...What else would it do?
 */

public class MapHolder implements OnMapReadyCallback {
    /* Some from RedFetch some from this example:
    http://theoryapp.com/parse-json-in-java/
     */
    private float defaultZoom = 4.0f;
    private Globals globals;
    private static class NameToLatLngTask extends AsyncTask<String, Object, LatLng> {
        public interface OnLatLngCallback {
            public void onLatLng(LatLng a);
        }

        OnLatLngCallback cb;
        MainActivity context;

        URL geocoderURLBuilder(String address) {
            URL result = null;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("maps.googleapis.com")
                        .appendPath("maps")
                        .appendPath("api")
                        .appendPath("geocode")
                        .appendPath("json")
                        // NB: A key is not necessary, but if it exists, it needs the proper
                        // restrictions.  Oddly, in a URL like this
                        // https://console.developers.google.com/apis/credentials/key/3?project=famous-sunbeam-200419
                        // You have to add permissions in the API restrictions tab before it works.
                        .appendQueryParameter("key", context.getResources().getString(R.string.google_maps_key))
                        .appendQueryParameter("address", URLEncoder.encode(address, "UTF-8"));
                result = new URL(builder.build().toString());
            } catch (UnsupportedEncodingException e) {
                Log.e("Geocoder", "Encoding address: " + e.toString());
            } catch (MalformedURLException e) {
                Log.e("Geocoder", "Building URL: " + e.toString());
            }
            return result;
        }

        public NameToLatLngTask(MainActivity ctx, String addr, OnLatLngCallback _cb) {
            context = ctx;
            execute(addr);
            cb = _cb;
        }

        protected LatLng latLngFromJsonString(String json) throws JSONException {
            JSONObject obj = new JSONObject(json);
            LatLng result = null;
            if (!obj.getString("status").equals("OK")) {
                Log.e("URLfetch", "returned status" + obj.getString("status"));
            } else {
                JSONObject loc = obj.getJSONArray("results").getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location");
                double lat = loc.getDouble("lat");
                double lng = loc.getDouble("lng");
                result = new LatLng(lat, lng);
                Log.d("Geocoder", "got lat: " + lat + ", lng: " + lng);
            }
            return result;
        }

        @Override
        protected LatLng doInBackground(String... params) {
            assert(params.length > 1);
            String name = params[0];
            URL url;
            LatLng pos = null;

            /* Try Geocoder first */
            {
                Geocoder geo = new Geocoder(context);

                /* XXX write me
                    Use the Geocoder object for fast(er) geocoding first
                 */
                try {
                    List<Address> locations = geo.getFromLocationName(name,1);
                    Address add = locations.get(0);
                    pos = new LatLng(add.getLatitude(),add.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /* go remote as a last resort*/
            url = geocoderURLBuilder(name);
            if (url == null) {
                cancel(true);
                return null;
            }

            try {
                String result = null;
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.connect();

                if( urlConn.getContentType().startsWith("application/json") )
                    result = fetchJson(urlConn);
                else
                    Log.e("URLfetch", "Result has bad type (not json)");

                if (result != null)
                        pos = latLngFromJsonString(result);
            } catch (IOException e) {
                Log.e("URLfetch", e.toString());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("JsonBuild", "JSON malformed");
            }

            if (pos == null) {
                cancel(false);
            }
            return pos;
        }

        protected String readStreamToString(InputStream in) throws IOException {
            int numRead;
            final int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream outString = new ByteArrayOutputStream();

            while ((numRead = in.read(buffer)) != -1) {
                outString.write(buffer, 0, numRead);
                if (isCancelled()) {
                    return null;
                }
            }
            return new String(outString.toByteArray(), "UTF-8");
        }

        protected String fetchJson(HttpURLConnection conn) {
            InputStream in = null;
            String result = null;
            try {
                in = new BufferedInputStream(conn.getInputStream());
                result = readStreamToString(in);
                Log.d("fetchJson", "json " + result);
            } catch( IOException e ) {
                e.printStackTrace();
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(LatLng result) {
            cb.onLatLng(result);
        }

        @Override
        protected void onCancelled(LatLng result) {
            Log.e("NameToLatLng", "cancelled");
            // Callback does not expect a null value
            //cb.onLatLng(null);
        }
    }


    private GoogleMap gMap;
    private MainActivity context;
    public ImageView infoButton;

    public MapHolder(MainActivity ctx) {
        context = ctx;
        globals = Globals.getInstance();
    }

    public boolean warnIfNotReady() {
        if (gMap == null) {
            Toast.makeText(context, "No map yet.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                final String IATA = marker.getTitle();
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setGravity(Gravity.CENTER);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                ImageView infoButton = new ImageView(context);
                infoButton = new ImageView(context);
                infoButton.setImageResource(android.R.drawable.ic_menu_info_details);
                infoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                public void onClick(View view) {
                    Log.d("MapHolder",IATA);
                }
            });

                info.addView(title);
                info.addView(snippet);
                info.addView(infoButton);

                return info;
            }
        });

        LatLng pos = new LatLng(0,0);
    }

    public void showAirport(String IATA,boolean moveCamera) {
        if (warnIfNotReady())
            return;
        gMap.clear();
        final String originIATA = IATA;
        Cursor cursor = context.db.getAirportInfo(IATA);
        cursor.moveToFirst();
        double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
        double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
        LatLng pos = new LatLng(lat,lng);
        String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
        String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        // Move camera if entering map from listviews
        if (moveCamera) gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,defaultZoom));
        // Origin Marker
        if (globals.SHOW_MARKERS) {
            Marker originMarker = gMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(IATA)
                    .snippet(String.format("%s\n%s, %s", name, city, country))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            originMarker.showInfoWindow();
        }
        ArrayList<String> routesList = context.db.getAirportRoutesList(IATA);
        int numRoutes = routesList.size();
        for (int i = 0; i < numRoutes; i++) {
            String destination = routesList.get(i);
            cursor = context.db.getAirportInfo(destination);
            cursor.moveToFirst();
            lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
            LatLng pos2 = new LatLng(lat,lng);
            city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
            country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
            name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            if (globals.SHOW_ROUTES) DrawLine(pos,pos2);
//            DrawDestDot(pos2);
            if (globals.SHOW_MARKERS) {
                Marker marker = gMap.addMarker(new MarkerOptions()
                        .position(pos2)
                        .title(destination)
                        .snippet(String.format("%s\n%s, %s",name,city,country)));
            }


            gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String airport = marker.getTitle();
                    showAirport(airport,false);
                }
            });
            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                int lastIndex = -1;
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (lastIndex>0) {
                        marker.hideInfoWindow();
                        return false;
                    }
                    marker.showInfoWindow();
                    lastIndex*=-1;
                    return false;
                }
            });
        }
//        if (infoButton == null) {
//            infoButton = new ImageView(context);
//            infoButton.setImageResource(android.R.drawable.ic_menu_info_details);
//        }
//        infoButton.setTooltipText(IATA);
//        infoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("MapHolder",originIATA);
//            }
//        });
//        Fragment frag = (Fragment) context.findViewById(R.id.main_fragment);
//        frag.addView


    }



    public void DrawLine(LatLng start, LatLng end) {
        Polyline line = gMap.addPolyline(new PolylineOptions()
        .add(start, end)
        .width(3)
        .color(Color.rgb(128,0,0)));
    }


    public void showAirline(String IATA, boolean moveCamera) {
        if (warnIfNotReady()) {
            return;
        }

        gMap.clear();
        Cursor cursor = context.db.getAirlineRoutes(IATA);
        TreeMap<String,Marker> markers = new TreeMap<>();
        while (cursor.moveToNext()) {
            String origIATA = cursor.getString(cursor.getColumnIndexOrThrow("origIATA"));
            String origName = cursor.getString(cursor.getColumnIndexOrThrow("origName"));
            String origCity = cursor.getString(cursor.getColumnIndexOrThrow("origCity"));
            String origCountry = cursor.getString(cursor.getColumnIndexOrThrow("origCountry"));
            Double origLat = cursor.getDouble(cursor.getColumnIndexOrThrow("origLat"));
            Double origLng = cursor.getDouble(cursor.getColumnIndexOrThrow("origLng"));
            LatLng origLatLng = new LatLng(origLat,origLng);

            String destIATA = cursor.getString(cursor.getColumnIndexOrThrow("destIATA"));
            String destName = cursor.getString(cursor.getColumnIndexOrThrow("destName"));
            String destCity = cursor.getString(cursor.getColumnIndexOrThrow("destCity"));
            String destCountry = cursor.getString(cursor.getColumnIndexOrThrow("destCountry"));
            Double destLat = cursor.getDouble(cursor.getColumnIndexOrThrow("destLat"));
            Double destLng = cursor.getDouble(cursor.getColumnIndexOrThrow("destLng"));
            LatLng destLatLng = new LatLng(destLat,destLng);

            if (globals.SHOW_MARKERS) {
                if (!markers.containsKey(origIATA)) {
                    Marker marker = gMap.addMarker(new MarkerOptions()
                            .position(origLatLng)
                            .title(origIATA)
                            .snippet(String.format("%s\n%s, %s", origName, origCity, origCountry))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    markers.put(origIATA,marker);
                }
                if (!markers.containsKey(destIATA)) {
                    Marker marker = gMap.addMarker(new MarkerOptions()
                            .position(destLatLng)
                            .title(destIATA)
                            .snippet(String.format("%s\n%s, %s", destName, destCity, destCountry))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    markers.put(destIATA,marker);
                }
            }
            if (globals.SHOW_ROUTES) {
                DrawLine(origLatLng,destLatLng);
            }


        }
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
    }
}
