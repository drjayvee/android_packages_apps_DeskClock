# Samsung Device Fixes

This document describes the changes made to fix alarm issues on Samsung devices, particularly the Samsung A3 (2017) with Samsung Experience 9.

## Issues Addressed

1. **Screen not activating** when alarm triggers ✅ FIXED
2. **No alarm notification** visible
3. **Home screen shown** instead of alarm activity ✅ FIXED
4. **Alarm UI not launching automatically** ✅ FIXED
5. **General Samsung compatibility** issues

## Changes Made

### 1. AlarmAlertWakeLock.java
- Added Samsung-specific detection using `Build.MANUFACTURER`
- Created `createScreenWakeLock()` method with aggressive wake lock flags
- Uses `FULL_WAKE_LOCK` for Samsung devices to overcome aggressive power management
- **NEW**: Added `createDeepSleepWakeLock()` method for waking device from deep sleep
- **NEW**: Added `acquireDeepSleepWakeLock()` method for early wake lock acquisition

### 2. AlarmService.java
- Changed from `acquireCpuWakeLock()` to `acquireScreenCpuWakeLock()`
- Ensures screen turns on when alarm triggers
- **CRITICAL FIX**: Added automatic AlarmActivity launch when alarm fires
- The service now properly launches the alarm UI in addition to starting sound/vibration
- **NEW**: Now uses `acquireDeepSleepWakeLock()` to ensure device wakes from deep sleep

### 3. AlarmActivity.java
- Added additional `FLAG_DISMISS_KEYGUARD` flag
- Helps ensure lock screen is properly dismissed

### 4. AlarmNotifications.java
- Added `DEFAULT_VIBRATE` to notification defaults
- Set priority to `PRIORITY_MAX` for alarm notifications
- Ensures notifications are shown prominently

### 5. AlarmStateManager.java
- **NEW**: Modified `onReceive()` to acquire deep sleep wake lock immediately when alarm state change is received
- **NEW**: For Samsung devices, acquires additional screen wake lock immediately
- **NEW**: Modified `AlarmManagerStateChangeScheduler.scheduleInstanceStateChange()` to use `setAlarmClock()` for FIRED_STATE transitions on Android M+
- **NEW**: This provides better reliability for waking device from deep sleep and Doze mode
- **NEW**: Uses `AlarmManager.AlarmClockInfo` which is designed specifically for alarm clock apps

## Keystore Information

The app is now signed with:
- **Keystore file**: `my_keystore.jks`
- **Keystore password**: `android`
- **Key alias**: `mekker_key`
- **Key password**: `android`

## Testing Instructions

1. **Uninstall previous version**:
   ```bash
   adb uninstall com.android.deskclock
   ```

2. **Install new version**:
   ```bash
   adb install mekker-fixed.apk
   ```

3. **Test alarm functionality**:
   - **Basic test**: Set an alarm for 1-2 minutes in the future, let screen turn off, wait for trigger
   - **Deep sleep test**: Set an alarm for middle of the night (e.g., 3 AM), let device enter deep sleep
   - **Doze mode test**: Let device sit idle for 30+ minutes to enter Doze mode, then test alarm
   - **Samsung power saving test**: Enable Samsung's power saving modes and test alarm reliability

## Expected Behavior

✅ Screen turns on automatically when alarm triggers (even from deep sleep)
✅ Alarm activity appears immediately (not home screen)
✅ Notification is visible in notification shade
✅ Vibration works properly
✅ Sound plays as expected
✅ Device wakes reliably from Doze mode
✅ Works with Samsung's aggressive power management enabled

## Samsung-Specific Troubleshooting

If issues persist:

1. **Disable battery optimization**:
   - Settings > Apps > DeskClock > Battery > Optimize battery usage > Select "All apps" > Find DeskClock and disable optimization

2. **Add to unmonitored apps**:
   - Settings > Device maintenance > Battery > Unmonitored apps > Add DeskClock

3. **Check notification permissions**:
   - Settings > Apps > DeskClock > Notifications > Enable all notification categories

4. **Check Do Not Disturb settings**:
   - Ensure alarms are allowed to bypass Do Not Disturb mode

5. **Disable Samsung power saving modes**:
   - Settings > Device maintenance > Battery > Power mode > Set to "Optimized" or "High performance"
   - Disable "Adaptive battery" and "Put unused apps to sleep"

6. **Check app sleep settings**:
   - Settings > Device maintenance > Battery > Sleeping apps > Ensure DeskClock is not listed

7. **Enable background data**:
   - Settings > Apps > DeskClock > Mobile data > Allow background data usage

## Build Instructions

```bash
# Build the app
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 8.0.412-tem
~/.local/share/mise/installs/gradle/4.1.0/gradle-4.1/bin/gradle build

# Sign the APK
~/Android/Sdk/build-tools/26.0.3/apksigner sign \
  --ks my_keystore.jks \
  --ks-pass pass:android \
  --ks-key-alias mekker_key \
  --key-pass pass:android \
  --out mekker-fixed.apk \
  app/build/outputs/apk/release/app-release-unsigned.apk
```

## Notes

- These changes are minimal and targeted specifically at Samsung's known issues
- The app maintains full compatibility with other Android devices
- All changes are backward compatible with the original AOSP DeskClock functionality