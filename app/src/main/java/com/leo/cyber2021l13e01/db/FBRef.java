package com.leo.cyber2021l13e01.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FBRef {
    private FBRef() {
        throw new RuntimeException("nah");
    }

    public static final FirebaseDatabase FBDB = FirebaseDatabase.getInstance();

    public static final String rootNameSubjects = "Subjects";
    public static final String rootNameStudents = "Students";
    public static final String rootNameGrades = "Grades";
    public static final String[] ROOT_NAMES = { rootNameStudents, rootNameGrades };

    public static final DatabaseReference rootSubjects = FBDB.getReference(rootNameSubjects);
    public static final DatabaseReference rootStudents = FBDB.getReference(rootNameStudents);
    public static final DatabaseReference rootGrades = FBDB.getReference(rootNameGrades);
}
