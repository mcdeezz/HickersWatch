package com.example.micha.hickerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView longitudeView, latitudeView, accuracyView, altitudeView, addressView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }
    /*
        Start listening to location change after every 0 seconds and 0 distance
      */
    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
      /*
        Initializing all the views
       */
    public void init() {
        longitudeView = findViewById(R.id.longitude_view);
        latitudeView = findViewById(R.id.latitude_view);
        accuracyView = findViewById(R.id.accuracy_view);
        altitudeView = findViewById(R.id.altitude_view);
        addressView = findViewById(R.id.address_view);
    }
    /*
      After listening, if there is a change in location then we update the views with the current
      latitude, accuracy, altitude, altitude and then take the latitude and longitude and turn them in to an address
      @param location
     */
    public void updateLocationInfo(Location locations){
        String lat , lon, accu, alt,address = "Address \n ";
        lat = "Latitude: "+ String.valueOf(locations.getLatitude());
        lon = "Longitude: " + String.valueOf(locations.getLongitude());
        accu = "Accuracy: " + String.valueOf(locations.getAccuracy());
        alt = "Altitude: " + String.valueOf(locations.getAltitude());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            // if for any reason we don't get any address then we need to post the appropriate message
            address += "Could not find Address";
            List<Address> addressList = geocoder.getFromLocation(locations.getLatitude(), locations.getLongitude() , 1);

            // only append the address text if we have an address
            if(addressList != null && addressList.size() > 0) {
                address = "\nAddress \n ";
                if (addressList.get(0).getSubThoroughfare() != null)
                    address += addressList.get(0).getSubThoroughfare() + " ";
                if (addressList.get(0).getThoroughfare() != null)
                    address += addressList.get(0).getThoroughfare() + "\n";
                if (addressList.get(0).getLocality() != null)
                    address += addressList.get(0).getLocality() + "\n";
                if (addressList.get(0).getPostalCode() != null)
                    address += addressList.get(0).getPostalCode() + "\n";
                if (addressList.get(0).getCountryName() != null)
                    address += addressList.get(0).getCountryName();
            }
            // either print the address or the no address message
            addressView.setText(address);

        } catch (IOException e) {

            e.printStackTrace();

        }
        // Updating the rest of the views with the right information
        longitudeView.setText(lon);
        latitudeView.setText(lat);
        accuracyView.setText(accu);
        altitudeView.setText(alt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // A listener for changes in location due to user movements
        locationListener = new LocationListener() {
            
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        // Permission checks for an older version of Android OS
        if(Build.VERSION.SDK_INT < 23){

            startListening();

        }else{
            // from Android version 23 and above we are required to explicitly ask user permission before we can start listening to their location changes
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                // if we do not have permission, then we ask
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                // if we do have permission already thew we start listening
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 , 0 , locationListener);
                // updating the views with the users last known location on app launch
                Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(currentLocation != null)
                    // update only if the last known location does not return null
                updateLocationInfo(currentLocation);
            }

        }
    }
}
