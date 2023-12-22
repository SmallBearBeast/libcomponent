package com.bear.libcomponent.component;

import android.os.Bundle;

import com.bear.libcomponent.provider.IBackPressedProvider;
import com.bear.libcomponent.provider.IMenuProvider;

public class FragmentComponent extends ContainerComponent implements IBackPressedProvider, IMenuProvider {

    private ComponentFrag componentFrag;

    void attachFragment(ComponentFrag fragment) {
        componentFrag = fragment;
    }

    public ComponentFrag getFragment() {
        return componentFrag;
    }

    protected void onCreateView() {

    }

    protected void onDestroyView() {

    }

    protected void onFirstVisible() {

    }

    public Bundle getArguments() {
        return componentFrag.getArguments();
    }
}
