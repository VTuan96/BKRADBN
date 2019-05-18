package com.example.bkrad_bn;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bkrad_bn.model.Device;

public class ConnectionActivity extends AppCompatActivity {

    Toolbar toolbar;

    Intent intent;
    Bundle bundle;

    Device device;

    Context mContext;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mContext = this;
        queue = Volley.newRequestQueue(mContext);

        initWidgets();

    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbarConnection);

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
