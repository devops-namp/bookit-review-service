package uns.ac.rs.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.RabbitMQContainer;

import java.util.Map;

public class RabbitMQResource implements QuarkusTestResourceLifecycleManager {

    private RabbitMQContainer rabbitMQContainer;

    @Override
    public Map<String, String> start() {
        rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.8-management")
                .withExposedPorts(5672, 15672);
        rabbitMQContainer.start();

        return Map.of(
                "quarkus.rabbitmq.hosts", rabbitMQContainer.getHost(),
                "quarkus.rabbitmq.port", String.valueOf(rabbitMQContainer.getMappedPort(5672))
        );
    }

    @Override
    public void stop() {
        if (rabbitMQContainer != null) {
            rabbitMQContainer.stop();
        }
    }

    public String getHost() {
        return rabbitMQContainer.getHost();
    }

    public int getPort() {
        return rabbitMQContainer.getMappedPort(5672);
    }

    public String getAmqpUrl() {
        return rabbitMQContainer.getAmqpUrl();
    }
}
