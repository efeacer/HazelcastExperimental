import cluster.Consumer;
import cluster.Producer;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import modelClasses.LogData;
import tableIO.LogTableReader;
import tableIO.TableReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A code to test Hazelcast performance under the Fraud Detection scheme.
 * @author Efe Acer
 * @version 1.0
 * Note: The test is not yet complete, more data and more Hazelcast nodes are
 *       needed to provide meaningful results.
 */
public class Main {

    // Constants
    private static final String TABLE_NAME = "C:\\Users\\StjEfeA\\Desktop\\lbf_log.xlsx";
    private static final String TEST_TABLE_NAME = "C:\\Users\\StjEfeA\\Desktop\\lbf_log_test.xlsx";
    private static final String MAP_NAME = "data";
    private static final double MISS_PENALTY = 80; // Cache miss penalty in milliseconds
    // TODO: Put these credentials into a config file if necessary
    private static final String CLUSTER_NAME = "dev";
    private static final String CLUSTER_PASSWORD = "dev-pass";

    public static void main(String[] args) {
      List<LogData> entries = null;
      List<LogData> testEntries = null;
        try {
            TableReader tr = new LogTableReader(TABLE_NAME);
            TableReader testTr = new LogTableReader(TEST_TABLE_NAME);
            // We are sure that reads return lists of only LogData objects, so we can double cast
            entries = (List<LogData>)(Object) tr.read();
            testEntries = (List<LogData>)(Object) testTr.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (entries == null || testEntries == null) {
            System.out.println("Cannot read table.");
            System.exit(-1); // Exit with failure
        }

        List<Object> keys = new ArrayList<>();
        List<Object> attributes = new ArrayList<>();
        entries.forEach(e -> {
            keys.add(e.getKey());
            attributes.add(e.getAttributes());
        });

        List<Object> testKeys = new ArrayList<>();
        testEntries.forEach(e -> testKeys.add(e.getKey()));

        // Populate the distributed Hazelcast Map
        Producer producer1 = new Producer(MAP_NAME);
        producer1.produce(keys, attributes);

        Consumer consumer1 = new Consumer(CLUSTER_NAME, CLUSTER_PASSWORD, MAP_NAME);
        EntryObject e = new PredicateBuilder().getEntryObject();

        double[] measurements = measureHitRate(e, consumer1, testKeys);
        System.out.printf("Cache Hit Rate (Percentage): %f%n", measurements[0] * 100.0);
        System.out.printf("Average Cache Access Time (Milliseconds): %f%n", measurements[1]);
        System.out.printf("Effective Memory Access Time - EAT (Milliseconds): %f%n",
                measureEAT(measurements[0], measurements[1], MISS_PENALTY));
    }

    /**
     * This method calculates and returns the effective memory access time in milliseconds,
     * using the cache hit rate average cache access time and miss penalty information.
     * @param hitRate The expected fraction cache hit rate
     * @param avgCacheAccessTime The average cache access time in milliseconds
     * @param missPenalty The cache miss penalty in milliseconds
     * @return The effective memory access time (EAT) for this particular application in milliseconds
     */
    private static double measureEAT( double hitRate, double avgCacheAccessTime, double missPenalty) {
        return avgCacheAccessTime + (1.0 - hitRate) * missPenalty;
    }

    /**
     * This method should be called after the producer has produced the distributed Hazelcast
     * map. The method simply retrieves each key in a test table to measure the hit rate and the
     * average cache access time. Hit rate is returned as a fraction and average access time is
     * returned in milliseconds.
     * @param e The entry object used to build the predicate of the retrieval
     * @param c The consumer that will handle the retrieval operations
     * @param testKeys A list specifying a list of keys to retrieve in the measurement
     * @return A pair containing the fraction cache hit rate and average cache access time
     */
    private static double[] measureHitRate(EntryObject e, Consumer c, List<Object> testKeys) {
        // Variables needed for the experiment
        long startTime;
        long stopTime;
        LogData.Attributes result;
        int numHits = 0;
        long totalTime = 0;
        for (Object testKey: testKeys) {
            startTime = System.currentTimeMillis();
            result = retrieveFromMap(e, c, (LogData.Key) testKey);
            stopTime = System.currentTimeMillis();
            totalTime += (stopTime - startTime);
            numHits += result != null ? 1 : 0;
        }
        double numKeys = (double) testKeys.size();
        return new double[]{numHits / numKeys, totalTime / numKeys};
    }

    /**
     * A method to retrieve the LogData.Attributes stored in a distributed Hazelcast map
     * corresponding to the specified LogData.Key.
     * @param e The entry object used to build the predicate of the retrieval
     * @param c The consumer that will handle the retrieval operation
     * @param k The specified key for retrieval
     * @return The LogData.Attributes object corresponding to the specified LogData.Key,
     *         null is returned in case there is no value corresponding to the key k
     */
    private static LogData.Attributes retrieveFromMap(EntryObject e, Consumer c, LogData.Key k) {
        Predicate p = e.key().get("timeOfDay").equal(k.getTimeOfDay())
                .and(e.key().get("weekDay").equal(k.getWeekDay()))
                .and(e.key().get("latitudeNumber").equal(k.getLatitudeNumber()))
                .and(e.key().get("longitudeNumber").equal(k.getLongitudeNumber()))
                .and(e.key().get("customerID").equal(k.getCustomerID()));
        Collection result = c.consume(p);
        Iterator it = result.iterator();
        if (it.hasNext()) {
            return (LogData.Attributes) it.next();
        }
        return null;
    }
}