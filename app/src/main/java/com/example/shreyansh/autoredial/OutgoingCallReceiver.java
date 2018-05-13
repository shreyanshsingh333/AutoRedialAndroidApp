package com.example.shreyansh.autoredial;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

public class OutgoingCallReceiver extends BroadcastReceiver{
    TelephonyManager telephonyManager;
    Context cont;
    CountDownTimer countDownTimer;
    @Override
    public void onReceive(Context context, Intent intent) {
        String state= intent.getStringExtra(telephonyManager.EXTRA_STATE);
        cont = context;
        if(state == null) {
            //Outgoig call
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d("TAG", "Outgoig number: " + number);
        }else if(state.equals(telephonyManager.EXTRA_STATE_RINGING)){
            //Incoming call
            Log.d("TAG","Ringing Now");
        }else if(state.equals(telephonyManager.EXTRA_STATE_OFFHOOK)){
            Log.d("TAG","Offhook Now");
           // mainActivity.checkIfMicrophoneIsBusy(context);

        /**    if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(mainActivity,
                        new String[]{Manifest.permission.RECORD_AUDIO},100);
            }else {
                if(mainActivity.checkIfMicrophoneIsBusy(context)){
                    Log.d("TAG","Mic is free");
                }else {
                    Log.d("TAG","MIC is busy");
                }
            }*/
            //String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            //Log.d("TAG","Outgoig number: "+number);
        }else if(state.equals(telephonyManager.EXTRA_STATE_IDLE)){
            Log.d("TAG","Idle Now");
            String[] res;
            res = getCallDetails();
            Log.d("TAG","res[0] : "+res[0]);
            Log.d("TAG","res[1] : "+res[1]);
            Log.d("TAG","res[2] :"+res[2]);
            Log.d("TAG","Outgoing type :"+CallLog.Calls.OUTGOING_TYPE);
            final String duration = res[0];
            final String number = res[1];
            final String type= res[2];
            countDownTimer = new CountDownTimer(10*1000,1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    if(type.equals("2")){
                        Log.d("TAG","outgoing call");
                         if (duration.equals("0")) {
                           redial(number);
                        }
                    }
                }
            }.start();

        }
    }



    private void redial(String number){
        Log.d("TAG","Inside redial no :"+number);
        if (ContextCompat.checkSelfPermission(cont,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+number));
            cont.startActivity(intent);
        }
    }


    private String[] getCallDetails() {
        StringBuffer sb = new StringBuffer();
        String lastDuration = "";
        String lastNumber="";
        String lastType="";
        String[] arr = new String[3];
        Uri contacts = CallLog.Calls.CONTENT_URI;
        if (ContextCompat.checkSelfPermission(cont,
                Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {

        }else {
            Cursor managedCursor = cont.getContentResolver().query(contacts, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("Call Details :");
            while (managedCursor.moveToNext()) {

                HashMap rowDataCall = new HashMap<String, String>();

                String phNumber = managedCursor.getString(number);
                lastNumber = phNumber;
                String callType = managedCursor.getString(type);
                lastType = callType;
                String callDate = managedCursor.getString(date);
                String callDayTime = new Date(Long.valueOf(callDate)).toString();
                // long timestamp = convertDateToTimestamp(callDayTime);
                String callDuration = managedCursor.getString(duration);
                lastDuration = callDuration;
                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }
                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                sb.append("\n----------------------------------");


            }

            managedCursor.close();
            arr[0]=lastDuration;
            arr[1]= lastNumber;
            arr[2]= lastType;
            return arr;
            // System.out.println(sb);
        }
        return null;
    }
}
