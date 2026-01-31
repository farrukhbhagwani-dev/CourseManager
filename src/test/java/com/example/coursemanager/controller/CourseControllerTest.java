package com.example.coursemanager.controller;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.coursemanager.model.Course;
import com.example.coursemanager.repository.CourseRepository;
import com.example.coursemanager.view.CourseView;

public class CourseControllerTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseView courseView;

    @InjectMocks
    private CourseController courseController;

    private AutoCloseable closeable;

    @Before
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testDisplayAllCourses() {
        List<Course> courses = asList(new Course());
        when(courseRepository.findAll()).thenReturn(courses);
        courseController.getAllCourses();
        verify(courseView).displayCourses(courses);
    }

    @Test
    public void testDisplayAllCoursesWithEmptyList() {
        when(courseRepository.findAll()).thenReturn(null);
        courseController.getAllCourses();
        verify(courseView).displayCourses(null);
    }

    @Test
    public void testAddNewCourseWhenCourseAlreadyExists() {
        Course toAdd = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        Course existing = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        when(courseRepository.findByCode("APT001")).thenReturn(existing);
        courseController.addCourse(toAdd);
        verify(courseView).showErrorMessage("Course already exists with code APT001", existing);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }
    
    @Test
    public void testAddNewCourseWhenCourseDoesNotExist() {
        Course course = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        when(courseRepository.findByCode("APT001")).thenReturn(null);
        courseController.addCourse(course);
        InOrder inOrder = inOrder(courseRepository, courseView);
        inOrder.verify(courseRepository).save(course);
        inOrder.verify(courseView).addCourse(course);
    }

    @Test
    public void testUpdatingExistingCourse() {
        Course course = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        when(courseRepository.findByCode("APT001")).thenReturn(course);
        courseController.updateCourse(course);
        InOrder inOrder = inOrder(courseRepository, courseView);
        inOrder.verify(courseRepository).update(course);
        inOrder.verify(courseView).updateCourse(course);
    }

    @Test
    public void testUpdatingMissingCourse() {
        Course course = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        when(courseRepository.findByCode("APT001")).thenReturn(null);
        courseController.updateCourse(course);
        verify(courseView).showErrorMessage("No course found with code APT001", course);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testDeletingExistingCourse() {
        Course course = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        when(courseRepository.findByCode("APT001")).thenReturn(course);
        courseController.deleteCourse(course);
        InOrder inOrder = inOrder(courseRepository, courseView);
        inOrder.verify(courseRepository).delete("APT001");
        inOrder.verify(courseView).deleteCourse(course);
    }

    @Test
    public void testDeletingMissingCourse() {
        Course course = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        when(courseRepository.findByCode("APT001")).thenReturn(null);
        courseController.deleteCourse(course);
        verify(courseView).showErrorMessage("No course found with code APT001", course);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }
}
