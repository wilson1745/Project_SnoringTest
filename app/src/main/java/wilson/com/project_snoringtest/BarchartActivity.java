package wilson.com.project_snoringtest;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class BarchartActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

   private static final String TAG = "LineTutorialActivity";
   private LineChart mChart;
   private ArrayList<Integer> active_list;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_barchart);

      mChart = findViewById(R.id.linechart);
      mChart.setOnChartGestureListener(this);
      mChart.setOnChartValueSelectedListener(this);
      mChart.setDragEnabled(true);
      mChart.setScaleEnabled(false);
      mChart.animateY(3000);

       /*//limit line
      LimitLine upper_line = new LimitLine(65f, "Danger");
      upper_line.setLineWidth(4f);
      upper_line.enableDashedLine(10f, 10, 0f);
      upper_line.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
      upper_line.setTextSize(15f);

      //limit line
      LimitLine lower_line = new LimitLine(35f, "Too Low");
      lower_line.setLineWidth(4f);
      lower_line.enableDashedLine(10f, 10, 0f);
      lower_line.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
      lower_line.setTextSize(15f);*/

      YAxis leftAxis = mChart.getAxisLeft();
      leftAxis.removeAllLimitLines();
      //leftAxis.addLimitLine(upper_line);
      //leftAxis.addLimitLine(lower_line);
      //leftAxis.setAxisMaximum(Float.MAX_VALUE);
      leftAxis.setAxisMaximum(30f);
      leftAxis.setAxisMinimum(0f);
      leftAxis.enableGridDashedLine(10f, 10f, 0);
      leftAxis.setDrawLimitLinesBehindData(true);

      mChart.getAxisRight().setEnabled(false);

      //set data
      ArrayList<Entry> yValues = new ArrayList<>();

      Intent intent = getIntent();
      if("send active_list".equals(intent.getAction())) {
         active_list = (ArrayList<Integer>) intent.getSerializableExtra("active_list");
         for(int i = 0;i < active_list.size(); i++){
            Log.e("tag", "LineChartActivity:" + active_list.get(i));
            yValues.add(new Entry(i, active_list.get(i)));
         }
      }

      //put into line data database
      LineDataSet set1 = new LineDataSet(yValues, "Sound Value");
      set1.setFillAlpha(110);

      //set text, color
      set1.setColor(Color.RED);
      set1.setLineWidth(3f);
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
      xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
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
