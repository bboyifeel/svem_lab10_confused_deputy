package com.example.contactsretrieval;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    ListView    listView;
    Context     context;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            // get String data from Intent
            if (intent != null) {
                ArrayList<String> contactList = intent.getStringArrayListExtra("contactList");
                listView = findViewById(R.id.listViewContacts);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, contactList);
                listView.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();

        context = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction("ContactsList");
        //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, filter);
        registerReceiver(mMessageReceiver, filter);


        Button btnStartService  = findViewById(R.id.btnStartService);
        Button btnStopService   = findViewById(R.id.btnStopService);

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getBaseContext(), ContactRetrievalService.class));
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getBaseContext(), ContactRetrievalService.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ContactsList");
        registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }

    void requestPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }


    public void get(View v)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"READ CONTACT PERMISSIONS NEEDED",Toast.LENGTH_SHORT).show();
            return;
        }


        listView = (ListView) findViewById(R.id.listViewContacts);
        // Retrieve contacts here
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);

        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID};

        int[] to = {android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }


    public void send(View v)
    {
        String[] projection = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);

        List<String> contactList = new ArrayList<String>();
        cursor.moveToFirst();
        do
        {
            contactList.add(cursor.getString(0) + " " + cursor.getString(1));
        } while(cursor.moveToNext());

        cursor.close();

        Intent intent = new Intent();
        intent.putStringArrayListExtra("contactList", (ArrayList<String>) contactList);
        setResult(RESULT_OK, intent);
        finish();
    }

}
