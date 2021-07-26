package com.appgiants.locationtracker.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.appgiants.locationtracker.R


class MainActivity : AppCompatActivity() {
    var listView: ListView? = null
    var arrayList: ArrayList<String?>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.lstview) //listview from xml
        arrayList = ArrayList() //empty array list.
    var    arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList!!)
        listView?.setAdapter(arrayAdapter)
        //checking whether the read contact permission is granted.
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
// requesting to the user for permission.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 100)
        } else {
//if app already has permission this block will execute.
            readContacts()
        }
    }

    // if the user clicks ALLOW in dialog this method gets called.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        readContacts()
    }

    // function to read contacts using content resolver
    private fun readContacts() {
        val contentResolver = contentResolver
        val cursor: Cursor? =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            do {
                var name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                Log.e("name",name)
                arrayList!!.add(name)
                val id: String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val phones = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null,
                    null
                )
                while (phones!!.moveToNext()) {
                    val phoneNumber =
                        phones!!.getString(phones!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    Log.e("Number", phoneNumber)
                }
            } while (cursor.moveToNext())
            var    arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList!!)
            listView?.setAdapter(arrayAdapter)
        }
    }
}