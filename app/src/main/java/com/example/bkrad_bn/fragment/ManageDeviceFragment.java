package com.example.bkrad_bn.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bkrad_bn.MainActivity;
import com.example.bkrad_bn.R;
import com.example.bkrad_bn.adapter.ManageDeviceAdapter;
import com.example.bkrad_bn.model.Device;
import com.example.bkrad_bn.ultils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageDeviceFragment extends Fragment {


    ListView lvManageDevice;
    ArrayList<Device> listDevice;
    ManageDeviceAdapter manageDeviceAdapter;

    Random random = new Random();
    RequestQueue queue;
    Context mContext;

    public ManageDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_device, container, false);

        lvManageDevice = view.findViewById(R.id.lvManageDevice);

        mContext = (MainActivity)getActivity();
        queue = Volley.newRequestQueue(getContext());

        listDevice = new ArrayList<>();

        return view;
    }

    private void getListDevices() {
        String url = Constant.URL + Constant.API_GET_ALL_MARKER;
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(final String response) {

                try {
                    JSONArray root = new JSONArray(response);
                    listDevice = new ArrayList<>();
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
                        device.setCreateDate(secRoot.getString("CreatedDate"));

                        listDevice.add(device);
                    }

                    manageDeviceAdapter = new ManageDeviceAdapter(mContext, listDevice);
                    lvManageDevice.setAdapter(manageDeviceAdapter);


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

    @Override
    public void onResume() {
        super.onResume();
        getListDevices();
    }
}
