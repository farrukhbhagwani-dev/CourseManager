package com.example.coursemanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.awaitility.Awaitility;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.example.coursemanager.controller.CourseController;
import com.example.coursemanager.model.Course;
import com.example.coursemanager.repository.mongo.CourseMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {

    @ClassRule
    public static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:4.4.3");

    private MongoClient mongoClient;

    private FrameFixture window;
    private CourseMongoRepository courseRepository;
    private CourseController courseController;

    public static final String COURSE_COLLECTION_NAME = "course";
    public static final String COURSE_DB_NAME = "coursemanager";

    @Override
    protected void onSetUp() {
        mongoClient = new MongoClient(
                new ServerAddress(
                        mongo.getHost(),
                        mongo.getFirstMappedPort()
                )
        );

        courseRepository = new CourseMongoRepository(mongoClient, COURSE_DB_NAME, COURSE_COLLECTION_NAME);

        for (Course c : courseRepository.findAll()) {
            courseRepository.delete(c.getCourseCode());
        }

        window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
            CourseSwingView courseSwingView = new CourseSwingView();
            courseController = new CourseController(courseRepository, courseSwingView);
            courseSwingView.setCourseController(courseController);
            return courseSwingView;
        }));

        window.show();
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    @Test
    public void testAddCourse() {
    	
        window.textBox("coursecodeTextBox").enterText("APT001");
        window.textBox("coursetitleTextBox").enterText("Advanced Programming");
        window.textBox("instructornameTextBox").enterText("Farrukh");
        window.textBox("credithoursTextBox").enterText("6");
        window.textBox("descriptionTextBox").enterText("Course added through UI");
        window.button(JButtonMatcher.withText("Add Course")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(courseRepository.findByCode("APT001"))
                        .isEqualTo(new Course("APT001", "Advanced Programming", "Farrukh", 6, "Course added through UI"))
        );
    }

    @Test
    public void testDeleteCourse() {
    	
        courseRepository.save(new Course("APT002", "Software Engineering", "Iqbal", 6, "To be deleted"));

        GuiActionRunner.execute(() -> courseController.getAllCourses());

        window.list("courseList").selectItem(0);
        window.button(JButtonMatcher.withText("Delete Selected")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(courseRepository.findByCode("APT002")).isNull()
        );
    }

    @Test
    public void testUpdateCourse() {
        courseRepository.save(new Course("APT003", "Databases", "Sara", 6, "Before update"));

        GuiActionRunner.execute(() -> courseController.getAllCourses());

        window.list("courseList").selectItem(0);

        window.textBox("coursecodeTextBox").requireText("APT003");
        window.textBox("coursecodeTextBox").requireDisabled();

        window.textBox("coursetitleTextBox").setText("Databases Updated");
        window.textBox("instructornameTextBox").setText("Sara");
        window.textBox("credithoursTextBox").setText("6");
        window.textBox("descriptionTextBox").setText("After update");

        window.button(JButtonMatcher.withText("Update Selected")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(courseRepository.findByCode("APT003"))
                        .isEqualTo(new Course("APT003", "Databases Updated", "Sara", 6, "After update"))
        );

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(window.list("courseList").contents())
                        .containsExactly(new Course("APT003", "Databases Updated", "Sara", 6, "After update").toString())
        );
    }
}
