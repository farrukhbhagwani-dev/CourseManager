package com.example.coursemanager.model;
import java.util.Objects;

public class Course {

    private String courseCode;
    private String courseTitle;
    private String instructorName;
    private int creditHours;
    private String description;

    public Course() {
    }

    public Course(String courseCode, String courseTitle, String instructorName, int creditHours, String description) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.instructorName = instructorName;
        this.creditHours = creditHours;
        this.description = description;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return creditHours == course.creditHours
                && Objects.equals(courseCode, course.courseCode)
                && Objects.equals(courseTitle, course.courseTitle)
                && Objects.equals(instructorName, course.instructorName)
                && Objects.equals(description, course.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode, courseTitle, instructorName, creditHours, description);
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseTitle + " - " + instructorName + " - " + creditHours + " - " + description;
    }
}
