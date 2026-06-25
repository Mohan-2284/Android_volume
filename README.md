# Volume Controller - Android App

An Android application that automatically sets the phone volume to maximum when receiving calls from specific phone numbers and restores the previous volume after the call ends.

## Features

✅ **Whitelist Management**: Add phone numbers that trigger automatic volume increase  
✅ **Auto Volume Control**: Automatically sets volume to maximum on incoming calls from whitelisted numbers  
✅ **Volume Restoration**: Restores the previous volume level after the call ends  
✅ **Event Logging**: Track all volume changes with timestamps  
✅ **User-Friendly UI**: Simple interface to manage whitelist and view logs  

## Installation

1. Clone this repository
2. Open in Android Studio
3. Build the project: `Build > Build APK(s)`
4. Install on your Android device

## Usage

1. **Add Phone Numbers**: Enter a phone number and tap "Add to Whitelist"
2. **Receive Calls**: When someone from the whitelist calls, volume automatically goes to maximum
3. **View Events**: Check the Event Logs section to see all volume changes
4. **Manage**: Use "Clear List" to remove all numbers or "Clear Logs" to reset event history

## Permissions Required

- `READ_PHONE_STATE`: To detect incoming calls
- `READ_CONTACTS`: To access contact information
- `CALL_PHONE`: For call handling
- `ANSWER_PHONE_CALLS`: To answer calls

## File Structure

```
├── AndroidManifest.xml              # App manifest with permissions
├── app/src/main/java/com/example/volumecontroller/
│   ├── MainActivity.java            # Main UI activity
│   └── CallReceiver.java            # Broadcast receiver for phone state
├── app/src/main/res/layout/
│   └── activity_main.xml            # UI layout
├── app/src/main/res/values/
│   └── strings.xml                  # App strings
└── app/build.gradle                 # Gradle build configuration
```

## Technical Details

- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 33 (Android 13)
- **Language**: Java
- **Storage**: SharedPreferences for whitelist and logs

## How It Works

1. The app registers a BroadcastReceiver to listen for phone state changes
2. When an incoming call is detected, it checks if the number is in the whitelist
3. If matched, it stores the current volume and sets it to maximum
4. When the call ends (IDLE state), it restores the previous volume
5. All events are logged for user reference

## License

Free to use and modify
