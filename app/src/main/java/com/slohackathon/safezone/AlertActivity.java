package com.slohackathon.safezone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.slohackathon.safezone.Model.Tracking;

public class AlertActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private GoogleMap mMap;
    private LocationManager locationmanager;
    private LocationListener locationListener;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mLocationDatabaseReference;
    DatabaseReference onlineRef, currentUserReference, counterRef, locations;
    private Button help;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        help=(Button)findViewById(R.id.emergency);
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mLocationDatabaseReference= mFirebaseDatabase.getReference().child("mycurrentlocation");
        //mLocationDatabaseReference.setValue("Hello, World!");
        locations=FirebaseDatabase.getInstance().getReference("Locations");
        onlineRef=FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef=FirebaseDatabase.getInstance().getReference("lastOnline");
        currentUserReference=FirebaseDatabase.getInstance().getReference("lastOnline").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        buildGoogleApiClient();
        locationmanager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Location*******", location.toString());

            }

            @Override

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23)
        {
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else
            {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
            else
            {
                locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AlertActivity.this, MapTracking.class);
                startActivity(intent);
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        locationmanager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Location*******", location.toString());
                mLastLocation=location;

                mMap.clear();
                LatLng newlocation=new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(newlocation).title("I am here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newlocation));
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            String.valueOf(location.getLatitude()),
                            String.valueOf(location.getLongitude())));

                    //mLocationDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Helllloooooooo");
                    //Log.d("Firebase*******",mLastLocation.toString() );
                    //mLocationDatabaseReference.push().setValue("Latitude : " + mLastLocation.getLatitude() + "  & Longitude : " + mLastLocation.getLongitude());
                    Toast.makeText(AlertActivity.this, "Location saved to the Firebasedatabase", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(AlertActivity.this, "Location not saved to the Firebasedatabase", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23)
        {
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
            else
            {
                locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

        // Add a marker in Sydney and move the camera

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //mLocationDatabaseReference.push().setValue("Latitude : " + mLastLocation.getLatitude() + "  & Longitude : " + mLastLocation.getLongitude());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;

        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    String.valueOf(mLastLocation.getLatitude()),
                    String.valueOf(mLastLocation.getLongitude())));

            //mLocationDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Helllloooooooo");
            //Log.d("Firebase*******",mLastLocation.toString() );
            //mLocationDatabaseReference.push().setValue("Latitude : " + mLastLocation.getLatitude() + "  & Longitude : " + mLastLocation.getLongitude());
            Toast.makeText(AlertActivity.this, "Location saved to the Firebasedatabase", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(AlertActivity.this, "Location not saved to the Firebasedatabase", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {



        }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mLocationDatabaseReference.push().setValue("Latitude : " + mLastLocation.getLatitude() + "  & Longitude : " + mLastLocation.getLongitude());
    }
}


