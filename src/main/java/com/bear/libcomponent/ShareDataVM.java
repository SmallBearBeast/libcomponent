package com.bear.libcomponent;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class ShareDataVM extends ViewModel {
    private Map<String, Object> mShareDataMap;

    public void put(String key, Object value) {
        if (mShareDataMap == null) {
            mShareDataMap = new HashMap<>();
        }
        mShareDataMap.put(key, value);
    }

    public <V> V get(String key) {
        if (mShareDataMap == null) {
            return null;
        }
        Object obj = mShareDataMap.get(key);
        return (V) obj;
    }

    @Override
    protected void onCleared() {
        mShareDataMap.clear();
        mShareDataMap = null;
    }
}
