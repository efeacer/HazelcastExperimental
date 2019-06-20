import cluster.Consumer;
import cluster.Producer;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import modelClasses.LogData;
import tableIO.LogTableReader;
import tableIO.TableReader;

import java.io.IOException;
import java.util.*;

/**
 * A code to test Hazelcast performance under the Fraud Detection scheme.
 * @author Efe Acer
 * @version 1.0
 * Note: The test is not yet complete, more data and more Hazelcast nodes are
 *       needed to provide meaningful results.
 */
public class Main {

    // Constants
    private static final String TABLE_NAME = "C:\\Users\\StjEfeA\\Desktop\\lbf_log_0612.xlsx";
    private static final String TEST_TABLE_NAME = "C:\\Users\\StjEfeA\\Desktop\\lbf_log_0617.xlsx";
    private static final String MAP_NAME = "data";
    private static final double MISS_PENALTY = 80; // Cache miss penalty in milliseconds
    private static final int NUM_PRODUCING_THREADS = 3; // Number of producing threads
    private static final int NUM_CONSUMING_THREADS = 3; // Number of producing threads
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
            entries = entries.subList(85000, entries.size());
            testEntries = (List<LogData>)(Object) testTr.read();
            testEntries = testEntries.subList(94000, testEntries.size());
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

        double[] measurements = measure(consumer1, testKeys);
        System.out.println("--------------------------------------------------");
        System.out.printf("Cache Hit Rate (Percentage): %f%n", measurements[0] * 100.0);
        System.out.printf("Average Cache Access Time (Milliseconds): %f%n", measurements[1]);
        System.out.printf("Maximum Cache Access Time (Milliseconds): %f%n", measurements[2]);
        System.out.printf("Effective Memory Access Time - EAT (Milliseconds): %f%n",
                measureEAT(measurements[0], measurements[1], MISS_PENALTY));
        System.out.println("--------------------------------------------------");

        /*
        runThreads(getProducingThreads(entries, NUM_PRODUCING_THREADS));

        List<Object> testKeys = new ArrayList<>();
        testEntries.forEach(e -> testKeys.add(e.getKey()));

        runThreads(getConsumingThreads(testKeys, NUM_CONSUMING_THREADS));
        */
    }

    /**
     * Starts all threads in a given Thread array and waits for all of them to finish with join calls.
     * @param threads The given array of Threads
     */
    private static void runThreads(Thread[] threads) {
        for (Thread t: threads) { t.start(); }
        for (Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method returns an array of specified number of Threads that are all responsible for
     * populating an Hazelcast map with parts of a list of key-value pairs.
     * @param entries The list of key-value pairs
     * @param numThreads The requested number of Threads
     * @return The resulting Thread array
     */
    private static Thread[] getProducingThreads(List<LogData> entries, int numThreads) {
        Thread[] threads = new Thread[numThreads];
        int totalSize = entries.size();
        int partSize = totalSize / numThreads;
        for (int i = 0; i < numThreads; i++) {
            List<Object> k = new ArrayList<>();
            List<Object> a = new ArrayList<>();
            List<LogData> sublist = i + 1 == numThreads ? entries.subList(i * partSize, (i + 1) * partSize) :
                    entries.subList(i * partSize, totalSize);
            sublist.forEach(e -> {
                k.add(e.getKey());
                a.add(e.getAttributes());
            });
            threads[i] = getProducingThread(k, a);
        }
        return threads;
    }

    /**
     * This method returns an array of specified number of Threads that are all responsible for
     * retrieving specified keys from an Hazelcast map.
     * @param keys The list of specified keys
     * @param numThreads The requested number of Threads
     * @return The resulting Thread array
     */
    private static Thread[] getConsumingThreads(List<Object> keys, int numThreads) {
        Thread[] threads = new Thread[numThreads];
        int totalSize = keys.size();
        int partSize = totalSize / numThreads;
        for (int i = 0; i < numThreads; i++) {
            List<Object> sublist = i + 1 == numThreads ? keys.subList(i * partSize, (i + 1) * partSize) :
                    keys.subList(i * partSize, totalSize);
            threads[i] = getConsumingThread(sublist);
        }
        return threads;
    }

    /**
     * A method to create and return a Thread that populates an Hazelcast map with the key-value
     * specified.
     * @param k The keys to insert to the map
     * @param a The values (attributes) corresponding to the specified keys
     * @return The Thread that is responsible for populating the map
     */
    private static Thread getProducingThread(List<Object> k, List<Object> a) {
        return new Thread(new Runnable() {
            Producer p = new Producer(MAP_NAME);
            public void run() { p.produce(k, a); }
        });
    }

    /**
     * A method to create and return a Thread that retrieves the specified keys from an Hazelcast map.
     * @param k The keys to retrieve from the map
     * @return The Thread that is responsible for retrieving keys from the map
     */
    private static Thread getConsumingThread(List<Object> k) {
        return new Thread(new Runnable() {
            Consumer c = new Consumer(CLUSTER_NAME, CLUSTER_PASSWORD, MAP_NAME);
            public void run() {
                EntryObject e = new PredicateBuilder().getEntryObject();
                double[] measurements = measure(c, k);
                System.out.println("--------------------------------------------------");
                System.out.printf("Cache Hit Rate (Percentage): %f%n", measurements[0] * 100.0);
                System.out.printf("Average Cache Access Time (Milliseconds): %f%n", measurements[1]);
                System.out.printf("Maximum Cache Access Time (Milliseconds): %f%n", measurements[2]);
                System.out.printf("Effective Memory Access Time - EAT (Milliseconds): %f%n",
                        measureEAT(measurements[0], measurements[1], MISS_PENALTY));
                System.out.println("--------------------------------------------------");
            }
        });
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
     * average and maximum cache access times. Hit rate is returned as a fraction and access times
     * are returned in milliseconds.
     * @param c The consumer that will handle the retrieval operations
     * @param testKeys A list specifying a list of keys to retrieve in the measurement
     * @return An array containing the fraction cache hit rate, average cache access time and
     *         maximum cache access time
     */
    private static double[] measure(Consumer c, List<Object> testKeys) {
        // Variables needed for the experiment
        long startTime;
        long stopTime;
        LogData.Attributes result;
        int numHits = 0;
        long totalTime = 0;
        double maxAccessTime = 0;
        for (Object testKey: testKeys) {
            startTime = System.currentTimeMillis();
            result = retrieveFromMap(c, (LogData.Key) testKey);
            stopTime = System.currentTimeMillis();
            long time = stopTime - startTime;
            totalTime += time;
            if (result != null) { numHits++; }
            if (time > maxAccessTime) { maxAccessTime = time; }
        }
        double numKeys = (double) testKeys.size();
        return new double[]{numHits / numKeys, totalTime / numKeys, maxAccessTime};
    }

    /**
     * A method to retrieve the LogData.Attributes stored in a distributed Hazelcast map
     * corresponding to the specified LogData.Key.
     * @param c The consumer that will handle the retrieval operation
     * @param k The specified key for retrieval
     * @return The LogData.Attributes object corresponding to the specified LogData.Key,
     *         null is returned in case there is no value corresponding to the key k
     */
    private static LogData.Attributes retrieveFromMap(Consumer c, LogData.Key k) {
        EntryObject e = new PredicateBuilder().getEntryObject();
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