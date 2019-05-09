package com.example.bkrad_bn.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bkrad_bn.DetailDeviceActivity;
import com.example.bkrad_bn.R;
import com.example.bkrad_bn.model.Device;
import com.example.bkrad_bn.ultils.Constant;

import org.w3c.dom.Text;

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

        TextView txtNameDev = convertView.findViewById(R.id.txtNameDev);
        TextView txtImeiDev = convertView.findViewById(R.id.txtImeiDev);
        TextView txtLatitudeDev = convertView.findViewById(R.id.txtLatitudeDev);
        TextView txtLongitudeDev = convertView.findViewById(R.id.txtLongitudeDev);
        CardView cvItemDev = convertView.findViewById(R.id.cvItemDev);

        final Device device = mList.get(position);
        txtNameDev.setText(device.getName());
        txtImeiDev.setText("Imei: " + device.getImei());
        txtLatitudeDev.setText("Latitude: " + device.getLat());
        txtLongitudeDev.setText("Longitude: " + device.getLon());

        cvItemDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailDeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constant.DETAIL_DEV, device);
                intent.putExtra(Constant.DETAIL_DEV, bundle);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }


}
