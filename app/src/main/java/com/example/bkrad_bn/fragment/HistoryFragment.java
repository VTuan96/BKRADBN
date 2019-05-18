package com.example.bkrad_bn.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.example.bkrad_bn.MainActivity;
import com.example.bkrad_bn.R;
import com.example.bkrad_bn.adapter.DeviceAdapter;
import com.example.bkrad_bn.adapter.GraphAdapter;
import com.example.bkrad_bn.adapter.ManageDeviceAdapter;
import com.example.bkrad_bn.model.Device;
import com.example.bkrad_bn.model.Graph;
import com.example.bkrad_bn.task.DownloadJSON;
import com.example.bkrad_bn.ultils.Constant;
import com.example.bkrad_bn.ultils.XuLyThoiGian;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements View.OnClickListener {


    public HistoryFragment() {
        // Required empty public constructor
    }

    private static final String TITLE="TITLE";
    private static final String GRAPH="GRAPH";
    private static final String TIME="TIME";
    private static final String DEVICEID="DEVICEID";
    private static final String DATE_TIME="DATE_TIME";

    private TextView txtItemContent, txt_Time;
    private ImageView ivSelectDevice, ivSelectParam;
    private TextView txtTitle;
    private RecyclerView rvItemBieuDoThongKe;
    private ArrayList<Graph> listGraph=new ArrayList<>();
    private GraphAdapter adapter;

    private ListView lvDevice, lvParam;
    private ArrayList<Device> listDevices;
    private DeviceAdapter adapterDevice;
    private ArrayAdapter<String> adapterParam;
    private ArrayList<String> listParam;



//    private CustomGraphAdapter adapter;

    private DownloadJSON downloadJSON;
    String arr_thongso[] = {"Alpha", "Beta"};

    public String tmpNgayThangNam="";
    public String tmpSelectedDeviceId="";

    private Calendar calendar;
    ProgressDialog pDialog;
    String selectedDateTime = "", formatDateTime = "";

    //All components of all graphs
    private ArrayList<Entry> entriesAlpha=new ArrayList<>();
    private ArrayList labelsAlpha = new ArrayList<String>();

    private ArrayList<Entry> entriesBeta=new ArrayList<>();
    private ArrayList labelsBeta = new ArrayList<String>();

    //All label of graph
    private String[] arrLabels=new String[]{"Gamma","Neutron" };

    Random random = new Random();

    Context mContext;
    RequestQueue queue;

    private Device selectedDevice = null;

    View viewParam = null, viewDev = null;

    String selectedParam = "";

    SearchView svDevice;

    LinearLayout lnlSelectDevice, lnlHistory, lnlDevice, lnlParam;

    ImageView ivExitParam, ivExitDevice;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_history,container,false);

        initWidgets(v);

        mContext = getContext();

        queue = Volley.newRequestQueue(mContext);

        tmpNgayThangNam = XuLyThoiGian.layNgayHienTai();

        String title= MainActivity.selectedDevice;
        tmpSelectedDeviceId= String.valueOf(MainActivity.selectedDeviceId);

        pDialog = new ProgressDialog(getContext());

        rvItemBieuDoThongKe.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        rvItemBieuDoThongKe.setLayoutManager(manager);

        selectedDevice = new Device();
        selectedDateTime = MainActivity.selectedDateTime;

        initGraph();

        getListDevices();

        lnlSelectDevice.setVisibility(View.VISIBLE);
        lnlHistory.setVisibility(View.GONE);

        selectDevAndParam();




        MainActivity.setOnDateChangedListener(new MainActivity.DateChanged() {
            @Override
            public void onDateChanged(String selectedDate, String formatDate) {
                selectedDateTime = selectedDate;
                formatDateTime = formatDate;
                System.out.println("selectedDevice =====> " + selectedDateTime);

                if (selectedDevice.getId() == 0) {
                    updateErrorInfor();
                } else {
                    createDeviceGraph(selectedDevice, selectedParam);
                }
            }
        });

        return v;
    }

    private void selectDevAndParam() {
        listParam = new ArrayList<>();
        listParam.add("Gamma");
        listParam.add("Neutron");

        adapterParam = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, listParam);
        lvParam.setAdapter(adapterParam);

        lvParam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lnlParam.setVisibility(View.GONE);
                lnlDevice.setVisibility(View.VISIBLE);

                selectedParam = listParam.get(position);

                adapterDevice = new DeviceAdapter(mContext, R.layout.layout_custom_item_select_device, listDevices);
                lvDevice.setAdapter(adapterDevice);
                adapterDevice.notifyDataSetChanged();

                lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedDevice = listDevices.get(position);
                        createDeviceGraph(selectedDevice, selectedParam);

                        lnlDevice.setVisibility(View.GONE);
                        lnlParam.setVisibility(View.VISIBLE);
                        lnlSelectDevice.setVisibility(View.GONE);
                        lnlHistory.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


    }

    private void updateErrorInfor() {
        txt_Time.setText("Ngày: " + selectedDateTime);
        txtTitle.setText("Thiết bị: " + "(Bạn chưa chọn thiết bị !!!)");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void initWidgets(View v){
        txtTitle= (TextView) v.findViewById(R.id.txtTitle);
        rvItemBieuDoThongKe=  v.findViewById(R.id.rvItemBieuDoThongKe);
        txt_Time = (TextView) v.findViewById(R.id.txt_time);
        ivSelectDevice = v.findViewById(R.id.ivSelectDevice);
        ivExitParam = v.findViewById(R.id.ivExitParam);
        ivExitDevice = v.findViewById(R.id.ivExitDevice);

        lvParam = v.findViewById(R.id.lvParam);
        lvDevice = v.findViewById(R.id.lvDevice);

        lnlHistory = v.findViewById(R.id.lnlHistory);
        lnlSelectDevice = v.findViewById(R.id.lnlSelectDevice);
        lnlDevice = v.findViewById(R.id.lnlDevice);
        lnlParam = v.findViewById(R.id.lnlParam);

        ivSelectDevice.setOnClickListener(this);
        ivExitParam.setOnClickListener(this);
        ivExitDevice.setOnClickListener(this);

        listDevices = new ArrayList<>();

    }

    public void initGraph(){
        entriesAlpha=new ArrayList<>();
        labelsAlpha = new ArrayList<String>();

        listGraph=new ArrayList<>();
        Graph graph=new Graph(arrLabels[0],entriesAlpha,labelsAlpha);
        listGraph.add(graph);

        adapter = new GraphAdapter( listGraph);
        rvItemBieuDoThongKe.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivSelectDevice:
//                getListDevices();
//                openSelectDevDialog();
                lnlSelectDevice.setVisibility(View.VISIBLE);
                lnlHistory.setVisibility(View.GONE);
                selectDevAndParam();

                break;
            case R.id.ivExitParam:
                lnlParam.setVisibility(View.GONE);
                lnlDevice.setVisibility(View.VISIBLE);

                adapterDevice = new DeviceAdapter(mContext, R.layout.layout_custom_item_select_device, listDevices);
                lvDevice.setAdapter(adapterDevice);
                adapterDevice.notifyDataSetChanged();

                lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedDevice = listDevices.get(position);
                        createDeviceGraph(selectedDevice, selectedParam);

                        lnlDevice.setVisibility(View.GONE);
                        lnlParam.setVisibility(View.VISIBLE);
                        lnlSelectDevice.setVisibility(View.GONE);
                        lnlHistory.setVisibility(View.VISIBLE);
                    }
                });

                break;

            case R.id.ivExitDevice:
                createDeviceGraph(selectedDevice, selectedParam);

                lnlDevice.setVisibility(View.GONE);
                lnlParam.setVisibility(View.VISIBLE);
                lnlSelectDevice.setVisibility(View.GONE);
                lnlHistory.setVisibility(View.VISIBLE);

                break;
        }
    }

    private void createDeviceGraph(Device device, String param) {
        if (param.equals(""))
            updateErrorInfor();
        else {
            String nameGraph = param;
            ArrayList<Entry> entriesAlpha = new ArrayList<>();
            ArrayList labels = new ArrayList<String>();

            String url = Constant.URL + Constant.API_GET_DATAPACKAGE_REPORT;
            Uri builder = Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("deviceId", String.valueOf(device.getId()))
                    .appendQueryParameter("dateTime", MainActivity.formatDateTime)
                    .appendQueryParameter("param", param)
                    .build();

            Log.i("HISTORY_ACTIVITY", builder.toString());

            final StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(final String response) {
                    Log.i("HISTORY_ACTIVITY", response);
                    try {
                        JSONArray root = new JSONArray(response);
                        if (root.length() > 0) {
                            for (int i = 0; i < root.length(); i++) {
                                JSONObject secRoot = root.getJSONObject(i);
                                String time = secRoot.getString("Time");
                                time = time.split("T")[1];
                                double value = secRoot.getDouble("Value");
                                entriesAlpha.add(new Entry(i, (float) value));
                                labels.add(time);
                            }

                            Graph graph = new Graph(nameGraph, entriesAlpha, labels);
                            listGraph.set(0, graph);

                        } else {
                            initGraph();
                        }

                        adapter.notifyDataSetChanged();

                        updateInfor(device);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();

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

                    if (statusCode == 400) //unauthorized
                    {
                        onResume();
                    }
                    return super.parseNetworkResponse(response);
                }
            };

            queue.add(request);


        }
    }

    private void updateInfor(Device device) {
        txtTitle.setText("Thiết bị: " + device.getName());
        txt_Time.setText("Ngày: " + selectedDateTime);
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

                        listDevices.add(device);
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
                builder1.setMessage("Error!");
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


}
