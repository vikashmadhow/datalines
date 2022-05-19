package ma.vi.datalines.text;

import ma.vi.datalines.Import;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A line reader for reading character-delimited text files.
 *
 * @author vikash.madhow@gmail.com
 */
public class CharacterDelimitedTextLineReader extends TextLineReader {
  @Override
  public boolean supports(File inputFile, String clientFileName, Import importDef) {
    if (importDef.columnSeparatorChars.length > 0) {
      BufferedReader in = null;
      try {
        // read up to 10 lines to ensure that this is indeed a text file
        in = new BufferedReader(new FileReader(inputFile));
        for (int i = 0; i < 10 && in.readLine() != null; i++) ;
        in.close();
        return true;
      } catch (Exception e) {
        if (in != null) {
          try {
            in.close();
          } catch (Exception ioe) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
          }
        }
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  protected List<Object> nextLine() {
    try {
      // read columns
      String line = reader.readLine();
      linesRead++;
      if (line == null) {
        return null;
      } else {
        if (separator == 0) {
          // Since it is dangerous to use more than one character as a separator, use the first
          // one appearing in the first line for the file.
          int closestSeparator = Integer.MAX_VALUE;
          char[] separators = importDef.columnSeparatorChars;
          for (char c : separators) {
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
        for (char c : line.toCharArray()) {
          if (c == importDef.columnQuoteChar) {
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
              row.add(column.toString());
              column.delete(0, column.length());
            }
          } else {
            column.append(c);
          }
        }
        if (column.length() > 0) {
          row.add(column.toString());
        }
        return row;
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
  }

  /**
   * The separator character to use for the current file among all the ones specified on the import
   * definition.
   */
  private char separator = 0;

  /**
   * The total number of characters read up to the current line, used to estimate the number of lines in the file.
   */
  private long totalCharactersRead = 0;

  /**
   * The number of lines read up to that point, used to estimate the number of lines in the file.
   */
  private long linesRead = 0;

  /**
   * Message log.
   */
  private static final Logger log = Logger.getLogger(CharacterDelimitedTextLineReader.class.getName());
}