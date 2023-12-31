package pb.se.bookingservice.port.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

@Configuration
@EnableMongoRepositories
public class MongoDBTestContainerConfig {
    public static final int TEST_PORT = 27018;
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(TEST_PORT);

    static {
        mongoDBContainer.start();
        var mappedPort = mongoDBContainer.getMappedPort(TEST_PORT);
        System.setProperty("mongodb.container.port", String.valueOf(mappedPort));
    }
}
