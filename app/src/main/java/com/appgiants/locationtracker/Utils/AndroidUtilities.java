/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.appgiants.locationtracker.Utils;


import com.appgiants.locationtracker.ApplicationClass;

public class AndroidUtilities {


    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if ( ApplicationClass.applicationHandler == null) {
            return;
        }
        if (delay == 0) {
            ApplicationClass.applicationHandler.post(runnable);
        } else {
            ApplicationClass.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        if (ApplicationClass.applicationHandler == null) {
            return;
        }
        ApplicationClass.applicationHandler.removeCallbacks(runnable);
    }

}
