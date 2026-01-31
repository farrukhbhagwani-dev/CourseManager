package com.example.coursemanager.view;
import java.util.List;

import com.example.coursemanager.model.Course;

public interface CourseView {

    void displayCourses(List<Course> courses);

    void addCourse(Course course);

    void deleteCourse(Course course);

    void updateCourse(Course course);

    void showErrorMessage(String message, Course course);
}
