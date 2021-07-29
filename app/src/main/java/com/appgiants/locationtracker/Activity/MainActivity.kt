package com.appgiants.locationtracker.Activity

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.appgiants.locationtracker.Adapter.ContactListAdapter
import com.appgiants.locationtracker.Model.ContactList
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.databinding.ActivityMainBinding
import com.cluttrfly.driver.ui.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream


class MainActivity : BaseActivity() {
    var rv: ListView? = null
    var arrayList: ArrayList<ContactList> = ArrayList()
    lateinit var binding:ActivityMainBinding
    override fun onNavigateUp(): Boolean {
        return super.onNavigateUp()
        onBackPressed()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tb)
        supportActionBar!!.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext,R.drawable.ic_back))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


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
        var dialog=ProgressDialog(activity)
        dialog.setMessage("Please wait")
        dialog.setTitle(getString(R.string.app_name))
        dialog.show()
        GlobalScope.launch(Dispatchers.Default) {

            val contentResolver = contentResolver
            val cursor: Cursor? =
                contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            if (cursor!!.moveToFirst()) {
                do {
                    var contactList = ContactList()
                    var name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    Log.e("name", name)
                    contactList.name = name
                    val id: String =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
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
                        contactList.number = phoneNumber.toString()
                    }
                    arrayList.add(contactList)
                } while (cursor.moveToNext())
                launch(Dispatchers.Main) {
                    dialog.dismiss()
                    var adapter=ContactListAdapter(arrayList,object :ContactListAdapter.onCountryListener{
                        override fun onCountryClicked(country: ContactList, position: Int) {
                            Toast.makeText(activity,country.name,Toast.LENGTH_SHORT).show()
                        }

                    })
                    binding.rv.adapter = adapter
                }
            }
        }
    }

    private fun getContactBitmap(contactId: Long): Bitmap? {
        var photo = BitmapFactory.decodeResource(
            resources,
            R.drawable.user
        )

        try {
            val contactUri: Uri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI,
                contactId
            )
            val displayPhotoUri: Uri =
                Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO)
            val fd = contentResolver.openAssetFileDescriptor(displayPhotoUri, "r")
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
}