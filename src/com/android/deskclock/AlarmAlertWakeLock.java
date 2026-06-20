/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.deskclock;

import android.content.Context;
import android.os.PowerManager;
import android.os.Build;

/**
 * Utility class to hold wake lock in app.
 */
public class AlarmAlertWakeLock {

    private static final String TAG = "AlarmAlertWakeLock";

    private static PowerManager.WakeLock sCpuWakeLock;

    public static PowerManager.WakeLock createPartialWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    public static PowerManager.WakeLock createScreenWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        int flags = PowerManager.SCREEN_BRIGHT_WAKE_LOCK 
                | PowerManager.ACQUIRE_CAUSES_WAKEUP 
                | PowerManager.ON_AFTER_RELEASE;
        
        // For Samsung devices, use a more aggressive wake lock
        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            flags |= PowerManager.FULL_WAKE_LOCK;
        }
        
        return pm.newWakeLock(flags, TAG);
    }

    /**
     * Creates a wake lock specifically for waking the device from deep sleep when alarm fires.
     * This is more aggressive than the screen wake lock and should be used when we need to
     * ensure the device wakes up from deep sleep.
     */
    public static PowerManager.WakeLock createDeepSleepWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        int flags = PowerManager.PARTIAL_WAKE_LOCK 
                | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        
        // For Samsung devices, use full wake lock to overcome aggressive power management
        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            flags = PowerManager.FULL_WAKE_LOCK 
                  | PowerManager.ACQUIRE_CAUSES_WAKEUP
                  | PowerManager.ON_AFTER_RELEASE;
        }
        
        return pm.newWakeLock(flags, TAG + ".DeepSleep");
    }

    public static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }

        sCpuWakeLock = createPartialWakeLock(context);
        sCpuWakeLock.acquire();
    }

    /**
     * Acquires a deep sleep wake lock to ensure device wakes from deep sleep.
     * This should be called when we need to ensure the device wakes up from deep sleep.
     */
    public static void acquireDeepSleepWakeLock(Context context) {
        PowerManager.WakeLock deepSleepWl = createDeepSleepWakeLock(context);
        deepSleepWl.acquire();
        // Note: This wake lock should be released separately as it's used for specific cases
    }

    public static void acquireScreenCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }
        sCpuWakeLock = createScreenWakeLock(context);
        sCpuWakeLock.acquire();
    }

    public static void releaseCpuLock() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
