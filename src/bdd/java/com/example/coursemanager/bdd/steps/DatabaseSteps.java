package com.example.coursemanager.bdd.steps;

import java.util.List;

import org.bson.Document;

import com.example.coursemanager.bdd.CourseSwingAppBDD;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class DatabaseSteps {

    static final String DB_NAME = "test-db";
    static final String COLLECTION_NAME = "test-collection";

    static final String COURSE_FIXTURE_1_CODE = "APT001";
    static final String COURSE_FIXTURE_1_TITLE = "Advanced Programming";
    static final String COURSE_FIXTURE_1_INSTRUCTOR = "Farrukh";
    static final int COURSE_FIXTURE_1_CREDITS = 6;
    static final String COURSE_FIXTURE_1_DESCRIPTION = "Simple course";

    static final String COURSE_FIXTURE_2_CODE = "APT002";
    static final String COURSE_FIXTURE_2_TITLE = "Database Systems";
    static final String COURSE_FIXTURE_2_INSTRUCTOR = "Sara";
    static final int COURSE_FIXTURE_2_CREDITS = 6;
    static final String COURSE_FIXTURE_2_DESCRIPTION = "Database course";

    private MongoClient mongoClient;

    @Before
    public void setUp() {
        mongoClient = new MongoClient(
                CourseSwingAppBDD.mongoHost(),
                CourseSwingAppBDD.mongoPort()
        );
        mongoClient.getDatabase(DB_NAME).drop();
    }

    @After
    public void tearDown() {
        mongoClient.close();
    }

    @Given("The database contains the courses with the following values")
    public void the_database_contains_the_courses_with_the_following_values(List<List<String>> values) {
        values.stream().skip(1).forEach(v ->
            addTestCourseToDatabase(
                v.get(0),
                v.get(1),
                v.get(2),
                Integer.parseInt(v.get(3)),
                v.get(4)
            )
        );
    }

    @Given("The database contains a few courses")
    public void the_database_contains_a_few_courses() {
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
    }

    @Given("The course is in the meantime removed from the database")
    public void the_course_is_in_the_meantime_removed_from_the_database() {
        mongoClient.getDatabase(DB_NAME)
            .getCollection(COLLECTION_NAME)
            .deleteOne(Filters.eq("courseCode", COURSE_FIXTURE_1_CODE));
    }

    private void addTestCourseToDatabase(String courseCode,
                                         String courseTitle,
                                         String instructorName,
                                         int creditHours,
                                         String description) {

        mongoClient.getDatabase(DB_NAME)
            .getCollection(COLLECTION_NAME)
            .insertOne(new Document()
                .append("courseCode", courseCode)
                .append("courseTitle", courseTitle)
                .append("instructorName", instructorName)
                .append("creditHours", creditHours)
                .append("description", description)
            );
    }
}
