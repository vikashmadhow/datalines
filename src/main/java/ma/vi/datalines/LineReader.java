package ma.vi.datalines;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * A line reader reads data from a file sequentially and returns the data
 * as an iterable list of lines, with each line consisting of a set of objects,
 * each corresponding to a column in the line. Different lines may have
 * different number of columns.
 * <p>
 * Use {@link LineReaderFactory} to get an appropriate instance to read from a file.
 *
 * @author vikash.madhow@gmail.com
 */
public interface LineReader extends Iterable<List<Object>>, Iterator<List<Object>>, AutoCloseable {
  /**
   * Returns true if this reader can read the input file.
   *
   * @param inputFile      The file to read.
   * @param clientFileName The file name of the file.
   * @param importDef      An import definition for reading the file.
   */
  boolean supports(File inputFile, String clientFileName, Import importDef);

  /**
   * Opens the file for reading. This method is called only if the supports method
   * of the class returned true.
   *
   * @param inputFile      The file to read.
   * @param clientFileName File name.
   * @param importDef      An import definition for reading the file.
   */
  void open(File inputFile, String clientFileName, Import importDef);

  /**
   * Returns an estimate of the total lines available from the reader, if available.
   * Otherwise returns -1. This is an estimate and may be inaccurate; it should not
   * be depended upon for correctness.
   * <p>
   * The estimates may change as the reader reads more lines. It will start normally at
   * -1 (unknown) and get better as more lines are read.
   */
  long estimateTotalLines();

  /**
   * Closes the reader.
   */
  void close();
}