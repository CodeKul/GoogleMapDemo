package com.codekul.googlemapdemo;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LatLng> listLatLng = new ArrayList<>();
    private FusedLocationProviderApi  fusedLocationProviderApi;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initUi();

        initFusedLocation();
    }

    private void initUi(){

        final Geocoder coder = new Geocoder(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.btnPoly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.addPolyline(new PolylineOptions().addAll(listLatLng));
            }
        });

        findViewById(R.id.btnOverlay).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
                groundOverlayOptions.image(BitmapDescriptorFactory.fromResource(R.drawable.my));
                groundOverlayOptions.position(new LatLng(18.5204,73.8567), 8600f, 6500f);

                mMap.addGroundOverlay(groundOverlayOptions);
            }
        });

        findViewById(R.id.btnGeocoding).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                try {
                    //List<Address> listAddress = coder.getFromLocation(18.5204, 73.8567, 5);

                    List<Address> listAddress = coder.getFromLocationName("CCD pune",5);
                    for(Address adrress : listAddress){
                        Log.i("@codekul","Admin Area - "+adrress.getAdminArea());
                        Log.i("@codekul","Country Code - "+adrress.getCountryCode());
                        Log.i("@codekul","Premises - "+adrress.getPremises());
                        Log.i("@codekul","Postal Code - "+adrress.getPostalCode());
                        Log.i("@codekul","Country Name - "+adrress.getCountryName());
                        Log.i("@codekul","Address LIne - "+adrress.getAddressLine(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initFusedLocation(){

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(0.5f);

        if (!isGooglePlayServicesAvailable()) {
            Log.i("@codekul","Play Services not available");
            finish();
        }

        fusedLocationProviderApi = LocationServices.FusedLocationApi;

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                        Log.i("@codekul","OnConnected");

                        fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {

                                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                                listLatLng.add(latLng);

                                Log.i("@codekul", "onLocationChanged");
                                mMap.addMarker(new MarkerOptions().position(latLng));

                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                        Log.i("@codekul","On ConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Log.i("@codekul","onConnectionFailed");
                    }
                })
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        googleApiClient.connect();

    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng markerPune = new LatLng(18.5204,73.8567);
        //mMap.addMarker(new MarkerOptions().position(markerPune).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPune));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker on Click"));
                listLatLng.add(latLng);
            }
        });
    }
}
