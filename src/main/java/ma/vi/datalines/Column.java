package ma.vi.datalines;

import java.util.Collections;
import java.util.Map;

/**
 * A column in the source data.
 *
 * @param name The name of the column. This should be unique identifier within
 *             the structure of the data to load.
 * @param type The type of the values that the column can contain. If not specified
 *             the column can hold any data type.
 * @param location The location of the column in the loaded data: this can be:
 *                 1) a column index starting from 1 (if the columns are indexed
 *                    in source; e.g. in an Excel or CSV file)
 *                 2) a start and end character position in terms of character
 *                    position. E.g. [1-5] identifies the column as starting at
 *                    the 1st character in the line and ends at the 5th character.
 *                 3) a column name, if columns are named in the source data.
 *                 4) null, if locations is derived from the position of columns
 *                    in the source data.
 * @param defaultValue A default value to assign to the column when the column
 *                     does not have a value (blank or null).
 * @param attributes A set of name value-pairs acting as metadata for the column
 *                   and which can be interpreted by the loader to validate and
 *                   transform the column value arbitrarily.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record Column(String name,
                     String type,
                     String location,
                     String defaultValue,
                     Map<String, String> attributes) {

  public Column(String name) {
    this(name, "string");
  }

  public Column(String name, String type) {
    this(name, type, null, null, Collections.emptyMap());
  }
}