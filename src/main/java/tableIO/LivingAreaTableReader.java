package tableIO;

import modelClasses.LivingArea;
import org.apache.poi.hssf.usermodel.HSSFRow;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A that is used to read living area excel tables.
 * @author Efe Acer
 * @version 1.0
 */
public class LivingAreaTableReader extends TableReader {

    /**
     * Constructor of the LivingAreaTableReader class, used to read the contents of
     * the table containing LivingArea objects.
     * @param tableName The name of the excel table (sheet) to read
     * @throws IOException An IO exception may be thrown depending on the internal initializations
     */
    public LivingAreaTableReader(String tableName) throws IOException {
        super(tableName);
    }

    /**
     * A method for the LivingAreaTableReader to read LivingArea objects from an excel sheet.
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
            HSSFRow row = (HSSFRow) rows.next();
            LivingArea livingArea = new LivingArea(dateToLocalDate(row.getCell(0).getDateCellValue()),
                    (int) row.getCell(1).getNumericCellValue(),
                    (int) row.getCell(2).getNumericCellValue(),
                    (int) row.getCell(3).getNumericCellValue(),
                    (int) row.getCell(4).getNumericCellValue(),
                    row.getCell(5).getNumericCellValue(),
                    row.getCell(6).getNumericCellValue());
            tableContents.add(livingArea);
        }
        return tableContents;
    }

    /**
     * A method to convert a Date object to a LocalDate object.
     * @param date The Date object to convert to LocalDate
     * @return The resulting LocalDate object after conversion
     */
    private LocalDate dateToLocalDate(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.toLocalDate();
    }
}