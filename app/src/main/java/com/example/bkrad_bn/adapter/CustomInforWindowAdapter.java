package com.example.bkrad_bn.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.bkrad_bn.MainActivity;
import com.example.bkrad_bn.R;
import com.example.bkrad_bn.model.Device;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.InputStream;

public class CustomInforWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public CustomInforWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((MainActivity) (context)).getLayoutInflater().inflate(R.layout.layout_custom_item_inforwindow, null);

        TextView txtAlpha = view.findViewById(R.id.txtAlpha);
        TextView txtBeta = view.findViewById(R.id.txtBeta);
        TextView txtDeviceName = view.findViewById(R.id.txtDeviceName);

        Device dev = (Device) (marker.getTag());
        txtAlpha.setText(dev.getGamma() + " ");
        txtBeta.setText(dev.getNeutron() + " ");
        txtDeviceName.setText(dev.getName());

        return view;
    }

}

