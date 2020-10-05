package com.armjld.gymzo.user.main;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.gymzo.R;
import com.armjld.gymzo.datebase.GymsManager;
import com.armjld.gymzo.data.gymData;
import com.armjld.gymzo.data.UserInFormation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class GymsMap extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    Location currentLocation;
    GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final String TAG = "Maps";
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    CardView gymsCard;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyms_map);
        gymsCard = findViewById(R.id.gymsCard);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> finish());
        final LocationManager manager2 = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if (!manager2.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            buildAlertMessageNoGps();
        } else {
            fetchLocation();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        buildGoogleAPIClient();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        fetchLocation();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        checkGPS();

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleAPIClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleAPIClient();
            mMap.setMyLocationEnabled(true);
        }


        for(int i = 0; i < GymsManager.gymsList.size(); i++) {
            gymData gymData = GymsManager.gymsList.get(i);
            String gymType = gymData.getType();
            double newLat = Double.parseDouble(gymData.getLat());
            double newLong = Double.parseDouble(gymData.get_long());

            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(newLat, newLong)));
            marker.setTag(gymData.getGid());
        }
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
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
    public void onClick(View view) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(@NonNull int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 105);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 1);
            assert dialog != null;
            dialog.show();
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onMarkerClick(Marker marker) {
        String gID = (String) marker.getTag();
        gymData gData = GymsManager.gymsList.stream().filter(x -> x.getGid().equals(gID)).findFirst().get();
        setCardDate(gData);
        gymsCard.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        gymsCard.setVisibility(View.GONE);
    }

    private void setCardDate(gymData gData) {
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtAddress = findViewById(R.id.txtAddress);
        TextView txtCity = findViewById(R.id.txtCity);
        RatingBar rbGym = findViewById(R.id.rbGym);
        ImageView btnFav = findViewById(R.id.btnFav);
        ImageView imgGymFirst = findViewById(R.id.imgGymFirst);
        TextView txtDistance = findViewById(R.id.txtDistance);
        ImageView imgBadge = findViewById(R.id.imgBadge);

        txtTitle.setText(gData.getName());
        txtAddress.setText(gData.getAddress());
        txtCity.setText(gData.getGov() + ", " + gData.getCity());
        rbGym.setRating(Float.parseFloat(gData.getRate()));
        Picasso.get().load(Uri.parse(gData.getPhoto())).into(imgGymFirst);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        float twoDigitsF = Float.valueOf(decimalFormat.format(gData.getDistance()));
        txtDistance.setText(twoDigitsF + " km away.");

        if(gData.getType().equals("1")){
            imgBadge.setVisibility(View.VISIBLE);
        } else if(gData.getType().equals("2")) {
            imgBadge.setVisibility(View.VISIBLE);
        }else if (gData.getType().equals("3")) {
            imgBadge.setVisibility(View.VISIBLE);
        } else {
            imgBadge.setVisibility(View.GONE);
        }

        gymsCard.setOnClickListener(v-> {
            GymProfile.gData = gData;
            startActivity(new Intent(this, GymProfile.class));
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void buildAlertMessageNoGps() {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(GymsMap.this).setMessage("الرجاء فتح اعدادات اللوكيشن ؟").setCancelable(true).setPositiveButton("حسنا", R.drawable.ic_tick_green, (dialogInterface, which) -> {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            dialogInterface.dismiss();
        }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
            dialogInterface.dismiss();
        }).build();
        mBottomSheetDialog.show();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(fusedLocationProviderClient == null) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null) {
                float userDistance = Float.parseFloat(UserInFormation.getGymdistance());
                currentLocation = location;
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                Circle circle = mMap.addCircle(new CircleOptions().center(latLng).radius(userDistance*1000).strokeColor(Color.BLUE).strokeWidth(5));
                circle.isVisible();
                float cZoom = getZoomLevel(circle);
                float zoomLv = cZoom + 5;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLv));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(cZoom), 2000, null);
            }
        });
    }

    private float getZoomLevel(Circle circle) {
        float zoomLevel = 0;
        if(circle != null) {
            double reduis = circle.getRadius();
            double scale = reduis / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel +.5f;
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGoogleAPIClient();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void checkGPS() {

        if(mGoogleApiClient == null) {
            buildGoogleAPIClient();
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            final LocationSettingsStates states = result1.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    fetchLocation();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(GymsMap.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Toast.makeText(this, "This is Sad.", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    fetchLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
    }

    private void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    if (mGoogleApiClient == null) {
                        buildGoogleAPIClient();
                    }
                    mMap.setMyLocationEnabled(true);
                }

            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


}