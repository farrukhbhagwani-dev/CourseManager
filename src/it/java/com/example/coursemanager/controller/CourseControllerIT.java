package com.example.coursemanager.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.example.coursemanager.model.Course;
import com.example.coursemanager.repository.CourseRepository;
import com.example.coursemanager.repository.mongo.CourseMongoRepository;
import com.example.coursemanager.view.CourseView;
import com.mongodb.MongoClient;

public class CourseControllerIT {

    @ClassRule
    public static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:4.4.3");

    @Mock
    private CourseView courseView;

    private CourseRepository courseRepository;

    private CourseController courseController;

    private AutoCloseable closeable;

    public static final String COURSE_COLLECTION_NAME = "course";
    public static final String COURSE_DB_NAME = "coursemanager";

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        courseRepository = new CourseMongoRepository(
                new MongoClient(mongo.getHost(), mongo.getFirstMappedPort()),
                COURSE_DB_NAME,
                COURSE_COLLECTION_NAME
        );

        for (Course c : courseRepository.findAll()) {
            courseRepository.delete(c.getCourseCode());
        }

        courseController = new CourseController(courseRepository, courseView);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testAllCoursesAreShownInTheView() {
        Course course = new Course("APT001", "Advanced Programming", "Farrukh", 6, "Course for IT test");
        courseRepository.save(course);

        courseController.getAllCourses();

        verify(courseView).displayCourses(asList(course));
    }

    @Test
    public void testNewCourseIsAddedToTheView() {
        Course course = new Course("APT002", "Software Engineering", "Iqbal", 6, "New course");

        courseController.addCourse(course);

        verify(courseView).addCourse(course);
    }

    @Test
    public void testSelectedCourseIsDeletedFromTheView() {
        Course courseToDelete = new Course("APT003", "Databases", "Sara", 6, "To be deleted");
        courseRepository.save(courseToDelete);

        courseController.deleteCourse(courseToDelete);

        verify(courseView).deleteCourse(courseToDelete);
    }

    @Test
    public void testSelectedCourseIsUpdatedInTheView() {
        Course original = new Course("APT004", "Networks", "Hassan", 6, "Before update");
        courseRepository.save(original);

        Course updated = new Course("APT004", "Networks Updated", "Hassan", 6, "After update");

        courseController.updateCourse(updated);

        verify(courseView).updateCourse(updated);
    }
}
