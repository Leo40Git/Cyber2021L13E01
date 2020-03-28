package com.leo.androidutils;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * {@link ValueEventListener} that runs a method on the UI thread when an event is recorded.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public abstract class ValueEventListenerUI implements ValueEventListener {
    /**
     * Activity to invoke {@link Activity#runOnUiThread(Runnable)} from.
     */
    private final Activity activity;

    protected ValueEventListenerUI(Activity activity) {
        this.activity = activity;
    }

    /**
     * Called {@linkplain #onDataChange on data changing} from the <i>listener</i> thread.
     * @param dataSnapshot current data on location
     */
    protected void onDataChangeImpl(@NonNull DataSnapshot dataSnapshot) {}

    /**
     * Called {@linkplain #onDataChange on data changing} from the <i>main/UI</i> thread.
     * @param dataSnapshot current data on location
     */
    protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {}

    /**
     * Called {@linkplain #onCancelled on cancellation} from the <i>listener</i> thread.
     * @param databaseError error description
     */
    protected void onCancelledImpl(@NonNull DatabaseError databaseError) {}

    /**
     * Called {@linkplain #onCancelled on data changing} from the <i>main/UI</i> thread.
     * @param databaseError error description
     */
    protected void onCancelledUI(@NonNull DatabaseError databaseError) {}

    @Override
    public final void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
        onDataChangeImpl(dataSnapshot);
        activity.runOnUiThread(() -> {
            onDataChangeUI(dataSnapshot);
        });
    }

    @Override
    public final void onCancelled(@NonNull final DatabaseError databaseError) {
        onCancelledImpl(databaseError);
        activity.runOnUiThread(() -> {
            onCancelledUI(databaseError);
        });
    }
}
