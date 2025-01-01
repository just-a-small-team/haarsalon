package com.jast.haarsalon;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

import static timber.log.Timber.DebugTree;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

@HiltAndroidApp
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        FirebaseApp.initializeApp(this);
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FirebaseCrashlytics.getInstance().log(message);

            if (t != null) {
                FirebaseCrashlytics.getInstance().recordException(t);
            }
        }
    }
}



