package ma.vi.datalines;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;

/**
 * Holds information on the structure of a file to import.
 *
 * @author vikash.madhow@gmail.com
 */
public class Import {

  public Import(int headerLines, int footerLines, boolean skipBlankLines,
                int[] startOfColumns, char[] columnSeparatorChars, char columnQuoteChar,
                boolean applyFormatting, boolean loadAllSheets, int sheet) {
    this.headerLines = headerLines;
    this.footerLines = footerLines;
    this.skipBlankLines = skipBlankLines;
    this.startOfColumns = startOfColumns;
    this.columnSeparatorChars = columnSeparatorChars;
    this.columnQuoteChar = columnQuoteChar;
    this.applyFormatting = applyFormatting;
    this.loadAllSheets = loadAllSheets;
    this.sheet = sheet;
  }

  public Import(int headerLines, int footerLines, boolean skipBlankLines,
                String startOfColumns, char[] columnSeparatorChars, char columnQuoteChar,
                boolean applyFormatting, boolean loadAllSheets, int sheet) {
    this(headerLines,
        footerLines,
        skipBlankLines,
        startOfColumns == null
        ? new int[0]
        : Stream.of(startOfColumns.split(","))
            .mapToInt(c -> parseInt(c.trim()))
            .toArray(),
        columnSeparatorChars,
        columnQuoteChar,
        applyFormatting,
        loadAllSheets,
        sheet);
  }

  public static final Import DEFAULT = new Import(
      1, 0, true, new int[0],
      new char[]{','}, '"', false,
      false, 1);

  /**
   * Number of header lines to ignore in the file. Default is 1.
   */
  public final int headerLines;

  /**
   * Number of footer lines to ignore in the source file. Default is none.
   */
  public final int footerLines;

  /**
   * Whether to skip and ignore blank lines. Default is true.
   */
  public final boolean skipBlankLines;

  /**
   * For importing fixed-length text files, this defines the start character position of
   * each column. Then, the first column in the file starts at startOfColumns[0] and goes
   * up to startOfColumns[1] - 1 or the end of the line if there are no more columns.
   */
  public final int[] startOfColumns;

  /**
   * Characters separating columns in the input file. This is only applicable for
   * character-separated files such as CSV. This is set to comma (,) by default.
   */
  public final char[] columnSeparatorChars;

  /**
   * The character used to quote columns in character-separated files. This is set
   * to the double-quote("), by default.
   */
  public final char columnQuoteChar;

  /**
   * For imported files supporting data formatting, such as Excel, the respective formats
   * are applied to the loaded content if this is set to true. Otherwise the raw data is
   * returned. For importing purposes raw data is preferred as it is easier for programs
   * to work with and that is why this setting defaults to false.
   */
  public final boolean applyFormatting;

  /**
   * For imported files supporting multiple sheets, such as Excel, data from all sheets
   * are loaded (one after the other) if this is set to true. By default, this is false
   * meaning that data from the first sheet only is loaded.
   */
  public final boolean loadAllSheets;

  /**
   * For imported files supporting multiple sheets, such as Excel, only data from the
   * specified sheet number is loaded. The sheet number corresponds to the order of the
   * sheets in the file with one corresponding to the first one (default). This setting
   * is ignored if {@link #loadAllSheets} is set to true.
   */
  public final int sheet;

  /**
   * The fields of the import.
   */
  public final Map<String, ImportField> fields = new HashMap<>();
}