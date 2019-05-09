package com.example.bkrad_bn.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bkrad_bn.MainActivity;
import com.example.bkrad_bn.R;
import com.example.bkrad_bn.adapter.CustomInforWindowAdapter;
import com.example.bkrad_bn.model.Device;
import com.example.bkrad_bn.task.Constants;
import com.example.bkrad_bn.ultils.Constant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    ArrayList<Device> listDevices;
    SupportMapFragment mapFragment;
    GoogleMap map;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    Context mContext;

    float MAX_ALPHA_THRESHOLD = 29f;
    float MAX_BETA_THRESHOLD = 79f;

    static Random random = null;

    float alpha = (float) Math.random();
    float beta = (float) Math.random();

    public static Device favoriteDev = null;
    public boolean IS_FIRST_TIME_USE_APP = true;

    SharedPreferences preferences = null;
    MediaPlayer mediaPlayer = null;

    CardView cvSound;
    ImageView ivWarningSound;

    public HomeFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        MainActivity.isWarningSound = true;
        initWidgets(view);

        mContext = (MainActivity) getContext();
        preferences = mContext.getSharedPreferences(Constants.FIRST_TIME_USE_APP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.FIRST_TIME_USE_APP, IS_FIRST_TIME_USE_APP);
        editor.commit();

        return view;
    }

    private void updateDeviesMap(ArrayList<Device> listDevices) {
        CustomInforWindowAdapter inforWindowAdapter = new CustomInforWindowAdapter(mContext);
        map.setInfoWindowAdapter(inforWindowAdapter);

        LatLng latLng0 = null;
        boolean FIRST_WARNING = false;
        for (int i = 0; i < listDevices.size(); i++) {
            Device dev = listDevices.get(i);
            Marker marker = map.addMarker(new MarkerOptions()
                    .icon(outOfRangeValue(dev.getAlpha(), dev.getBeta()) ? BitmapDescriptorFactory.fromResource(R.drawable.pxdo) : BitmapDescriptorFactory.fromResource(R.drawable.pxxanh))
                    .position(new LatLng(dev.getLat(), dev.getLon()))
                    .title(dev.getName())
                    .snippet("Radiation detection"));
            marker.setTag(dev);
//            marker.showInfoWindow();

            if (outOfRangeValue(dev.getAlpha(), dev.getBeta()) && !FIRST_WARNING && MainActivity.isWarningSound){
                FIRST_WARNING = true;
                MainActivity.mediaPlayer = MediaPlayer.create(mContext, R.raw.warning_sound);
                MainActivity.mediaPlayer.start(); // no need to call prepare(); create() does that for you
                MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    };
                });
            }

            if (listDevices.get(i).equals(favoriteDev) && favoriteDev != null) {
                latLng0 = new LatLng(favoriteDev.getLat(), favoriteDev.getLon());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng0, 10));
                marker.showInfoWindow();
            }

        }



    }

    private boolean outOfRangeValue(float alpha, float beta){
        if ((alpha > MAX_ALPHA_THRESHOLD) || (beta > MAX_BETA_THRESHOLD)){
            return true;
        }
        return false;
    }

    private void initWidgets(View view) {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cvSound = view.findViewById(R.id.cvSound);
        ivWarningSound = view.findViewById(R.id.ivWarningSound);

        cvSound.setOnClickListener(this);
        ivWarningSound.setImageDrawable(MainActivity.isWarningSound ? getResources().getDrawable(R.drawable.ic_volume_on) :
                getResources().getDrawable(R.drawable.ic_volume_off) );
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private ArrayList<Device> getListDevices() {
        ArrayList<Device> list = new ArrayList<>();

        Device dev0 = new Device(1, "SANSLAB01", "00000001", random.nextInt(5) + 25.5f, random.nextInt(5) + 75.5f, 21.122567f, 106.006095f);
        Device dev1 = new Device(2, "SANSLAB02", "00000002", random.nextInt(5) + 25.5f, random.nextInt(5) + 75.5f,  21.004314f, 105.8419583f);
        Device dev2 = new Device(3, "SANSLAB03", "00000003", random.nextInt(5) + 25.5f, random.nextInt(5) + 75.5f,  20.997144f, 105.8569703f);
        Device dev3 = new Device(4, "SANSLAB04", "00000004", random.nextInt(5) + 25.5f, random.nextInt(5) + 75.5f,  20.98896f, 105.8388993f);

        list.add(dev0);
        list.add(dev1);
        list.add(dev2);
        list.add(dev3);

        preferences = mContext.getSharedPreferences(Constants.FIRST_TIME_USE_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        IS_FIRST_TIME_USE_APP = preferences.getBoolean(Constants.FIRST_TIME_USE_APP, true);

        if (list.size() > 0 && IS_FIRST_TIME_USE_APP){
            favoriteDev = list.get(0);

            IS_FIRST_TIME_USE_APP = !IS_FIRST_TIME_USE_APP;
            editor.putBoolean(Constants.FIRST_TIME_USE_APP, IS_FIRST_TIME_USE_APP);
            editor.commit();
        } else {

        }

        return list;
    }


    private void setUpMap() {
        map.clear();
//        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(true);
//        map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setBuildingsEnabled(true);
        map.getMinZoomLevel();

        if (ActivityCompat.checkSelfPermission((MainActivity)mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission((MainActivity)mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        LatLng latLng0 = new LatLng(21.122567, 106.006095);
        CameraPosition googlePlex = CameraPosition.builder()
                .target(latLng0)
                .zoom(10)
                .build();

//        map.animateCamera(CameraUpdateFactory.zoomTo(13));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 500, null);
    }

    public synchronized void buildGoogoleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!checkPermissions())
//                requestLocationPermission();

            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            Permissions.check(mContext, permissions, null, null, new PermissionHandler() {
                @Override
                public void onGranted() {
                    // do your task.
                    if (ContextCompat.checkSelfPermission((MainActivity)mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogoleApiClient();
                        }
//                        map.setMyLocationEnabled(true);
                        onResume();
                    }
                }

                @Override
                public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                    Toast.makeText(mContext, "Denied", Toast.LENGTH_SHORT).show();
                }
            });
        }

        map = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission((MainActivity)mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
//                buildGoogoleApiClient();
//                map.setMyLocationEnabled(true);

                if (mGoogleApiClient == null) {
                    buildGoogoleApiClient();
                }
//                        map.setMyLocationEnabled(true);
                onResume();

            }
        } else {
            buildGoogoleApiClient();

            //show button My Location
            map.setMyLocationEnabled(true);
        }

        setUpMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        double TIME_TO_UPDATE = 5000;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval((long) TIME_TO_UPDATE);
        mLocationRequest.setFastestInterval((long) TIME_TO_UPDATE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission((MainActivity)mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        clearMap();

        releaseMediaPlayer(MainActivity.mediaPlayer);


        random = new Random();
        listDevices = getListDevices();
        updateDeviesMap(listDevices);
    }

    private void clearMap() {
        if (map != null){
            map.clear();
        }
    }

    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission((MainActivity)mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

//    public void requestLocationPermission() {
//
//        // Asking user if explanation is needed
//        boolean isShowRationale = ActivityCompat.shouldShowRequestPermissionRationale((MainActivity)mContext,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        if (isShowRationale) {
////                onRestart();
//
//        } else {
//            // No explanation needed, we can request the permission.
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_LOCATION);
//        }
//
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission((MainActivity)mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogoleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                        onResume();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();

//        if (preferences != null)
//            TIME_TO_UPDATE = Double.parseDouble(preferences.getString(CommonDefine.TIME_UPDATE_LOCATION, "10")) * 1000;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval((long) 5000);
        mLocationRequest.setFastestInterval((long) 5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkPermissions()) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (ActivityCompat.checkSelfPermission((MainActivity)mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission((MainActivity)mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                clearMap();
                random = new Random();
                listDevices = getListDevices();
                updateDeviesMap(listDevices);
            }
        }


    }

    @Override
    public void onPause() {
        releaseMediaPlayer(MainActivity.mediaPlayer);
        super.onPause();
    }

    private void releaseMediaPlayer(MediaPlayer mp){
        try {
//            mp.reset();
//            mp.prepare();
//            mp.stop();
            if (mp != null)
                mp.release();
            mp = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cvSound){
            MainActivity.isWarningSound = !MainActivity.isWarningSound;

            ivWarningSound.setImageDrawable(MainActivity.isWarningSound ? getResources().getDrawable(R.drawable.ic_volume_on) :
                    getResources().getDrawable(R.drawable.ic_volume_off) );
        }
    }
}


