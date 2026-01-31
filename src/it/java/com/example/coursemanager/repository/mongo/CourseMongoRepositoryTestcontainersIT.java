package com.example.coursemanager.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.example.coursemanager.model.Course;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class CourseMongoRepositoryTestcontainersIT {

    @ClassRule
    public static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:4.4.3");

    private MongoClient client;
    private CourseMongoRepository courseRepository;
    private MongoCollection<Document> courseCollection;

    public static final String COURSE_COLLECTION_NAME = "course";
    public static final String COURSE_DB_NAME = "coursemanager";

    @Before
    public void setup() {
        client = new MongoClient(
                new ServerAddress(
                        mongo.getHost(),
                        mongo.getFirstMappedPort()
                )
        );

        courseRepository = new CourseMongoRepository(client, COURSE_DB_NAME, COURSE_COLLECTION_NAME);

        MongoDatabase database = client.getDatabase(COURSE_DB_NAME);

        database.drop();

        courseCollection = database.getCollection(COURSE_COLLECTION_NAME);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAll() {
        addTestCourseToDatabase("APT001", "Advanced Programming", "Farrukh", 6, "Course one");
        addTestCourseToDatabase("APT002", "Software Engineering", "Iqbal", 6, "Course two");

        assertThat(courseRepository.findAll())
                .containsExactly(
                        new Course("APT001", "Advanced Programming", "Farrukh", 6, "Course one"),
                        new Course("APT002", "Software Engineering", "Iqbal", 6, "Course two")
                );
    }

    @Test
    public void testFindByCode() {
        addTestCourseToDatabase("APT001", "Advanced Programming", "Farrukh", 6, "Course one");
        addTestCourseToDatabase("APT002", "Software Engineering", "Iqbal", 6, "Course two");

        assertThat(courseRepository.findByCode("APT002"))
                .isEqualTo(new Course("APT002", "Software Engineering", "Iqbal", 6, "Course two"));
    }

    @Test
    public void testSave() {
        Course course = new Course("APT003", "Databases", "Sara", 6, "Database course");

        courseRepository.save(course);

        assertThat(readAllCoursesFromDatabase())
                .containsExactly(course);
    }

    @Test
    public void testDelete() {
        addTestCourseToDatabase("APT004", "Networks", "Hassan", 6, "To be deleted");

        courseRepository.delete("APT004");

        assertThat(readAllCoursesFromDatabase())
                .isEmpty();
    }

    @Test
    public void testUpdate() {
        Course original = new Course("APT005", "Security", "Umar", 6, "Before update");
        courseRepository.save(original);

        assertThat(readAllCoursesFromDatabase()).containsExactly(original);

        Course updated = new Course("APT005", "Security Updated", "Umar", 6, "After update");
        courseRepository.update(updated);

        assertThat(readAllCoursesFromDatabase()).containsExactly(updated);
    }

    private void addTestCourseToDatabase(String code, String title, String instructor, int creditHours, String description) {
        courseCollection.insertOne(
                new Document()
                        .append("courseCode", code)
                        .append("courseTitle", title)
                        .append("instructorName", instructor)
                        .append("creditHours", creditHours)
                        .append("description", description)
        );
    }

    private List<Course> readAllCoursesFromDatabase() {
        return StreamSupport.stream(courseCollection.find().spliterator(), false)
                .map(d -> new Course(
                        "" + d.get("courseCode"),
                        "" + d.get("courseTitle"),
                        "" + d.get("instructorName"),
                        ((Number) d.get("creditHours")).intValue(),
                        "" + d.get("description")
                ))
                .collect(Collectors.toList());
    }
}
