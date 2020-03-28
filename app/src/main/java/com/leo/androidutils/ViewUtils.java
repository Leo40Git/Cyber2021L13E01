package com.leo.androidutils;

import android.view.View;
import android.view.ViewGroup;

/**
 * Utility methods for managing {@link View}s.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public final class ViewUtils {
    private ViewUtils() {
        throw new RuntimeException("nah");
    }

    /**
     * Recursively sets all views in a {@link ViewGroup}'s enabled state to the specified state.
     * @param vg {@link ViewGroup} to set enabled state in
     * @param state new enabled state
     */
    public static void setViewEnabledRecursive(final ViewGroup vg, final boolean state) {
        vg.setEnabled(state);
        final int c = vg.getChildCount();
        for (int i = 0; i < c; i++) {
            View v = vg.getChildAt(i);
            if (v instanceof ViewGroup)
                setViewEnabledRecursive((ViewGroup) v, state);
            else
                v.setEnabled(state);
        }
    }
}
