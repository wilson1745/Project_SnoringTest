package wilson.com.project_snoringtest;

import android.annotation.SuppressLint;
import android.hardware.Camera;
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
import com.shuyu.waveview.AudioWaveView;
import com.shuyu.waveview.FileUtils;
import com.vondear.rxtools.RxVibrateTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

   private TextView time_v, snore_v, active_v, sound_v, snoring_v;
   private Button btn_play, btn_stop, btn_track_off, btn_restart, btn_sound, btn_stop_snore;
   private TimeThread timeThread;
   private boolean isRunning = true;
   private boolean run = true;
   private static final int msgKey1 = 1;
   private long second = 0, active = 0;
   private ArrayList<Integer> active_list;
   private String TAG = "MainActivity";
   CustomMp3Recorder mRecorder;
   String filePath;
   AudioWaveView audioWave;
   private List<Integer> countStatus;
   private List<Integer> integerList;
   private List<Integer> countSnoring;
   private Handler mUIHandler;
   boolean isPause = false;

   private Disposable subscribe;
   private Disposable subscribe1;
   private Disposable subscribe2;
   private Disposable subscribe3;

   int countOnce = 0;
   int countMore = 0;
   int count = 0;
   /**
    * 是否处于打鼾状态
    */
   boolean isStartVibrate = false;
   boolean isFlash = false;
   private int setValue = 60;
   Date mEndDate;
   Date mStartDate;

   int last = 0, sum = 0;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mUIHandler = new Handler();
      integerList = new ArrayList<>();
      countStatus = new ArrayList<>();
      countSnoring = new ArrayList<>();

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

      btn_stop_snore.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (!subscribe.isDisposed()) {
               subscribe.dispose();
            }
            if (!subscribe1.isDisposed()) {
               subscribe1.dispose();
            }
            if (!subscribe2.isDisposed()) {
               subscribe2.dispose();
            }
            if (!subscribe3.isDisposed()) {
               subscribe3.dispose();
            }
            isStartVibrate = false;
            if (mRecorder != null && mRecorder.isRecording()) {
               mRecorder.setPause(false);
               mRecorder.stop();
               audioWave.stopView();
            }

            sum = countSnoring.size() - last;
            last = countSnoring.size();
            active_list.add(sum);

            active++;
            Log.e(TAG, "active: " + active);
            Log.e(TAG, "sum: " + sum);


            int cCount = countSnoring.size();
            mEndDate = new Date();
            long dur = mEndDate.getTime() - mStartDate.getTime();
            float vital = (float) cCount * 100000 / dur;
            String account = String.format(Locale.CHINA, "%.2f%%", vital);

            Log.e(TAG, "cCount: " + cCount);
            Log.e(TAG, "dur: " + String.valueOf(dur));
            Log.e(TAG, "vital: " + String.valueOf(vital));
            Log.e(TAG, "account: " + account);
            countSnoring.clear();
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
      if(file.exists()) {
         Log.e(TAG, "file is exists!!!!!!");
      }
      int offset = SizeUtils.dp2px(1);
      filePath = FileUtils.getAppPath() + UUID.randomUUID().toString() + ".mp3";
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

      /**
       * 打鼾状态检测，一秒轮循检测，监测机制
       * 1.大于60分贝的话记录，一秒内超过某分贝即为打鼾(一秒内记录值大约为20次，超过三次，低于十五次即为打鼾，否则为其他状态)
       * 2.
       *
       */

      subscribe = Observable.interval(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
         @SuppressLint("CheckResult")
         @Override
         public void accept(Long aLong) throws Exception {
            if (integerList.size() > 0) {
               countOnce = 0;
               countMore = 0;
               count = 0;
               Observable.fromIterable(integerList).subscribe(new Consumer<Integer>() {
                  @Override
                  public void accept(Integer integer) throws Exception {
                     Log.e(TAG, "進到Observable.fromIterable....accept中");
                     if (integer > setValue) {
                        Log.e(TAG, "integer > setValue");
                        countOnce += integer;
                        ++count;
                     }
                     countMore += integer;
                  }
               }, new Consumer<Throwable>() {
                  @Override
                  public void accept(Throwable throwable) throws Exception {

                  }
               });
               Log.e("一秒数据", Arrays.toString(integerList.toArray()));

               int iOnce = countOnce / 3;
               int iMore = countMore / integerList.size();


               if (iOnce > setValue) {
                  countStatus.add(iOnce);
               }
               Log.e("Count", count + "--");
               if (isStartVibrate && iMore > setValue && count <= (16 >= integerList.size() ? integerList.size()
                       : 16) && !isPause) {
                  countSnoring.add(iMore);
                  RxVibrateTool.vibrateOnce(MainActivity.this, 300);

               }
               integerList.clear();
            }
         }
      });
      countStatus.clear();

      /**
       * 检测是否处于打鼾状态
       */
      subscribe1 = Observable.interval(10, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
         @Override
         public void accept(Long aLong) throws Exception {
            /**
             *
             */
            if (5 <= countStatus.size()) {
               countSnoring.add(countStatus.size());
               if (!isStartVibrate && !isPause) {
                  RxVibrateTool.vibrateOnce(MainActivity.this, 1000);
               }
               countStatus.clear();
               isStartVibrate = true;
            } else {
               isStartVibrate = false;
            }
         }
      });

      /**
       * 开启超时检测打鼾，一分钟后  一定时间内开启，否则关闭打鼾状态
       */
      subscribe2 = Observable.timer(1, TimeUnit.MINUTES)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidScheduler.mainThread())
              .subscribe(new Consumer<Long>() {
                 @Override
                 public void accept(Long aLong) throws Exception {
                    isStartVibrate = false;
                 }
              }, new Consumer<Throwable>() {
                 @Override
                 public void accept(Throwable throwable) throws Exception {

                 }
              });
      subscribe3 = Flowable.interval(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
         @Override
         public void accept(Long aLong) throws Exception {
            if (countSnoring.size() >= 15) {
               isFlash = !isFlash;
               if (isFlash) {
                  //openCameraFlash();
               } else {
                  //closeCameraFlash();
               }
            }
         }
      }, new Consumer<Throwable>() {
         @Override
         public void accept(Throwable throwable) throws Exception {

         }
      });

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
                        //integerList.add(value);
                        //Log.e(TAG, "sound_v: " + value);
                        integerList.add(value);
                        //Log.e(TAG, "integerList.size: " + integerList.size());
                     }
                  }
               });
            }
         });
      } catch (IOException e) {
         e.printStackTrace();
         Toast.makeText(this, "权限未获取", Toast.LENGTH_SHORT).show();
         //resolveError();
         return;
      }
      //Log.e(TAG, "So far so good!!!!!!");
      //resolveRecordUI();
      mStartDate = new Date();
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
      audioWave = findViewById(R.id.audioWave);
      snoring_v = findViewById(R.id.snoring_view);
      btn_stop_snore = findViewById(R.id.btn_stopsnore);
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

                  if(last == 0) {
                     last = countSnoring.size();
                     sum = last;
                     active_list.add(sum);
                  }
                  else {
                     sum = countSnoring.size() - last;
                     last = countSnoring.size();
                     active_list.add(sum);
                  }

                  Log.e(TAG, "active: " + active);
                  Log.e(TAG, "sum: " + sum);

                  active_v.setText(String.valueOf(active));
                  second = 0;
               }
               break;
            default:
               break;
         }
      }
   };
}
