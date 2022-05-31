package com.padma.bscaller;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MyForeGroundService extends Service {


    private final static String TAG = "MyForegroundService";
    public static boolean sIsReceived; // this is made true and false after each timer clock
    public static Timer sTimer = null;
    public static int i;
    public static boolean sIsAppWorkFinished = true;
    private static BroadcastReceiver mVolumeButtonRegister;
    final int MAX_ITERATION = 5;
    final Handler mHandler = new Handler();
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    public MyForeGroundService() {
    }

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyForeGroundService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;//needed for stop.

        if (intent != null) {
            msg.setData(intent.getExtras());
            mServiceHandler.sendMessage(msg);
        } else {
            Toast.makeText(MyForeGroundService.this, "The Intent to start is null?!", Toast.LENGTH_SHORT).show();
        }

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mVolumeButtonRegister);
        mVolumeButtonRegister = null;
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public Notification getNotification(String message) {

        return new NotificationCompat.Builder(getApplicationContext(), MainActivity.id1)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setChannelId(MainActivity.id1)
                .setContentTitle("BAS Caller")
                .setContentText(message)
                .build();
    }

    private void volumeButtonRegister() {

        mVolumeButtonRegister = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {

                if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {

                    Log.i("BAC", "run: volume button pressed");
                    sIsReceived = true; // Make this true whenever isReceived called
                    if (sTimer == null && sIsAppWorkFinished) {
                        sTimer = new Timer();
                        sTimer.schedule(new TimerTask() {

                            @Override
                            public void run() {

                                Log.i("BAC", "run: volume button pressed");

                                if (sIsReceived) {
                                    i++;
                                } else {
                                    cancel();
                                    sTimer.cancel();
                                    sTimer.purge();
                                    sTimer = null;
                                    i = 0;
                                }
                                if (i >= MAX_ITERATION) {
                                    cancel();
                                    sTimer.cancel();
                                    sTimer.purge();
                                    sTimer = null;
                                    i = 0;
                                    Log.i("BAC", "run: Three sec click");
                                    if (!Utils.appInForeground(context)) {

                                        Intent intent1 = new Intent(MyForeGroundService.this, MainActivity.class);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent1);
                                    }


                                }

                                sIsReceived = false; //Make this false every time a timer iterates
                            }
                        }, 0, 200);
                    }


                }
            }
        };

        registerReceiver(mVolumeButtonRegister, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            Notification notification = getNotification("BAS Caller ON");
            startForeground(msg.arg1, notification);  //not sure what the ID needs to be.
            volumeButtonRegister();
            //        stopSelf(msg.arg1);  //notification will go away as well.
        }
    }
}
