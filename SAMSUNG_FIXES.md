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

### 2. AlarmService.java
- Changed from `acquireCpuWakeLock()` to `acquireScreenCpuWakeLock()`
- Ensures screen turns on when alarm triggers
- **CRITICAL FIX**: Added automatic AlarmActivity launch when alarm fires
- The service now properly launches the alarm UI in addition to starting sound/vibration

### 3. AlarmActivity.java
- Added additional `FLAG_DISMISS_KEYGUARD` flag
- Helps ensure lock screen is properly dismissed

### 4. AlarmNotifications.java
- Added `DEFAULT_VIBRATE` to notification defaults
- Set priority to `PRIORITY_MAX` for alarm notifications
- Ensures notifications are shown prominently

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
   - Set an alarm for 1-2 minutes in the future
   - Let the device screen turn off or lock the device
   - Wait for the alarm to trigger

## Expected Behavior

✅ Screen turns on automatically when alarm triggers
✅ Alarm activity appears immediately (not home screen)
✅ Notification is visible in notification shade
✅ Vibration works properly
✅ Sound plays as expected

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