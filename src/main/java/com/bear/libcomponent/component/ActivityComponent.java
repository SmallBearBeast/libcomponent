package com.bear.libcomponent.component;

import com.bear.libcomponent.provider.IBackPressedProvider;
import com.bear.libcomponent.provider.IMenuProvider;

public class ActivityComponent extends ContainerComponent implements IBackPressedProvider, IMenuProvider {

    private ComponentAct componentAct;

    public void attachActivity(ComponentAct activity) {
        componentAct = activity;
    }

    @Override
    public ComponentAct getActivity() {
        return componentAct;
    }
}
