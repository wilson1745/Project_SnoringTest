package wilson.com.project_snoringtest;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BarchartActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

   private static final String TAG = "LineTutorialActivity";
   BarChart barChart;
   private ArrayList<Integer> active_list;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_barchart);

      barChart = findViewById(R.id.barchart);

      barChart.setDrawBarShadow(false);
      barChart.setDrawValueAboveBar(true);
      barChart.setMaxVisibleValueCount(50);
      barChart.setPinchZoom(false);
      barChart.setDrawGridBackground(true);

      YAxis leftAxis = barChart.getAxisLeft();
      leftAxis.removeAllLimitLines();
      //leftAxis.addLimitLine(upper_line);
      //leftAxis.addLimitLine(lower_line);
      //leftAxis.setAxisMaximum(Float.MAX_VALUE);
      leftAxis.setAxisMaximum(30f);
      leftAxis.setAxisMinimum(0f);
      leftAxis.enableGridDashedLine(10f, 10f, 0);
      leftAxis.setDrawLimitLinesBehindData(true);

      barChart.getAxisRight().setEnabled(false);

      //set data
      ArrayList<BarEntry> yValues = new ArrayList<>();

      Intent intent = getIntent();
      if("send active_list".equals(intent.getAction())) {
         active_list = (ArrayList<Integer>) intent.getSerializableExtra("active_list");
         for(int i = 0;i < active_list.size(); i++){
            Log.e("tag", "BarchartActivity:" + active_list.get(i));
            yValues.add(new BarEntry(i, active_list.get(i)));
         }
      }

      BarDataSet set1 = new BarDataSet(yValues, "Sound Value");
      set1.setColors(ColorTemplate.COLORFUL_COLORS);

      BarData data = new BarData(set1);

      //space between two bars
      float groupSpace = 0.25f;
      float barSpace = 0.02f;
      float barWidth = 0.3f;

      barChart.setData(data);
      //data.setBarWidth(0.9f);
      data.setBarWidth(barWidth);

      XAxis xAxis = barChart.getXAxis();
      String[] values = new String[active_list.size()];
      for(int i = 0; i < active_list.size(); i++) {
         values[i] = String.valueOf(i + 1);
      }
      xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
      xAxis.setLabelCount(values.length, false);

      xAxis.setGranularity(1);
      xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
      //xAxis.setCenterAxisLabels(true);
      //xAxis.setAxisMinimum(1);


      /*//set text, color
      set1.setColor(Color.RED);
      set1.setValueTextSize(10f);
      set1.setValueTextColor(Color.BLUE);
      set1.setDrawValues(false);

      ArrayList<ILineDataSet> datasets = new ArrayList<>();
      datasets.add(set1);

      LineData data = new LineData(datasets);

      mChart.setData(data);

      String[] values = new String[active_list.size()];
      for(int i = 0; i < active_list.size(); i++) {
         values[i] = String.valueOf(i + 1);
      }

      XAxis xAxis = mChart.getXAxis();
      xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
      xAxis.setGranularity(1);
      xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);*/
   }

   @Override
   public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

   }

   @Override
   public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

   }

   @Override
   public void onChartLongPressed(MotionEvent me) {

   }

   @Override
   public void onChartDoubleTapped(MotionEvent me) {

   }

   @Override
   public void onChartSingleTapped(MotionEvent me) {

   }

   @Override
   public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

   }

   @Override
   public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

   }

   @Override
   public void onChartTranslate(MotionEvent me, float dX, float dY) {

   }

   @Override
   public void onValueSelected(Entry e, Highlight h) {

   }

   @Override
   public void onNothingSelected() {

   }

   public class MyXAxisValueFormatter implements IAxisValueFormatter {

      private String[] values;

      public MyXAxisValueFormatter(String[] values) {
         this.values = values;
      }

      @Override
      public String getFormattedValue(float value, AxisBase axis) {
         return values[(int) value];
      }
   }
}
