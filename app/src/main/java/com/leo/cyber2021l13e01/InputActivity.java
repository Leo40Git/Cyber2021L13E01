package com.leo.cyber2021l13e01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.leo.androidutils.KeyValuePair;
import com.leo.androidutils.ValueEventListenerUI;
import com.leo.cyber2021l13e01.db.FBRef;

import java.util.ArrayList;

/**
 * Displays a screen to add subjects, students and grades to the database.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public class InputActivity extends AppCompatActivity {

    private class SubjectsChangeListener extends ValueEventListenerUI {
        private SubjectsChangeListener() {
            super(InputActivity.this);
        }

        @Override
        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
            subjectsError = false;
        }

        @Override
        protected void onCancelledUI(@NonNull DatabaseError databaseError) {
            subjectsError = true;
        }
    }
    private SubjectsChangeListener subjectsCL;
    private boolean subjectsError;
    private ArrayList<String> subjects;

    private class StudentsChangeListener extends ValueEventListenerUI {
        private StudentsChangeListener() {
            super(InputActivity.this);
        }
    }
    private StudentsChangeListener studentsCL;
    private boolean studentsError;
    private ArrayList<KeyValuePair> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        subjectsCL = new SubjectsChangeListener();
        FBRef.rootSubjects.addValueEventListener(subjectsCL);
        studentsCL = new StudentsChangeListener();
        FBRef.rootStudents.addValueEventListener(studentsCL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FBRef.rootSubjects.removeEventListener(subjectsCL);
        subjectsCL = null;
        FBRef.rootStudents.removeEventListener(studentsCL);
        studentsCL = null;
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
            case R.id.menuView:
                gi = new Intent(this, ViewActivity.class);
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
