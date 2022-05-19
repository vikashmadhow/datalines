package ma.vi.datalines;

import ma.vi.base.lang.Errors;
import ma.vi.base.lang.NotFoundException;
import ma.vi.datalines.html.HtmlLineReader;
import ma.vi.datalines.text.CharacterDelimitedTextLineReader;
import ma.vi.datalines.text.FixedLengthTextLineReader;
import ma.vi.datalines.xl.XlsLineReader;
import ma.vi.datalines.xl.XlsxLineReader;

import java.io.File;

/**
 * Factory for obtaining line readers to read data from input files.
 *
 * @author vikash.madhow@gmail.com
 */
public class LineReaderFactory {
  /**
   * Returns a line reader initialised to read the input file if one could be found,
   * or throws NotFoundException otherwise.
   *
   * @param inputFile      The file to read.
   * @param clientFileName File name.
   * @param importDef      Import definition for reading the file.
   */
  public static LineReader get(File inputFile, String clientFileName, Import importDef) {
    try {
      for (Class<? extends LineReader> readerClass : readers) {
        LineReader reader = readerClass.getDeclaredConstructor().newInstance();
        if (reader.supports(inputFile, clientFileName, importDef)) {
          reader.open(inputFile, clientFileName, importDef);
          return reader;
        }
      }
    } catch (Exception e) {
      throw Errors.unchecked(e);
    }
    throw new NotFoundException("A line reader which knows how to read '" + clientFileName
                              + "' (" + inputFile + ") could not be found.");
  }

  /**
   * A list of classes of known line readers.
   */
  @SuppressWarnings("unchecked")
  private static final Class<? extends LineReader>[] readers = new Class[] {
    XlsxLineReader.class,
    XlsLineReader.class,
    HtmlLineReader.class,
    FixedLengthTextLineReader.class,
    CharacterDelimitedTextLineReader.class,
  };

  private LineReaderFactory() {}
}