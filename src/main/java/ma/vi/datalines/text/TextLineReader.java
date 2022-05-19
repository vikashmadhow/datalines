package ma.vi.datalines.text;

import ma.vi.datalines.AbstractLineReader;
import ma.vi.datalines.Import;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * An abstract line reader for reading text files.
 *
 * @author vikash.madhow@gmail.com
 */
public abstract class TextLineReader extends AbstractLineReader {
  @Override
  public void openFile(File inputFile, String clientFileName, Import importDef) {
    try {
      fileLength = inputFile.length();
      reader = new BufferedReader(new FileReader(inputFile));
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not open text file '" + clientFileName + "'. Reason: " + e, e);
    }
  }

  /**
   * Close reader.
   */
  @Override
  public void close() {
    super.close();
    try {
      if (reader != null) {
        reader.close();
        reader = null;
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
  }

  @Override
  public long estimateTotalLines() {
    return estimateTotalLines;
  }

  /**
   * The current estimate of the total number of lines in the text file.
   */
  protected long estimateTotalLines = -1;

  /**
   * The length in bytes of the file being read.
   */
  protected long fileLength;

  /**
   * File reader.
   */
  protected BufferedReader reader;
}