package com.example.bkrad_bn.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.bkrad_bn.R;
import com.example.bkrad_bn.adapter.ManageDeviceAdapter;
import com.example.bkrad_bn.model.Device;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageDeviceFragment extends Fragment {


    ListView lvManageDevice;
    ArrayList<Device> listDevice;
    ManageDeviceAdapter manageDeviceAdapter;

    Random random = new Random();

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

        listDevice = new ArrayList<>();
        listDevice = getListDevices();
        manageDeviceAdapter = new ManageDeviceAdapter(getContext(), listDevice);
        lvManageDevice.setAdapter(manageDeviceAdapter);

        return view;
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

        return list;
    }

}
