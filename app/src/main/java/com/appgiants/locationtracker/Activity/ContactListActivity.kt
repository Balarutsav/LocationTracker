package com.appgiants.locationtracker.Activity

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.appgiants.locationtracker.Adapter.ContactListAdapter
import com.appgiants.locationtracker.Model.ContactList
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.databinding.ActivityMainBinding
import com.appgiants.locationtracker.room.AppDatabase
import com.cluttrfly.driver.ui.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream


class ContactListActivity : BaseActivity() {
    var rv: ListView? = null
    var arrayList: ArrayList<ContactList> = ArrayList()
    lateinit var db:AppDatabase
    lateinit var binding: ActivityMainBinding
    override fun onNavigateUp(): Boolean {
        return super.onNavigateUp()
        onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tb)
        supportActionBar!!.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.ic_back
            )
        )
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        db = AppDatabase.getInstance(baseContext)

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

        GlobalScope.launch(Dispatchers.Default) {
            var adapter = ContactListAdapter(arrayList,
                object : ContactListAdapter.OnConuntryClicked {
                    override fun onCountryClicked(contactList: ContactList, position: Int) {
                        var intent= Intent(activity,FindLocation::class.java)
                        intent.putExtra("contact_id",contactList.id)
                        startActivity(intent)
                    }

                })
            binding.rv.adapter = adapter
            binding.rv.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )

            arrayList.addAll(db.userDao().getAll())


            var list:ArrayList<ContactList> = ArrayList()
            val contentResolver = contentResolver
            val cursor: Cursor? =
                contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            db.userDao().deleteAll()

            if (cursor!!.moveToFirst()) {
                do {

                    val id: String =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    var contactList = ContactList(id = id.toLong())
                    var name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    Log.e("name", name)
                    contactList.name = name

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
                    list.add(contactList)

                    db.userDao().insertAll(contactList)
                } while (cursor.moveToNext())
                launch(Dispatchers.Main) {
                    arrayList.clear()
                    arrayList.addAll(list)
                    adapter.notifyDataSetChanged()



                }
            }
        }
    }


}