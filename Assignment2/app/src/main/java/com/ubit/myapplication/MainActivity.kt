package com.ubit.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.ubit.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun getContacts(view: View) {
        var intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        resultLauncher.launch(intent)

    }
    @SuppressLint("Range") //yeh pata nahi kya hai lkn hai
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val contentResolver=contentResolver
            val contentURI=data?.data!!
            val projection= arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME // only two attributes
            )
            var name=""
            var id=""
            val selectionArgs = arrayOf("1") //like in python array of list
            val selection ="${ContactsContract.Contacts.HAS_PHONE_NUMBER} = ?" // ? one by one contact in this

            val cursor=contentResolver.query(contentURI,projection,selection,selectionArgs,ContactsContract.Contacts.DISPLAY_NAME)
            try {
            if (cursor != null) {
                while(cursor.moveToNext()){
                    name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    id=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                }
                cursor.close()

            }
            val phoneProjection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE //type needed in GetTypeLabel func
            )

            val phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                phoneProjection,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(id), //get by id
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

                if (phoneCursor != null) { //one by one check hoga
                    while (phoneCursor.moveToNext()) {
                        val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val phoneType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))

                        val phoneLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(resources, phoneType, "Unknown").toString()
                        Toast.makeText(this, "Name: $name Contact: $phoneNumber",Toast.LENGTH_LONG).show() //print


                    }
                }

        } catch (e:Exception ){
//log to check any error in the logcat
        Log.e("QUERY_ERROR", "Error querying content provider: ${e.message}", e)

    }
        }
    }


}