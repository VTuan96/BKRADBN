package com.example.bkrad_bn.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * Created by vutuan on 12/12/2017.
 */

public class Graph implements Parcelable {
    private String name_graph;
    private ArrayList<Entry> entriesAlpha;
    private ArrayList<Entry> entriesBeta;
    private ArrayList<String> labels;

    public Graph(String name_graph, ArrayList<Entry> entriesAlpha, ArrayList<Entry> entriesBeta, ArrayList<String> labels) {
        this.name_graph = name_graph;
        this.entriesAlpha = entriesAlpha;
        this.entriesBeta = entriesBeta;
        this.labels = labels;
    }

    protected Graph(Parcel in) {
        name_graph = in.readString();
        entriesAlpha = in.createTypedArrayList(Entry.CREATOR);
        entriesBeta = in.createTypedArrayList(Entry.CREATOR);
        labels = in.createStringArrayList();
    }

    public static final Creator<Graph> CREATOR = new Creator<Graph>() {
        @Override
        public Graph createFromParcel(Parcel in) {
            return new Graph(in);
        }

        @Override
        public Graph[] newArray(int size) {
            return new Graph[size];
        }
    };

    public String getName_graph() {
        return name_graph;
    }

    public void setName_graph(String name_graph) {
        this.name_graph = name_graph;
    }

    public ArrayList<Entry> getEntriesAlpha() {
        return entriesAlpha;
    }

    public void setEntriesAlpha(ArrayList<Entry> entriesAlpha) {
        this.entriesAlpha = entriesAlpha;
    }

    public ArrayList<Entry> getEntriesBeta() {
        return entriesBeta;
    }

    public void setEntriesBeta(ArrayList<Entry> entriesBeta) {
        this.entriesBeta = entriesBeta;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public Graph() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name_graph);
        parcel.writeTypedList(entriesAlpha);
        parcel.writeTypedList(entriesBeta);
        parcel.writeStringList(labels);
    }
}
