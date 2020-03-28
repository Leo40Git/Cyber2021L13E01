package com.leo.cyber2021l13e01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.core.text.TextUtilsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.leo.androidutils.KeyValuePair;
import com.leo.androidutils.ValueEventListenerUI;
import com.leo.cyber2021l13e01.db.FBRef;
import com.leo.cyber2021l13e01.db.Grade;
import com.leo.cyber2021l13e01.db.Student;

import java.util.HashMap;
import java.util.Locale;

import static com.leo.androidutils.AlertDialogUtils.confirmActionDialog;

/**
 * Displays the information currently stored in the database.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public class ViewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    public static final String[] ORDINAL_NUMBERS = { "1st", "2nd", "3rd", "4th" };

    private Spinner spnRootSelect;
    private ArrayAdapter<String> spnRootSelectAdp;

    private TextView tvStatus;

    private ListView lvDisplay;
    private ArrayAdapter<KeyValuePair> lvDisplayAdp;

    private TextView tvEmpty;

    private class SubjectsChangeListener extends ValueEventListenerUI {
        private SubjectsChangeListener() {
            super(ViewActivity.this);
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
            super(ViewActivity.this);
        }

        @Override
        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
            studentsErr = null;
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
            studentsErr = String.format("Could not retrieve students: %s", databaseError.getMessage());
            students.clear();
        }
    }
    private StudentsChangeListener studentsCL;
    private HashMap<String, String> students;
    private String studentsErr;

    private class GradesChangeListener extends ValueEventListenerUI {
        private GradesChangeListener() {
            super(ViewActivity.this);
        }

        @Override
        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
            tvStatus.setText(String.format(Locale.getDefault(), "Successfully retrieved %d grades.", dataSnapshot.getChildrenCount()));
            grades.clear();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                Grade gra = child.getValue(Grade.class);
                if (gra == null)
                    continue;
                String graVal = String.format("%s - %s, %s Quarter",
                        (students.containsKey(gra.getStudentKey()) ? students.get(gra.getStudentKey()) : "(unknown student)"),
                        (subjects.containsKey(gra.getSubjectKey()) ? subjects.get(gra.getSubjectKey()) : "(unknown subject)"),
                        ORDINAL_NUMBERS[gra.getQuarter()]);
                grades.put(child.getKey(), graVal);
            }
            renewDisplayAdapter();
        }

        @Override
        protected void onCancelledUI(@NonNull DatabaseError databaseError) {
            tvStatus.setText(String.format("Could not retrieve grades: %s", databaseError.getMessage()));
            grades.clear();
            renewDisplayAdapter();
        }

        private void renewDisplayAdapter() {
            if (grades.isEmpty()) {
                lvDisplay.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                KeyValuePair[] pairs = new KeyValuePair[grades.size()];
                int i = 0;
                for (HashMap.Entry<String, String> entry : grades.entrySet())
                    pairs[i++] = new KeyValuePair(entry.getKey(), entry.getValue());
                lvDisplayAdp = new ArrayAdapter<>(ViewActivity.this, R.layout.support_simple_spinner_dropdown_item, pairs);
                lvDisplay.setAdapter(lvDisplayAdp);
                tvEmpty.setVisibility(View.GONE);
                lvDisplay.setVisibility(View.VISIBLE);
            }
        }
    }
    private GradesChangeListener gradesCL;
    private HashMap<String, String> grades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        spnRootSelect = findViewById(R.id.spnRootSelect);
        lvDisplay = findViewById(R.id.lvDisplay);
        tvStatus = findViewById(R.id.tvStatus);
        tvEmpty = findViewById(R.id.tvEmpty);

        lvDisplay.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvDisplay.setOnItemClickListener(this);

        subjects = new HashMap<>();
        students = new HashMap<>();
        grades = new HashMap<>();

        subjectsCL = new SubjectsChangeListener();
        FBRef.rootSubjects.addValueEventListener(subjectsCL);
        studentsCL = new StudentsChangeListener();
        FBRef.rootStudents.addValueEventListener(studentsCL);

        spnRootSelect.setOnItemSelectedListener(this);
        spnRootSelectAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, FBRef.ROOT_NAMES);
        spnRootSelect.setAdapter(spnRootSelectAdp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FBRef.rootSubjects.removeEventListener(subjectsCL);
        subjectsCL = null;
        FBRef.rootStudents.removeEventListener(studentsCL);
        studentsCL = null;
        if (gradesCL != null) {
            FBRef.rootGrades.removeEventListener(gradesCL);
            gradesCL = null;
        }
    }

    /**
     * Called when an item in {@link #spnRootSelect} is selected.<br>
     * Displays the information in the selected root.
     * @param parent {@link #spnRootSelect}
     * @param view selected item's {@link View}
     * @param position selected item's position
     * @param id selected item's ID
     */
    @Override
    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (parent == spnRootSelect) {
            String newRoot = spnRootSelectAdp.getItem(position);
            if (FBRef.rootNameStudents.equals(newRoot)) {
                if (gradesCL != null) {
                    FBRef.rootGrades.removeEventListener(gradesCL);
                    gradesCL = null;
                }
                tvStatus.setText(String.format(Locale.getDefault(), "Successfully retrieved %d students.", students.size()));
                if (students.isEmpty()) {
                    if (studentsErr != null)
                        tvStatus.setText(studentsErr);
                    lvDisplay.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    KeyValuePair[] pairs = new KeyValuePair[students.size()];
                    int i = 0;
                    for (HashMap.Entry<String, String> entry : students.entrySet())
                        pairs[i++] = new KeyValuePair(entry.getKey(), entry.getValue());
                    lvDisplayAdp = new ArrayAdapter<>(ViewActivity.this, R.layout.support_simple_spinner_dropdown_item, pairs);
                    lvDisplay.setAdapter(lvDisplayAdp);
                    tvEmpty.setVisibility(View.GONE);
                    lvDisplay.setVisibility(View.VISIBLE);
                }
            } else if (FBRef.rootNameGrades.equals(newRoot)) {
                tvStatus.setText("Loading grades...");
                gradesCL = new GradesChangeListener();
                FBRef.rootGrades.addValueEventListener(gradesCL);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Called when an item in {@link #lvDisplay} is clicked on.<br>
     * Displays information about the selected item.
     * @param parent {@link #spnRootSelect}
     * @param view selected item's {@link View}
     * @param position selected item's position
     * @param id selected item's ID
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == lvDisplay) {
            String root = spnRootSelectAdp.getItem(spnRootSelect.getSelectedItemPosition());
            KeyValuePair sel = lvDisplayAdp.getItem(position);
            if (root == null || sel == null)
                return;
            if (FBRef.rootNameStudents.equals(root))
                displayStudentInfo(sel);
            else if (FBRef.rootNameGrades.equals(root))
                displayGradeInfo(sel);
        }
    }

    /**
     * Shows an {@link AlertDialog} containing details about the specified student.
     * @param stu student to show info for
     */
    private void displayStudentInfo(final KeyValuePair stu) {
        FBRef.rootStudents.child(stu.getKey()).addListenerForSingleValueEvent(new ValueEventListenerUI(ViewActivity.this) {
            @Override
            protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
                Student stuObj = dataSnapshot.getValue(Student.class);
                if (stuObj == null)
                    return;

                String source = String.format(Locale.getDefault(),
                        "<b>Name:</b> %s<br>"
                                + "<b>Address:</b> %s<br>"
                                + "<b>Phone:</b> %s (home) / %s (mobile)<br>"
                                + "<b>Mother:</b> %s, %s<br>"
                                + "<b>Father:</b> %s, %s",
                        TextUtilsCompat.htmlEncode(stuObj.getName()),
                        TextUtilsCompat.htmlEncode(stuObj.getAddress()),
                        TextUtilsCompat.htmlEncode(stuObj.getPhoneHome()),
                        TextUtilsCompat.htmlEncode(stuObj.getPhoneMobile()),
                        TextUtilsCompat.htmlEncode(stuObj.getMomName()),
                        TextUtilsCompat.htmlEncode(stuObj.getMomPhone()),
                        TextUtilsCompat.htmlEncode(stuObj.getDadName()),
                        TextUtilsCompat.htmlEncode(stuObj.getDadPhone())
                );

                AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                adb.setTitle("Student details");
                adb.setMessage(HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_COMPACT));
                adb.setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                adb.setNegativeButton("Remove", (dialog, which) -> {
                    confirmActionDialog(ViewActivity.this, String.format("Remove student %s?", stu.getValue()),
                            () -> FBRef.rootStudents.child(stu.getKey()).removeValue());
                    dialog.dismiss();
                });
                adb.show();
            }

            @Override
            protected void onCancelledUI(@NonNull DatabaseError databaseError) {
                AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                adb.setTitle("Student details");
                adb.setMessage(String.format("Could not retrieve information for selected student %s:\n%s", stu.getValue(), databaseError.getMessage()));
                adb.setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                adb.show();
            }
        });
    }

    /**
     * Shows an {@link AlertDialog} containing details about the specified grade.
     * @param gra grade to show info for
     */
    private void displayGradeInfo(final KeyValuePair gra) {
        FBRef.rootGrades.child(gra.getKey()).addListenerForSingleValueEvent(new ValueEventListenerUI(ViewActivity.this) {
            @Override
            protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
                Grade graObj = dataSnapshot.getValue(Grade.class);
                if (graObj == null)
                    return;

                String studentName = "(unknown student)";
                if (students.containsKey(graObj.getStudentKey()))
                    studentName = students.get(graObj.getStudentKey());
                if (studentName == null)
                    studentName = "(unknown student)";

                String subjectName = "(unknown subject)";
                if (subjects.containsKey(graObj.getSubjectKey()))
                    subjectName = subjects.get(graObj.getSubjectKey());
                if (subjectName == null)
                    subjectName = "(unknown subject)";

                String source = String.format(Locale.getDefault(),
                        "<b>Student:</b> %s<br>"
                                + "<b>Quarter:</b> %s<br>"
                                + "<b>Subject:</b> %s<br>"
                                + "<b>Value:</b> %s",
                        TextUtilsCompat.htmlEncode(studentName),
                        ORDINAL_NUMBERS[graObj.getQuarter()],
                        TextUtilsCompat.htmlEncode(subjectName),
                        TextUtilsCompat.htmlEncode(Integer.toString(graObj.getValue()))
                );

                AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                adb.setTitle("Grade details");
                adb.setMessage(HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_COMPACT));
                adb.setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                adb.setNegativeButton("Remove", (dialog, which) -> {
                    confirmActionDialog(ViewActivity.this, "Remove this grade?",
                            () -> FBRef.rootGrades.child(gra.getKey()).removeValue());
                    dialog.dismiss();
                });
                adb.show();
            }

            @Override
            protected void onCancelledUI(@NonNull DatabaseError databaseError) {
                AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                adb.setTitle("Student details");
                adb.setMessage(String.format("Could not retrieve information for selected grade:\n%s", databaseError.getMessage()));
                adb.setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                adb.show();
            }
        });
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
            case R.id.menuFilter:
                gi = new Intent(this, FilterActivity.class);
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
