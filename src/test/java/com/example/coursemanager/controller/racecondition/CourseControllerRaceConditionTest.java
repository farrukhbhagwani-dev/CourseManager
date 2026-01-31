package com.example.coursemanager.controller.racecondition;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.coursemanager.controller.CourseController;
import com.example.coursemanager.model.Course;
import com.example.coursemanager.repository.CourseRepository;
import com.example.coursemanager.view.CourseView;

public class CourseControllerRaceConditionTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseView courseView;

    @InjectMocks
    private CourseController courseController;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testAddingSameCourseAtTheSameTimeAddsOnlyOneCourse() {
        List<Course> courses = new ArrayList<>();
        Course course = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        when(courseRepository.findByCode(anyString())).thenAnswer(invocation -> courses.stream().findFirst().orElse(null));
        doAnswer(invocation -> {
            courses.add(course);
            return null;
        }).when(courseRepository).save(any(Course.class));
        List<Thread> threads = IntStream.range(0, 10)
            .mapToObj(i -> new Thread(() -> courseController.addCourse(course)))
            .peek(Thread::start)
            .collect(Collectors.toList());
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
            .until(() -> threads.stream().noneMatch(Thread::isAlive));
        assertThat(courses).containsExactly(course);
    }

    @Test
    public void testDeletingSameCourseAtTheSameTimeRemovesCourseOnce() {
        List<Course> courses = new ArrayList<>();
        Course course = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        courses.add(course);
        when(courseRepository.findByCode(anyString())).thenAnswer(invocation -> courses.stream().findFirst().orElse(null));
        doAnswer(invocation -> {
            courses.remove(course);
            return null;
        }).when(courseRepository).delete(anyString());
        List<Thread> threads = IntStream.range(0, 10)
            .mapToObj(i -> new Thread(() -> courseController.deleteCourse(course)))
            .peek(Thread::start)
            .collect(Collectors.toList());
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
            .until(() -> threads.stream().noneMatch(Thread::isAlive));
        assertThat(courses).isEmpty();
    }

    @Test
    public void testUpdatingSameCourseAtSameTimeEndsWithOneCorrectUpdate() {
        List<Course> courses = new ArrayList<>();
        Course original = new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Old Description");
        courses.add(original);
        Course updated = new Course("APT001","Advanced Programming Techniques","Farrukh",7,"Updated Description");
        when(courseRepository.findByCode(anyString())).thenAnswer(invocation -> courses.stream().findFirst().orElse(null));
        doAnswer(invocation -> {
        	Course courseFromController = invocation.getArgument(0);
            courses.clear();
            courses.add(courseFromController);
            return null;
        }).when(courseRepository).update(any(Course.class));
        List<Thread> threads = IntStream.range(0, 10)
            .mapToObj(i -> new Thread(() -> courseController.updateCourse(updated)))
            .peek(Thread::start)
            .collect(Collectors.toList());
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
            .until(() -> threads.stream().noneMatch(Thread::isAlive));
        assertThat(courses).containsExactly(updated);
    }
}
