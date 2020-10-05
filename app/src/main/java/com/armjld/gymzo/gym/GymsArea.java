package com.armjld.gymzo.gym;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.armjld.gymzo.data.gymData;
import com.armjld.gymzo.data.UserInFormation;
import com.armjld.gymzo.user.main.Gyms;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class GymsArea implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static Location currentLocation;
    public static FusedLocationProviderClient fusedLocationProviderClient;

    public static ArrayList<gymData> gymsList = new ArrayList<>();
    public static String TAG = "Gyms Area";
    public static DatabaseReference gymDatabase = FirebaseDatabase.getInstance().getReference().child("gyms");

    public static void getGyms(Context mContext) {
        if(currentLocation == null) {
            fetchLocation(mContext);
            return;
        }
        gymsList.clear();
        gymsList.trimToSize();
        float distanceKilo = Float.parseFloat(UserInFormation.getGymdistance());
        gymDatabase.orderByChild("rate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot eachGym : snapshot.getChildren()) {
                        int count = 0;
                        gymData gData = eachGym.getValue(gymData.class);
                        assert gData != null;

                        double newLat = Double.parseDouble(gData.getLat());
                        double newLong = Double.parseDouble(gData.get_long());
                        Location gymLocation = new Location(LocationManager.GPS_PROVIDER);
                        gymLocation.setLatitude(newLat);
                        gymLocation.setLongitude(newLong);

                        double distanceMeter = currentLocation.distanceTo(gymLocation);

                        Log.i(TAG, "Got it : " + gData.getGid());
                        if(((distanceMeter / 1000) <= distanceKilo) && gData.getGender().equals(UserInFormation.getGender()) || gData.getGender().equals("both")) {
                            count++;
                            gData.setDistance((distanceMeter / 1000));
                            gymsList.add(gData);
                            if(count == 25) {
                                Log.i(TAG, "Loop got 20 Objects");
                                break;
                            }
                        }
                    }

                    Collections.sort(gymsList, (o1, o2) -> {
                        String one = String.valueOf(o1.getDistance());
                        String two = String.valueOf(o2.getDistance());
                        return one.compareTo(two);
                    });

                    Gyms.pobulateRecycler(mContext);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public static void fetchLocation(Context mContext) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null) {
                currentLocation = location;
                getGyms(mContext);
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /*mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }*/
    }

    @Override
    public void onConnectionSuspended(@NonNull int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
