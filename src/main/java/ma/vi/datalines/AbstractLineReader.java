package ma.vi.datalines;

import java.io.File;
import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

/**
 * An abstract implementation of {@link LineReader} simplifying concrete
 * implementations by taking care of discarding header and footer lines
 * and skipping blank lines, as defined by the import definition.
 *
 * @author vikash.madhow@gmail.com
 */
public abstract class AbstractLineReader implements LineReader {
  @Override
  public final void open(File      inputFile,
                         String    fileName,
                         Structure structure) {
    this.fileName = fileName;
    buffer = new ArrayDeque<>();

    openFile(inputFile, fileName, structure);
    if (structure == null) {
      /*
       * Derive structure from header.
       */
      List<Object> line = readNextLine(true, false);
      if (line != null && line != SEPARATOR) {
        List<Column> cols = new ArrayList<>();
        int i = 1;
        for (Object o: line) {
          String colName = o.toString().trim().toLowerCase()
                            .replaceAll("\\W", "_");
          cols.add(new Column(colName, "string", String.valueOf(i), null, emptyMap()));
          i++;
        }
        structure = new Structure(1, 0, true,
                                  new char[]{','}, '"', false, 1, cols);
        buffer.add(line);
      }
    }

    this.structure = structure == null ? new Structure() : structure;
    this.maxBufferedLines = Math.max(this.structure.footerLines() * 2 + 1, 10);
    columnByLocations = new HashMap<>();
    for (Column column: this.structure.columns()) {
      if (column.location() != null) {
        columnByLocations.put(column.location(), column);
      }
    }
  }

  /**
   * Subclasses must implement this method to open file for reading.
   */
  protected abstract void openFile(File      inputFile,
                                   String    fileName,
                                   Structure structure);

  @Override
  public Iterator<List<Object>> iterator() {
    return this;
  }

  @Override
  public boolean hasNext() {
    if (closed) {
      return false;
    } else {
      boolean reset;
      reset: do {
        reset = false;

        // read and discard header lines: if skipBlankLines is true,
        // blank lines does not count towards the header lines count.
        while (headerLinesRead < structure.headerLines()) {
          List<Object> line = readNextLine(structure.ignoreBlankLines(), false);
          if (line == SEPARATOR) {
            headerLinesRead = 0;
          } else {
            headerLinesRead++;
          }
        }

        // If number of lines in buffer cannot be used to determine
        // whether we have reached the end of input, read more up to
        // the maxBufferedLines threshold.
        if (buffer.isEmpty()) {
          List<Object> line;
          while (buffer.size() < maxBufferedLines) {
            line = readNextLine(structure.ignoreBlankLines(), true);
            if (line == null || line == SEPARATOR) {
              // End-of-input or end-of-sheet: eliminate at most footerLines rows.
              for (int i = 0; i < structure.footerLines() && !buffer.isEmpty(); i++) {
                buffer.removeLast();
              }
              if (line == SEPARATOR) {
                // End of sheet: if buffer is empty, read from next sheet, otherwise,
                // continue with this sheet until it ends and ensure that next sheet
                // is read properly (skipping header lines as required).
                headerLinesRead = 0;
                if (buffer.isEmpty()) {
                  reset = true;
                  continue reset;
                } else {
                  break;
                }
              } else {
                break;
              }
            } else {
              buffer.add(line);
            }
          }
        }
      } while (reset);

      if (!buffer.isEmpty()) {
        return true;
      } else {
        close();
        return false;
      }
    }
  }

  @Override
  public List<Object> next() {
    if (buffer.isEmpty()) {
      if (!hasNext()) {
        throw new NoSuchElementException("No more lines to read.");
      }
    }
    return buffer.remove();
  }

  /**
   * Reads the next line, skipping blank lines if import definition requires so.
   */
  private List<Object> readNextLine(boolean ignoreBlankLines, boolean convertToColumnType) {
    List<Object> line;
    do {
      line = nextLine(convertToColumnType);
    } while (ignoreBlankLines && line != null && isEmpty(line));
    return line;
  }

  /**
   * Returns true if the line is empty. A line is empty if the list is empty
   * or every element is null or empty.
   */
  private boolean isEmpty(List<Object> line) {
    if (!line.isEmpty()) {
      for (Object column : line) {
        if (column != null && column.toString().trim().length() > 0) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Returns the next line from the underlying input or null if none. This method
   * should continue to return after the final line was read even if called several times.
   */
  protected abstract List<Object> nextLine(boolean convertToColumnType);

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Line readers does not support remove().");
  }

  /**
   * Reading is complete; close any resources.
   */
  public void close() {
    closed = true;
  }

  /**
   * The import file name as it is on the client-side.
   */
  protected String fileName;

  /**
   * The structure of the input file.
   */
  protected Structure structure;

  /**
   * Columns by location
   */
  protected Map<String, Column> columnByLocations;

  /**
   * A look-ahead buffer big enough to ensure that footer lines are ignored.
   */
  private Deque<List<Object>> buffer;

  /**
   * The look-ahead buffer is also used as a performance buffer with up to this number
   * of lines read in anticipation. This is always greater than the number of footerLines + 1.
   */
  private int maxBufferedLines;

  /**
   * The number of header lines that have already been read to ensure that the
   * number of header lines in import definition are discarded.
   */
  private int headerLinesRead;

  /**
   * Set to true when the reader is closed. Used to prevent any further attempt to
   * read lines after the underlying inputs have been closed.
   */
  protected boolean closed;

  /**
   * Separates multiple pages of rows (for files supporting those such as Excel).
   * This special row is returned after all rows of one sheet and before any rows from
   * the next. When the reader encounters this separator, it reset the headerLinesRead
   * variable to 0 so that header lines are read for this new sheet.
   */
  protected static final List<Object> SEPARATOR = singletonList("SEPARATOR");
}