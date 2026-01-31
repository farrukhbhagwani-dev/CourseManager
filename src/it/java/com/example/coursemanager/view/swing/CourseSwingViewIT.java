package com.example.coursemanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.awaitility.Awaitility;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.coursemanager.controller.CourseController;
import com.example.coursemanager.model.Course;
import com.example.coursemanager.repository.mongo.CourseMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@RunWith(GUITestRunner.class)
public class CourseSwingViewIT extends AssertJSwingJUnitTestCase {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;

    private MongoClient mongoClient;

    private FrameFixture window;
    private CourseSwingView courseSwingView;
    private CourseController courseController;
    private CourseMongoRepository courseRepository;

    public static final String COURSE_COLLECTION_NAME = "course";
    public static final String COURSE_DB_NAME = "coursemanager";

    @BeforeClass
    public static void setupServer() {
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind();
    }

    @AfterClass
    public static void shutdownServer() {
        server.shutdown();
    }

    @Override
    protected void onSetUp() {
        mongoClient = new MongoClient(new ServerAddress(serverAddress));
        courseRepository = new CourseMongoRepository(mongoClient, COURSE_DB_NAME, COURSE_COLLECTION_NAME);

        for (Course c : courseRepository.findAll()) {
            courseRepository.delete(c.getCourseCode());
        }

        GuiActionRunner.execute(() -> {
            courseSwingView = new CourseSwingView();
            courseController = new CourseController(courseRepository, courseSwingView);
            courseSwingView.setCourseController(courseController);
            return courseSwingView;
        });

        window = new FrameFixture(robot(), courseSwingView);
        window.show();
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    @Test
    @GUITest
    public void testAllCourses() {
        Course course1 = new Course("APT001", "Advanced Programming", "Farrukh", 6, "Course one");
        Course course2 = new Course("APT002", "Software Engineering", "Iqbal", 6, "Course two");

        courseRepository.save(course1);
        courseRepository.save(course2);

        GuiActionRunner.execute(() -> courseController.getAllCourses());

        assertThat(window.list("courseList").contents())
                .containsExactly(course1.toString(), course2.toString());
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        window.textBox("coursecodeTextBox").enterText("APT003");
        window.textBox("coursetitleTextBox").enterText("Databases");
        window.textBox("instructornameTextBox").enterText("Sara");
        window.textBox("credithoursTextBox").enterText("6");
        window.textBox("descriptionTextBox").enterText("Database course");
        window.button(JButtonMatcher.withText("Add Course")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(window.list("courseList").contents())
                        .containsExactly(new Course("APT003", "Databases", "Sara", 6, "Database course").toString())
        );
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        GuiActionRunner.execute(() ->
                courseController.addCourse(new Course("APT004", "Networks", "Hassan", 6, "To be deleted"))
        );

        window.list("courseList").selectItem(0);
        window.button(JButtonMatcher.withText("Delete Selected")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(window.list("courseList").contents()).isEmpty()
        );
    }

    @Test
    @GUITest
    public void testUpdateButtonSuccess() {
        Course course = new Course("APT005", "Security", "Umar", 6, "Before update");
        courseRepository.save(course);

        GuiActionRunner.execute(() -> courseSwingView.getListCourseModel().addElement(course));

        window.list("courseList").selectItem(0);

        window.textBox("coursecodeTextBox").requireText("APT005");
        window.textBox("coursecodeTextBox").requireDisabled();

        window.textBox("coursetitleTextBox").setText("Security Updated");
        window.textBox("instructornameTextBox").setText("Umar");
        window.textBox("credithoursTextBox").setText("6");
        window.textBox("descriptionTextBox").setText("After update");

        window.button(JButtonMatcher.withText("Update Selected")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(window.list("courseList").contents())
                        .containsExactly(new Course("APT005", "Security Updated", "Umar", 6, "After update").toString())
        );
    }
}
