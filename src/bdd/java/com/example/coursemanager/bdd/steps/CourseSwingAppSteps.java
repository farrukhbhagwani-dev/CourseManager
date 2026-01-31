package com.example.coursemanager.bdd.steps;

import static com.example.coursemanager.bdd.steps.DatabaseSteps.COLLECTION_NAME;
import static com.example.coursemanager.bdd.steps.DatabaseSteps.DB_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import com.example.coursemanager.bdd.CourseSwingAppBDD;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CourseSwingAppSteps {

    private FrameFixture window;

    @After
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @When("The Course View is shown")
    public void the_Course_View_is_shown() {
        application("com.example.coursemanager.app.swing.CourseSwingApp")
            .withArgs(
                "--mongo-host=" + CourseSwingAppBDD.mongoHost(),
                "--mongo-port=" + CourseSwingAppBDD.mongoPort(),
                "--db-name=" + DB_NAME,
                "--db-collection=" + COLLECTION_NAME
            )
            .start();

        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Course Manager".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(BasicRobot.robotWithCurrentAwtHierarchy());
    }

    @Then("The list contains elements with the following values")
    public void the_list_contains_elements_with_the_following_values(List<List<String>> values) {
        values.forEach(v ->
            assertThat(window.list("courseList").contents())
                .anySatisfy(e -> assertThat(e).contains(v.get(0), v.get(1), v.get(2), v.get(3), v.get(4)))
        );
    }

    @When("The user enters the following values in the text fields")
    public void the_user_enters_the_following_values_in_the_text_fields(List<Map<String, String>> values) {
        values.stream()
            .flatMap(m -> m.entrySet().stream())
            .forEach(e -> window
                .textBox(e.getKey() + "TextBox")
                .enterText(e.getValue())
            );
    }

    @When("The user clicks the {string} button")
    public void the_user_clicks_the_button(String buttonText) {
        window.button(JButtonMatcher.withText(buttonText)).click();
    }

    @Then("An error is shown containing the following values")
    public void an_error_is_shown_containing_the_following_values(List<List<String>> values) {
        assertThat(window.label("errorMessageLabel").text())
            .contains(values.get(0));
    }

    @Given("The user provides course data in the text fields")
    public void the_user_provides_course_data_in_the_text_fields() {
        window.textBox("coursecodeTextBox").enterText("APT010");
        window.textBox("coursetitleTextBox").enterText("Databases");
        window.textBox("instructornameTextBox").enterText("Sara");
        window.textBox("credithoursTextBox").enterText("6");
        window.textBox("descriptionTextBox").enterText("Database course");
    }

    @Then("The list contains the new course")
    public void the_list_contains_the_new_course() {
        assertThat(window.list("courseList").contents())
            .anySatisfy(e -> assertThat(e).contains("APT010", "Databases", "Sara", "6", "Database course"));
    }

    @Given("The user provides course data in the text fields, specifying an existing code")
    public void the_user_provides_course_data_in_the_text_fields_specifying_an_existing_code() {
        window.textBox("coursecodeTextBox").enterText(DatabaseSteps.COURSE_FIXTURE_1_CODE);
        window.textBox("coursetitleTextBox").enterText("New Title");
        window.textBox("instructornameTextBox").enterText("New Instructor");
        window.textBox("credithoursTextBox").enterText("6");
        window.textBox("descriptionTextBox").enterText("New description");
    }

    @Then("An error is shown containing the title of the existing course")
    public void an_error_is_shown_containing_the_title_of_the_existing_course() {
        assertThat(window.label("errorMessageLabel").text())
            .contains(DatabaseSteps.COURSE_FIXTURE_1_TITLE);
    }

    @Given("The user selects a course from the list")
    public void the_user_selects_a_course_from_the_list() {
        window.list("courseList")
            .selectItem(Pattern.compile(".*" + DatabaseSteps.COURSE_FIXTURE_1_TITLE + ".*"));
    }

    @Then("The course is removed from the list")
    public void the_course_is_removed_from_the_list() {
        assertThat(window.list("courseList").contents())
            .noneMatch(e -> e.contains(DatabaseSteps.COURSE_FIXTURE_1_TITLE));
    }

    @Then("An error is shown containing the title of the selected course")
    public void an_error_is_shown_containing_the_title_of_the_selected_course() {
        assertThat(window.label("errorMessageLabel").text())
            .contains(DatabaseSteps.COURSE_FIXTURE_1_TITLE);
    }

    @Given("The user selects the course with code {string} from the list")
    public void the_user_selects_the_course_with_code_from_the_list(String courseCode) {
        window.list("courseList")
            .selectItem(Pattern.compile(".*" + courseCode + ".*"));
    }

    @When("The user updates the course details with the following values")
    public void the_user_updates_the_course_details_with_the_following_values(List<Map<String, String>> values) {
        Map<String, String> courseDetails = values.get(0);

        window.textBox("coursetitleTextBox").setText(courseDetails.get("courseTitle"));
        window.textBox("instructornameTextBox").setText(courseDetails.get("instructorName"));
        window.textBox("credithoursTextBox").setText(courseDetails.get("creditHours"));
        window.textBox("descriptionTextBox").setText(courseDetails.get("description"));

        window.button(JButtonMatcher.withText("Update Selected")).click();
    }

    @Then("The list reflects the updated details for the course with code {string}")
    public void the_list_reflects_the_updated_details_for_the_course_with_code(String courseCode) {
        assertThat(window.list("courseList").contents())
            .anySatisfy(e -> assertThat(e).contains(
                courseCode,
                "Advanced Programming Updated",
                "Farrukh",
                "6",
                "Updated description"
            ));
    }
}
