package com.example.bkrad_bn;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bkrad_bn.fragment.AccountFragment;
import com.example.bkrad_bn.fragment.HistoryFragment;
import com.example.bkrad_bn.fragment.HomeFragment;
import com.example.bkrad_bn.fragment.ManageDeviceFragment;
import com.example.bkrad_bn.fragment.MoreFragment;
import com.example.bkrad_bn.model.Device;
import com.example.bkrad_bn.ultils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

//    private TextView mTextMessage;
    private Fragment fragment = null;
    private FragmentManager manager = null;
    private FragmentTransaction transaction =  null;
    MenuItem menuItem = null;
    public static String tmpNgayThangNam = "";
    public static String selectedDateTime = "";
    public static String formatDateTime = "";
    public static int selectedDeviceId = 0;
    public static String selectedDevice = "";
    private Calendar calendar;
    private int year, month, day;

    public static DateChanged dateChangedListener;

    public static MediaPlayer mediaPlayer = null;
    public static boolean isWarningSound = true;

    SharedPreferences preferences = null;

    Context mContext;

    RequestQueue queue;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transaction = manager.beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

            switch (item.getItemId()) {
                case R.id.mnuHome:
                    fragment = new HomeFragment();
                    fragment.onAttach(getApplicationContext());
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    setTitle("Giám sát hệ thống");

                    return true;
                case R.id.mnuHistory:
                    setTitle("Lịch sử");
                    fragment = new HistoryFragment();
                    fragment.onAttach(getApplicationContext());
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();

                    return true;
                case R.id.mnuMore:
                    fragment = new MoreFragment();
                    fragment.onAttach(getApplicationContext());
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    setTitle("Thêm");

                    return true;
                case R.id.mnuManage:
                    fragment = new ManageDeviceFragment();
                    fragment.onAttach(getApplicationContext());
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    setTitle("Quản lý thiết bị");

                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = (MainActivity) this;

        queue = Volley.newRequestQueue(mContext);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        manager = getSupportFragmentManager();

//        tmpNgayThangNam = XuLyThoiGian.layNgayHienTai();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        preferences = getSharedPreferences(Constant.IS_WARNING_SOUND, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant.IS_WARNING_SOUND, MainActivity.isWarningSound);
        editor.commit();

        checkAuthentication();

        if ( month < 9 ){
            if (day<10){
                selectedDateTime = "0" + day + "-" + "0"+ (month+1) +"-" + year;
                formatDateTime = "0"+ (month+1) + "0" + day + "-" +"-" + year;
            } else {
                selectedDateTime = day + "-" + "0" + (month + 1) + "-" + year;
                formatDateTime = "0" + (month + 1) + "-" + day + "-" + year;
            }
        } else{
            if (day<10){
                selectedDateTime = "0" + day + "-" +  (month+1) +"-" + year;
                formatDateTime = (month + 1) + "-" + "0" + day + "-" + year;
            } else {
                selectedDateTime = day + "-" + (month + 1) + "-" + year;
                formatDateTime = (month + 1) + "-" + day + "-" + year;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuExit) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit Application?");
            alertDialogBuilder
                    .setMessage("Click yes to exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(homeIntent);
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (item.getItemId() == R.id.mnuTime){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener , year, month, day);
            datePickerDialog.show();
        }


        return true;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day

                    day = arg3;
                    month = arg2;
                    year = arg1;

                    if ( month < 9 ){
                        if (day<10){
                            selectedDateTime = "0" + day + "-" + "0"+ (month+1) +"-" + year;
                            formatDateTime = "0"+ (month+1) + "-" + "0" + day + "-" + year;
                        } else {
                            selectedDateTime = day + "-" + "0" + (month + 1) + "-" + year;
                            formatDateTime = "0" + (month + 1) + "-" + day + "-" + year;
                        }
                    } else{
                        if (day<10){
                            selectedDateTime = "0" + day + "-" +  (month+1) +"-" + year;
                            formatDateTime = (month + 1) + "-" + "0" + day + "-" + year;
                        } else {
                            selectedDateTime = day + "-" + (month + 1) + "-" + year;
                            formatDateTime = (month + 1) + "-" + day + "-" + year;
                        }
                    }


                    dateChangedListener.onDateChanged(selectedDateTime, formatDateTime);
                }
            };

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
    }

    public interface DateChanged {
        public void onDateChanged(String selectedDate, String formatDate);
    }

    public static void setOnDateChangedListener(DateChanged onDateChangedListener){
        dateChangedListener = onDateChangedListener;
    }

    private void checkAuthentication() {
        String url = Constant.URL + Constant.API_GET_ALL_MARKER;
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(final String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setMessage("Vui lòng đăng nhập !!!");
                builder1.setTitle("Login");
                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();

                                Intent intent = new Intent(mContext, LoginActivity.class);
                                startActivity(intent);
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        preferences = getSharedPreferences(Constant.ACCESS_CODE,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.ACCESS_TOKEN, "");
        editor.commit();
    }
}
