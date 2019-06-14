package cluster;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class to model a Hazelcast member (called a node) that populates a
 * distributed Hazelcast map.
 * @author Efe Acer
 * @version 1.0.1
 */
public class Producer {

    // Properties
    private Map<Object, Object> map;
    private IdGenerator idGenerator; // use Hazelcast ID generator to ensure uniqueness

    /**
     * Constructor of the cluster.Producer class, which is in fact a Hazelcast node that
     * creates or retrieves (depending on the order of execution) a distributed Hazelcast
     * map using an HazelcastInstance.
     * @param mapName The name that will be used to refer to the distributed Hazelcast map
     */
    public Producer(String mapName) {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        map = hz.getMap(mapName);
        idGenerator = hz.getIdGenerator("newid");
    }

    /**
     * A method for the cluster.Producer to insert certain key and attribute values into the distributed Hazelcast map.
     * @param keys Key attributes to insert
     * @param attributes Other attributes to insert
     * Note: The method can be changed later to do any other data pre-processing task
     */
    public void produce(List<Object> keys, List<Object> attributes) {
        List<Object[]> pairs = keys.stream()
                .flatMap(i -> attributes.stream()
                        .map(j -> new Object[]{i, j}))
                .collect(Collectors.toList());
        pairs.forEach(p ->  map.put(p[0], p[1]));
    }

    /**
     * A method for the cluster.Producer to insert certain values into the distributed Hazelcast map.
     * @param entries The entries which will be inserted into the distributed Hazelcast map
     * Note: The method can be changed later to do any other data pre-processing task
     */
    public void produce(List<Object> entries) {
        // Use Hazelcast's ID generator to ensure uniqueness
        entries.forEach(e -> map.put(idGenerator.newId(), e));
    }

    /**
     * A method for the Producer to be able to index the distributed Hazelcast map for
     * faster queries.
     * @param indices The list of indices to add to the distributed Hazelcast map to allow
     *                faster queries. The List comes in String-Boolean pairs, where the String
     *                is the attribute name and Boolean represents whether the index is an
     *                ordered one or not
     */
    public void indexMap(Map<String, Boolean> indices) {
        indices.forEach((k, v) -> ((IMap) map).addIndex(k, v));
    }
}