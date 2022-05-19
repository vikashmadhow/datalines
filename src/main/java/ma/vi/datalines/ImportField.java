package ma.vi.datalines;

import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record ImportField(String name,
                          String type,
                          String defaultValue,
                          int    columnIndex,
                          Map<String, String> attributes) {}
