package com.example.bkrad_bn.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.example.bkrad_bn.LoginActivity;
import com.example.bkrad_bn.MainActivity;
import com.example.bkrad_bn.R;
import com.example.bkrad_bn.adapter.CustomInforWindowAdapter;
import com.example.bkrad_bn.model.Customer;
import com.example.bkrad_bn.model.Device;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.Single;
import io.reactivex.functions.Action;

import static android.content.Context.MODE_PRIVATE;

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

    RequestQueue queue;

    public HomeFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        mContext = (MainActivity) getContext();
        preferences = mContext.getSharedPreferences(Constant.IS_WARNING_SOUND, MODE_PRIVATE);
        MainActivity.isWarningSound = preferences.getBoolean(Constant.IS_WARNING_SOUND, true);

        initWidgets(view);

        preferences = mContext.getSharedPreferences(Constant.FIRST_TIME_USE_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant.FIRST_TIME_USE_APP, IS_FIRST_TIME_USE_APP);
        editor.commit();

        preferences = mContext.getSharedPreferences(Constant.ACCESS_CODE, MODE_PRIVATE);
        String token = preferences.getString(Constant.ACCESS_TOKEN, "");

        queue = Volley.newRequestQueue(mContext);

        releaseMediaPlayer(MainActivity.mediaPlayer);

        if (!token.equals("")){
            HubConnection hubConnection = HubConnectionBuilder.create(Constant.URL + "radHub")
                    .withAccessTokenProvider(Single.defer(() -> {
                        // Your logic here.
                        return Single.just(token);
                    })).build();

            hubConnection.start().doOnComplete(new Action() {
                @Override
                public void run() throws Exception {
                    System.out.println("New Message: " + "Connected");
//                    Exception exception =


                }
            });

            hubConnection.on("ReceivePayload", (message) -> {
//                try {
//                    Gson gson = new Gson();
//                    String json = gson.toJson(message);
//                    JSONObject jsonObject = new JSONObject(json);
//                    System.out.print(jsonObject.getString("Datetime_Packet"));
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//                clearMap();
//                releaseMediaPlayer(MainActivity.mediaPlayer);
//                getListDevices();

            },Object.class);
        } else {
            System.out.println("error");
        }

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
                    .icon(dev.isWarning() ? BitmapDescriptorFactory.fromResource(R.drawable.pxdo) : BitmapDescriptorFactory.fromResource(R.drawable.pxxanh))
                    .position(new LatLng(dev.getLat(), dev.getLon()))
                    .title(dev.getName())
                    .snippet("Radiation detection"));
            marker.setTag(dev);
            marker.showInfoWindow();

            if (dev.isWarning() && !FIRST_WARNING && MainActivity.isWarningSound){
                FIRST_WARNING = true;

                releaseMediaPlayer(MainActivity.mediaPlayer);

                MainActivity.mediaPlayer = MediaPlayer.create(mContext, R.raw.warning_sound);
                MainActivity.mediaPlayer.start(); // no need to call prepare(); create() does that for you
                MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    };
                });
            }
            boolean test = listDevices.get(i).equals(favoriteDev);
            System.out.println(test);
            if (listDevices.get(i).equals(favoriteDev) && favoriteDev != null) {
                latLng0 = new LatLng(favoriteDev.getLat(), favoriteDev.getLon());
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng0, 10));
                marker.showInfoWindow();
            }
        }
    }

    private void initWidgets(View view) {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cvSound = view.findViewById(R.id.cvSound);
        ivWarningSound = view.findViewById(R.id.ivWarningSound);

        cvSound.setOnClickListener(this);
        ivWarningSound.setImageDrawable(MainActivity.isWarningSound ? getResources().getDrawable(R.drawable.ic_volume_on) :
                getResources().getDrawable(R.drawable.ic_volume_off));
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

    private void getListDevices() {
        String url = Constant.URL + Constant.API_GET_ALL_MARKER;
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(final String response) {

                try {
                    JSONArray root = new JSONArray(response);
                    listDevices = new ArrayList<>();
                    for (int i = 0 ; i < root.length(); i++){
                        JSONObject secRoot = root.getJSONObject(i);
                        JSONObject thirdRoot = secRoot.getJSONObject("Projector");
                        Device device = new Device();
                        device.setId(secRoot.getInt("ProjectorId"));
                        device.setGamma((float) secRoot.getDouble("Gamma"));
                        device.setNeutron((float) secRoot.getDouble("Neutron"));
                        device.setLat((float) secRoot.getDouble("Latitude"));
                        device.setLon((float) secRoot.getDouble("Longitude"));
                        device.setName(thirdRoot.getString("Name"));
                        device.setImei(thirdRoot.getString("Imei"));
                        device.setWarning(secRoot.getBoolean("IsWarning"));
                        device.setCreateDate(secRoot.getString("CreatedDate"));

                        listDevices.add(device);
                    }


                    preferences = mContext.getSharedPreferences(Constant.FIRST_TIME_USE_APP, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    IS_FIRST_TIME_USE_APP = preferences.getBoolean(Constant.FIRST_TIME_USE_APP, true);

                    if (listDevices.size() > 0 && IS_FIRST_TIME_USE_APP){
                        favoriteDev = listDevices.get(0);

                        IS_FIRST_TIME_USE_APP = !IS_FIRST_TIME_USE_APP;
                        editor.putBoolean(Constant.FIRST_TIME_USE_APP, IS_FIRST_TIME_USE_APP);
                        editor.commit();
                    }

                    updateDeviesMap(listDevices);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

//                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
//                builder1.setMessage("Vui lòng đăng nhập !!!");
//                builder1.setTitle("Lỗi chưa đăng nhập");
//                builder1.setPositiveButton(
//                        "OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                                getActivity().finish();
//
//                                Intent intent = new Intent(mContext, LoginActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//
//                AlertDialog alert11 = builder1.create();
//                alert11.show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences pref = mContext.getSharedPreferences(Constant.ACCESS_CODE, MODE_PRIVATE);
                String access_token = pref.getString(Constant.ACCESS_TOKEN,"");
                params.put("Authorization","Bearer "+ access_token);
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int statusCode = response.statusCode;

                if (statusCode==400) //unauthorized
                {
                    onResume();
                } else if (statusCode == 401){

                }
                return super.parseNetworkResponse(response);
            }
        };

        queue.add(request);

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
                    Toast.makeText(mContext, "Permission Denied!", Toast.LENGTH_SHORT).show();
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
        getListDevices();

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

        getListDevices();

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
                getListDevices();

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

            preferences = mContext.getSharedPreferences(Constant.IS_WARNING_SOUND, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constant.IS_WARNING_SOUND, MainActivity.isWarningSound);
            editor.commit();
        }
    }
}


