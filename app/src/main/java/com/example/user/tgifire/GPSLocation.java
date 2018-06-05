package com.example.user.tgifire;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSLocation implements LocationListener {

    private final Context mContext;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    Location current_location;
    double GPS_X;
    double GPS_Y;

    public GPSLocation(Context context) {

        this.mContext = context;

        getGPS();

    }

    public Location getGPS() {

        if(Build.VERSION.SDK_INT > 23 && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;

        }

        try {

            LocationManager loc_manager =(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = loc_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = loc_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            loc_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            if(isNetworkEnabled) {

                loc_manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

                if(loc_manager != null) {

                    current_location = loc_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if(current_location != null) {

                        GPS_X = current_location.getLatitude();
                        GPS_Y = current_location.getLongitude();

                    }

                }

            }

            if(isGPSEnabled) {

                loc_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                if(loc_manager != null) {

                    current_location = loc_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(current_location != null) {

                        GPS_X = current_location.getLatitude();
                        GPS_Y = current_location.getLongitude();

                    }

                }

            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return current_location;

    }

    public double getGPS_X() {

        return GPS_X;

    }

    public double getGPS_Y() {

        return GPS_Y;

    }

    public static String getAddress(Context mContext, double GPS_X, double GPS_Y) {

        String current_address=null;
        Geocoder geo = new Geocoder(mContext, Locale.KOREA);
        List <Address> address;

        try {

            if(geo != null) {

                address = geo.getFromLocation(GPS_X, GPS_Y, 1);

                if(address != null && address.size() > 0) {

                    current_address = address.get(0).getAddressLine(0).toString();

                }

            }

        } catch(IOException e) {

            e.printStackTrace();

        }

        return current_address;

    }

    @Override
    public void onLocationChanged(Location location) {

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
}