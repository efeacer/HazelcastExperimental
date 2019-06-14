import cluster.Consumer;
import cluster.Producer;
import modelClasses.LivingArea;
import tableIO.LivingAreaTableReader;
import tableIO.TableReader;

import java.io.IOException;
import java.util.ArrayList;
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
    private static final String TABLE_NAME = "C:\\Users\\StjEfeA\\Desktop\\lbf_liv_area.xls";
    private static final String MAP_NAME = "data";
    // TODO: Put these credentials into a config file if necessary
    private static final String CLUSTER_NAME = "dev";
    private static final String CLUSTER_PASSWORD = "dev-pass";

    public static void main(String[] args) {
        List<LivingArea> entries = null;
        try {
            TableReader tr = new LivingAreaTableReader(TABLE_NAME);
            // We are sure that tr.read returns a list of only LivingArea objects, so we can double cast
            entries = (List<LivingArea>)(Object) tr.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (entries == null) {
            System.out.println("Cannot read table.");
            System.exit(-1); // Exit with failure
        }

        List<Object> keys = new ArrayList<>();
        List<Object> attributes = new ArrayList<>();
        entries.forEach(e -> {
            keys.add(e.getKey());
            attributes.add(e.getAttributes());
        });

        Producer producer1 = new Producer(MAP_NAME);

        producer1.produce(keys, attributes);

        Consumer consumer1 = new Consumer(CLUSTER_NAME, CLUSTER_PASSWORD, MAP_NAME);

        consumer1.consume();
    }
}
