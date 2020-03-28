package com.leo.cyber2021l13e01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.leo.androidutils.KeyValuePair;
import com.leo.androidutils.OptionalInt;
import com.leo.androidutils.ValueEventListenerUI;
import com.leo.androidutils.ViewUtils;
import com.leo.cyber2021l13e01.db.FBRef;
import com.leo.cyber2021l13e01.db.Grade;
import com.leo.cyber2021l13e01.db.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.leo.androidutils.AlertDialogUtils.confirmActionDialog;
import static com.leo.androidutils.InputUtils.getEditTextContents;
import static com.leo.androidutils.InputUtils.parseEditText;

/**
 * Displays a screen to add subjects, students and grades to the database.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public class InputActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private Spinner spnRootSelect;
    private ArrayAdapter<String> spnRootSelectAdp;

    private ArrayAdapter<KeyValuePair> subjectsAdp;

    private LinearLayout laySubjectInput;
    private TextView tvSubjectStatus;
    private EditText etSubject;
    private Button btnSubjectInsert;
    private ListView lvSubjects;

    private LinearLayout layStudentInput;
    private TextView tvStudentStatus;
    private EditText etStudentName;
    private EditText etStudentAddress;
    private EditText etStudentPhoneHome;
    private EditText etStudentPhoneMobile;
    private EditText etStudentMomName;
    private EditText etStudentMomPhone;
    private EditText etStudentDadName;
    private EditText etStudentDadPhone;
    private Button btnStudentInsert;

    private LinearLayout layGradeInput;
    private TextView tvGradeStatus;
    private Spinner spnGradeStudent;
    private ArrayAdapter<KeyValuePair> spnGradeStudentAdp;
    private RadioGroup rgGradeQuarter;
    private Spinner spnGradeSubject;
    private EditText etGradeValue;
    private Button btnGradeInsert;

    private class SubjectsChangeListener extends ValueEventListenerUI {
        private SubjectsChangeListener() {
            super(InputActivity.this);
        }

        @Override
        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
            tvSubjectStatus.setText(String.format(Locale.getDefault(), "Successfully retrieved %d subjects.", dataSnapshot.getChildrenCount()));
            subjects.clear();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                subjects.put(child.getKey(), child.getValue(String.class));
            }
            renewSubjectsAdapter();
            lvSubjects.setEnabled(true);
            if (subjects.isEmpty()) {
                ViewUtils.setViewEnabledRecursive(layGradeInput, false);
                tvGradeStatus.setEnabled(true);
                tvGradeStatus.setText("No subjects in the database! Please add one.");
            } else if (!students.isEmpty()) {
                ViewUtils.setViewEnabledRecursive(layGradeInput, true);
                tvGradeStatus.setText("Ready for addition!");
            }
        }

        @Override
        protected void onCancelledUI(@NonNull DatabaseError databaseError) {
            String err = String.format("Could not retrieve subjects: %s", databaseError.getMessage());
            tvSubjectStatus.setText(err);
            subjects.clear();
            renewSubjectsAdapter();
            lvSubjects.setEnabled(false);
            ViewUtils.setViewEnabledRecursive(layGradeInput, false);
            tvGradeStatus.setEnabled(true);
            tvGradeStatus.setText(err);
        }

        private void renewSubjectsAdapter() {
            KeyValuePair[] pairs = new KeyValuePair[subjects.size()];
            int i = 0;
            for (HashMap.Entry<String, String> entry : subjects.entrySet())
                pairs[i++] = new KeyValuePair(entry.getKey(), entry.getValue());
            subjectsAdp = new ArrayAdapter<>(InputActivity.this, R.layout.support_simple_spinner_dropdown_item, pairs);
            lvSubjects.setAdapter(subjectsAdp);
            spnGradeSubject.setAdapter(subjectsAdp);
        }
    }
    private SubjectsChangeListener subjectsCL;
    private HashMap<String, String> subjects;

    private class StudentsChangeListener extends ValueEventListenerUI {
        private StudentsChangeListener() {
            super(InputActivity.this);
        }

        @Override
        protected void onDataChangeUI(@NonNull DataSnapshot dataSnapshot) {
            tvStudentStatus.setText(String.format(Locale.getDefault(), "Successfully retrieved %d students.", dataSnapshot.getChildrenCount()));
            students.clear();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                Student stu = child.getValue(Student.class);
                if (stu == null)
                    continue;
                students.add(new KeyValuePair(child.getKey(), stu.getName()));
            }
            renewStudentsAdapter();
            if (students.isEmpty()) {
                ViewUtils.setViewEnabledRecursive(layGradeInput, false);
                tvGradeStatus.setEnabled(true);
                tvGradeStatus.setText("No students in the database! Please add one.");
            } else if (!subjects.isEmpty()) {
                ViewUtils.setViewEnabledRecursive(layGradeInput, true);
                tvGradeStatus.setText("Ready for addition!");
            }
        }

        @Override
        protected void onCancelledUI(@NonNull DatabaseError databaseError) {
            String err = String.format("Could not retrieve students: %s", databaseError.getMessage());
            tvStudentStatus.setText(err);
            students.clear();
            students.add(new KeyValuePair(null, "[error]"));
            renewStudentsAdapter();
            spnGradeStudent.setEnabled(false);
            ViewUtils.setViewEnabledRecursive(layGradeInput, false);
            tvGradeStatus.setEnabled(true);
            tvGradeStatus.setText(err);
        }

        private void renewStudentsAdapter() {
            spnGradeStudentAdp = new ArrayAdapter<>(InputActivity.this, R.layout.support_simple_spinner_dropdown_item, students);
            spnGradeStudent.setAdapter(spnGradeStudentAdp);
        }
    }
    private StudentsChangeListener studentsCL;
    private ArrayList<KeyValuePair> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        spnRootSelect = findViewById(R.id.spnRootSelect);

        laySubjectInput = findViewById(R.id.laySubjectInput);
        tvSubjectStatus = findViewById(R.id.tvSubjectStatus);
        etSubject = findViewById(R.id.etSubject);
        btnSubjectInsert = findViewById(R.id.btnSubjectInsert);
        lvSubjects = findViewById(R.id.lvSubjects);

        layStudentInput = findViewById(R.id.layStudentInput);
        tvStudentStatus = findViewById(R.id.tvStudentStatus);
        etStudentName = findViewById(R.id.etStudentName);
        etStudentAddress = findViewById(R.id.etStudentAddress);
        etStudentPhoneHome = findViewById(R.id.etStudentPhoneHome);
        etStudentPhoneMobile = findViewById(R.id.etStudentPhoneMobile);
        etStudentMomName = findViewById(R.id.etStudentMomName);
        etStudentMomPhone = findViewById(R.id.etStudentMomPhone);
        etStudentDadName = findViewById(R.id.etStudentDadName);
        etStudentDadPhone = findViewById(R.id.etStudentDadPhone);
        btnStudentInsert = findViewById(R.id.btnStudentInsert);

        layGradeInput = findViewById(R.id.layGradeInput);
        tvGradeStatus = findViewById(R.id.tvGradeStatus);
        spnGradeStudent = findViewById(R.id.spnGradeStudent);
        rgGradeQuarter = findViewById(R.id.rgGradeQuarter);
        etGradeValue = findViewById(R.id.etGradeValue);
        spnGradeSubject = findViewById(R.id.spnGradeSubject);
        btnGradeInsert = findViewById(R.id.btnGradeInsert);

        spnRootSelect.setOnItemSelectedListener(this);
        spnRootSelectAdp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, FBRef.ROOT_NAMES_FULL);
        spnRootSelect.setAdapter(spnRootSelectAdp);

        lvSubjects.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvSubjects.setOnItemClickListener(this);

        btnSubjectInsert.setOnClickListener(this);
        btnStudentInsert.setOnClickListener(this);
        btnGradeInsert.setOnClickListener(this);

        subjects = new HashMap<>();
        students = new ArrayList<>();

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
     * Called when {@link #btnSubjectInsert}, {@link #btnStudentInsert} or {@link #btnGradeInsert} are clicked on.<br>
     * Adds a new grade, student or grade entry into the database, based on which button was clicked.
     * @param v {@link #btnSubjectInsert}, {@link #btnStudentInsert} or {@link #btnGradeInsert}
     */
    @Override
    public void onClick(final View v) {
        if (v == btnSubjectInsert) {
            String subject = getEditTextContents(this, etSubject);
            if (subject == null)
                return;
            FBRef.rootSubjects.push().setValue(subject);
            Toast.makeText(this, "Subject added successfully!", Toast.LENGTH_LONG).show();
        } else if (v == btnStudentInsert) {
            String name = getEditTextContents(this, etStudentName);
            if (name == null)
                return;
            String address = getEditTextContents(this, etStudentAddress);
            if (address == null)
                return;
            String phoneHome = getEditTextContents(this, etStudentPhoneHome);
            if (phoneHome == null)
                return;
            String phoneMobile = getEditTextContents(this, etStudentPhoneMobile);
            if (phoneMobile == null)
                return;
            String momName = getEditTextContents(this, etStudentMomName);
            if (momName == null)
                return;
            String momPhone = getEditTextContents(this, etStudentMomPhone);
            if (momPhone == null)
                return;
            String dadName = getEditTextContents(this, etStudentDadName);
            if (dadName == null)
                return;
            String dadPhone = getEditTextContents(this, etStudentDadPhone);
            if (dadPhone == null)
                return;
            Student stu = new Student(name, address, phoneHome, phoneMobile, momName, momPhone, dadName, dadPhone);
            FBRef.rootStudents.push().setValue(stu);
            Toast.makeText(this, "Student added successfully!", Toast.LENGTH_LONG).show();
        } else if (v == btnGradeInsert) {
            KeyValuePair selStu = spnGradeStudentAdp.getItem(spnGradeStudent.getSelectedItemPosition());
            if (selStu == null) {
                Toast.makeText(this, "Please select a student!", Toast.LENGTH_LONG).show();
                return;
            }
            String studentKey = selStu.getKey();
            int quarter;
            switch (rgGradeQuarter.getCheckedRadioButtonId()) {
                case R.id.rbGradeQuarter1:
                    quarter = 0;
                    break;
                case R.id.rbGradeQuarter2:
                    quarter = 1;
                    break;
                case R.id.rbGradeQuarter3:
                    quarter = 2;
                    break;
                case R.id.rbGradeQuarter4:
                    quarter = 3;
                    break;
                default:
                    Toast.makeText(this, "Please select a quarter!", Toast.LENGTH_LONG).show();
                    return;
            }
            KeyValuePair selSub = subjectsAdp.getItem(spnGradeSubject.getSelectedItemPosition());
            if (selSub == null) {
                Toast.makeText(this, "Please select a subject!", Toast.LENGTH_LONG).show();
                return;
            }
            String subjectKey = selSub.getKey();
            OptionalInt valueO = parseEditText(this, etGradeValue);
            if (!valueO.isPresent())
                return;
            int value = valueO.getAsInt();
            if (value < 0 || value > 100) {
                Toast.makeText(this, etGradeValue.getHint() + " must be between 0 and 100!", Toast.LENGTH_LONG).show();
            }
            Grade gra = new Grade(studentKey, quarter, subjectKey, value);
            FBRef.rootGrades.push().setValue(gra);
            Toast.makeText(this, "Grade added successfully!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called when an item in {@link #spnRootSelect} is selected.<br>
     * Changes the displayed input form to the one for the selected root.
     * @param parent {@link #spnRootSelect}
     * @param view selected item's {@link View}
     * @param position selected item's position
     * @param id selected item's ID
     */
    @Override
    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (parent == spnRootSelect) {
            final String newRoot = spnRootSelectAdp.getItem(position);
            if (FBRef.rootNameSubjects.equals(newRoot)) {
                layStudentInput.setVisibility(View.GONE);
                layGradeInput.setVisibility(View.GONE);
                laySubjectInput.setVisibility(View.VISIBLE);
            }
            else if (FBRef.rootNameStudents.equals(newRoot)) {
                laySubjectInput.setVisibility(View.GONE);
                layGradeInput.setVisibility(View.GONE);
                layStudentInput.setVisibility(View.VISIBLE);
            }
            else if (FBRef.rootNameGrades.equals(newRoot)) {
                laySubjectInput.setVisibility(View.GONE);
                layStudentInput.setVisibility(View.GONE);
                layGradeInput.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Called when an item in {@link #lvSubjects} is clicked on.<br>
     * Asks if to remove the selected subject.
     * @param parent {@link #spnRootSelect} or {@link #lvSubjects}
     * @param view selected item's {@link View}
     * @param position selected item's position
     * @param id selected item's ID
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == lvSubjects) {
            final KeyValuePair selSubject = subjectsAdp.getItem(position);
            if (selSubject == null)
                return;
            confirmActionDialog(this, String.format("Remove subject \"%s?\"", selSubject.getValue()),
                    () -> FBRef.rootSubjects.child(selSubject.getKey()).removeValue());
        }
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
