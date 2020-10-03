package com.armjld.gymzo.user.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.armjld.gymzo.R;
import com.armjld.gymzo.StartUp;
import com.armjld.gymzo.gym.GymsArea;
import com.armjld.gymzo.gym.gymData;
import com.armjld.gymzo.language.BaseActivity;
import com.armjld.gymzo.login.LoginManager;
import com.armjld.gymzo.user.settings.userSettings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    boolean doubleBackToExitPressedOnce = false;
    public static String whichFrag = "Gyms";
    public static BottomNavigationView bottomNavigationView;
    static String TAG = "Main Activity";

    public static GoogleApiClient mGoogleApiClient;
    public static final int REQUEST_CHECK_SETTINGS = 102;
    public static LocationRequest mLocationRequest;

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("Gyms");
        if(fragment != null && fragment.isVisible()) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                System.exit(0);
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.str_press_again, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
        } else {
            whichFrag = "Home";
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new Gyms(), whichFrag).addToBackStack("Gyms").commit();
            bottomNavigationView.setSelectedItemId(R.id.gyms);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGoogleAPIClient(this);
        if(!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        final LocationManager manager2 = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );

        if (!manager2.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            buildAlertMessageNoGps(this);
        }

        checkLocationPermission(this);
        checkGPS(this);
        buildGoogleAPIClient(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, whichFrag(), whichFrag).addToBackStack("Gyms").commit();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private Fragment whichFrag() {
        Fragment frag = null;
        switch (whichFrag) {
            case "Gyms" : {
                frag = new Gyms();
                bottomNavigationView.setSelectedItemId(R.id.gyms);
                break;
            }
            case "Profile" : {
                frag = new UserProfile();
                bottomNavigationView.setSelectedItemId(R.id.profile);

                break;
            }

            case "Settings" : {
                frag = new userSettings();
                bottomNavigationView.setSelectedItemId(R.id.settings);
                break;
            }
        }
        return frag;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = item -> {
        Fragment fragment = null;
        String fragTag = "";
        switch (item.getItemId()) {
            case R.id.gyms : {
                fragment = new Gyms();
                fragTag = "Gyms";
                break;
            }

            case R.id.profile : {
                fragment = new UserProfile();
                fragTag = "Profile";
                break;
            }

            case R.id.settings : {
                fragment = new userSettings();
                fragTag = "Settings";
                break;
            }

        }
        assert fragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, fragTag).addToBackStack("Gyms").commit();
        return true;
    };

    public void buildGoogleAPIClient(Context mContext) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void checkGPS(Context mContext) {
        if(mGoogleApiClient == null) {
            buildGoogleAPIClient(mContext);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    GymsArea.fetchLocation(this);
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {

                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    checkGPS(this);
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:

                    break;
            }
        }
    }

    public void buildAlertMessageNoGps(Context mContext) {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("الرجاء فتح اعدادات اللوكيشن ؟").setCancelable(true).setPositiveButton("حسنا", R.drawable.ic_tick_green, (dialogInterface, which) -> {
            mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            dialogInterface.dismiss();
        }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
            dialogInterface.dismiss();
        }).build();
        mBottomSheetDialog.show();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public void checkLocationPermission(Context mContext){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            GymsArea.fetchLocation(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleApiClient == null) {
                        buildGoogleAPIClient(this);
                    }
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(@NonNull int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}