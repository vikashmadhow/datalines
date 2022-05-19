package ma.vi.datalines.text;

import ma.vi.datalines.Import;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A line reader for reading fixed-length text files.
 *
 * @author vikash.madhow@gmail.com
 */
public class FixedLengthTextLineReader extends TextLineReader {
  @Override
  public boolean supports(File inputFile, String clientFileName, Import importDef) {
    if (importDef.startOfColumns.length != 0) {
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
    }
    return false;
  }

  @Override
  public void openFile(File inputFile, String clientFileName, Import importDef) {
    super.openFile(inputFile, clientFileName, importDef);
    startOfColumns = importDef.startOfColumns;
  }

  @Override
  protected List<Object> nextLine() {
    try {
      // read columns
      String line = reader.readLine();
      if (line == null) {
        return null;
      } else {
        if (estimateTotalLines == -1) {
          estimateTotalLines = fileLength / line.length();
        }
        List<Object> row = new ArrayList<>();
        for (int i = 0; i < startOfColumns.length; i++) {
          if (startOfColumns[i] < line.length()) {
            row.add(i < startOfColumns.length - 1 && startOfColumns[i + 1] < line.length() ?
                    line.substring(startOfColumns[i], startOfColumns[i + 1]) :
                    line.substring(startOfColumns[i]));
          }
        }
        return row;
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
  }

  /**
   * The index of each column in a line.
   */
  private int[] startOfColumns;
}