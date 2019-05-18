package com.example.bkrad_bn;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bkrad_bn.model.Device;
import com.example.bkrad_bn.ultils.Constant;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    Intent intent;
    Bundle bundle;

    Device device;

    Context mContext;
    RequestQueue queue;

    EditText etPhoneUser, etNameUser, etEmailUser, etEnterpriseUser;
    ImageView ivEditPhone, ivEditFullName, ivEditEmail, ivEditEnterprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initWidgets();

        mContext = this;
        queue = Volley.newRequestQueue(mContext);

        toolbar.setTitle("Thông tin tài khoản");

        intent = getIntent();
        if (intent != null){

//            getInforDev();

        } else {
            toolbar.setTitle("Không có chi tiết về thiết bị");
        }
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbarUser);
        etPhoneUser = findViewById(R.id.etPhoneUser);
        etNameUser = findViewById(R.id.etNameUser);
        etEmailUser = findViewById(R.id.etEmailUser);
        etEnterpriseUser = findViewById(R.id.etEnterpriseUser);

        ivEditPhone = findViewById(R.id.ivEditUsername);
        ivEditFullName = findViewById(R.id.ivEditFullName);
        ivEditEmail = findViewById(R.id.ivEditEmail);
        ivEditEnterprise = findViewById(R.id.ivEditEnterprise);

        ivEditEnterprise.setOnClickListener(this);
        ivEditEmail.setOnClickListener(this);
        ivEditPhone.setOnClickListener(this);
        ivEditFullName.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.ivEditUsername:
                etPhoneUser.setEnabled(true);
                break;
            case R.id.ivEditFullName:
                etNameUser.setEnabled(true);
                break;
            case R.id.ivEditEmail:
                etEmailUser.setEnabled(true);
                break;
            case R.id.ivEditEnterprise:
                etEnterpriseUser.setEnabled(true);
                break;
        }
    }
}
