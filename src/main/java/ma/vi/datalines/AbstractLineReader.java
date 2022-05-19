package ma.vi.datalines;

import java.io.File;
import java.util.*;

/**
 * An abstract implementation of {@link LineReader} simplifying concrete
 * implementations by taking care of discarding header and footer lines
 * and skipping blank lines, as defined by the import definition.
 *
 * @author vikash.madhow@gmail.com
 */
public abstract class AbstractLineReader implements LineReader {
  @Override
  public final void open(File inputFile, String clientFileName, Import importDef) {
    this.importDef = importDef;
    importFields = new HashMap<>();
    for (ImportField field : importDef.fields.values()) {
      importFields.put(field.columnIndex(), field);
    }
    this.clientFileName = clientFileName;
    buffer = new ArrayDeque<>();
    maxBufferedLines = Math.max(importDef.footerLines * 2 + 1, 10);
    openFile(inputFile, clientFileName, importDef);
  }

  /**
   * Subclasses must implement this method to open file for reading.
   */
  protected abstract void openFile(File inputFile, String clientFileName, Import importDef);

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
      reset:
      do {
        reset = false;

        // read and discard header lines: if skipBlankLines is true,
        // blank lines does not count towards the header lines count.
        while (headerLinesRead < importDef.headerLines) {
          List<Object> line = readNextLine();
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
            line = readNextLine();
            if (line == null || line == SEPARATOR) {
              // End-of-input or end-of-sheet: eliminate at most footerLines rows.
              for (int i = 0; i < importDef.footerLines && !buffer.isEmpty(); i++) {
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
      }
      while (reset);

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
  private List<Object> readNextLine() {
    List<Object> line;
    boolean skipBlankLines = importDef.skipBlankLines;
    do {
      line = nextLine();
    }
    while (skipBlankLines && line != null && isEmpty(line));
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
  protected abstract List<Object> nextLine();

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Line readers does not support remove() op.");
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
  protected String clientFileName;

  /**
   * The import definition.
   */
  protected Import importDef;

  protected Map<Integer, ImportField> importFields;

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
  protected static final List<Object> SEPARATOR = Collections.singletonList("SEPARATOR");
}