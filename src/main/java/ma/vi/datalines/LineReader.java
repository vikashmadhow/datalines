package ma.vi.datalines;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

/**
 * A line reader reads data from a file sequentially and returns the data as an
 * iterable list of lines, with each line being a sequence of objects and each
 * corresponding to a column in the line. Different lines may have different
 * number of columns.
 * <p>
 * Use {@link LineReaderFactory} to get an appropriate instance to read from a
 * file.
 *
 * @author vikash.madhow@gmail.com
 */
public interface LineReader extends Iterable<List<Object>>,
                                    Iterator<List<Object>>,
                                    AutoCloseable {
  /**
   * Returns true if this reader can read the input file.
   *
   * @param inputFile The file to read.
   * @param name File name.
   * @param structure The structure describing the content of the input file.
   */
  boolean supports(File      inputFile,
                   String    name,
                   Structure structure);

  /**
   * Opens the file for reading. This method is called only if the supports method
   * of the class returned true.
   *
   * If the structure is not provided, an attempt will be made to obtain it from
   * the file itself. This is possible for files such as Excel which have a header
   * line. The structure obtained may contain errors.
   *
   * @param inputFile The file to read.
   * @param fileName File name.
   * @param structure The structure describing the content of the input file.
   */
  default void open(File inputFile, String fileName, Structure structure) {
    try {
      open(new FileInputStream(inputFile), fileName, structure);
    } catch(IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  default void open(InputStream input, String fileName, Structure structure) {
    try {
      int pos = fileName.lastIndexOf('.');
      String prefix = pos == -1 ? fileName : fileName.substring(0, pos);
      String suffix = pos == -1 ? "tmp" : fileName.substring(pos + 1);
      File file = File.createTempFile(prefix, suffix);
      Files.copy(input, file.toPath());
    } catch(IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

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