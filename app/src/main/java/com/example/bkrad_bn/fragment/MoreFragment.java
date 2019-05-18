package com.example.bkrad_bn.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.bkrad_bn.ConnectionActivity;
import com.example.bkrad_bn.DetailDeviceActivity;
import com.example.bkrad_bn.R;
import com.example.bkrad_bn.UserActivity;
import com.example.bkrad_bn.helper.InforUserDialog;


public class MoreFragment extends Fragment implements View.OnClickListener {

    LinearLayout lnAccountInfor, lnUpdateUser, lnDefaultConnect, lnConnectConfig;


    public MoreFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        initWidgets(view);



        return view;
    }

    private void initWidgets(View view) {
        lnAccountInfor = view.findViewById(R.id.lnAccountInfor);
        lnUpdateUser = view.findViewById(R.id.lnUpdateUser);
        lnConnectConfig = view.findViewById(R.id.lnConnectConfig);
        lnDefaultConnect = view.findViewById(R.id.lnDefaultConnect);

        lnAccountInfor.setOnClickListener(this);
        lnUpdateUser.setOnClickListener(this);
        lnConnectConfig.setOnClickListener(this);
        lnDefaultConnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id){
            case R.id.lnAccountInfor:
                intent = new Intent(getContext(), UserActivity.class);
                startActivity(intent);
                break;
            case R.id.lnUpdateUser:
                intent = new Intent(getContext(), UserActivity.class);
                startActivity(intent);
                break;
            case R.id.lnDefaultConnect:
                intent = new Intent(getContext(), ConnectionActivity.class);
                startActivity(intent);
                break;
            case R.id.lnConnectConfig:
                intent = new Intent(getContext(), ConnectionActivity.class);
                startActivity(intent);
                break;
        }
    }
}
