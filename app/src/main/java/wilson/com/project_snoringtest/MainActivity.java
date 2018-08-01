package wilson.com.project_snoringtest;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

   private TextView time_v, snore_v, active_v;
   private Button btn_play, btn_stop, btn_track_off, btn_restart, btn_chart;
   private TimeThread timeThread;
   private boolean isRunning = true;
   private boolean run = true;
   private static final int msgKey1 = 1;
   private long second = 0, active = 0;
   private ArrayList<Integer> active_list;
   private String TAG = "MainActivity";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      active_list = new ArrayList<>();

      timeThread = new TimeThread();
      findView();

      timeThread.start();

      btn_play.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            run = true;
            timeThread.interrupt();
         }
      });

      btn_stop.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            run = false;
         }
      });

      btn_track_off.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            isRunning = false;
            time_v.setText("0");
            active_v.setText("0");
         }
      });

      btn_restart.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            run = true;
            isRunning = true;
            second = 0;
            active = 0;
            timeThread = new TimeThread();
            timeThread.start();
         }
      });
   }

   private void findView() {
      time_v = findViewById(R.id.time_view);
      snore_v = findViewById(R.id.snore_view);
      active_v = findViewById(R.id.active_view);
      btn_play = findViewById(R.id.btn_play);
      btn_stop = findViewById(R.id.btn_stop);
      btn_track_off = findViewById(R.id.btn_track_off);
      btn_restart = findViewById(R.id.btn_restart);
   }

   public class TimeThread extends Thread {
      @Override
      public void run () {
         while(isRunning) {
            try {
               if(!run) {
                  timeThread.sleep(Long.MAX_VALUE);
               }
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            try {
               Thread.sleep(1);
               //Thread.sleep(1000);
               Message msg = new Message();
               msg.what = msgKey1;
               timeHandler.sendMessage(msg);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
         Log.e("TAG", "Thread has been terminated!");
      }
   }

   @SuppressLint("HandlerLeak")
   private Handler timeHandler = new Handler() {
      @Override
      public void handleMessage (Message msg) {
         super.handleMessage(msg);
         switch (msg.what) {
            case msgKey1:
               second++;
               time_v.setText(String.valueOf(second));

               if((second % 3600) == 0) {
                  active++;
                  Log.e(TAG, "時段換算");
                  Log.e(TAG, "second: " + second);
                  Log.e(TAG, "active: " + active);
                  active_v.setText(String.valueOf(active));
               }
               break;
            default:
               break;
         }
      }
   };
}
