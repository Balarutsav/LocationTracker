package com.appgiants.locationtracker.Activity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.appgiants.locationtracker.ApplicationClass
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.Utils.NotificationBadge
import com.cluttrfly.driver.ui.base.BaseActivity
import me.leolin.shortcutbadger.ShortcutBadger


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash)
        init()
    }
    fun init(){

            Handler(Looper.getMainLooper()).postDelayed({


            val application = application
            if (application !is ApplicationClass) {
                Log.e("Splash Activity", "Failed to cast application to MyApplication.")
                startMainActivity()
            }

            // Show the app open ad.

            // Show the app open ad.
            (application as ApplicationClass)
                .showAdIfAvailable(
                    this@SplashActivity,
                    object : ApplicationClass.OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            startMainActivity()
                        }
                    })
        }, 10000)
    }

    private fun startMainActivity() {
        startActivityWithNewTaskAnimation(StartActivity::class.java)
    }
    private fun showNotification(title: String, message: String, data: Map<String, String>?) {
        var intent = Intent(applicationContext, HomeScreenActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // This code targets Android O and Above (Channels).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val DEFAULT_CHANNEL_ID ="12313213"
            val NOTIFICATION_CHANNEL_NAME: CharSequence = "notification_channel_school_app"
            val CHANNEL_DESCRIPTION = "channel_description"
            val notificationChannel = NotificationChannel(
                DEFAULT_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = CHANNEL_DESCRIPTION
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            assert(notificationManager != null)
            notificationManager.createNotificationChannel(notificationChannel)
            val notificationBuilder: Notification.Builder =
                Notification.Builder(this, DEFAULT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText("Hello How are you")
                    .setAutoCancel(true)
                    .setBadgeIconType(Notification.BADGE_ICON_LARGE)
                    .setNumber(10)
                    .setContentIntent(pendingIntent)
            notificationManager.notify(0, notificationBuilder.build())
        }


        // This Code targets Android N and lower.
        val notificationBuilder: Notification.Builder = Notification.Builder(this)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        ShortcutBadger.applyCount(applicationContext,10)
        ShortcutBadger.applyNotification(activity,notificationBuilder.build(),10)
        notificationManager.notify(0, notificationBuilder.build())
    }
    private fun shortcutAdd(name: String, number: Int) {
        // Intent to be send, when shortcut is pressed by user ("launched")
        val shortcutIntent = Intent(applicationContext, SplashActivity::class.java)
//        shortcutIntent.action = Constants.ACTION_PLAY

        // Create bitmap with number in it -> very default. You probably want to give it a more stylish look
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.setColor(-0x7f7f80) // gray
        paint.setTextAlign(Paint.Align.CENTER)
        paint.setTextSize(50f)
        Canvas(bitmap).drawText("" + number, 50f, 50f, paint)
        /*(findViewById<View>(R.id.icon) as ImageView).setImageBitmap(bitmap)*/

        // Decorate the shortcut
        val addIntent = Intent()
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap)

        // Inform launcher to create shortcut
        addIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"
        applicationContext.sendBroadcast(addIntent)
    }

    private fun shortcutDel(name: String) {
        // Intent to be send, when shortcut is pressed by user ("launched")
        val shortcutIntent = Intent(applicationContext, SplashActivity::class.java)
//        shortcutIntent.action = Constants.ACTION_PLAY

        // Decorate the shortcut
        val delIntent = Intent()
        delIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        delIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)

        // Inform launcher to remove shortcut
        delIntent.action = "com.android.launcher.action.UNINSTALL_SHORTCUT"
        applicationContext.sendBroadcast(delIntent)
    }
    fun setBadge(context: Context, count: Int) {
        val launcherClassName = getLauncherClassName(context) ?: return
        val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
        intent.putExtra("badge_count", count)
        intent.putExtra("badge_count_package_name", context.getPackageName())
        intent.putExtra("badge_count_class_name", launcherClassName)
        context.sendBroadcast(intent)
    }

    fun getLauncherClassName(context: Context): String? {
        val pm: PackageManager = context.getPackageManager()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfos) {
            val pkgName = resolveInfo.activityInfo.applicationInfo.packageName
            if (pkgName.equals(context.getPackageName(), ignoreCase = true)) {
                return resolveInfo.activityInfo.name
            }
        }
        return null
    }
}