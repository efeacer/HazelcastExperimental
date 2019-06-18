package tableIO;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * An abstract class to read excel tables.
 * @author Efe Acer
 * @version 1.0
 */
public abstract class TableReader {

    // Properties
    XSSFSheet sheet;

    /**
     * Constructor of the TableReader class, initializes some necessary objects.
     * @param tableName The name of the excel table (sheet) to read
     * @throws IOException An IO exception may be thrown depending on the internal initializations
     */
    TableReader(String tableName) throws IOException {
        // Properties
        InputStream in = new BufferedInputStream(new FileInputStream(tableName));
        XSSFWorkbook wb = new XSSFWorkbook(in);
        sheet = wb.getSheetAt(0);
    }

    /**
     * An abstract method that should be overwritten by the sub-TableReader-classes
     * @return The contents of the excel table
     */
    public abstract List<Object> read();
}