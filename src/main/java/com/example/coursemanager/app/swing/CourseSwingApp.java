package com.example.coursemanager.app.swing;
import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.coursemanager.controller.CourseController;
import com.example.coursemanager.repository.mongo.CourseMongoRepository;
import com.example.coursemanager.view.swing.CourseSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class CourseSwingApp implements Callable<Void> {

    @Option(names = { "--mongo-host" }, description = "MongoDB host address")
    private String mongoHost = "localhost";

    @Option(names = { "--mongo-port" }, description = "MongoDB host port")
    private int mongoPort = 27017;

    @Option(names = { "--db-name" }, description = "Database name")
    private String databaseName = "coursemanager";

    @Option(names = { "--db-collection" }, description = "Collection name")
    private String collectionName = "course";

    public static void main(String[] args) {
        new CommandLine(new CourseSwingApp()).execute(args);
    }

    @Override
    public Void call() {
        EventQueue.invokeLater(() -> {
            try {
                CourseMongoRepository repository = new CourseMongoRepository(
                        new MongoClient(new ServerAddress(mongoHost, mongoPort)),databaseName,collectionName
                );

                CourseSwingView view = new CourseSwingView();
                CourseController controller = new CourseController(repository, view);

                view.setCourseController(controller);
                view.setVisible(true);
                controller.getAllCourses();

            } 
            catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
            }
        });
        return null;
    }
}
