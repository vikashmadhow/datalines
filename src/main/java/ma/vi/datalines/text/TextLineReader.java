package ma.vi.datalines.text;

import ma.vi.datalines.AbstractLineReader;
import ma.vi.datalines.Format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * An abstract line reader for reading text files.
 *
 * @author vikash.madhow@gmail.com
 */
public abstract class TextLineReader extends AbstractLineReader {
  public static boolean hasTextContent(File inputFile) {
    try (BufferedReader textIn = new BufferedReader(new FileReader(inputFile))) {
      /*
       * Read up to 10 lines to ensure that this is indeed a text file.
       */
      for (int i = 0; i < 10 && textIn.readLine() != null; i++);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  public void openFile(File inputFile, String fileName, Format format) {
    try {
      fileLength = inputFile.length();
      reader = new BufferedReader(new FileReader(inputFile));
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not open text file '" + fileName + "'. Reason: " + e, e);
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
      }
    } catch(IOException e) {
      throw new RuntimeException(e);
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