package com.example.bkrad_bn;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bkrad_bn.adapter.ManageDeviceAdapter;
import com.example.bkrad_bn.model.Device;
import com.example.bkrad_bn.ultils.Constant;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailDeviceActivity extends AppCompatActivity {

    Toolbar toolbar;

    Intent intent;
    Bundle bundle;

    Device device;

    Context mContext;
    RequestQueue queue;

    TextView txtGammaDev, txtNeutronDev, txtLocationDev, txtLastestTimeDev, txtNameDev, txtNameEnterprise, txtAddressEnterprise, txtPhoneEnterprise;
    CardView cvPhoneEnterprise;

    String phoneEnterprise = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_device);

        initWidgets();

        mContext = this;
        queue = Volley.newRequestQueue(mContext);

        intent = getIntent();
        if (intent != null){
            bundle = intent.getBundleExtra(Constant.DETAIL_DEV);
            device = (Device) bundle.getSerializable(Constant.DETAIL_DEV);
            toolbar.setTitle(device.getName());

            getInforDev();

        } else {
            toolbar.setTitle("Không có chi tiết về thiết bị");
        }

        cvPhoneEnterprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(Animation.INFINITE);
                anim.setDuration(3000);

                // Start animating the image
                ImageView ivPhoneEnterprise = findViewById(R.id.ivPhoneEnterprise);
                ivPhoneEnterprise.startAnimation(anim);

                // Later.. stop the animation
                ivPhoneEnterprise.setAnimation(null);

                String[] permissions = {Manifest.permission.CALL_PHONE};
                Permissions.check(mContext, permissions, null, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        // do your task.
                        if (ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            if (phoneEnterprise.length() > 0) {
                                Uri number = Uri.parse("tel:" + phoneEnterprise);
                                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                startActivity(callIntent);
                            }
                        }
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(mContext, "Permission Denied !", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    private void getInforDev() {
        String url = Constant.URL + Constant.API_GET_INFOR_MARKER;
        Uri builder = Uri.parse(url)
                .buildUpon()
                .appendPath(String.valueOf(device.getId()))
                .build();
        final StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(final String response) {

                try {
                    JSONObject root = new JSONObject(response);
                    JSONObject secRoot = root.getJSONObject("LatestData");
                    JSONObject thirdRoot = secRoot.getJSONObject("Projector");
                    JSONObject fourRoot = thirdRoot.getJSONObject("Enterprise");
                    JSONArray managerRoot = root.getJSONArray("Managers");
                    double gamma = secRoot.getDouble("Gamma");
                    txtGammaDev.setText(gamma + "");

                    double neutron = secRoot.getDouble("Neutron");
                    txtNeutronDev.setText(neutron + "");

                    txtLocationDev.setText("(" + secRoot.getString("Latitude")
                            + ", " + secRoot.getString("Longitude") + ")");

                    String lastestTime = secRoot.getString("CreatedDate").substring(0, 19);
                    txtLastestTimeDev.setText(lastestTime.replace("T", " "));

                    txtNameDev.setText(device.getName());
                    txtNameEnterprise.setText(fourRoot.getString("Name"));

                    if (managerRoot.length() > 0) {
                        phoneEnterprise = managerRoot.getJSONObject(0).getString("PhoneNumber");
                        txtPhoneEnterprise.setText(phoneEnterprise);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();


                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setMessage("Lỗi khi đăng nhập. Vui Lòng Thử lại");
                builder1.setTitle("Lỗi mạng");
                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
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
                }
                return super.parseNetworkResponse(response);
            }
        };

        queue.add(request);
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbarDetailDev);
        txtGammaDev = findViewById(R.id.txtGammaDev);
        txtNeutronDev = findViewById(R.id.txtNeutronDev);
        txtLocationDev = findViewById(R.id.txtLocationDev);
        txtLastestTimeDev = findViewById(R.id.txtLastestTimeDev);
        txtNameDev = findViewById(R.id.txtNameDev);
        txtNameEnterprise = findViewById(R.id.txtNameEnterprise);
        txtAddressEnterprise = findViewById(R.id.txtAddressEnterprise);
        txtPhoneEnterprise = findViewById(R.id.txtPhoneEnterprise);
        cvPhoneEnterprise = findViewById(R.id.cvPhoneEnterprise);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
