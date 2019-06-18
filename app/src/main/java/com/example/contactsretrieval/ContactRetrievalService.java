package com.example.contactsretrieval;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactRetrievalService extends Service {
    public ContactRetrievalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        sendMessage();

        this.stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }


    public void sendMessage()
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

        Intent intent = new Intent("ContactsList");
        intent.putStringArrayListExtra("contactList", (ArrayList<String>) contactList);
        //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        sendBroadcast(intent);
    }

}
