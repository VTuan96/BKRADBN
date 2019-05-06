package com.example.bkrad_bn.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bkrad_bn.R;
import com.example.bkrad_bn.model.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends ArrayAdapter<Device> {
    private Context mContext;
    private ArrayList<Device> mList;

    public DeviceAdapter(@NonNull Context context, int resource, @NonNull List<Device> objects) {
        super(context, resource, objects);
        mList = (ArrayList<Device>) objects;
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position,  @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_custom_item_select_device, parent, false);
        }

        Device dev = mList.get(position);

        TextView txtItemDeviceName = convertView.findViewById(R.id.txtItemDeviceName);
        txtItemDeviceName.setText(dev.getName());

        return convertView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
