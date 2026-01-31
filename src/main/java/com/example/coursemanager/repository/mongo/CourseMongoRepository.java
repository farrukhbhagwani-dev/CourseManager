package com.example.coursemanager.repository.mongo;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.example.coursemanager.model.Course;
import com.example.coursemanager.repository.CourseRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class CourseMongoRepository implements CourseRepository {

    private final MongoCollection<Document> courseCollection;
    private static final String CODE = "courseCode";
    private static final String TITLE = "courseTitle";
    private static final String INSTRUCTOR = "instructorName";
    private static final String CREDIT = "creditHours";
    private static final String DESCRIPTION = "description";

    public CourseMongoRepository(MongoClient client, String databaseName, String collectionName) {
        this.courseCollection = client.getDatabase(databaseName).getCollection(collectionName);
    }

    @Override
    public void save(Course course) {
        courseCollection.insertOne(
                new Document()
                        .append(CODE, course.getCourseCode())
                        .append(TITLE, course.getCourseTitle())
                        .append(INSTRUCTOR, course.getInstructorName())
                        .append(CREDIT, course.getCreditHours())
                        .append(DESCRIPTION, course.getDescription())
        );
    }

    @Override
    public List<Course> findAll() {
        return StreamSupport.stream(courseCollection.find().spliterator(), false).map(this::fromDocument).collect(Collectors.toList());
    }

    @Override
    public Course findByCode(String courseCode) {
        Document d = courseCollection.find(Filters.eq(CODE, courseCode)).first();
        return d != null ? fromDocument(d) : null;
    }

    @Override
    public void update(Course course) {
        Document update = new Document()
                .append(TITLE, course.getCourseTitle())
                .append(INSTRUCTOR, course.getInstructorName())
                .append(CREDIT, course.getCreditHours())
                .append(DESCRIPTION, course.getDescription());

        courseCollection.updateOne(
                Filters.eq(CODE, course.getCourseCode()),
                new Document("$set", update)
        );
    }

    @Override
    public void delete(String courseCode) {
        courseCollection.deleteOne(Filters.eq(CODE, courseCode));
    }

    private Course fromDocument(Document d) {
        return new Course(
                "" + d.get(CODE),
                "" + d.get(TITLE),
                "" + d.get(INSTRUCTOR),
                ((Number) d.get(CREDIT)).intValue(),
                "" + d.get(DESCRIPTION)
        );
    }
}
