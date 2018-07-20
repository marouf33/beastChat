package com.maroufb.beastchat.application;

import android.app.Application;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.FirebaseDatabase;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        AppEventsLogger.activateApp(this);

    }


}
