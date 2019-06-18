import tableIO.LogTableReader;
import tableIO.TableReader;

import java.io.IOException;
import java.util.List;

/**
 * A code to test the TableReader subclasses
 * @author Efe Acer
 * @version 1.0
 */
public class TableReaderTest {

    public static void main(String[] args) {
        List<Object> tableContents;
        try {
            TableReader tr = new LogTableReader("C:\\Users\\StjEfeA\\Desktop\\lbf_log.xlsx");
            tableContents = tr.read();
            tableContents.forEach((System.out::println));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
