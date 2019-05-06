package com.example.bkrad_bn.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.bkrad_bn.R;
import com.example.bkrad_bn.model.Device;

import java.util.ArrayList;
import java.util.List;

public class ManageDeviceAdapter extends ArrayAdapter<Device> {
    private Context mContext;
    private ArrayList<Device> mList;

    public ManageDeviceAdapter(@NonNull Context context, @NonNull List<Device> objects) {
        super(context, R.layout.layout_item_manage_device, objects);
        this.mContext = context;
        this.mList = (ArrayList<Device>) objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_manage_device, parent, false);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }


}
