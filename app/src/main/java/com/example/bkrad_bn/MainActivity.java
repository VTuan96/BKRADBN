package com.example.bkrad_bn;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import com.example.bkrad_bn.fragment.AccountFragment;
import com.example.bkrad_bn.fragment.HistoryFragment;
import com.example.bkrad_bn.fragment.HomeFragment;
import com.example.bkrad_bn.fragment.ManageDeviceFragment;
import com.example.bkrad_bn.fragment.MoreFragment;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

//    private TextView mTextMessage;
    private Fragment fragment = null;
    private FragmentManager manager = null;
    private FragmentTransaction transaction =  null;
    MenuItem menuItem = null;
    public static String tmpNgayThangNam = "";
    public static String selectedDateTime = "";
    public static int selectedDeviceId = 0;
    public static String selectedDevice = "";
    private Calendar calendar;
    private int year, month, day;

    public static DateChanged dateChangedListener;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transaction = manager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.mnuHome:
                    fragment = new HomeFragment();
                    fragment.onAttach(getApplicationContext());
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    setTitle("Giám sát hệ thống");
                    return true;
                case R.id.mnuHistory:
                    setTitle("Lịch sử");
                    fragment = new HistoryFragment();
                    fragment.onAttach(getApplicationContext());
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    return true;
//                case R.id.mnuSetting:
//                    fragment = new ManageDeviceFragment();
//                    fragment.onAttach(MainActivity.this);
//                    transaction.replace(R.id.contain, fragment);
//                    transaction.commit();
//                    setTitle("Cấu hình cảnh báo");
//                    return true;
                case R.id.mnuMore:
                    fragment = new MoreFragment();
                    fragment.onAttach(getApplicationContext());
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    setTitle("Thêm");
                    return true;
                case R.id.mnuManage:
                    fragment = new ManageDeviceFragment();
                    fragment.onAttach(getApplicationContext());
                    transaction.replace(R.id.contain, fragment);
                    transaction.commit();
                    setTitle("Quản lý thiết bị");
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        manager = getSupportFragmentManager();

//        tmpNgayThangNam = XuLyThoiGian.layNgayHienTai();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        if ( month < 9 ){
            if (day<9){
                selectedDateTime = "0" + day + "/" + "0"+ (month+1) +"/" + year;
            } else
                selectedDateTime = day + "/" + "0"+ (month+1) +"/" + year;
        } else{
            if (day<9){
                selectedDateTime = "0" + day + "/" +  (month+1) +"/" + year;
            } else
                selectedDateTime = day + "/" + (month+1) +"/" + year;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuExit) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit Application?");
            alertDialogBuilder
                    .setMessage("Click yes to exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(homeIntent);
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (item.getItemId() == R.id.mnuTime){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener , year, month, day);
            datePickerDialog.show();
        }


        return true;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day

                    year = arg1; month = arg2; day = arg3;

                    if ( arg2 < 9 ){
                        tmpNgayThangNam =  "0"+ (arg2+1) +"/" + arg3 +"/" + arg1;

                        if (arg3<9){
                            selectedDateTime = "0" + arg3 + "/" + "0"+ (arg2+1) +"/" + arg1;
                        } else
                            selectedDateTime = arg3 + "/" + "0"+ (arg2+1) +"/" + arg1;
                    } else{
                        tmpNgayThangNam =  (arg2+1)+ "/" + arg3 +"/" +  arg1;

                        if (arg3<9){
                            selectedDateTime = "0" + arg3 + "/" +  (arg2+1) +"/" + arg1;
                        } else
                            selectedDateTime = arg3 + "/" +  (arg2+1) +"/" + arg1;
                    }

                    dateChangedListener.onDateChanged(selectedDateTime);
                }
            };

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
    }

    public interface DateChanged {
        public void onDateChanged(String selectedDate);
    }

    public static void setOnDateChangedListener(DateChanged onDateChangedListener){
        dateChangedListener = onDateChangedListener;
    }
}
