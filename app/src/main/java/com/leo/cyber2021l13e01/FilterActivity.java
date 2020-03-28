package com.leo.cyber2021l13e01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.leo.androidutils.KeyValuePair;
import com.leo.androidutils.ValueEventListenerUI;
import com.leo.cyber2021l13e01.db.FBRef;
import com.leo.cyber2021l13e01.db.Grade;
import com.leo.cyber2021l13e01.db.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Displays information from the database, sorted and filtered to the user's specifications.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private class SubjectsChangeListener extends ValueEventListenerUI {
        private SubjectsChangeListener() {
            super(FilterActivity.this);
        }

        @Override
        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
            subjects.clear();
            for (DataSnapshot child : dataSnapshot.getChildren())
                subjects.put(child.getKey(), child.getValue(String.class));
        }

        @Override
        protected void onCancelledUI(@NonNull DatabaseError databaseError) {
            subjects.clear();
        }
    }
    private SubjectsChangeListener subjectsCL;
    private HashMap<String, String> subjects;

    private class StudentsChangeListener extends ValueEventListenerUI {
        private StudentsChangeListener() {
            super(FilterActivity.this);
        }

        @Override
        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
            students.clear();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                Student stu = child.getValue(Student.class);
                if (stu == null)
                    continue;
                students.put(child.getKey(), stu.getName());
            }
        }

        @Override
        protected void onCancelledUI(@NonNull DatabaseError databaseError) {
            students.clear();
        }
    }
    private StudentsChangeListener studentsCL;
    private HashMap<String, String> students;

    /**
     * Creates a {@link Query} to display sorted and filtered content from the database.<br>
     * Created by Leo40Git on 28/03/2020.
     * @author Leo40Git
     */
    public static abstract class QueryFactory {
        /**
         * Returns the factory's name.
         * @return factory name
         */
        @NonNull
        @Override
        public abstract String toString();

        /**
         * Result of {@link #prepare(Activity, Consumer)}. Sent to the specified callback.
         */
        public static class PrepareResult {
            public final boolean ready;
            public final String err;
            public final ArrayList<KeyValuePair> params;

            public PrepareResult(boolean ready, String err, ArrayList<KeyValuePair> params) {
                this.ready = ready;
                this.err = err;
                this.params = params;
            }
        }

        /**
         * Prepares the query for creation, getting available parameters.<br>
         * Will call the specified callback <i>on the UI thread</i> when preparation is complete.
         * @param context activity to invoke {@link Activity#runOnUiThread(Runnable)} from
         * @param cb callback when preparation is done
         */
        public abstract void prepare(Activity context, Consumer<PrepareResult> cb);

        public interface OnQueryResult {
            void success(ArrayList<String> results);
            void failure(DatabaseError databaseError);
        }

        /**
         * Executes a query.
         * @param context activity to invoke {@link Activity#runOnUiThread(Runnable)} from
         * @param param query parameter
         * @param onResults callback for results. will be executed <i>on the UI thread</i>
         */
        public abstract void doQuery(Activity context, KeyValuePair param, OnQueryResult onResults);
    }

    private final QueryFactory[] filters = {
            new QueryFactory() {
                @NonNull
                @Override
                public String toString() {
                    return "All grades of student";
                }

                @Override
                public void prepare(Activity context, Consumer<PrepareResult> cb) {
                    FBRef.rootStudents.addListenerForSingleValueEvent(new ValueEventListenerUI(context) {
                        @Override
                        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<KeyValuePair> params = new ArrayList<>();
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Student stu = child.getValue(Student.class);
                                if (stu == null)
                                    continue;
                                params.add(new KeyValuePair(child.getKey(), stu.getName()));
                            }
                            if (params.isEmpty()) {
                                params.add(new KeyValuePair(null, "(no students in database, cannot execute filter)"));
                                cb.accept(new PrepareResult(false, "No students in database", params));
                            }
                            cb.accept(new PrepareResult(true, String.format(Locale.getDefault(), "Successfully retrieved %d students.", params.size()), params));
                        }

                        @Override
                        protected void onCancelledUI(@NonNull DatabaseError databaseError) {
                            cb.accept(new PrepareResult(false, String.format("Could not retrieve students: %s", databaseError.getMessage()), new ArrayList<>()));
                        }
                    });
                }

                @Override
                public void doQuery(Activity context, KeyValuePair param, OnQueryResult onResults) {
                    Query q = FBRef.rootGrades.orderByChild("studentKey").equalTo(param.getKey());
                    q.addListenerForSingleValueEvent(new ValueEventListenerUI(context) {
                        @Override
                        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> results = new ArrayList<>();
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Grade gra = child.getValue(Grade.class);
                                if (gra == null)
                                    continue;
                                results.add(String.format(Locale.getDefault(), "%s, %s Quarter, %d",
                                        (subjects.containsKey(gra.getSubjectKey()) ? subjects.get(gra.getSubjectKey()) : "(unknown subject)"),
                                        ViewActivity.ORDINAL_NUMBERS[gra.getQuarter()],
                                        gra.getValue()));
                            }
                            onResults.success(results);
                        }

                        @Override
                        protected void onCancelledUI(@NonNull DatabaseError databaseError) {
                            onResults.failure(databaseError);
                        }
                    });
                }
            },
            new QueryFactory() {
                @NonNull
                @Override
                public String toString() {
                    return "All grades in subject";
                }

                @Override
                public void prepare(Activity context, Consumer<PrepareResult> cb) {
                    ArrayList<KeyValuePair> params = new ArrayList<>();
                    for (HashMap.Entry<String, String> entry : subjects.entrySet())
                        params.add(new KeyValuePair(entry.getKey(), entry.getValue()));
                    context.runOnUiThread(() -> cb.accept(new PrepareResult(true, "Ready to roll!", params)));
                }

                @Override
                public void doQuery(Activity context, KeyValuePair param, OnQueryResult onResults) {
                    Query q = FBRef.rootGrades.orderByChild("subjectKey").equalTo(param.getKey());
                    q.addListenerForSingleValueEvent(new ValueEventListenerUI(context) {
                        @Override
                        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> results = new ArrayList<>();
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Grade gra = child.getValue(Grade.class);
                                if (gra == null)
                                    continue;
                                results.add(String.format(Locale.getDefault(), "%s - %s Quarter, %d",
                                        (students.containsKey(gra.getStudentKey()) ? students.get(gra.getStudentKey()) : "(unknown student)"),
                                        ViewActivity.ORDINAL_NUMBERS[gra.getQuarter()],
                                        gra.getValue()));
                            }
                            onResults.success(results);
                        }

                        @Override
                        protected void onCancelledUI(@NonNull DatabaseError databaseError) {
                            onResults.failure(databaseError);
                        }
                    });
                }
            }
    };

    private Spinner spnFilterSelect;
    private ArrayAdapter<QueryFactory> spnFilterSelectAdp;

    private Spinner spnFilterParam;
    private ArrayAdapter<KeyValuePair> spnFilterParamAdp;

    private Button btnFilterExec;

    private TextView tvStatus;

    private ListView lvDisplay;
    private ArrayAdapter<String> lvDisplayAdp;

    private TextView tvNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        spnFilterSelect = findViewById(R.id.spnFilterSelect);
        spnFilterParam = findViewById(R.id.spnFilterParam);
        btnFilterExec = findViewById(R.id.btnFilterExec);
        tvStatus = findViewById(R.id.tvStatus);
        lvDisplay = findViewById(R.id.lvDisplay);
        tvNoResults = findViewById(R.id.tvNoResults);

        spnFilterSelect.setOnItemSelectedListener(this);
        spnFilterSelectAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, filters);
        spnFilterSelect.setAdapter(spnFilterSelectAdp);

        btnFilterExec.setOnClickListener(this);

        lvDisplay.setChoiceMode(ListView.CHOICE_MODE_NONE);

        subjects = new HashMap<>();
        students = new HashMap<>();

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
     * Called when {@link #btnFilterExec} is clicked on.<br>
     * Executes the selected filter.
     * @param v {@link #btnFilterExec}
     */
    @Override
    public void onClick(View v) {
        KeyValuePair param = spnFilterParamAdp.getItem(spnFilterParam.getSelectedItemPosition());
        if (param == null)
            return;
        QueryFactory filter = spnFilterSelectAdp.getItem(spnFilterSelect.getSelectedItemPosition());
        if (filter == null)
            return;
        btnFilterExec.setEnabled(false);
        filter.doQuery(this, param, new QueryFactory.OnQueryResult() {
            private ArrayList<String> results;

            @Override
            public void success(ArrayList<String> results) {
                tvStatus.setText(String.format(Locale.getDefault(), "Query successful, %d results", results.size()));
                this.results = results;
                renewDisplayAdapter();
            }

            @Override
            public void failure(DatabaseError databaseError) {
                tvStatus.setText(String.format("Query failed: %s", databaseError.getMessage()));
                results = new ArrayList<>();
                renewDisplayAdapter();
            }

            private void renewDisplayAdapter() {
                lvDisplayAdp = new ArrayAdapter<>(FilterActivity.this, R.layout.support_simple_spinner_dropdown_item, results);
                lvDisplay.setAdapter(lvDisplayAdp);
                if (results.isEmpty()) {
                    lvDisplay.setVisibility(View.GONE);
                    tvNoResults.setVisibility(View.VISIBLE);
                } else {
                    tvNoResults.setVisibility(View.GONE);
                    lvDisplay.setVisibility(View.VISIBLE);
                }
                btnFilterExec.setEnabled(true);
            }
        });
    }

    /**
     * Called when an item in {@link #spnFilterSelect} is selected.<br>
     * Populates {@link #spnFilterParam} with {@linkplain QueryFactory#prepare(Activity, Consumer)}  the selected filter's parameter values.}
     * If the filter has no parameter, disables {@link #spnFilterParam}.
     * @param parent {@link #spnFilterSelect}
     * @param view selected item's {@link View}
     * @param position selected item's position
     * @param id selected item's ID
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spnFilterSelect) {
            QueryFactory filter = spnFilterSelectAdp.getItem(position);
            if (filter == null)
                return;
            filter.prepare(this, (result) -> {
                tvStatus.setText(result.err);
                spnFilterParamAdp = new ArrayAdapter<>(FilterActivity.this, R.layout.support_simple_spinner_dropdown_item, result.params);
                if (result.ready) {
                    btnFilterExec.setEnabled(true);
                    if (result.params.isEmpty()) {
                        spnFilterParamAdp.add(new KeyValuePair(null, "(no parameter)"));
                        spnFilterParam.setEnabled(false);
                    } else
                        spnFilterParam.setEnabled(true);
                } else {
                    spnFilterParam.setEnabled(false);
                    btnFilterExec.setEnabled(false);
                }
                spnFilterParam.setAdapter(spnFilterParamAdp);
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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
            case R.id.menuView:
                gi = new Intent(this, ViewActivity.class);
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
