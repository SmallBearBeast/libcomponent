package com.bear.libcomponent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public class ShareVM extends ViewModel {
    private Map<String, Object> shareDataMap;

    public void put(String key, Object value) {
        if (shareDataMap == null) {
            shareDataMap = new HashMap<>();
        }
        shareDataMap.put(key, value);
    }

    public <V> V get(String key) {
        if (shareDataMap == null) {
            return null;
        }
        Object obj = shareDataMap.get(key);
        return (V) obj;
    }

    @Override
    protected void onCleared() {
        shareDataMap.clear();
        shareDataMap = null;
    }

    /**
     * Put shared data for easy access by other components.
     *
     * @param key   The name of shared data.
     * @param value The value of shared data.
     */
    public static void put(FragmentActivity activity, @NonNull String key, Object value) {
        ViewModelProvider provider = new ViewModelProvider(activity, new ViewModelProvider.NewInstanceFactory());
        provider.get(ShareVM.class).put(key, value);
    }

    /**
     * Get the value corresponding to the key
     *
     * @param key The name of shared data.
     * @return The value of shared data.
     */
    public static <V> V get(FragmentActivity activity, @NonNull String key) {
        ViewModelProvider provider = new ViewModelProvider(activity, new ViewModelProvider.NewInstanceFactory());
        return provider.get(ShareVM.class).get(key);
    }

    /**
     * Put shared data for easy access by other components.
     *
     * @param key   The name of shared data.
     * @param value The value of shared data.
     */
    public static void put(Fragment fragment, @NonNull String key, Object value) {
        ViewModelProvider provider = new ViewModelProvider(fragment, new ViewModelProvider.NewInstanceFactory());
        provider.get(ShareVM.class).put(key, value);
    }

    /**
     * Get the value corresponding to the key
     *
     * @param key The name of shared data.
     * @return The value of shared data.
     */
    public static <V> V get(Fragment fragment, @NonNull String key) {
        ViewModelProvider provider = new ViewModelProvider(fragment, new ViewModelProvider.NewInstanceFactory());
        return provider.get(ShareVM.class).get(key);
    }
}
