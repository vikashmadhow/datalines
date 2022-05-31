package ma.vi.datalines.html;

import ma.vi.datalines.AbstractLineReader;
import ma.vi.datalines.Format;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A line reader which can read data from an HTML table.
 *
 * @author vikash.madhow@gmail.com
 */
public class HtmlTableLineReader extends AbstractLineReader {
  @Override
  public boolean supports(File inputFile, String name, Format format) {
    try (BufferedReader in = new BufferedReader(new FileReader(inputFile))) {
      String line;
      while ((line = in.readLine()) != null && line.trim().length() == 0);
      if (line != null) {
        line = line.trim().toLowerCase().replace(" ", "");
        return line.startsWith("<!doctypehtml>") || line.startsWith("<html");
      } else {
        return false;
      }
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  public void openFile(File inputFile, String fileName, Format format) {
    try {
      Document doc = Jsoup.parse(inputFile, "UTF-8");
      Elements tableRows = doc.select("tr");
      numberOfRows = tableRows.size();
      rows = tableRows.iterator();
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not parse HTML file '" + fileName + "'. Reason: " + e, e);
    }
  }

  @Override
  protected List<Object> nextLine(boolean convertToColumnType) {
    if (rows.hasNext()) {
      List<Object> row = new ArrayList<>();
      Elements cells = rows.next().children();
      for (Element cell : cells) {
        Elements children = cell.children();
        if (children.size() > 0) {
          Element first = children.first();
          if (first.hasAttr("href")) {
            row.add(first.absUrl("href"));
          } else {
            row.add(cell.text());
          }
        } else if (cell.childNodes().size() > 0) {
          row.add(cell.text());
        } else {
          row.add(null);
        }
      }
      return row;
    } else {
      return null;
    }
  }

  @Override
  public long estimateTotalLines() {
    return numberOfRows;
  }

  @Override
  public void close() {
    super.close();
    rows = null;
  }

  private Iterator<Element> rows;

  private long numberOfRows;
}