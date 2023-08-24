package com.bear.libcomponent.component;

import android.content.Context;

import com.bear.libcomponent.component.ComponentAct;

public interface IContextProvider {
    void attachContext(Context c);

    Context getContext();

    ComponentAct getActivity();
}
