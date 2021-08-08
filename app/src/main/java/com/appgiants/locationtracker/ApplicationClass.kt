package com.appgiants.locationtracker

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log

import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*



class ApplicationClass : Application(), Application.ActivityLifecycleCallbacks,
    LifecycleObserver {


    var startTime: Long = 0


    private var appOpenAdManager: AppOpenAdManager? = null
    private var currentActivity: Activity? = null
    companion object {


        @JvmField
        var context: Context?=null

        @JvmField
        var applicationHandler: Handler? = null
        private const val LOG_TAG = "AppOpenAdManager"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294"
        var currentNetworkInfo: NetworkInfo? = null


        private val connectivityManager: ConnectivityManager? = null

        private val applicationInited = false
    }
    override fun onCreate() {
        super.onCreate()
        context= applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(
            this
        ) { }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()
    }

    /** LifecycleObserver method that shows the app open ad when the app moves to foreground.  */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        appOpenAdManager!!.showAdIfAvailable(currentActivity!!)
    }

    /** ActivityLifecycleCallback methods.  */
    override fun onActivityCreated(activity: Activity, @Nullable savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!appOpenAdManager!!.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    /**
     * Shows an app open ad.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager!!.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete
     * (i.e. dismissed or fails to show).
     */
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    /** Inner class that loads and shows app open ads.  */
    private inner class AppOpenAdManager
    /** Constructor.  */
    {
        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad.  */
        private var loadTime: Long = 0

        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        private fun loadAd(context: Context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable) {
                return
            }
            isLoadingAd = true
            val request: AdRequest =AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                Companion.AD_UNIT_ID,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().getTime()
                        Log.d(Companion.LOG_TAG, "onAdLoaded.")
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                        Log.d(Companion.LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                    }
                })
        }

        /** Check if ad was loaded more than n hours ago.  */
        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().getTime() - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }// Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        /** Check if ad exists and can be shown.  */
        private val isAdAvailable: Boolean
            private get() =// Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
                // https://support.google.com/admob/answer/9341964?hl=en
                appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         */
        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener =
                object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        // Empty because the user will go back to the activity that shows the ad.
                    }
                }
        ) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(Companion.LOG_TAG, "The app open ad is already showing.")
                return
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable) {
                Log.d(Companion.LOG_TAG, "The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }
            Log.d(Companion.LOG_TAG, "Will show ad.")
            appOpenAd!!.setFullScreenContentCallback(
                object : FullScreenContentCallback() {
                    /** Called when full screen content is dismissed.  */
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        Log.d(Companion.LOG_TAG, "onAdDismissedFullScreenContent.")

                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    /** Called when fullscreen content failed to show.  */
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        appOpenAd = null
                        isShowingAd = false
                        Log.d(
                            Companion.LOG_TAG,
                            "onAdFailedToShowFullScreenContent: " + adError.message
                        )

                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    /** Called when fullscreen content is shown.  */
                    override fun onAdShowedFullScreenContent() {
                        Log.d(Companion.LOG_TAG, "onAdShowedFullScreenContent.")

                    }
                })
            isShowingAd = true
            appOpenAd!!.show(activity)
        }


    }
}