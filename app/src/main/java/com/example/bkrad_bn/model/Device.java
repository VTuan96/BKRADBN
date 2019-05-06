package com.example.bkrad_bn.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable {
    public int Id;
    public String Name;
    public String Imei;
    public float Alpha;
    public float Beta;
    public float Lat;
    public float Lon;

    public Device() {
        this.Id = 0;
    }

    public Device(int id, String name, String imei, float alpha, float beta, float lat, float lon) {
        Id = id;
        Name = name;
        Imei = imei;
        Alpha = alpha;
        Beta = beta;
        Lat = lat;
        Lon = lon;
    }

    protected Device(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        Imei = in.readString();
        Alpha = in.readFloat();
        Beta = in.readFloat();
        Lat = in.readFloat();
        Lon = in.readFloat();
    }


    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };


    @Override
    public String toString() {
        return "Device{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Imei='" + Imei + '\'' +
                ", Alpha='" + Alpha + '\'' +
                ", Beta='" + Beta + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Id);
        parcel.writeString(Name);
        parcel.writeString(Imei);
        parcel.writeFloat(Alpha);
        parcel.writeFloat(Beta);
        parcel.writeFloat(Lat);
        parcel.writeFloat(Lon);
    }

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

    public float getAlpha() {
        return Alpha;
    }

    public void setAlpha(float alpha) {
        Alpha = alpha;
    }

    public float getBeta() {
        return Beta;
    }

    public void setBeta(float beta) {
        Beta = beta;
    }

    public static Creator<Device> getCREATOR() {
        return CREATOR;
    }

    @Override
    public boolean equals(Object obj) {
        Device dev = (Device) obj;
        return (this.Id == dev.getId()) && (this.Imei == dev.getImei());
    }
}
