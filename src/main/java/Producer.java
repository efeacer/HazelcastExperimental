import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IdGenerator;

import java.util.List;
import java.util.Map;

/**
 * A class to model a Hazelcast member (called a node) that populates a
 * distributed Hazelcast map.
 * @author Efe Acer
 * @version 1.0
 * Note: Everything is package private for now.
 */
class Producer {

    // Properties
    private Map<Long, String> map;
    private IdGenerator idGenerator; /* use Hazelcast ID generator to ensure uniqueness */

    /**
     * Constructor of the Producer class, which is in fact a Hazelcast node that
     * creates or retrieves (depending on the order of execution) a distributed Hazelcast
     * map using an HazelcastInstance.
     * @param mapName The name that will be used to refer to the distributed Hazelcast map
     */
    Producer(String mapName) {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        map = hz.getMap(mapName);
        idGenerator = hz.getIdGenerator("newid");
    }

    /**
     * A method for the Producer to insert certain values into the distributed Hazelcast map.
     * @param entries The entries which will be inserted into the distributed Hazelcast map
     * Note: The method can be changed later to do any other data pre-processing task
     */
    void produce(List<String> entries) {
        // Use Hazelcast's ID generator to ensure uniqueness
        entries.forEach(e -> map.put(idGenerator.newId(), e));
    }
}