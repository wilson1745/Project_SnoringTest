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
import android.widget.Toast;

import com.czt.mp3recorder.MP3Recorder;
import com.shuyu.waveview.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

   private TextView time_v, snore_v, active_v, sound_v;
   private Button btn_play, btn_stop, btn_track_off, btn_restart, btn_sound;
   private TimeThread timeThread;
   private boolean isRunning = true;
   private boolean run = true;
   private static final int msgKey1 = 1;
   private long second = 0, active = 0;
   private ArrayList<Integer> active_list;
   private String TAG = "MainActivity";
   CustomMp3Recorder mRecorder;
   String filePath;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      active_list = new ArrayList<>();

      timeThread = new TimeThread();
      findView();

      btn_play.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            timeThread.start();
            //run = true;
            //timeThread.interrupt();
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

      btn_sound.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            resolveRecord();
         }
      });
   }

   @SuppressLint("HandlerLeak")
   private void resolveRecord() {
      filePath = FileUtils.getAppPath();
      File file = new File(filePath);
      if (!file.exists()) {
         Log.e(TAG, "!file.exists()");
         if (!file.mkdirs()) {
            Log.e(TAG, "!file.mkdirs()");
            Toast.makeText(this, "创建文件失败", Toast.LENGTH_SHORT).show();
            return;
         }
      }
      int offset = SizeUtils.dp2px(1);
      /*filePath = FileUtils.getAppPath() + UUID.randomUUID().toString() + ".mp3";
      mRecorder = new CustomMp3Recorder(new File(filePath));
      int size = ScreenUtils.getScreenWidth() / offset;//控件默认的间隔是1
      mRecorder.setDataList(audioWave.getRecList(), size);

      mRecorder.setErrorHandler(new Handler() {
         @Override
         public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MP3Recorder.ERROR_TYPE) {
               Toast.makeText(MainActivity.this, "没有麦克风权限", Toast.LENGTH_SHORT).show();
               //resolveError();
            }
         }
      });
      integerList.clear();
      countSnoring.clear();


      try {
         mRecorder.start(new CustomMp3Recorder.VolumeListener() {
            @Override
            public void onVolumeListener(final Double volume) {
               mUIHandler.post(new Runnable() {
                  @Override
                  public void run() {
                     int value = volume.intValue();
                     if (!isPause) {
                        sound_v.setText(String.valueOf(value));
                        integerList.add(value);
                     }
                  }
               });
            }
         });
         audioWave.startView();
      } catch (IOException e) {
         e.printStackTrace();
         Toast.makeText(this, "权限未获取", Toast.LENGTH_SHORT).show();
         resolveError();
         return;
      }*/
   }

   private void findView() {
      time_v = findViewById(R.id.time_view);
      snore_v = findViewById(R.id.snore_view);
      active_v = findViewById(R.id.active_view);
      btn_play = findViewById(R.id.btn_play);
      btn_stop = findViewById(R.id.btn_stop);
      btn_track_off = findViewById(R.id.btn_track_off);
      btn_restart = findViewById(R.id.btn_restart);
      btn_sound = findViewById(R.id.btn_sound);
      sound_v = findViewById(R.id.sound_view);
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
