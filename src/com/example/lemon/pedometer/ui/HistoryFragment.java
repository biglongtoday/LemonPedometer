package com.example.lemon.pedometer.ui;

import java.util.ArrayList;
import java.util.List;

import name.bagi.levente.pedometer.Pedometer.MyPagerAdapter;
import name.bagi.levente.pedometer.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pedometer.db.DBManager;
import com.example.pedometer.model.UserPedometer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
//import com.example.pedometer.model.UserPedometer;

@SuppressLint("ShowToast") public class HistoryFragment extends Fragment{
	
	private View view;
	private BarChart historyChart;
	//private DBManager dbm;
	private static Context context;
	private DBManager dbm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.history, container,false);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = MyPagerAdapter.getContext();
		dbm = DBManager.getInstance(context);
		init();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!dbm.isDBActive())
			dbm = DBManager.getInstance(context);
		setBarData();
	}

	public void init(){
		
		historyChart = (BarChart) view.findViewById(R.id.history);
		//historyChart.setOnSeekbarChangeListener(this);
		
		historyChart.setDrawYValues(true);
		historyChart.setDescription("计步器数据统计");
		
		historyChart.setDrawValuesForWholeStack(true);
		historyChart.set3DEnabled(true);
		historyChart.setPinchZoom(false);
		historyChart.setDrawBarShadow(false);
		
		MyValueFormatter customFormatter = new MyValueFormatter();
		YLabels yLabels = historyChart.getYLabels();
        //yLabels.setPosition(YLabelPosition.BOTH_SIDED);
        yLabels.setLabelCount(50);
        
        yLabels.setFormatter(customFormatter);

        XLabels xLabels = historyChart.getXLabels();
        xLabels.setPosition(XLabelPosition.TOP);
        xLabels.setCenterXLabelText(true);
        setBarData();
		
	}
	
	static List<UserPedometer> pedometers;
	public void setBarData(){
		pedometers = dbm.queryUserPedometer();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < pedometers.size(); i++) {
        	//行走的日期
        	String s = pedometers.get(i).getDate();
            xVals.add(s);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < pedometers.size(); i++) {
            
            float val1 = pedometers.get(i).getPaces();
            float val2 = pedometers.get(i).getCalories();
            yVals1.add(new BarEntry(new float[] {
                     val1, val2 
            }, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setStackLabels(new String[] {
                "steps", "caloris"
        });

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        historyChart.setData(data);
       
  
        
        historyChart.invalidate(); 
        //dbm.closeDB();
        
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dbm.closeDB();
	}
	

}
