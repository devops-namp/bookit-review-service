package uns.ac.rs.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Map;

public class MongoDBResource implements QuarkusTestResourceLifecycleManager {

    final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10")
            .withExposedPorts(27017)
            .withEnv("MONGO_INITDB_DATABASE", "testdatabase");

    @Override
    public Map<String, String> start() {
        mongoDBContainer.start();
        return Map.of(
                "quarkus.mongodb.connection-string", createConnectionString()
        );
    }

    private String createConnectionString() {
        return "mongodb://" + mongoDBContainer.getContainerIpAddress() +
                ":" + mongoDBContainer.getMappedPort(27017) +
                "/testdatabase";
    }

    @Override
    public void stop() {
        mongoDBContainer.stop();
    }
}