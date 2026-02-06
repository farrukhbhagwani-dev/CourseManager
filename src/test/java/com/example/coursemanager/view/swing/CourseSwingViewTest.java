package com.example.coursemanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import com.example.coursemanager.controller.CourseController;
import com.example.coursemanager.model.Course;

@RunWith(GUITestRunner.class)
public class CourseSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private CourseSwingView courseSwingView;

    @Mock
    private CourseController courseController;

    private AutoCloseable closeable;

    private static final String CODE = "APT001";
    private static final String TITLE = "Advanced Programming Techniques";
    private static final String INSTRUCTOR = "Farrukh";
    private static final String CREDIT_HOURS = "6";
    private static final String DESCRIPTION = "Test Driven Development";

    private static final Course COURSE =
        new Course(CODE, TITLE, INSTRUCTOR, 6, DESCRIPTION);

    @Override
    protected void onSetUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);

        GuiActionRunner.execute(() -> {
            courseSwingView = new CourseSwingView();
            courseSwingView.setCourseController(courseController);
            return courseSwingView;
        });

        window = new FrameFixture(robot(), courseSwingView);
        window.show();
    }

    @Override
    protected void onTearDown() throws Exception {
        closeable.close();
    }

    private void typeAllFieldsValidSoAddEnables() {
        window.textBox("coursecodeTextBox").enterText(CODE);
        window.textBox("coursetitleTextBox").enterText(TITLE);
        window.textBox("instructornameTextBox").enterText(INSTRUCTOR);
        window.textBox("credithoursTextBox").enterText(CREDIT_HOURS);
        window.textBox("descriptionTextBox").enterText(DESCRIPTION);
    }

    private void clickAddCourse() {
        window.button(JButtonMatcher.withText("Add Course")).click();
    }

    private void clickUpdateSelected() {
        window.button(JButtonMatcher.withText("Update Selected")).click();
    }

    private void clickDeleteSelected() {
        window.button(JButtonMatcher.withText("Delete Selected")).click();
    }

    private void clickCancel() {
        window.button(JButtonMatcher.withText("Cancel")).click();
    }

    private String makeStringOfLength(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append("a");
        return sb.toString();
    }
    
    private boolean invokePrivateBooleanMethod(String methodName, String value) throws Exception {
        java.lang.reflect.Method method =
            CourseSwingView.class.getDeclaredMethod(methodName, String.class);
        method.setAccessible(true);
        return ((Boolean) method.invoke(courseSwingView, value)).booleanValue();
    }

    @Test
    @GUITest
    public void testInitialStateButtonsDisabledFieldsEmptyLabelsCorrectAndCourseListEmpty() {
    	
    	window.label(org.assertj.swing.core.matcher.JLabelMatcher.withText("Course Code")).requireVisible();
        window.label(org.assertj.swing.core.matcher.JLabelMatcher.withText("Course Title")).requireVisible();
        window.label(org.assertj.swing.core.matcher.JLabelMatcher.withText("Instructor Name")).requireVisible();
        window.label(org.assertj.swing.core.matcher.JLabelMatcher.withText("Credit Hours")).requireVisible();
        window.label(org.assertj.swing.core.matcher.JLabelMatcher.withText("Description")).requireVisible();
    	
        window.textBox("coursecodeTextBox").requireEmpty();
        window.textBox("coursetitleTextBox").requireEmpty();
        window.textBox("instructornameTextBox").requireEmpty();
        window.textBox("credithoursTextBox").requireEmpty();
        window.textBox("descriptionTextBox").requireEmpty();
        
        window.button("btnAdd").requireDisabled();
        window.button("btnCancel").requireDisabled();
        window.button("btnUpdateSelected").requireDisabled();
        window.button("btnDeleteSelected").requireDisabled();
        
        assertThat(window.list("courseList").contents()).isEmpty();
        
        window.label("errorMessageLabel").requireText("");

        
    }

    @Test
    @GUITest
    public void testWhenAllFieldsAreFilledAddCourseButtonBecomesEnabled() {
        typeAllFieldsValidSoAddEnables();
        window.button("btnAdd").requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenAnyFieldIsEmptyAddCourseButtonStaysDisabled() {
        window.textBox("coursecodeTextBox").enterText(" ");
        window.textBox("coursetitleTextBox").enterText(TITLE);
        window.textBox("instructornameTextBox").enterText(INSTRUCTOR);
        window.textBox("credithoursTextBox").enterText(CREDIT_HOURS);

        window.button("btnAdd").requireDisabled();
    }

    @Test
    @GUITest
    public void testWhenCourseIsSelectedFieldsFilledCourseCodeDisabledUpdateDeleteCancelEnabledAndAddDisabled() {
        GuiActionRunner.execute(() -> courseSwingView.getListCourseModel().addElement(COURSE));

        window.list("courseList").selectItem(0);

        window.textBox("coursecodeTextBox").requireText(CODE);
        window.textBox("coursetitleTextBox").requireText(TITLE);
        window.textBox("instructornameTextBox").requireText(INSTRUCTOR);
        window.textBox("credithoursTextBox").requireText("6");
        window.textBox("descriptionTextBox").requireText(DESCRIPTION);

        window.textBox("coursecodeTextBox").requireDisabled();

        window.button("btnUpdateSelected").requireEnabled();
        window.button("btnDeleteSelected").requireEnabled();
        window.button("btnCancel").requireEnabled();
        window.button("btnAdd").requireDisabled();
    }

    @Test
    @GUITest
    public void testWhenCancelButtonIsClickedFieldsBecomeEmptyAndSelectionIsCleared() {
        GuiActionRunner.execute(() -> courseSwingView.getListCourseModel().addElement(COURSE));

        window.list("courseList").selectItem(0);
        clickCancel();

        window.textBox("coursecodeTextBox").requireText("");
        window.textBox("coursetitleTextBox").requireText("");
        window.textBox("instructornameTextBox").requireText("");
        window.textBox("credithoursTextBox").requireText("");
        window.textBox("descriptionTextBox").requireText("");

        assertThat(window.list("courseList").selection()).isEmpty();

        window.button("btnUpdateSelected").requireDisabled();
        window.button("btnDeleteSelected").requireDisabled();
        window.button("btnAdd").requireDisabled();
    }

    @Test
    @GUITest
    public void testWhenWindowStartsAndCoursesAreLoadedTheyAppearInTheList() {
        Course c2 = new Course("APT002", "Software Engineering", "Farrukh", 6, "Design Patterns");

        GuiActionRunner.execute(() ->
            courseSwingView.displayCourses(Arrays.asList(COURSE, c2))
        );

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("courseList").contents())
                .containsExactly(COURSE.toString(), c2.toString());
        });
    }

    @Test
    @GUITest
    public void testWhenAddCourseButtonIsClickedCourseIsAddedInTheCourseList() {
        typeAllFieldsValidSoAddEnables();
        clickAddCourse();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(courseController).addCourse(COURSE);
        });

        GuiActionRunner.execute(() -> courseSwingView.addCourse(COURSE));

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("courseList").contents()).containsExactly(COURSE.toString());
        });
    }

    @Test
    @GUITest
    public void testWhenDeleteSelectedButtonIsClickedCourseIsDeletedFromTheCourseList() {
        Course c2 = new Course("APT002", "Software Engineering", "Farrukh", 6, "Design Patterns");

        GuiActionRunner.execute(() -> {
            DefaultListModel<Course> model = courseSwingView.getListCourseModel();
            model.addElement(COURSE);
            model.addElement(c2);
        });

        window.list("courseList").selectItem(1);
        clickDeleteSelected();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(courseController).deleteCourse(c2);
        });

        GuiActionRunner.execute(() -> courseSwingView.deleteCourse(c2));

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("courseList").contents()).containsExactly(COURSE.toString());
        });
    }

    @Test
    @GUITest
    public void testWhenUpdateSelectedButtonIsClickedCourseIsUpdatedInTheCourseList() {
        GuiActionRunner.execute(() -> courseSwingView.getListCourseModel().addElement(COURSE));
        window.list("courseList").selectItem(0);

        window.textBox("coursetitleTextBox").setText("");
        window.textBox("coursetitleTextBox").enterText("Advanced Programming");
        window.textBox("credithoursTextBox").setText("");
        window.textBox("credithoursTextBox").enterText("7");

        Course updated =
            new Course(CODE, "Advanced Programming", INSTRUCTOR, 7, DESCRIPTION);

        clickUpdateSelected();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(courseController).updateCourse(updated);
        });

        GuiActionRunner.execute(() -> courseSwingView.updateCourse(updated));

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("courseList").contents()).containsExactly(updated.toString());
        });
    }

    @Test
    @GUITest
    public void testWhenCourseCodeIsWrongCourseCannotBeAddedToCourseListAndErrorMessageIsShown() {
        window.textBox("coursecodeTextBox").enterText("AP01!!");
        window.textBox("coursetitleTextBox").enterText(TITLE);
        window.textBox("instructornameTextBox").enterText(INSTRUCTOR);
        window.textBox("credithoursTextBox").enterText(CREDIT_HOURS);
        window.textBox("descriptionTextBox").enterText(DESCRIPTION);

        clickAddCourse();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            window.label("errorMessageLabel")
                .requireText("Course Code must be 3 letters followed by 3 digits (e.g., APT001)");
        });

        verify(courseController, never()).addCourse(COURSE);
    }

    @Test
    @GUITest
    public void testWhenCourseTitleHasWrongCharactersCourseCannotBeAddedToCourseListAndErrorMessageIsShown() {
        window.textBox("coursecodeTextBox").enterText(CODE);
        window.textBox("coursetitleTextBox").enterText("Advanced 123");
        window.textBox("instructornameTextBox").enterText(INSTRUCTOR);
        window.textBox("credithoursTextBox").enterText(CREDIT_HOURS);
        window.textBox("descriptionTextBox").enterText(DESCRIPTION);

        clickAddCourse();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            window.label("errorMessageLabel")
                .requireText("Course Title must contain only letters and spaces");
        });

        verify(courseController, never()).addCourse(COURSE);
    }

    @Test
    @GUITest
    public void testWhenInstructorNameHasWrongCharactersCourseCannotBeAddedToCourseListAndErrorMessageIsShown() {
        window.textBox("coursecodeTextBox").enterText(CODE);
        window.textBox("coursetitleTextBox").enterText(TITLE);
        window.textBox("instructornameTextBox").enterText("Farrukh99");
        window.textBox("credithoursTextBox").enterText(CREDIT_HOURS);
        window.textBox("descriptionTextBox").enterText(DESCRIPTION);

        clickAddCourse();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            window.label("errorMessageLabel")
                .requireText("Instructor Name must contain only letters and spaces");
        });

        verify(courseController, never()).addCourse(COURSE);
    }

    @Test
    @GUITest
    public void testWhenCreditHoursIsNotNumericCourseCannotBeAddedToCourseListAndErrorMessageIsShown() {
        window.textBox("coursecodeTextBox").enterText(CODE);
        window.textBox("coursetitleTextBox").enterText(TITLE);
        window.textBox("instructornameTextBox").enterText(INSTRUCTOR);
        window.textBox("credithoursTextBox").enterText("6a");
        window.textBox("descriptionTextBox").enterText(DESCRIPTION);

        clickAddCourse();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            window.label("errorMessageLabel")
                .requireText("Credit Hours must be a number");
        });

        verify(courseController, never()).addCourse(COURSE);
    }

    @Test
    @GUITest
    public void testWhenDescriptionIsOver200CharactersCourseCannotBeAddedToCourseListAndErrorMessageIsShown() {
        window.textBox("coursecodeTextBox").enterText(CODE);
        window.textBox("coursetitleTextBox").enterText(TITLE);
        window.textBox("instructornameTextBox").enterText(INSTRUCTOR);
        window.textBox("credithoursTextBox").enterText(CREDIT_HOURS);
        window.textBox("descriptionTextBox").enterText(makeStringOfLength(201));

        clickAddCourse();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            window.label("errorMessageLabel")
                .requireText("Description cannot exceed 200 characters");
        });

        verify(courseController, never()).addCourse(COURSE);
    }
    
    @Test
    @GUITest
    public void testUpdateSelectedDoesNothingWhenFormIsInvalid() {
        GuiActionRunner.execute(() -> courseSwingView.getListCourseModel().addElement(COURSE));
        window.list("courseList").selectItem(0);

        window.textBox("coursecodeTextBox").requireDisabled(); // selected mode
        window.textBox("coursetitleTextBox").setText("123");   // invalid title

        clickUpdateSelected();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(courseController, never()).updateCourse(org.mockito.ArgumentMatchers.any(Course.class));
        });
    }

    @Test
    @GUITest
    public void testDeleteSelectedDoesNothingWhenNoSelection() {
        clickDeleteSelected();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(courseController, never()).deleteCourse(org.mockito.ArgumentMatchers.any(Course.class));
        });
    }

    @Test
    @GUITest
    public void testShowErrorMessageWithCourseRemovesThatCourseFromList() {
        GuiActionRunner.execute(() -> courseSwingView.getListCourseModel().addElement(COURSE));

        GuiActionRunner.execute(() ->
            courseSwingView.showErrorMessage("Error: ", COURSE)
        );

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("courseList").contents()).isEmpty();
            window.label("errorMessageLabel").requireText("Error: " + COURSE.toString());
        });
    }
    
    @Test
    @GUITest
    public void testPrivateValidationMethodsWithNullInput() throws Exception {
        assertThat(invokePrivateBooleanMethod("isCourseCodeValid", null)).isFalse();
        assertThat(invokePrivateBooleanMethod("isLettersAndSpaces", null)).isFalse();
        assertThat(invokePrivateBooleanMethod("isNumeric", null)).isFalse();
    }
    
    @Test
    @GUITest
    public void testUpdateCourseWhenCourseCodeNotFoundDoesNotChangeList() {
        GuiActionRunner.execute(() -> courseSwingView.getListCourseModel().addElement(COURSE));

        Course notPresent =
            new Course("APT999", "Networks", "Farrukh", 6, "Some desc");

        GuiActionRunner.execute(() -> courseSwingView.updateCourse(notPresent));

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("courseList").contents())
                .containsExactly(COURSE.toString());
        });
    }

    @Test
    @GUITest
    public void testDeleteSelectedClickedWithNoSelectionDoesNotCallController() {
        GuiActionRunner.execute(() -> courseSwingView.getListCourseModel().addElement(COURSE));

        window.list("courseList").clearSelection();

        GuiActionRunner.execute(() -> window.button("btnDeleteSelected").target().setEnabled(true));

        clickDeleteSelected();

        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(courseController, never()).deleteCourse(any(Course.class));
        });
    }

    @Test
    @GUITest
    public void testWhenUpdatingAndCourseTitleIsOnlySpacesErrorMessageIsShown() {
        GuiActionRunner.execute(() -> courseSwingView.getListCourseModel().addElement(COURSE));
        window.list("courseList").selectItem(0);

        window.textBox("coursetitleTextBox").setText("   ");

        clickUpdateSelected();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            window.label("errorMessageLabel")
                .requireText("Course Title must contain only letters and spaces");
        });

        verify(courseController, never()).updateCourse(org.mockito.ArgumentMatchers.any(Course.class));
    }
    
}
