package pb.se.bookingservice.port.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

@Configuration
@EnableMongoRepositories
public class MongoDBTestContainerConfig {
    private static final int MONGO_PORT = 27017;
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    static {
        mongoDBContainer.start();
        var mappedPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        System.setProperty("mongodb.container.port", String.valueOf(mappedPort));
    }
}
