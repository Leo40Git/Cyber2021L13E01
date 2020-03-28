package com.leo.androidutils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

/**
 * Utility methods for {@link AlertDialog}s.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public final class AlertDialogUtils {
    private AlertDialogUtils() {
        throw new RuntimeException("nah");
    }

    /**
     * Displays a yes/no {@link AlertDialog}. If "yes" is selected, runs the specified {@link Runnable}.
     * @param ctx {@link Context} of dialog
     * @param prompt prompt to display in dialog
     * @param onYes {@link Runnable} to run if "yes" is selected
     */
    public static void confirmActionDialog(final Context ctx, final CharSequence prompt, final Runnable onYes) {
        AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
        adb.setTitle("Confirm action");
        adb.setMessage(prompt);
        adb.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
        adb.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            onYes.run();
            dialog.dismiss();
        });
        adb.show();
    }
}
