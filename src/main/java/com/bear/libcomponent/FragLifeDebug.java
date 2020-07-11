package com.bear.libcomponent;

import android.util.Log;

import androidx.lifecycle.GenericLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public class FragLifeDebug implements GenericLifecycleObserver {
    private String TAG = "FragLifeDebug";

    public FragLifeDebug(String tag) {
        TAG = TAG + "-" + tag;
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        Log.d(TAG, "event = " + event);
    }
}
