package com.example.bkrad_bn.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Device implements Serializable {
    public int Id;
    public String Name;
    public String Imei;
    public float Gamma;
    public float Neutron;
    public float Lat;
    public float Lon;
    public boolean isWarning;
    public String CreateDate;


    public Device() {
        this.Id = 0;
    }

    public Device(int id, String name, String imei, float alpha, float beta, float lat, float lon, boolean isWarning, String createDate) {
        Id = id;
        Name = name;
        Imei = imei;
        Gamma = alpha;
        Neutron = beta;
        Lat = lat;
        Lon = lon;
        this.isWarning = isWarning;
        CreateDate = createDate;
    }

    protected Device(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        Imei = in.readString();
        Gamma = in.readFloat();
        Neutron = in.readFloat();
        Lat = in.readFloat();
        Lon = in.readFloat();
    }


//    public static final Creator<Device> CREATOR = new Creator<Device>() {
//        @Override
//        public Device createFromParcel(Parcel in) {
//            return new Device(in);
//        }
//
//        @Override
//        public Device[] newArray(int size) {
//            return new Device[size];
//        }
//    };


    @Override
    public String toString() {
        return "Device{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Imei='" + Imei + '\'' +
                ", Alpha='" + Gamma + '\'' +
                ", Beta='" + Neutron + '\'' +
                '}';
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public boolean isWarning() {
        return isWarning;
    }

    public void setWarning(boolean warning) {
        isWarning = warning;
    }

    public float getLat() {
        return Lat;
    }

    public void setLat(float lat) {
        Lat = lat;
    }

    public float getLon() {
        return Lon;
    }

    public void setLon(float lon) {
        Lon = lon;
    }

//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeInt(Id);
//        parcel.writeString(Name);
//        parcel.writeString(Imei);
//        parcel.writeFloat(Gamma);
//        parcel.writeFloat(Neutron);
//        parcel.writeFloat(Lat);
//        parcel.writeFloat(Lon);
//    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImei() {
        return Imei;
    }

    public void setImei(String imei) {
        Imei = imei;
    }

    public float getGamma() {
        return Gamma;
    }

    public void setGamma(float gamma) {
        Gamma = gamma;
    }

    public float getNeutron() {
        return Neutron;
    }

    public void setNeutron(float neutron) {
        Neutron = neutron;
    }

//    public static Creator<Device> getCREATOR() {
//        return CREATOR;
//    }

    @Override
    public boolean equals(Object obj) {
        Device dev = (Device) obj;
        return ((this.Id == dev.getId()) && this.Imei.equals(dev.getImei()));
    }
}
