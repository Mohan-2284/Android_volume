package com.example.volumecontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

    private static final String TAG = "CallReceiver";
    private static int previousVolume = -1;
    private AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        if (intent.getAction() != null && intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state != null) {
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    Log.d(TAG, "Incoming call from: " + incomingNumber);
                    handleIncomingCall(context, incomingNumber);
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Log.d(TAG, "Call ended");
                    handleCallEnded(context);
                }
            }
        }
    }

    private void handleIncomingCall(Context context, String incomingNumber) {
        if (incomingNumber != null && isNumberInWhitelist(context, incomingNumber)) {
            previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0);
            Log.d(TAG, "Volume set to MAX: " + maxVolume + " from: " + previousVolume);
            logEvent(context, "INCOMING CALL from: " + incomingNumber + " - Volume set to MAX");
        }
    }

    private void handleCallEnded(Context context) {
        if (previousVolume != -1) {
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, previousVolume, 0);
            Log.d(TAG, "Volume restored to: " + previousVolume);
            logEvent(context, "CALL ENDED - Volume restored to: " + previousVolume);
            previousVolume = -1;
        }
    }

    private boolean isNumberInWhitelist(Context context, String phoneNumber) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String whitelistNumbers = prefs.getString("whitelist_numbers", "");
        
        if (whitelistNumbers.isEmpty()) {
            return false;
        }
        
        String[] numbers = whitelistNumbers.split(",");
        for (String number : numbers) {
            String trimmedNumber = number.trim();
            if (phoneNumber.equals(trimmedNumber) || phoneNumber.endsWith(trimmedNumber)) {
                return true;
            }
        }
        return false;
    }

    private void logEvent(Context context, String message) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String logs = prefs.getString("event_logs", "");
        long timestamp = System.currentTimeMillis();
        String timeStamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date(timestamp));
        logs = "[" + timeStamp + "] " + message + "\n" + logs;
        prefs.edit().putString("event_logs", logs).apply();
    }
}
