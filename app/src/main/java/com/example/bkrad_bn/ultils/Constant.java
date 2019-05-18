package com.example.bkrad_bn.ultils;

public class Constant {
    //public static final String URL = "http://192.168.1.144:6688/";
    public static final String URL = "https://rad.sanslab.vn/";
    public static final String TAG_LOGIN = "LOGIN ACTIVITY";
    public static final String TAG_MAIN = "MAIN ACTIVITY";
    public static final String TAG_URL_SERVICE = "URL";
    public static final String TAG_DATA_RESPONSE= "Data Response";

    //old API of BKRES
//    public static final String API_CUSTOMER_LOGIN = "Customer/Login?";
//    public static final String API_GET_LAKE_AND_DEVICE = "Lake/GetLakeAndDeviceByHomeId?";
//    public static final String API_GET_DATA_PACKAGE = "Datapackage/GetDatapackageByDeviceId?";
//    public static final String API_GET_DATA_THONGKE = "ThongKe/GetValues?";

    //new API of BKRERS LORA
    public static final String API_GET_TOKEN = "api/account/login";
    public static final String API_GET_ALL_MARKER = "api/datapackage/getallmarker";
    public static final String API_GET_DATAPACKAGE_REPORT = "api/datapackage/report";
    public static final String API_GET_INFOR_MARKER = "api/datapackage/getinformarker/";
    public static final String API_GET_DATA_PACKAGE = "api/datapackage/report?";
    public static final String API_GET_DATA_THONGKE = "ThongKe/GetValues?";

    public static final float DEFAULT_TEMP_MAX = 33f;
    public static final float DEFAULT_TEMP_MIN = 18f;
    public static final float DEFAULT_HUMI_MAX = 70f;
    public static final float DEFAULT_HUMI_MIN = 35f;


    public static final String SELECTED_DEVICE = "SELECTED_DEVICE";
    public static final String SELECTED_LAKE = "SELECTED_LAKE";
    public static final String SELECTED_IMEI_DEVICE = "SELECTED_IMEI_DEVICE";
    public static final String LIST_LAKE = "LIST_LAKE";
    public static final String LIST_DEVICE = "LIST_DEVICE";

    public static final String DETAIL_DEV = "DETAIL_DEV";

    public static final String ACCESS_CODE = "ACCESS_CODE";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String IS_WARNING_SOUND = "IS_WARNING_SOUND";
    public static final long DELAY_TAB = 200;
    public static String FIRST_TIME_USE_APP = "FIRST_TIME_USE_APP";
}
