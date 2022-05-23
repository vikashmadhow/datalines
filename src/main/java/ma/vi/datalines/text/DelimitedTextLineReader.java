package ma.vi.datalines.text;

import ma.vi.base.util.Convert;
import ma.vi.datalines.Column;
import ma.vi.datalines.Structure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static ma.vi.datalines.Structure.DEFAULT_COLUMN_QUOTE;
import static ma.vi.datalines.Structure.DEFAULT_COLUMN_SEP;

/**
 * A line reader for reading character-delimited text files.
 *
 * @author vikash.madhow@gmail.com
 */
public class DelimitedTextLineReader extends TextLineReader {
  @Override
  public boolean supports(File inputFile, String fileName, Structure structure) {
    if (structure.columnSeparatorChars().length > 0) {
      return hasTextContent(inputFile);
    }
    return false;
  }

  @Override
  protected List<Object> nextLine(boolean convertToColumnType) {
    try {
      String line = reader.readLine();
      linesRead++;
      if (line == null) {
        return null;
      } else {
        if (separator == 0) {
          /*
           * Using more than one character as a separator can lead to errors in
           * parsing the file. Instead, use the first one appearing in the first
           * line for the file.
           */
          int closestSeparator = Integer.MAX_VALUE;
          char[] separators = structure != null ? structure.columnSeparatorChars() : DEFAULT_COLUMN_SEP;
          for (char c: separators) {
            int pos = line.indexOf(c);
            if (pos != -1 && pos < closestSeparator) {
              closestSeparator = pos;
              separator = c;
            }
          }
          log.info("Using " + separator + " as column separator");
        }

        totalCharactersRead += line.length();
        if (totalCharactersRead > 0) {
          long averageLineLength = totalCharactersRead / linesRead;
          estimateTotalLines = fileLength / averageLineLength;
        }

        boolean quoted = false;
        List<Object> row = new ArrayList<>();
        StringBuilder column = new StringBuilder();
        for (char c: line.toCharArray()) {
          if (c == (structure != null ? structure.columnQuoteChar() : DEFAULT_COLUMN_QUOTE)) {
            if (quoted) {
              // End quote.
              quoted = false;
            } else if (column.toString().trim().length() == 0) {
              // Start quote
              quoted = true;
            } else {
              // A quote character has been found in the middle of a column.
              // Just add to the column, without changing the quote status.
              column.append(c);
            }
          } else if (c == separator) {
            if (quoted) {
              // Separator char found inside a quoted string, append.
              column.append(c);
            } else {
              row.add(convertValue(column.toString(), row.size(), convertToColumnType));
              column.delete(0, column.length());
            }
          } else {
            column.append(c);
          }
        }
        if (column.length() > 0) {
          row.add(convertValue(column.toString(), row.size(), convertToColumnType));
        }
        return row;
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
  }

  private Object convertValue(Object value, int pos, boolean convertToColumnType) {
    if (convertToColumnType) {
      Column col = structure == null ? null
                 : structure.columns().size() >= pos ? structure.columns().get(pos)
                 : null;
      value = convertValue(value, col);
    }
    return value;
  }

  protected static Object convertValue(Object value, Column col) {
    if (col != null && col.type() != null) {
      try                 { value = Convert.toType(value, col.type()); }
      catch (Exception e) { log.warning("Could not convert " + value + " to " + col.type()); }
    }
    return value;
  }

  /**
   * The separator character to use for the current file among all the ones
   * specified in the data structure.
   */
  private char separator = 0;

  /**
   * The total number of characters read up to the current line, used to estimate
   * the number of lines in the file.
   */
  private long totalCharactersRead = 0;

  /**
   * The number of lines read up to that point, used to estimate the number of
   * lines in the file.
   */
  private long linesRead = 0;

  private static final Logger log = Logger.getLogger(DelimitedTextLineReader.class.getName());
}