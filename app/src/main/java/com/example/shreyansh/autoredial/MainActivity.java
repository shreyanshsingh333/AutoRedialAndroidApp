package com.example.shreyansh.autoredial;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    MainActivity instance;
    String[] permissionsRequired = new String[]{Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE};
    private NotificationManager mNotificationManager;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    TextView dial;

    public MainActivity getInstance() {
        return instance;
    }


3
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance=this;

        dial = findViewById(R.id.dial_text);

        TextView normal = findViewById(R.id.normal_text);
       /** normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},100);
                }else {
                    if(checkIfMicrophoneIsBusy(MainActivity.this)){
                        Log.d("TAG","Mic is free");
                    }else {
                        Log.d("TAG","MIC is busy");
                    }
                }
            }
        });*/





        // Get the notification manager instance
       /* mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){ // If api level minimum 23

            // If notification policy access granted for this package
            if(mNotificationManager.isNotificationPolicyAccessGranted()){


                // Set the interruption filter
                //mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            }else {
                Toast.makeText(MainActivity.this,"Give DND access Permission.",Toast.LENGTH_LONG).show();
                // If notification policy access not granted for this package
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }*/

        if(ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[1])){
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Contacts and Telephone permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
            }
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }





    }





    public static boolean checkIfMicrophoneIsBusy(Context ctx){
        AudioRecord audio = null;
        boolean ready = true;
        try{
            int baseSampleRate = 44100;
            int channel = AudioFormat.CHANNEL_IN_MONO;
            int format = AudioFormat.ENCODING_PCM_16BIT;
            int buffSize = AudioRecord.getMinBufferSize(baseSampleRate, channel, format );
            audio = new AudioRecord(MediaRecorder.AudioSource.MIC, baseSampleRate, channel, format, buffSize );
            audio.startRecording();
            short buffer[] = new short[buffSize];
            int audioStatus = audio.read(buffer, 0, buffSize);

            if(audioStatus == AudioRecord.ERROR_INVALID_OPERATION || audioStatus == AudioRecord.STATE_UNINITIALIZED /* For Android 6.0 */)
                ready = false;
        }
        catch(Exception e){
            ready = false;
        }
        finally {
            try{
                audio.release();
            }
            catch(Exception e){}
        }

        return ready;
    }

    private void proceedAfterPermission() {
        // Toast.makeText(getBaseContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
        // Get intent object sent from the IncomingCallReceiver
        Intent intent=getIntent();
        Bundle b=intent.getExtras();

        /**TextView tv=findViewById(R.id.txtmessage);
         if(b!=null){
         Display rejected number in the TextView
         tv.setText(b.getString("number"));

         }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT){
            //check if all permissions are granted
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if(allgranted){
                proceedAfterPermission();
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[1])){

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }


}
