package com.example.bkrad_bn.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.example.bkrad_bn.R;
import com.example.bkrad_bn.UserActivity;

public class InforUserDialog extends Dialog implements View.OnClickListener {

    Button btnBackUser, btnUpdateUser;

    public InforUserDialog( @NonNull Context context) {
        super(context);

        setContentView(R.layout.layout_infor_user_dialog);

        initWidgets();

    }

    private void initWidgets() {
        btnBackUser = findViewById(R.id.btnBackUser);
        btnUpdateUser = findViewById(R.id.btnUpdateUser);

        btnUpdateUser.setOnClickListener(this);
        btnBackUser.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnBackUser){
            this.dismiss();
        } else if (id == R.id.btnUpdateUser){
            this.dismiss();

            Intent intent = new Intent(getContext(), UserActivity.class);
            getContext().startActivity(intent);
        }
    }
}
