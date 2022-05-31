package ma.vi.datalines.xl;

import ma.vi.datalines.AbstractLineReader;
import ma.vi.datalines.Format;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static org.apache.poi.ss.usermodel.CellType.*;

/**
 * A line reader which can read pre-Excel-2007 (xls) files.
 *
 * @author vikash.madhow@gmail.com
 */
public class XlsLineReader extends AbstractLineReader {
  @Override
  public boolean supports(File inputFile, String name, Format format) {
    try (HSSFWorkbook ignored = new HSSFWorkbook(new FileInputStream(inputFile))) {
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  public void openFile(File inputFile, String fileName, Format format) {
    try {
      applyFormatting = format.applyFormatting();
      this.fileName = fileName;

      /*
       * Open workbook.
       */
      file = new FileInputStream(inputFile);
      workbook = new HSSFWorkbook(file);
      evaluator = workbook.getCreationHelper().createFormulaEvaluator();

      /*
       * Sheets to read.
       */
      if (format.page() <= 0) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
          sheetIds.add(i);
        }
      } else {
        sheetIds.add(format.page() - 1);
      }

      if (!sheetIds.isEmpty()) {
        /*
         * open first sheet.
         */
        nextSheet();
      } else {
        throw new IllegalStateException("No sheet data found in " + fileName);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not read Excel 97 (xls) file '" + fileName + "'. Reason: " + e, e);
    }
  }

  /**
   * Load the next sheets and initialize rows and columns data.
   */
  private void nextSheet() {
    if (sheetIds.isEmpty()) {
      throw new IllegalStateException("No more sheets to read from");
    } else {
      Integer sheetId = sheetIds.remove(0);
      if (sheetId >= workbook.getNumberOfSheets()) {
        throw new IllegalStateException("This workbook (" + fileName
                                      + ") does not have a sheet at position "
                                      + sheetId);
      }
      HSSFSheet sheet = workbook.getSheetAt(sheetId);
      rows = sheet.rowIterator();
    }
  }

  @Override
  protected List<Object> nextLine(boolean convertToColumnType) {
    if (rows.hasNext()) {
      List<Object> row = new ArrayList<>();
      Row cells = rows.next();
      for (int i = 0; i < cells.getLastCellNum(); i++) {
        Cell cell = cells.getCell(i);
        Object contents = null;
        if (cell != null) {
          Object valueContainer = cell;
          CellType type = cell.getCellType();
          if (type == FORMULA) {
            try {
              CellValue value = evaluator.evaluate(cell);
              type = value.getCellType();
              valueContainer = value;
            } catch (Exception e) {
              /*
               * Error evaluating cell value, just use formula as a normal string
               */
              contents = cell.getCellFormula();
              type = null;
            }
          }

          if (type == BOOLEAN) {
            contents = valueContainer instanceof Cell c
                     ? c.getBooleanCellValue()
                     : ((CellValue)valueContainer).getBooleanValue();
          } else if (type == ERROR) {
            contents = "ERROR: " + (valueContainer instanceof Cell c
                                  ? c.getErrorCellValue()
                                  : ((CellValue)valueContainer).getErrorValue());
          } else if (type == STRING) {
            contents = valueContainer instanceof Cell c
                     ? c.getStringCellValue()
                     : ((CellValue)valueContainer).getStringValue();
          } else if (type == BLANK) {
            contents = null;
          } else if (type == NUMERIC) {
            /*
             * It's a number, but almost certainly one with a special style or format
             */
            CellStyle style = cell.getCellStyle();
            int formatIndex = style.getDataFormat();
            String formatString = style.getDataFormatString();
            if (formatString == null) {
              formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
            }
            double cellValue = valueContainer instanceof Cell c
                             ? c.getNumericCellValue()
                             : ((CellValue) valueContainer).getNumberValue();
            boolean format = applyFormatting;
            if (formatString != null && (format || formatString.trim().equalsIgnoreCase("general"))) {
              contents = formatter.formatRawCellContents(cellValue, formatIndex, formatString);

            } else if (DateUtil.isADateFormat(formatIndex, formatString)) {
              contents = DateUtil.getJavaDate(cellValue);

            } else {
              contents = cellValue;
            }
          }
        }
        row.add(contents);
      }
      return row;

    } else {
      /*
       * If end of current sheet, move to next, if any.
       */
      if (!sheetIds.isEmpty()) {
        nextSheet();
        return SEPARATOR;
      }
      return null;
    }
  }

  @Override
  public long estimateTotalLines() {
    return -1;
  }

  /**
   * Close sheet and resources.
   */
  @Override
  public void close() {
    super.close();
    try {
      if (file != null) {
        file.close();
        file = null;
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
  }

  /**
   * The Xls file input stream.
   */
  private FileInputStream file;

  /**
   * The workbook.
   */
  private HSSFWorkbook workbook;

  /**
   * Formula evaluator.
   */
  private FormulaEvaluator evaluator;

  /**
   * Rows in the current sheet.
   */
  private Iterator<Row> rows;

  /**
   * The ids of sheets to load. If the import is for a single sheet, this will contain the
   * id of the first sheet only or that of the specified sheet number; otherwise it will
   * contain the ids of all sheets in the file.
   */
  private final List<Integer> sheetIds = new ArrayList<>();

  /**
   * Whether to apply formatting to the contents of read cells, or not.
   */
  private boolean applyFormatting;

  /**
   * Data formatter.
   */
  private static final DataFormatter formatter = new DataFormatter();

  /**
   * Logger.
   */
  private static final Logger log = Logger.getLogger(XlsLineReader.class.getName());
}