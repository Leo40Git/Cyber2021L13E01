package com.leo.cyber2021l13e01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Displays the information currently stored in the database.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
    }

    /**
     * Creates the activity's options menu.<br>
     * Inflates the "main" menu.
     * @param menu the activity's menu
     * @return <code>true</code>
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Called when an option in the activity's options menu is selected.<br>
     * {@linkplain #startActivity(Intent) Starts the selected activity.}
     * @param item selected menu's option.
     * @return <code>true</code>
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent gi = null;
        switch (item.getItemId()) {
            case R.id.menuInput:
                gi = new Intent(this, InputActivity.class);
                break;
            case R.id.menuSortFilter:
                gi = new Intent(this, SortFilterActivity.class);
                break;
            case R.id.menuCredits:
                gi = new Intent(this, CreditsActivity.class);
                break;
            default:
                break;
        }
        if (gi != null)
            startActivity(gi);
        return true;
    }

}
