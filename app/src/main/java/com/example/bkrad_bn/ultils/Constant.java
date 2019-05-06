package com.example.bkrad_bn.ultils;

public class Constant {
    //public static final String URL = "http://192.168.1.144:6688/";
    public static final String URL = "https://nhayen.sanslab.vn/";
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
    public static final String API_GET_TOKEN = "oauth/token";
    public static final String API_GET_CURRENT_USER = "api/user/getcurrentuser/";
    public static final String API_GET_ALL_DEVICE = "api/device/getall";
    public static final String API_GET_NEWEST_PARAM = "api/datapackage/getparamnewest/";
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

}
