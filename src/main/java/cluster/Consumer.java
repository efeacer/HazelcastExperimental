package cluster;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;

import java.util.Collection;
import java.util.Map;

/**
 * A class to model a Hazelcast client that reads a distributed Hazelcast map.
 * @author Efe Acer
 * @version 1.0.1
 */
public class Consumer {

    // Propertie(s)
    private Map<Object, Object> map;

    /**
     * Constructor of the cluster.Consumer class, which configures the Hazelcast client.
     * @param clusterName The name of the Hazelcast cluster to access
     * @param clusterPassword The password of the Hazelcast cluster to access
     * @param mapName The name of the distributed Hazelcast map
     */
    public Consumer(String clusterName, String clusterPassword, String mapName) {
        ClientConfig consumerConfig = new ClientConfig();
        GroupConfig groupConfig = consumerConfig.getGroupConfig();
        groupConfig.setName(clusterName);
        groupConfig.setPassword(clusterPassword);
        HazelcastInstance hzConsumer = HazelcastClient.newHazelcastClient(consumerConfig);
        map = hzConsumer.getMap(mapName);
    }

    /**
     * A method for the cluster.Consumer to read the key-value pairs inside the distributed
     * Hazelcast map it is initialized with.
     * Note: The method can be changed later to do any other data post-processing task
     */
    public void consume() {
        map.forEach((k, v) -> System.out.println(k + " " + v));
    }

    /**
     * A method for the cluster.Consumer to read and return the values in the distributed Hazelcast map
     * after selecting them according to a specified predicate.
     * @param p The predicate appearing on the where part of the select query
     * @return The values filtered after the selection
     */
    public Collection consume(Predicate p) {
        return ((IMap) map).values(p);
    }
}