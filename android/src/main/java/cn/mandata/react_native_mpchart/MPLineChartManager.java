package cn.mandata.react_native_mpchart;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2015/11/6.
 */
public class MPLineChartManager extends MPBarLineChartManager {
    private String CLASS_NAME="MPLineChart";
    private Random random;//用于产生随机数

    private LineChart chart;
    private LineData data;
    private LineDataSet dataSet;
    private ReactContext reactContext;
    @Override
    public String getName() {
        return this.CLASS_NAME;
    }

    @Override
    protected LineChart createViewInstance(ThemedReactContext reactContext) {
        LineChart chart=new LineChart(reactContext);
        this.reactContext = reactContext;
        return  chart;
    }

    //{XValues:[],YValues:[{Data:[],Label:""},{}]}
    @ReactProp(name="data")
    public void setData(LineChart chart,ReadableMap rm){

        ReadableArray xArray=rm.getArray("xValues");
        ArrayList<String> xVals=new ArrayList<String>();
        for(int m=0;m<xArray.size();m++){
            xVals.add(xArray.getString(m));
        }
        ReadableArray ra=rm.getArray("yValues");
        LineData chartData=new LineData(xVals);
        for(int i=0;i<ra.size();i++){
            ReadableMap map=ra.getMap(i);
            ReadableArray data=map.getArray("data");
            String label=map.getString("label");
            float[] vals=new float[data.size()];
            ArrayList<Entry> entries=new ArrayList<Entry>();
            for (int j=0;j<data.size();j++){
                vals[j]=(float)data.getDouble(j);
                Entry be=new Entry((float)data.getDouble(j),j);
                entries.add(be);
            }
            /*BarEntry be=new BarEntry(vals,i);
            entries.add(be);*/
            ReadableMap config= map.getMap("config");
            LineDataSet dataSet=new LineDataSet(entries,label);
            if(config.hasKey("drawCircles")) dataSet.setDrawCircles(config.getBoolean("drawCircles"));
            if(config.hasKey("circleSize")) dataSet.setCircleSize((float) config.getDouble("circleSize"));
            if(config.hasKey("lineWidth")) dataSet.setLineWidth((float) config.getDouble("lineWidth"));
            if(config.hasKey("color")) {
                int[] colors=new int[]{Color.parseColor(config.getString("color"))};
                dataSet.setColors(colors);
            }
            chartData.addDataSet(dataSet);
        }
        chart.setBackgroundColor(Color.WHITE);
        chart.setData(chartData);
        chart.invalidate();

        final View chartView = (View)chart;
        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {}

            @Override
            public void onChartGestureEnd(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {
                WritableMap event = Arguments.createMap();
                event.putString("message", "Chart gesture ended!");
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(chartView.getId(), "topChange", event);
            }

            @Override
            public void onChartLongPressed(MotionEvent motionEvent) {}

            @Override
            public void onChartDoubleTapped(MotionEvent motionEvent) {}

            @Override
            public void onChartSingleTapped(MotionEvent motionEvent) {}

            @Override
            public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {}

            @Override
            public void onChartScale(MotionEvent motionEvent, float v, float v1) {}

            @Override
            public void onChartTranslate(MotionEvent motionEvent, float v, float v1) {}
        });
    }
}
