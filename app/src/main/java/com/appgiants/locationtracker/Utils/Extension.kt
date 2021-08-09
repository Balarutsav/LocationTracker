package com.appgiants.locationtracker.Utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatImageView
import com.appgiants.locationtracker.R
import com.bumptech.glide.Glide
import me.leolin.shortcutbadger.ShortcutBadger
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*

fun Context.hideKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    val activity = this as Activity
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

 fun Context.updateBadge(count: Int) {
    try {
        if (count == 0) ShortcutBadger.removeCount(this) else ShortcutBadger.applyCount(this, count)
    } catch (t: Throwable) {
        Log.w("Badges", t)
    }
}

fun AppCompatImageView.loadImage(context: Context, imgurl: String) {
    Glide.with(context).load(imgurl).into(this);
}

fun printlog(Activity: String?, message: String?) {
    Log.e(Activity, message!!)
}
fun String.getFormattedData(): String {
 try {
        val convertedPrice = this.toFloat()
        return String.format("%.2f", convertedPrice)
    } catch (e: NumberFormatException) {
        e.printStackTrace()
    }
    return this
}

fun Context.getAddressFromLocation(
    latitude: Double, longitude: Double,
    context: Context, handler: Handler?
) {
    val thread: Thread = object : Thread() {
        override fun run() {
            val geocoder = Geocoder(context, Locale.getDefault())
            var result: String? = null
            var jsonObject = JSONObject()
            try {
                val addressList = geocoder.getFromLocation(
                    latitude, longitude, 1
                )
                jsonObject.put(AppConstants.latitude,latitude.toString())
                jsonObject.put(AppConstants.longitude,longitude.toString())
                if (addressList != null && addressList.size > 0) {
                    val address = addressList[0]
                    val sb = StringBuilder()
                    sb.append(address.getAddressLine(0)).append("\n")
                    Log.e("Full address",address.getAddressLine(0))
                    Log.e("feature name",address.featureName)
                    jsonObject.put(AppConstants.COUNTRY,address.countryName)
                    jsonObject.put(AppConstants.STATE,address.adminArea)
                    jsonObject.put(AppConstants.PINCODE,address.postalCode)
                    jsonObject.put(AppConstants.city,address.locality)
                    jsonObject.put(AppConstants.COMPLETE_ADDRESS,address.getAddressLine(0))
                    sb.append(address.thoroughfare).append("\n")
                    Log.e("through fare",address.thoroughfare)
                    sb.append(address.locality).append("\n")
                    sb.append(address.postalCode).append("\n")
                    Log.e("postal code",address.postalCode)
                    sb.append(address.adminArea).append("\n")
                    Log.e("admin area",address.adminArea)
                    sb.append(address.countryName)
                    Log.e("country name",address.countryName    )
                    result = jsonObject.toString();
                }
            } catch (e: Exception) {
                Log.e("Get Address", "Unable connect to Geocoder", e)
            } finally {
                val message = Message.obtain()
                message.target = handler
                if (result != null) {
                    message.what = 1
                    val bundle = Bundle()
                    bundle.putString("address", result)
                    message.data = bundle
                } else {
                    message.what = 1
                    val bundle = Bundle()
                    result = """Latitude: $latitude Longitude: $longitude
 Unable to get address for this lat-long."""
                    bundle.putString("address", result)
                    message.data = bundle
                }
                message.sendToTarget()
            }
        }
    }
    thread.start()


}
fun Context.showAlert( message: String){

    AlertDialog.Builder(this)
        .setTitle(this.resources.getString(R.string.app_name))
        .setMessage(message)
        .setPositiveButton("Okay",
            DialogInterface.OnClickListener { dialog, which ->

            dialog.dismiss()
            })
        .show()
}

fun Context.getContactBitmap(contactId: Long): Bitmap? {
    var photo = BitmapFactory.decodeResource(
        this.resources,
        R.drawable.user
    )

    try {
        val contactUri: Uri = ContentUris.withAppendedId(
            ContactsContract.Contacts.CONTENT_URI,
            contactId
        )
        val displayPhotoUri: Uri =
            Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO)
        val fd =this.contentResolver.openAssetFileDescriptor(displayPhotoUri, "r")
        val inputStream: InputStream? = fd!!.createInputStream()
        if (inputStream != null) {
            photo = BitmapFactory.decodeStream(inputStream)
        }
        assert(inputStream != null)
        inputStream?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return photo
}
