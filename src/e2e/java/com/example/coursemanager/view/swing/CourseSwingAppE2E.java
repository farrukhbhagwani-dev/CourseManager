package com.example.coursemanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class CourseSwingAppE2E extends AssertJSwingJUnitTestCase {

    @ClassRule
    public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

    private static final String DB_NAME = "test-db";
    private static final String COLLECTION_NAME = "test-collection";

    private static final String COURSE_FIXTURE_1_CODE = "APT001";
    private static final String COURSE_FIXTURE_1_TITLE = "Advanced Programming";
    private static final String COURSE_FIXTURE_1_INSTRUCTOR = "Farrukh";
    private static final int COURSE_FIXTURE_1_CREDITS = 6;
    private static final String COURSE_FIXTURE_1_DESCRIPTION = "Simple course";

    private static final String COURSE_FIXTURE_2_CODE = "APT002";
    private static final String COURSE_FIXTURE_2_TITLE = "Software Engineering";
    private static final String COURSE_FIXTURE_2_INSTRUCTOR = "Ali";
    private static final int COURSE_FIXTURE_2_CREDITS = 6;
    private static final String COURSE_FIXTURE_2_DESCRIPTION = "Second course";

    private MongoClient mongoClient;
    private FrameFixture window;

    @Override
    protected void onSetUp() throws Exception {
        String containerIpAddress = mongo.getContainerIpAddress();
        Integer mappedPort = mongo.getFirstMappedPort();

        mongoClient = new MongoClient(containerIpAddress, mappedPort);
        mongoClient.getDatabase(DB_NAME).drop();
        addTestCourseToDatabase(
                COURSE_FIXTURE_1_CODE,
                COURSE_FIXTURE_1_TITLE,
                COURSE_FIXTURE_1_INSTRUCTOR,
                COURSE_FIXTURE_1_CREDITS,
                COURSE_FIXTURE_1_DESCRIPTION
        );
        addTestCourseToDatabase(
                COURSE_FIXTURE_2_CODE,
                COURSE_FIXTURE_2_TITLE,
                COURSE_FIXTURE_2_INSTRUCTOR,
                COURSE_FIXTURE_2_CREDITS,
                COURSE_FIXTURE_2_DESCRIPTION
        );
        application("com.example.coursemanager.app.swing.CourseSwingApp")
                .withArgs(
                        "--mongo-host=" + containerIpAddress,
                        "--mongo-port=" + mappedPort.toString(),
                        "--db-name=" + DB_NAME,
                        "--db-collection=" + COLLECTION_NAME
                )
                .start();
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Course Manager".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(robot());
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    @Test
    @GUITest
    public void testOnStartAllDatabaseElementsAreShown() {
        assertThat(window.list("courseList").contents())
                .anySatisfy(e -> assertThat(e).contains(
                        COURSE_FIXTURE_1_CODE,
                        COURSE_FIXTURE_1_TITLE,
                        COURSE_FIXTURE_1_INSTRUCTOR,
                        String.valueOf(COURSE_FIXTURE_1_CREDITS),
                        COURSE_FIXTURE_1_DESCRIPTION
                ))
                .anySatisfy(e -> assertThat(e).contains(
                        COURSE_FIXTURE_2_CODE,
                        COURSE_FIXTURE_2_TITLE,
                        COURSE_FIXTURE_2_INSTRUCTOR,
                        String.valueOf(COURSE_FIXTURE_2_CREDITS),
                        COURSE_FIXTURE_2_DESCRIPTION
                ));
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        window.textBox("coursecodeTextBox").enterText("APT010");
        window.textBox("coursetitleTextBox").enterText("Databases");
        window.textBox("instructornameTextBox").enterText("Sara");
        window.textBox("credithoursTextBox").enterText("6");
        window.textBox("descriptionTextBox").enterText("Database course");
        window.button(JButtonMatcher.withText("Add Course")).click();

        assertThat(window.list("courseList").contents())
                .anySatisfy(e -> assertThat(e).contains("APT010", "Databases", "Sara", "6", "Database course"));
    }

    @Test
    @GUITest
    public void testAddButtonError() {
        window.textBox("coursecodeTextBox").enterText(COURSE_FIXTURE_1_CODE);
        window.textBox("coursetitleTextBox").enterText("New Title");
        window.textBox("instructornameTextBox").enterText("New Instructor");
        window.textBox("credithoursTextBox").enterText("6");
        window.textBox("descriptionTextBox").enterText("New description");
        window.button(JButtonMatcher.withText("Add Course")).click();

        assertThat(window.label("errorMessageLabel").text())
                .contains(
                        COURSE_FIXTURE_1_CODE,
                        COURSE_FIXTURE_1_TITLE,
                        COURSE_FIXTURE_1_INSTRUCTOR,
                        String.valueOf(COURSE_FIXTURE_1_CREDITS),
                        COURSE_FIXTURE_1_DESCRIPTION
                );
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        window.list("courseList")
                .selectItem(Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));

        window.button(JButtonMatcher.withText("Delete Selected")).click();

        assertThat(window.list("courseList").contents())
                .noneMatch(e -> e.contains(COURSE_FIXTURE_1_TITLE));
    }

    @Test
    @GUITest
    public void testDeleteButtonError() {
        window.list("courseList")
                .selectItem(Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));
        removeTestCourseFromDatabase(COURSE_FIXTURE_1_CODE);
        window.button(JButtonMatcher.withText("Delete Selected")).click();
        assertThat(window.label("errorMessageLabel").text())
                .contains(
                        COURSE_FIXTURE_1_CODE,
                        COURSE_FIXTURE_1_TITLE,
                        COURSE_FIXTURE_1_INSTRUCTOR,
                        String.valueOf(COURSE_FIXTURE_1_CREDITS),
                        COURSE_FIXTURE_1_DESCRIPTION
                );
    }

    @Test
    @GUITest
    public void testUpdateButtonSuccess() {
        window.list("courseList")
                .selectItem(Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));

        window.textBox("coursetitleTextBox").setText("Advanced Programming Updated");
        window.textBox("instructornameTextBox").setText("Farrukh");
        window.textBox("credithoursTextBox").setText("6");
        window.textBox("descriptionTextBox").setText("Updated description");

        window.button(JButtonMatcher.withText("Update Selected")).click();

        assertThat(window.list("courseList").contents())
                .anySatisfy(e -> assertThat(e).contains(
                        "Advanced Programming Updated",
                        "6",
                        "Updated description"
                ));
    }

    private void addTestCourseToDatabase(String code, String title, String instructor, int creditHours, String description) {
        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).insertOne(
                new Document()
                        .append("courseCode", code)
                        .append("courseTitle", title)
                        .append("instructorName", instructor)
                        .append("creditHours", creditHours)
                        .append("description", description)
        );
    }

    private void removeTestCourseFromDatabase(String code) {
        mongoClient
                .getDatabase(DB_NAME)
                .getCollection(COLLECTION_NAME)
                .deleteOne(Filters.eq("courseCode", code));
    }
}
