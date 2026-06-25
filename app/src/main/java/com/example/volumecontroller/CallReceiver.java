package com.example.volumecontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;

public class CallReceiver extends BroadcastReceiver {

    private static int previousVolume = -1;
    private AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                handleIncomingCall(context, incomingNumber);
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                handleCallEnded(context);
            }
        }
    }

    private void handleIncomingCall(Context context, String incomingNumber) {
        if (isNumberInWhitelist(context, incomingNumber)) {
            // Store current volume
            previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            
            // Set volume to maximum
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0);
            
            // Log the action
            logEvent(context, "INCOMING CALL from: " + incomingNumber + " - Volume set to MAX");
        }
    }

    private void handleCallEnded(Context context) {
        if (previousVolume != -1) {
            // Restore previous volume
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, previousVolume, 0);
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
            if (number.trim().equals(phoneNumber) || phoneNumber.endsWith(number.trim())) {
                return true;
            }
        }
        return false;
    }

    private void logEvent(Context context, String message) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String logs = prefs.getString("event_logs", "");
        logs = message + "\n" + logs;
        prefs.edit().putString("event_logs", logs).apply();
    }
}