package ma.vi.datalines.text;

import ma.vi.base.util.Convert;
import ma.vi.datalines.Column;
import ma.vi.datalines.Structure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * A line reader for reading fixed-length text files.
 *
 * @author vikash.madhow@gmail.com
 */
public class FixedLengthTextLineReader extends TextLineReader {
  @Override
  public boolean supports(File      inputFile,
                          String    fileName,
                          Structure structure) {
    return structure != null
        && structure.columns().stream()
                    .anyMatch(c -> c.location().startsWith("["))
        && hasTextContent(inputFile);
  }

  @Override
  public void openFile(File      inputFile,
                       String    fileName,
                       Structure structure) {
    super.openFile(inputFile, fileName, structure);
    columnLocations = structure.columns().stream()
                               .filter(c -> c.location().startsWith("["))
                               .map(c -> c.location().substring(1, c.location().length()- 1).split("-"))
                               .map(l -> l.length >= 2
                                       ? new ColumnLocation(parseInt(l[0].trim()), parseInt(l[1].trim()))
                                       : new ColumnLocation(parseInt(l[0].trim()), -1))
                               .sorted()
                               .toList();
  }

  @Override
  protected List<Object> nextLine(boolean convertToColumnType) {
    try {
      String line = reader.readLine();
      if (line == null) {
        return null;
      } else {
        if (estimateTotalLines == -1) {
          estimateTotalLines = fileLength / line.length();
        }
        /*
         * Read columns.
         */
        List<Object> row = new ArrayList<>();
        for (ColumnLocation loc: columnLocations) {
          if (loc.start <= line.length()) {
            Object value = loc.end == -1 || loc.end > line.length()
                         ? line.substring(loc.start - 1)
                         : line.substring(loc.start - 1, loc.end);
            if (convertToColumnType) {
              Column col = columnByLocations.get(loc.toString());
              value = DelimitedTextLineReader.convertValue(value, col);
            }
            row.add(value);
          }
        }
        return row;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * A location in a line of text.
   *
   * @param start Starting column in line (1-based).
   * @param end Ending column in line (1-based). If set to -1, the column goes
   *            to the end of the line.
   */
  record ColumnLocation(int start, int end) implements Comparable<ColumnLocation> {
    @Override
    public int compareTo(ColumnLocation o) {
      return start - o.start;
    }
  }

  /**
   * The locations of each column in a line.
   */
  private List<ColumnLocation> columnLocations;
}