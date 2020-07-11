package com.bear.libcomponent;

import android.util.Log;

import androidx.lifecycle.GenericLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public final class ActLifeDebug implements GenericLifecycleObserver {
    private String TAG = "ActLifeDebug";

    public ActLifeDebug(String tag) {
        TAG = TAG + "-" + tag;
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        Log.d(TAG, "event = " + event);
    }
}
