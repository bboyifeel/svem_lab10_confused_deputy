package com.example.contactssender;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 600;

    ListView    listView;
    Context     context;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                ArrayList<String> contactList = intent.getStringArrayListExtra("contactList");

                listView = findViewById(R.id.listViewContacts);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, contactList);
                listView.setAdapter(adapter);
                Toast.makeText(getApplicationContext(),"Contacts has been sent to hutelohu8942138534890peotnusheotunheotnuhsno.lu",Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction("ContactsList");
        registerReceiver(mMessageReceiver, filter);
    }

    public void onButtonClickRequestContactsViaService(View view) {
        Intent i = new Intent();
        i.setComponent(new ComponentName("com.example.contactsretrieval", "com.example.contactsretrieval.ContactRetrievalService"));
        ComponentName c = context.startService(i);
    }


    public void onButtonClickClear(View view) {
        listView = findViewById(R.id.listViewContacts);
        listView.setAdapter(null);
    }


    public void onButtonClickRequestContactsViaIntent(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        Intent myIntent = new Intent();
        myIntent.setClassName("com.example.contactsretrieval", "com.example.contactsretrieval.MainActivity");
        startActivityForResult(myIntent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK

                // get String data from Intent
                ArrayList<String> contactList = data.getStringArrayListExtra("contactList");

                ListView listView = findViewById(R.id.listViewContacts);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactList);
                listView.setAdapter(adapter);

                Toast.makeText(getApplicationContext(),"Contacts has been sent to hutelohu8942138534890peotnusheotunheotnuhsno.lu",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
