package com.bear.libcomponent.base;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * 不保证backPressedListener执行顺序，如果有添加多个BackPressedListener且要求执行顺序时候，建议还是在onBackPressed上处理。
 */
public class BackPressedHelper {
    public static void addBackPressedListener(FragmentActivity activity, BackPressedListener listener) {
        OnBackPressedDispatcher dispatcher = activity.getOnBackPressedDispatcher();
        dispatcher.addCallback(activity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!listener.onBackPressed()) {
                    setEnabled(false);
                    dispatcher.onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    public static void addBackPressedListener(Fragment fragment, BackPressedListener listener) {
        addBackPressedListener(fragment.requireActivity(), listener);
    }

    public interface BackPressedListener {
        boolean onBackPressed();
    }
}
