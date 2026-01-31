package com.example.coursemanager.repository;
import java.util.List;

import com.example.coursemanager.model.Course;

public interface CourseRepository {

    void save(Course course);

    List<Course> findAll();

    Course findByCode(String courseCode);

    void update(Course course);

    void delete(String courseCode);
}
