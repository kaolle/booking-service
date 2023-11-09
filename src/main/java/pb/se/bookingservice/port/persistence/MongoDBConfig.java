package pb.se.bookingservice.port.persistence;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import pb.se.bookingservice.port.task.UpdateTestBookingsTask;

@Configuration
@Order(1)
@Profile({"dev", "prod"})
public class MongoDBConfig {

    private static final Logger logger = LogManager.getLogger(MongoDBConfig.class);

    @Value("${SPRING_DATA_MONGODB_URI}")
    private String mongodbUri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    public MongoTemplate mongoTemplate() {
        //logger.info("SPRING_DATA_MONGODB_URI = {}", mongodbUri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongodbUri))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);

        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, database));
    }
}
