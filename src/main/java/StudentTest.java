import cluster.Consumer;
import cluster.Producer;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.SqlPredicate;
import modelClasses.Student;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A code to test the cluster, Producer and Consumer classes, which are indeed Hazelcast
 * nodes and clients that share a common distributed Hazelcast map. The code also
 * performs and reports some runtime measurements.
 * @author Efe Acer
 * @version 1.0.1
 * Note: The experiment is not yet complete, more data and more Hazelcast nodes are
 *       needed to provide meaningful results.
 */
public class StudentTest {

    // Constants
    private static final String MAP_NAME = "data";
    // TODO: Put these credentials into a config file if necessary
    private static final String CLUSTER_NAME = "dev";
    private static final String CLUSTER_PASSWORD = "dev-pass";
    private static final List<Object> entries1 = Arrays.asList(
            new Student("Efe", "Acer", 21, 21602217, 3.97, false),
            new Student("Emir", "Arıkan", 22, 21503421, 2.88, false),
            new Student("Berke", "Kural", 25, 21204443, 4.00, true),
            new Student("Ahmet", "Ballı", 19, 21802180, 3.56, false)
    );
    private static final List<Object> entries2 = Arrays.asList(
            new Student("Efe", "Takıcı", 21, 21602417, 3.37, false),
            new Student("Emircan", "Emir", 28, 21005521, 3.88, true),
            new Student("Berk", "Havalı", 20, 21707443, 2.00, false),
            new Student("Mehmet", "Ballı", 19, 21802181, 2.56, false),
            new Student("Aslı", "Zor", 19, 21800290, 3.40, false)
    );
    private static final List<Object> entries3 = Arrays.asList(
            new Student("Jale", "Hane", 21, 21602317, 3.37, false),
            new Student("Lale", "Nane", 22, 21504541, 3.23, false),
            new Student("Ceren", "Kor", 22, 21500290, 2.40, false)
    );
    // EntryObject instance is needed to build select query predicates using the PredicateBuilder class
    private static final EntryObject E = new PredicateBuilder().getEntryObject();
    // Predicate to select the graduated students with age less than or equal to 25 using Predicate Builder
    private static final Predicate P1 = E.is("graduated").and(E.get("age").lessEqual(25));
    // Predicate to select the currently studying students with a GPA between 3.30 and 3.80 using SQL
    private static final Predicate P2 = new SqlPredicate("graduated = false AND gpa BETWEEN 3.30 AND 3.80");
    // Predicate to select students with a surname starting with A or B using SQL
    private static final Predicate P3 = new SqlPredicate("surname LIKE 'A%' OR surname LIKE 'B%'");
    // Map for indexing the distributed Hazelcast Map
    private static final Map<String, Boolean> INDICES;
    static {
        Map<String, Boolean> temp = new HashMap<>();
        temp.put("gpa", true); // ordered index, since we have ranged queries for this field
        temp.put("graduated", false); // // not ordered, since boolean field cannot have range
        INDICES = Collections.unmodifiableMap(temp);
    }


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
        System.out.println(String.format("PR1: produce() took %d milliseconds", stopTime - startTime));

        startTime = System.currentTimeMillis();
        producer2.produce(entries2);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("PR2: produce() took %d milliseconds", stopTime - startTime));

        startTime = System.currentTimeMillis();
        producer3.produce(entries3);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("PR3: produce() took %d milliseconds", stopTime - startTime));

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

        // Consume using predicates
        startTime = System.currentTimeMillis();
        consumer1.consume(P1);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("C1: consume(P1) took %d milliseconds", stopTime - startTime));

        startTime = System.currentTimeMillis();
        consumer1.consume(P2);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("C1: consume(P2) took %d milliseconds", stopTime - startTime));

        startTime = System.currentTimeMillis();
        consumer1.consume(P3);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("C1: consume(P3) took %d milliseconds", stopTime - startTime));

        // Producer add indices and then we run the ranged query with P2 again
        startTime = System.currentTimeMillis();
        producer1.indexMap(INDICES);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format(System.lineSeparator() + "PR1: indexMap(INDICES) took %d milliseconds",
                stopTime - startTime));

        startTime = System.currentTimeMillis();
        consumer1.consume(P2);
        stopTime = System.currentTimeMillis();
        System.out.println(String.format("C1: consume(P2) took %d milliseconds after indexing",
                stopTime - startTime));
    }
}
