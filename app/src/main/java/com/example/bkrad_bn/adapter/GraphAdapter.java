package com.example.bkrad_bn.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bkrad_bn.R;
import com.example.bkrad_bn.model.Graph;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Created by vutuan on 12/12/2017.
 */

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.ViewHolder> {
    ArrayList<Graph> listGraph;
    Context context;

    public GraphAdapter(ArrayList<Graph> listGraph) {
        this.listGraph = listGraph;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bieu_do_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        context=parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Graph graph=listGraph.get(position);

        if (graph!=null && graph.getEntriesBeta().size() > 0 && graph.getEntriesAlpha().size() > 0){
            //set ten bieu do
            String nameGraph=graph.getName_graph();
            holder.txtTenBieuDo.setText(nameGraph);

            //set du lieu cho bieu do
            ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

            LineDataSet dataset = new LineDataSet(graph.getEntriesAlpha(), "Nhiệt độ");
            dataset.setColors(Color.WHITE);
            dataset.setValueTextColor(context.getResources().getColor(R.color.colorWhite));
            dataset.setCircleColor(Color.WHITE);
            dataset.setDrawFilled(false);
            lineDataSets.add(dataset);

            LineDataSet dataset1 = new LineDataSet(graph.getEntriesBeta(), "Độ ẩm");
            dataset1.setColors(Color.BLACK);
            dataset1.setValueTextColor(context.getResources().getColor(R.color.colorWhite));
            dataset1.setCircleColor(Color.BLACK);
            dataset1.setDrawFilled(false);
            lineDataSets.add(dataset1);

            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value < graph.getLabels().size() && value >= 0)
                        return graph.getLabels().get((int) value);
                    else return String.valueOf(value);
                }

            };

            XAxis xAxis = holder.graph.getXAxis();
            xAxis.setTextColor(Color.WHITE);
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            YAxis leftYAxis = holder.graph.getAxisLeft();
            YAxis rightYAxis = holder.graph.getAxisRight();
            leftYAxis.setTextColor(Color.WHITE);
            rightYAxis.setTextColor(Color.WHITE);

            holder.graph.setData(new LineData(lineDataSets));
            holder.graph.setDrawGridBackground(false);
            holder.graph.getAxisRight().setDrawGridLines(false);
            holder.graph.getAxisLeft().setDrawGridLines(false);
            holder.graph.getXAxis().setDrawGridLines(false);
            holder.graph.animateY(3000);
            holder.graph.invalidate();
            holder.graph.setBackgroundColor(context.getResources().getColor(R.color.colorSecondPrimary));
            holder.graph.setContentDescription("");
        }

    }

    @Override
    public int getItemCount() {
        return listGraph.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTenBieuDo;
        public LineChart graph;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTenBieuDo= (TextView) itemView.findViewById(R.id.txt_TenBieuDo);
            graph= (LineChart) itemView.findViewById(R.id.graph);
        }
    }
}
