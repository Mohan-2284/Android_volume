package com.example.volumecontroller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private EditText phoneNumberInput;
    private TextView logTextView;
    private TextView volumeStatusView;
    private AudioManager audioManager;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        logTextView = findViewById(R.id.logTextView);
        volumeStatusView = findViewById(R.id.volumeStatusView);
        Button addButton = findViewById(R.id.addButton);
        Button clearButton = findViewById(R.id.clearButton);
        Button clearLogsButton = findViewById(R.id.clearLogsButton);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        requestPermissions();

        addButton.setOnClickListener(v -> addPhoneNumber());
        clearButton.setOnClickListener(v -> clearWhitelist());
        clearLogsButton.setOnClickListener(v -> clearLogs());

        displayWhitelist();
        updateVolumeStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayWhitelist();
        updateLogs();
        updateVolumeStatus();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.ANSWER_PHONE_CALLS
                        },
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void addPhoneNumber() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        String whitelistNumbers = prefs.getString("whitelist_numbers", "");
        
        if (whitelistNumbers.contains(phoneNumber)) {
            Toast.makeText(this, "Number already in whitelist", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!whitelistNumbers.isEmpty()) {
            whitelistNumbers += "," + phoneNumber;
        } else {
            whitelistNumbers = phoneNumber;
        }

        prefs.edit().putString("whitelist_numbers", whitelistNumbers).apply();
        phoneNumberInput.setText("");
        displayWhitelist();
        Toast.makeText(this, "Phone number added!", Toast.LENGTH_SHORT).show();
    }

    private void clearWhitelist() {
        prefs.edit().remove("whitelist_numbers").apply();
        displayWhitelist();
        Toast.makeText(this, "Whitelist cleared!", Toast.LENGTH_SHORT).show();
    }

    private void clearLogs() {
        prefs.edit().remove("event_logs").apply();
        updateLogs();
        Toast.makeText(this, "Logs cleared!", Toast.LENGTH_SHORT).show();
    }

    private void displayWhitelist() {
        String whitelistNumbers = prefs.getString("whitelist_numbers", "");
        String displayText = "Whitelisted Numbers:\n\n";
        
        if (whitelistNumbers.isEmpty()) {
            displayText += "No numbers added yet";
        } else {
            String[] numbers = whitelistNumbers.split(",");
            for (String number : numbers) {
                displayText += "• " + number.trim() + "\n";
            }
        }

        logTextView.setText(displayText);
    }

    private void updateLogs() {
        String logs = prefs.getString("event_logs", "");
        String displayText = "Event Logs:\n\n";
        
        if (logs.isEmpty()) {
            displayText += "No events yet";
        } else {
            displayText += logs;
        }

        logTextView.setText(displayText);
    }

    private void updateVolumeStatus() {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        String status = "Current Volume: " + currentVolume + " / " + maxVolume;
        volumeStatusView.setText(status);
    }
}
