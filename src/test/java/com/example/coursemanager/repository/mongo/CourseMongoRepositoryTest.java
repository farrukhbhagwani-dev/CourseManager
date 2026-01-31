package com.example.coursemanager.repository.mongo;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.coursemanager.model.Course;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class CourseMongoRepositoryTest {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;
    private MongoClient client;
    private CourseMongoRepository courseMongoRepository;
    private MongoCollection<Document> courseCollection;
    public static final String COURSE_COLLECTION_NAME = "course";
    public static final String COURSEMANAGER_DB_NAME = "coursemanager";

    @BeforeClass
    public static void setupServer() {
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind();
    }

    @AfterClass
    public static void shutdownServer() {
        server.shutdown();
    }

    @Before
    public void setup() {
        client = new MongoClient(new ServerAddress(serverAddress));
        courseMongoRepository = new CourseMongoRepository(client,COURSEMANAGER_DB_NAME,COURSE_COLLECTION_NAME);
        MongoDatabase database = client.getDatabase(COURSEMANAGER_DB_NAME);
        database.drop();
        courseCollection = database.getCollection(COURSE_COLLECTION_NAME);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAllWhenDatabaseIsEmpty() {
        assertThat(courseMongoRepository.findAll()).isEmpty();
    }

    @Test
    public void testFindAllWhenDatabaseIsNotEmpty() {
        addTestCourseToDatabase(
            "APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        addTestCourseToDatabase(
            "APT002","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        assertThat(courseMongoRepository.findAll()).containsExactly(
                new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development"),
                new Course("APT002","Advanced Programming Techniques","Farrukh",6,"Test Driven Development"));
    }

    @Test
    public void testFindByCodeNotFound() {
        assertThat(courseMongoRepository.findByCode("APT001")).isNull();
    }

    @Test
    public void testFindByCodeFound() {
        addTestCourseToDatabase(
            "APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        addTestCourseToDatabase(
            "APT002","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        assertThat(courseMongoRepository.findByCode("APT002")).isEqualTo(
                new Course("APT002","Advanced Programming Techniques","Farrukh",6,"Test Driven Development"));
    }

    @Test
    public void testSave() {
        Course course =
            new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        courseMongoRepository.save(course);
        assertThat(readAllCoursesFromDatabase()).containsExactly(course);
    }

    @Test
    public void testDelete() {
        addTestCourseToDatabase(
            "APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        courseMongoRepository.delete("APT001");
        assertThat(readAllCoursesFromDatabase()).isEmpty();
    }

    @Test
    public void testUpdate() {
        Course original =
            new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Test Driven Development");
        courseMongoRepository.save(original);
        assertThat(readAllCoursesFromDatabase()).containsExactly(original);
        Course updated =
            new Course("APT001","Advanced Programming Techniques","Farrukh",6,"Updated Description");
        courseMongoRepository.update(updated);
        assertThat(readAllCoursesFromDatabase()).containsExactly(updated);
    }

    private void addTestCourseToDatabase(
        String code,String title,String instructor,int creditHours,String description) {
        courseCollection.insertOne(new Document()
            .append("courseCode",code)
            .append("courseTitle",title)
            .append("instructorName",instructor)
            .append("creditHours",creditHours)
            .append("description",description));
    }

    private List<Course> readAllCoursesFromDatabase() {
        return StreamSupport.stream(courseCollection.find().spliterator(),false).map(d -> new Course(
                "" + d.get("courseCode"),
                "" + d.get("courseTitle"),
                "" + d.get("instructorName"),
                ((Number)d.get("creditHours")).intValue(),
                "" + d.get("description"))).collect(Collectors.toList());
    }
}
