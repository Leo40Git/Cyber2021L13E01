package com.leo.cyber2021l13e01.db;

/**
 * Represents a grade in the database.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public class Grade {
    private String studentKey;
    private int quarter;
    private String subjectKey;
    private int value;

    public Grade() {}

    public Grade(String studentKey, int quarter, String subjectKey, int value) {
        this.studentKey = studentKey;
        this.quarter = quarter;
        this.subjectKey = subjectKey;
        this.value = value;
    }

    public String getStudentKey() {
        return studentKey;
    }

    public int getQuarter() {
        return quarter;
    }

    public String getSubjectKey() {
        return subjectKey;
    }

    public int getValue() {
        return value;
    }
}
