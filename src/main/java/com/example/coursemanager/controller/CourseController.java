package com.example.coursemanager.controller;
import com.example.coursemanager.model.Course;
import com.example.coursemanager.repository.CourseRepository;
import com.example.coursemanager.view.CourseView;

public class CourseController {

    private final CourseRepository repository;
    private final CourseView view;

    public CourseController(CourseRepository repository, CourseView view) {
        this.repository = repository;
        this.view = view;
    }

    public synchronized void addCourse(Course course) {
        Course existing = repository.findByCode(course.getCourseCode());
        if (existing != null) {
            view.showErrorMessage("Course already exists with code " + course.getCourseCode(), existing);
            return;
        }
        repository.save(course);
        view.addCourse(course);
    }

    public void getAllCourses() {
        view.displayCourses(repository.findAll());
    }

    public synchronized void updateCourse(Course course) {
        Course existing = repository.findByCode(course.getCourseCode());
        if (existing == null) {
            view.showErrorMessage("No course found with code " + course.getCourseCode(), course);
            return;
        }
        repository.update(course);
        view.updateCourse(course);
    }

    public synchronized void deleteCourse(Course course) {
        Course existing = repository.findByCode(course.getCourseCode());
        if (existing == null) {
            view.showErrorMessage("No course found with code " + course.getCourseCode(), course);
       
            return;
        }
        repository.delete(course.getCourseCode());
        view.deleteCourse(course);
    }
}
