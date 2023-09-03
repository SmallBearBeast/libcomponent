package com.bear.libcomponent.component;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.bear.libcomponent.base.BaseAct;

public abstract class ComponentAct extends BaseAct {
    protected <C extends IComponent> void regActComponent(C component, Object tag) {
        ComponentService.get().regActComponent(this, component, tag);
    }

    protected <C extends IComponent> void regActComponent(C component) {
        ComponentService.get().regActComponent(this, component);
    }

    public <C extends IComponent> C getComponent(Class<C> clz, Object tag) {
        return ComponentService.get().getComponent(clz, tag);
    }

    public <C extends IComponent> C getComponent(Class<C> clz) {
        return ComponentService.get().getComponent(clz);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ComponentService.get().dispatchOnBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return ComponentService.get().dispatchOnCreateOptionsMenu(menu, getMenuInflater());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return ComponentService.get().dispatchOnOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ComponentService.get().dispatchOnCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return ComponentService.get().dispatchOnContextItemSelected(item);
    }
}
