package com.example.bkrad_bn.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkrad_bn.MainActivity;
import com.example.bkrad_bn.R;
import com.example.bkrad_bn.adapter.DeviceAdapter;
import com.example.bkrad_bn.adapter.GraphAdapter;
import com.example.bkrad_bn.model.Device;
import com.example.bkrad_bn.model.Graph;
import com.example.bkrad_bn.task.DownloadJSON;
import com.example.bkrad_bn.ultils.XuLyThoiGian;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;

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
    private ImageView ivSelectDevice;
    private TextView txtTitle;
    private RecyclerView rvItemBieuDoThongKe;
    private ArrayList<Graph> listGraph=new ArrayList<>();
    private GraphAdapter adapter;

    private ListView lvDevice;
    ArrayList<Device> listDevices;
    private DeviceAdapter adapterDevice;

    AlertDialog dialog = null;

//    private CustomGraphAdapter adapter;

    private DownloadJSON downloadJSON;
    String arr_thongso[] = {"Alpha", "Beta"};

    public String tmpNgayThangNam="";
    public String tmpSelectedDeviceId="";

    private Calendar calendar;
    private int year, month, day;
    ProgressDialog pDialog;
    String selectedDateTime = "";

    //All components of all graphs
    private ArrayList<Entry> entriesAlpha=new ArrayList<>();
    private ArrayList labelsAlpha = new ArrayList<String>();

    private ArrayList<Entry> entriesBeta=new ArrayList<>();
    private ArrayList labelsBeta = new ArrayList<String>();

    //All label of graph
    private String[] arrLabels=new String[]{"Alpha","Beta" };

    public static HistoryFragment newInstance(String title, int selectDeviceID){
        HistoryFragment df=new HistoryFragment();
        Bundle bundle=new Bundle();
        bundle.putString(TITLE,title);
        bundle.putInt(DEVICEID,selectDeviceID);
        df.setArguments(bundle);
        return df;
    }

    private Device selectedDevice = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_history,container,false);

        initWidgets(v);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        tmpNgayThangNam = XuLyThoiGian.layNgayHienTai();

        String title= MainActivity.selectedDevice;
        tmpSelectedDeviceId= String.valueOf(MainActivity.selectedDeviceId);

        pDialog = new ProgressDialog(getContext());

//        txtTitle.setText("Thiết bị : "+title);
//        txt_Time.setText("Ngày : "+ selectedDateTime);
        rvItemBieuDoThongKe.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        rvItemBieuDoThongKe.setLayoutManager(manager);

        selectedDevice = new Device();

        initGraph();

        listDevices = getListDevices();

        ivSelectDevice.setOnClickListener(this);

        MainActivity.setOnDateChangedListener(new MainActivity.DateChanged() {
            @Override
            public void onDateChanged(String selectedDate) {
                selectedDateTime = selectedDate;
                System.out.println("selectedDevice =====> " + selectedDateTime);

                if (selectedDevice.getId() == 0) {
                    updateErrorInfor();
                } else {
                    createDeviceGraph(selectedDevice);
                }
//                System.out.println("selectedDevice =====> " + selectedDateTime);
            }
        });

        return v;
    }

    private void updateErrorInfor() {
        txt_Time.setText("Ngày: " + selectedDateTime);
        txtTitle.setText("Thiết bị: " + "(Bạn chưa chọn thiết bị!!!)");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void initWidgets(View v){
        txtItemContent= (TextView) v.findViewById(R.id.txtItemContent);
        txtTitle= (TextView) v.findViewById(R.id.txtTitle);
        rvItemBieuDoThongKe=  v.findViewById(R.id.rvItemBieuDoThongKe);
        txt_Time = (TextView) v.findViewById(R.id.txt_time);
        ivSelectDevice = v.findViewById(R.id.ivSelectDevice);
    }

    public void initGraph(){
        entriesAlpha=new ArrayList<>();
        labelsAlpha = new ArrayList<String>();

        entriesBeta=new ArrayList<>();
        labelsBeta = new ArrayList<String>();

        listGraph=new ArrayList<>();
        Graph graph=new Graph(arrLabels[0],entriesAlpha, entriesBeta, labelsAlpha);
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
//        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivSelectDevice:
                openSelectDialog();
                break;
        }
    }

    private void openSelectDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_select_device, null);

        lvDevice = view.findViewById(R.id.lvDevice);
        setupListViewDevice();

        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = listDevices.get(position);
                createDeviceGraph(listDevices.get(position));
                txtItemContent.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle("Lựa chọn thiết bị")
                // Add action buttons
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialog = builder.create();
        dialog.show();

    }

    private void createDeviceGraph(Device device) {
        selectedDateTime = MainActivity.selectedDateTime;

        String nameGraph = "Biểu đồ lịch sử";
        ArrayList<Entry> entriesAlpha = new ArrayList<>();
        ArrayList<Entry> entriesBeta = new ArrayList<>();
        ArrayList labels = new ArrayList<String>();

        for(int j=0; j<100; j++){
            double valueAlpha = Math.random();
            double valueBeta = Math.random();
            entriesAlpha.add(new Entry(j,(float)valueAlpha));
            entriesBeta.add(new Entry(j,(float)valueBeta));
            labels.add("07:00");
        }
        Graph graph=new Graph(nameGraph, entriesAlpha, entriesBeta, labels);

        listGraph.set(0, graph);
        adapter.notifyDataSetChanged();

        updateInfor(device);

    }

    private void updateInfor(Device device) {
        txtTitle.setText("Thiết bị: " + device.getName());
        txt_Time.setText("Ngày: " + selectedDateTime);
    }


    private void setupListViewDevice() {
        listDevices = getListDevices();
        adapterDevice = new DeviceAdapter(getContext(), R.layout.layout_custom_item_select_device, listDevices);
        lvDevice.setAdapter(adapterDevice);
        adapterDevice.notifyDataSetChanged();


    }


    private ArrayList<Device> getListDevices() {
        ArrayList<Device> list = new ArrayList<>();

        Device dev0 = new Device(1, "BBKRAD-01", "00-00-00-01", 10.2f, 10.3f, 21.122567f, 106.006095f);
        Device dev1 = new Device(2, "BBKRAD-02", "00-00-00-02", 10.2f, 10.3f, 21.004314f, 105.8419583f);
        Device dev2 = new Device(3, "BBKRAD-03", "00-00-00-03", 10.2f, 10.3f, 20.997144f, 105.8569703f);
        Device dev3 = new Device(4, "BBKRAD-04", "00-00-00-04", 10.2f, 10.3f, 20.98896f, 105.8388993f);

        list.add(dev0);
        list.add(dev1);
        list.add(dev2);
        list.add(dev3);

        return list;
    }


//    @Override
//    public void onDateChanged(int year, int month, int dayOfMonth) {
//        System.out.println("Date changed");
//    }
}
