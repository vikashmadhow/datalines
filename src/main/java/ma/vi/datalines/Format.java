package ma.vi.datalines;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Holds information on the structure of a file to import.
 *
 * @param headerLines Number of header lines to ignore in the data. Default is 1.
 * @param footerLines Number of footer lines to ignore in the data. Default is 0.
 * @param ignoreBlankLines Whether to ignore blank lines in the data. Default is true.
 * @param columnSeparatorChars Characters separating columns in the data.This is
 *                             only applicable for character-separated files such
 *                             as CSV. Default is the tab and comma ('\t', ',').
 * @param columnQuoteChar The character used to quote columns in character-separated
 *                        files. Default is the double-quote ('"').
 * @param applyFormatting For files supporting data formatting, such as Excel,
 *                        relevant formats are applied to the content when loading
 *                        if this is set to true. Otherwise, the raw data is loaded.
 *                        For loading purposes raw data is generally preferred as
 *                        it is easier for programs to work with and the default
 *                       is thus set to false.
 * @param page For files supporting multiple pages (or, equivalently, sheets such
 *             as Excel), only data from the specified page number is loaded, or
 *             if the page number is set to 0 or -1, all pages are loaded in order.
 *             The default is to only load the first page (page 1).
 * @param columns The columns in the loaded data. If empty or null, the columns
 *                will be derived from the structure of the data, where possible.
 *
 * @author vikash.madhow@gmail.com
 */
public record Format(int     headerLines,
                     int     footerLines,
                     boolean ignoreBlankLines,
                     char[]  columnSeparatorChars,
                     char    columnQuoteChar,
                     boolean applyFormatting,
                     int     page,
                     List<Column> columns) {
  public Format() {
    this(1, 0, true, DEFAULT_COLUMN_SEP,
         DEFAULT_COLUMN_QUOTE, false, 1, emptyList());
  }

  public static Format TabSeparated() {
    return newBuilder().columnSeparatorChars('\t', ',').build();
  }

  public static Build newBuilder() {
    return new Build();
  }

  public Map<String, Column> columnsMap() {
    return columns == null ? emptyMap()
         : columns.stream().collect(toMap(Column::name, identity(),
                                          (c1, c2) -> c1,
                                          LinkedHashMap::new));
  }

  public List<String> columnNames() {
    return columns == null ? emptyList()
         : columns.stream().map(Column::name).toList();
  }

  public static class Build {
    public Build headerLines(int headerLines) {
      this.headerLines = headerLines;
      return this;
    }

    public Build footerLines(int footerLines) {
      this.footerLines = footerLines;
      return this;
    }

    public Build ignoreBlankLines(boolean ignoreBlankLines) {
      this.ignoreBlankLines = ignoreBlankLines;
      return this;
    }

    public Build columnSeparatorChars(char... columnSeparatorChars) {
      this.columnSeparatorChars = columnSeparatorChars;
      return this;
    }

    public Build columnQuoteChar(char columnQuoteChar) {
      this.columnQuoteChar = columnQuoteChar;
      return this;
    }

    public Build applyFormatting(boolean applyFormatting) {
      this.applyFormatting = applyFormatting;
      return this;
    }

    public Build column(Column col) {
      this.columns.add(col);
      return this;
    }

    public Build page(int page) {
      this.page=page;
      return this;
    }

    public Format build() {
      return new Format(headerLines, footerLines, ignoreBlankLines, columnSeparatorChars,
                        columnQuoteChar, applyFormatting, page, columns);
    }

    private int     headerLines = 1;
    private int     footerLines = 0;
    private boolean ignoreBlankLines = true;
    private char[]  columnSeparatorChars = new char[] {'\t', ','};
    private char    columnQuoteChar = '"';
    private boolean applyFormatting = false;
    private int     page = 1;
    private final List<Column> columns = new ArrayList<>();
  }

  public static char[] DEFAULT_COLUMN_SEP = new char[] {'\t', ','};

  public static char DEFAULT_COLUMN_QUOTE = '"';
}