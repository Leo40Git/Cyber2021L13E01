package com.leo.androidutils;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * Utility methods for implementing user inputs with {@link EditText}.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public final class InputUtils {
    private InputUtils() {
        throw new RuntimeException("nah");
    }

    /**
     * Gets the contents of an {@link EditText}, clearing it in the process. If it is already empty, displays a {@link Toast} about how it should not be empty.
     * @param ctx {@link Context} of field
     * @param et {@link EditText} to act on
     * @return contents of the {@link EditText}, or <code>null</code> if it was empty.
     */
    public static String getEditTextContents(final Context ctx, final EditText et) {
        String c = et.getText().toString().trim();
        if (c.isEmpty()) {
            Toast.makeText(ctx, et.getHint() + " cannot be empty!", Toast.LENGTH_LONG).show();
            return null;
        }
        et.setText("");
        return c;
    }

    /**
     * Parses the contents of an {@link EditText} as an <code>int</code>, clearing it in the process. If it is already empty or doesn't contain a number, displays a {@link Toast} about the issue.
     * @param ctx {@link Context} of field
     * @param et {@link EditText} to act on
     * @return the parsed value of the field, or an empty {@link OptionalInt} if the field was empty or didn't contain a number.
     */
    @NonNull
    public static OptionalInt parseEditText(final Context ctx, final EditText et) {
        String c = et.getText().toString().trim();
        if (c.isEmpty()) {
            Toast.makeText(ctx, et.getHint() + " cannot be empty!", Toast.LENGTH_LONG).show();
            return OptionalInt.empty();
        }
        int x;
        try {
            x = Integer.parseInt(c);
        } catch (NumberFormatException e) {
            Toast.makeText(ctx, et.getHint() + " does not contain a number!", Toast.LENGTH_LONG).show();
            return OptionalInt.empty();
        }
        et.setText("");
        return OptionalInt.of(x);
    }
}
