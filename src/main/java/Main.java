import java.util.Arrays;
import java.util.List;

/**
 * A code to test the Producer and Consumer classes, which are indeed Hazelcast
 * nodes and clients that share a common distributed Hazelcast map. The code also
 * performs and reports some runtime measurements.
 * @author Efe Acer
 * @version 1.0
 * Note: The experiment is not yet complete, more data and more Hazelcast nodes are
 *       needed to provide meaningful results.
 */
public class Main {

    // Constants
    private static final String MAP_NAME = "data";
    // TODO: Put these credentials into a config file if necessary
    private static final String CLUSTER_NAME = "dev";
    private static final String CLUSTER_PASSWORD = "dev-pass";
    private static final List<String> entries1 = Arrays.asList("p1_item1", "p1_item2", "p1_item3");
    private static final List<String> entries2 = Arrays.asList("p2_item1", "p2_item2");
    private static final List<String> entries3 = Arrays.asList("p3_item1", "p3_item2", "p3_item3", "p3_item4");

    public static void main(String[] args) {
        Producer producer1 = new Producer(MAP_NAME);
        Producer producer2 = new Producer(MAP_NAME);
        Producer producer3 = new Producer(MAP_NAME);

        // Used for measurements
        long startTime;
        long stopTime;

        startTime = System.currentTimeMillis();
        producer1.produce(entries1);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("P1: produce() took %d milliseconds", stopTime - startTime));

        startTime = System.currentTimeMillis();
        producer2.produce(entries2);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("P2: produce() took %d milliseconds", stopTime - startTime));

        startTime = System.currentTimeMillis();
        producer3.produce(entries3);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("P3: produce() took %d milliseconds", stopTime - startTime));

        Consumer consumer1 = new Consumer(CLUSTER_NAME, CLUSTER_PASSWORD, MAP_NAME);
        Consumer consumer2 = new Consumer(CLUSTER_NAME, CLUSTER_PASSWORD, MAP_NAME);

        startTime = System.currentTimeMillis();
        consumer1.consume();
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("C1: consume() took %d milliseconds", stopTime - startTime));

        startTime = System.currentTimeMillis();
        consumer2.consume();
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("C2: consume() took %d milliseconds", stopTime - startTime));
    }
}
