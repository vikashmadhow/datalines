package ma.vi.datalines;

import ma.vi.base.string.Strings;
import org.json.JSONObject;

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

  /**
   * Returns the value of the attribute with the specified name if present in
   * the attributes map, or null otherwise.
   *
   * @param name Name of attribute whose value is to be returned.
   * @return Value associated with the attribute name or null if no such
   *         attribute exists in the attributes map of the column.
   */
  public <X> X attribute(String name) {
    return attribute(name, null);
  }

  /**
   * Returns the value of the attribute with the specified name if present in
   * the attributes map, or the provided default value, otherwise.
   *
   * @param name Name of attribute whose value is to be returned.
   * @param defaultValue Value to return if an attribute with the specified name
   *                     does not exist in the attributes map of the column.
   * @return Value associated with the attribute name or the provided default
   *         value if no such attribute exists in the attributes map of the column.
   */
  public <X> X attribute(String name, X defaultValue) {
    if (attributes != null && attributes.containsKey(name))
      return (X)JSONObject.stringToValue(attributes.get(name));
    return defaultValue;
  }

  /**
   * @return A human-readable label for the column, specified as the `short_label`
   *         or `label` attribute; if these are not provided, the label is derived
   *         by expanding the name of the column.
   */
  public String label() {
    return attributes.containsKey("short_label") ? attribute("short_label")
         : attributes.containsKey("SHORT_LABEL") ? attribute("SHORT_LABEL")
         : attributes.containsKey("label")       ? attribute("label")
         : attributes.containsKey("LABEL")       ? attribute("LABEL")
         : Strings.capFirst(Strings.expandByCase(name()));
  }
}