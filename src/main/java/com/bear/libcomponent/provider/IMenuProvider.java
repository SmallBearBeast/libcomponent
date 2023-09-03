package com.bear.libcomponent.provider;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IMenuProvider {

    default boolean onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        return true;
    }

    default void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {

    }

    default boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
    }

    default boolean onContextItemSelected(@NonNull MenuItem item) {
        return true;
    }
}
