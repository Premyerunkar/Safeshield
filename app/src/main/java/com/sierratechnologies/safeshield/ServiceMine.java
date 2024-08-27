package com.sierratechnologies.safeshield;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class ServiceMine extends Service {

    private boolean isRunning = false;
    private FusedLocationProviderClient fusedLocationClient;
    private MediaPlayer mediaPlayer;
    private boolean isSirenEnabled = true; // Default to true (siren plays)

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    SmsManager manager = SmsManager.getDefault();
    String myLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        myLocation = "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
                    } else {
                        myLocation = "Unable to Find Location :";
                    }
                });

        mediaPlayer = MediaPlayer.create(this, R.raw.siren_sound);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        ShakeDetector.create(this, () -> {
            if (isSirenEnabled) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                }

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }

            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            String ENUM = sharedPreferences.getString("ENUM", "NONE");
            if (!ENUM.equalsIgnoreCase("NONE")) {
                manager.sendTextMessage(ENUM, null, "I'm in Trouble!\nSending My Location:\n" + myLocation, null, null);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if ("STOP".equalsIgnoreCase(intent.getAction())) {
                if (isRunning) {
                    this.stopForeground(true);
                    this.stopSelf();
                }
            } else if ("START".equalsIgnoreCase(intent.getAction())) {
                isSirenEnabled = intent.getBooleanExtra("SIREN_ENABLED", true);

                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("MYID", "CHANNELFOREGROUND", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    m.createNotificationChannel(channel);

                    Notification notification = new Notification.Builder(this, "MYID")
                            .setContentTitle("Safeshield")
                            .setContentText("Your motions and activities are detected")
                            .setSmallIcon(R.drawable.img_1)
                            .setContentIntent(pendingIntent)
                            .build();
                    this.startForeground(115, notification);
                    isRunning = true;
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
