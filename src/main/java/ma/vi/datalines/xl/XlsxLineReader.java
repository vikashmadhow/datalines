package ma.vi.datalines.xl;

import ma.vi.base.util.Numbers;
import ma.vi.datalines.AbstractLineReader;
import ma.vi.datalines.Structure;
import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * A line reader which can read Excel 2007 (xlsx) files.
 *
 * @author vikash.madhow@gmail.com
 */
public class XlsxLineReader extends AbstractLineReader {
  @Override
  public boolean supports(File inputFile, String name, Structure structure) {
    try (OPCPackage ignored = OPCPackage.open(inputFile, PackageAccess.READ)) {
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void openFile(File inputFile, String fileName, Structure structure) {
    try {
      applyFormatting = structure.applyFormatting();
      this.fileName = fileName;

      /*
       * Open Xlsx package and get the part that holds the workbook.
       */
      excelPackage = OPCPackage.open(inputFile, PackageAccess.READ);
      PackageRelationship coreDocRelationship = excelPackage.getRelationshipsByType(PackageRelationshipTypes.CORE_DOCUMENT).getRelationship(0);
      PackagePart workbookPart = excelPackage.getPart(coreDocRelationship);

      /*
       * Find id of worksheet(s) to read.
       */
      int sheetNumber = 1;
      PackageRelationshipCollection relationships = workbookPart.getRelationships();
      for (PackageRelationship rel: relationships) {
        if (rel.getRelationshipType().equals(SHEET_REL_TYPE)) {
          if (structure.page() <= 0) {
            sheetIds.add(rel.getId());
          } else if (sheetNumber == structure.page()) {
            sheetIds.add(rel.getId());
            break;
          }
          sheetNumber++;
        }
      }

      if (!sheetIds.isEmpty()) {
        xlsx = new XSSFReader(excelPackage);
        sharedStringsTable = xlsx.getSharedStringsTable();
        styles = xlsx.getStylesTable();
        sheetIn = xlsx.getSheet(sheetIds.remove(0));
        reader = inputFactory.createXMLStreamReader(sheetIn);

      } else {
        throw new IllegalStateException("No sheet data found in " + fileName);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not read XLSX file '" + fileName + "'. Reason: " + e, e);
    }
  }

  @Override
  protected List<Object> nextLine(boolean convertToColumnType) {
    try {
      if (moveToStartOfRow()) {
        // read columns
        List<Object> row = new ArrayList<>();
        while (reader.nextTag() == START_ELEMENT && "c".equals(reader.getLocalName())) {
          /*
           * Get cell reference to determine column (this is necessary because
           * null cells are not saved in the xml file, therefore a simple counter
           * increment will not work).
           */
          String cellRef = reader.getAttributeValue(null, "r");
          int currentCell = cellRef == null ? row.size() : new CellReference(cellRef).getCol();

          /*
           * Fill gaps with null, if any
           */
          while (currentCell > row.size()) row.add(null);

          /*
           * Get cell type and style.
           */
          Object contents = null;
          CellDataType dataType = CellDataType.NUMBER;
          String cellType = reader.getAttributeValue(null, "t");
          String cellStyleStr = reader.getAttributeValue(null, "s");
          int formatIndex = -1;
          String formatString = null;
          if ("b".equals(cellType)) {
            dataType = CellDataType.BOOL;

          } else if ("e".equals(cellType)) {
            dataType = CellDataType.ERROR;

          } else if ("inlineStr".equals(cellType)) {
            dataType = CellDataType.INLINESTR;

          } else if ("s".equals(cellType)) {
            dataType = CellDataType.SSTINDEX;

          } else if ("str".equals(cellType)) {
            dataType = CellDataType.FORMULA;

          } else if (cellStyleStr != null) {
            /*
             * A number, but almost certainly with a special style or format.
             */
            int styleIndex = Integer.parseInt(cellStyleStr);
            XSSFCellStyle style = styles.getStyleAt(styleIndex);
            formatIndex = style.getDataFormat();
            formatString = style.getDataFormatString();
            if (formatString == null) {
              formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
            }
          }

          // Read cell contents, parse and format.
          while (reader.nextTag() != END_ELEMENT || !reader.getLocalName().equals("c")) {
            String cellValue = reader.getElementText();
            if (reader.getLocalName().equals("v")) {
              switch (dataType) {
                case BOOL:
                  contents = cellValue.charAt(0) == '0' ? "FALSE" : "TRUE";
                  break;

                case ERROR:
                  contents = "ERROR: " + cellValue;
                  break;

                case INLINESTR:
                  contents = new XSSFRichTextString(cellValue).toString();
                  break;

                case SSTINDEX:
                  int idx = Integer.parseInt(cellValue.trim());
                  contents = sharedStringsTable.getItemAt(idx).toString();
                  break;

                case NUMBER:
                  if (DateUtil.isADateFormat(formatIndex, formatString)
                   || (columnByLocations.containsKey(String.valueOf(currentCell + 1))
                    && columnByLocations.get(String.valueOf(currentCell + 1)).type().contains("date"))) {
                    contents = DateUtil.getJavaDate(Double.parseDouble(cellValue));
                  } else if (applyFormatting && formatString != null) {
                    contents = formatter.formatRawCellContents(Double.parseDouble(cellValue), formatIndex, formatString);
                  } else {
                    contents = Numbers.convert(cellValue.trim());
                  }
                  break;

                case FORMULA:
                default:
                  contents = cellValue;
                  break;
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
          reader.close();
          sheetIn.close();

          sheetIn = xlsx.getSheet(sheetIds.remove(0));
          reader = inputFactory.createXMLStreamReader(sheetIn);

          /*
           * Return separator so that header lines read are reset to 0 and headers
           * are read again for this sheet.
           */
          return SEPARATOR;
        }
        return null;
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
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
      if (reader != null) {
        reader.close();
        reader = null;
      }
      if (sheetIn != null) {
        sheetIn.close();
        sheetIn = null;
      }
      if (excelPackage != null) {
        excelPackage.close();
        excelPackage = null;
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
  }

  /**
   * Moves the reader to the next row or stays at the same position if already on a row.
   * Returns true if a row was found, false otherwise.
   */
  private boolean moveToStartOfRow() {
    return moveToStartOf("row");
  }

  /**
   * Moves to the start of the tag element or stays at the same position if already on one.
   * Returns true if on the tag element, false otherwise.
   */
  private boolean moveToStartOf(String tag) {
    try {
      if (!reader.hasNext()) {
        return false;
      } else {
        int event = reader.getEventType();
        while (event != START_ELEMENT || !reader.getLocalName().equals(tag)) {
          event = reader.next();
          if (event == END_DOCUMENT) {
            return false;
          }
        }
        return true;
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
  }

  /**
   * Cell data types.
   */
  enum CellDataType {
    BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
  }

  /**
   * The Excel reader.
   */
  private XSSFReader xlsx;

  /**
   * XML streaming reader factory.
   */
  private final XMLInputFactory inputFactory = XMLInputFactory.newFactory();

  /**
   * Streaming reader for the XML sheet.
   */
  private XMLStreamReader reader;

  /**
   * The ids of sheets to load. If the import is for a single sheet, this will contain the
   * id of the first sheet only; otherwise it will contain the ids of all sheets in the file.
   */
  private final List<String> sheetIds = new ArrayList<>();

  /**
   * The sheet input stream.
   */
  private InputStream sheetIn;

  /**
   * Shared strings table of Excel.
   */
  private SharedStringsTable sharedStringsTable;

  /**
   * Styles table.
   */
  private StylesTable styles;

  /**
   * Whether to apply formatting to the contents of read cells, or not.
   */
  private boolean applyFormatting;

  /**
   * Data formatter.
   */
  private static final DataFormatter formatter = new DataFormatter();

  /**
   * The Excel package object.
   */
  private OPCPackage excelPackage;

  /**
   * Relationship type for Excel worksheets.
   */
  private static final String SHEET_REL_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet";
}