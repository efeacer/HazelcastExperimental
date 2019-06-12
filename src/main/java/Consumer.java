import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

/**
 * A class to model a Hazelcast client that reads a distributed Hazelcast map.
 * @author Efe Acer
 * @version 1.0
 * Note: Everything is package private for now.
 */
class Consumer {

    // Propertie(s)
    private Map<Long, String> map;

    /**
     * Constructor of the Consumer class, which configures the Hazelcast client.
     * @param clusterName The name of the Hazelcast cluster to access
     * @param clusterPassword The password of the Hazelcast cluster to access
     * @param mapName The name of the distributed Hazelcast map
     */
    Consumer(String clusterName, String clusterPassword, String mapName) {
        ClientConfig consumerConfig = new ClientConfig();
        GroupConfig groupConfig = consumerConfig.getGroupConfig();
        groupConfig.setName(clusterName);
        groupConfig.setPassword(clusterPassword);
        HazelcastInstance hzConsumer = HazelcastClient.newHazelcastClient(consumerConfig);
        map = hzConsumer.getMap(mapName);
    }

    /**
     * A method for the Consumer to read the key-value pairs inside the distributed
     * Hazelcast map it is initialized with.
     * Note: The method can be changed later to do any other data post-processing task
     */
    void consume() {
        map.forEach((k, v) -> System.out.println(String.format("Key: %d | Value: %s", k, v)));
    }
}
