package tableIO;

import modelClasses.LogData;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class that is used to read fraud computation log data excel tables.
 * @author Efe Acer
 * @version 1.0
 */
public class LogTableReader extends TableReader {

    /**
     * Constructor of the LogTableReader class, used to read the contents of
     * the table containing LogData objects.
     * @param tableName The name of the excel table (sheet) to read
     * @throws IOException An IO exception may be thrown depending on the internal initializations
     */
    public LogTableReader(String tableName) throws IOException {
        super(tableName);
    }

    /**
     * A method for the LogTableReader to read LogData objects from an excel sheet.
     * @return The contents of the excel table
     */
    @Override
    public List<Object> read() {
        List<Object> tableContents = new ArrayList<>();
        Iterator rows = sheet.rowIterator();
        boolean headerRow = true;
        while (rows.hasNext()) {
            if (headerRow) {
                rows.next();
                headerRow = false;
                continue;
            }
            XSSFRow row = (XSSFRow) rows.next();
            LogData logData = new LogData((int) row.getCell(0).getNumericCellValue(),
                    (int) row.getCell(1).getNumericCellValue(),
                    row.getCell(2).getNumericCellValue(),
                    row.getCell(3).getNumericCellValue(),
                    (int) row.getCell(4).getNumericCellValue(),
                    (int) row.getCell(5).getNumericCellValue(),
                    (int) row.getCell(6).getNumericCellValue(),
                    (int) row.getCell(7).getNumericCellValue());
            tableContents.add(logData);
        }
        return tableContents;
    }
}