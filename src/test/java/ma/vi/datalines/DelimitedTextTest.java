package ma.vi.datalines;

import ma.vi.base.util.Convert;
import ma.vi.datalines.text.DelimitedTextLineReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DelimitedTextTest {
  @Test
  public void readDelimitedTextUnstructured() throws Exception {
    try (DelimitedTextLineReader r = new DelimitedTextLineReader()) {
      r.open(new File(DelimitedTextTest.class.getResource("/data/test_data.csv").toURI()),
                 "test_data.csv", Structure.TabSeparated());

      assertEquals(Arrays.asList("1",	  "Vikash Madhow",     "1234567",	    "59, Avenue Telfair, Quatre Bornes","17 05 1977"),  r.next());
      assertEquals(Arrays.asList("2",	  "Avish Madhow",      "12844-4343",	"59, Avenue Telfair, Quatre Bornes","1 02 2001"),   r.next());
      assertEquals(Arrays.asList("3",	  "Hemisha Madhow",    "323232",	    "59, Avenue Telfair, Quatre Bornes","11 08 2002"),  r.next());
      assertEquals(Arrays.asList("4",	  "Ashveena Madhow",   "3232",	      "59, Avenue Telfair, Quatre Bornes","22 08 1978"),  r.next());
      assertEquals(Arrays.asList("5",	  "Light Madhow",      "21212121",	  "59, Avenue Telfair, Quatre Bornes","1 01 2010"),   r.next());
      assertEquals(Arrays.asList("6",	  "Kimi Madhow",       "1212121",     "59, Avenue Telfair, Quatre Bornes","15 09 2018"),  r.next());
      assertEquals(Arrays.asList("7",	  "Toffee Madhow",     "1212121",     "59, Avenue Telfair, Quatre Bornes","1 12 2018"),   r.next());
      assertEquals(Arrays.asList("8",	  "Misha Madhow",      "1212121",     "59, Avenue Telfair, Quatre Bornes","5 09 2019"),   r.next());
      assertEquals(Arrays.asList("9",	  "Mika Madhow",       "1212121",     "59, Avenue Telfair, Quatre Bornes","5 09 2019"),   r.next());
      assertEquals(Arrays.asList("10",	"Swiss Roll Madhow", "1212121",     "59, Avenue Telfair, Quatre Bornes","5 09 2019"),   r.next());
      assertEquals(Arrays.asList("11",	"Sinead Madhow",     "121-2121",	  "59, Avenue Telfair, Quatre Bornes","10 05 2019"),  r.next());
      assertEquals(Arrays.asList("12",	"Bono Madhow",       "121-2122",	  "59, Avenue Telfair, Quatre Bornes","10 05 2019"),  r.next());
      assertEquals(Arrays.asList("13",	"Doloress Madhow",   "121-2123",	  "59, Avenue Telfair, Quatre Bornes","1 01 2018"),   r.next());
      assertEquals(Arrays.asList("14",	"Pepper Madhow",     "121-2124",	  "59, Avenue Telfair, Quatre Bornes","1 03 2018"),   r.next());
      assertEquals(Arrays.asList("15",	"Oreo Madhow",       "121-2125",	  "59, Avenue Telfair, Quatre Bornes","5 12 2021"),   r.next());
      assertEquals(Arrays.asList("16",	"Solero Madhow",     "121-2126",	  "59, Avenue Telfair, Quatre Bornes","5 10 2021"),   r.next());
      assertFalse(r.hasNext());
    }
  }

  @Test
  public void readDelimitedTextStructured() throws Exception {
    try (DelimitedTextLineReader r = new DelimitedTextLineReader()) {
      r.open(new File(DelimitedTextTest.class.getResource("/data/test_data.csv").toURI()),
             "test_data.csv",
             Structure.newBuilder()
                      .columnSeparatorChars('\t')
                      .column(new Column("id",      "long"))
                      .column(new Column("name" ,   "string"))
                      .column(new Column("phone",   "string"))
                      .column(new Column("address", "string"))
                      .column(new Column("dob",     "date"))
                      .build());

      assertEquals(Arrays.asList(1L,  "Vikash Madhow",           "1234567",   "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("17 05 1977")),  r.next());
      assertEquals(Arrays.asList(2L,	"Avish Madhow",           "12844-4343",	"59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 02 2001")),   r.next());
      assertEquals(Arrays.asList(3L,	"Hemisha Madhow",         "323232",	    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("11 08 2002")),  r.next());
      assertEquals(Arrays.asList(4L,	"Ashveena Madhow",        "3232",	      "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("22 08 1978")),  r.next());
      assertEquals(Arrays.asList(5L,	"Light Madhow",           "21212121",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 01 2010")),   r.next());
      assertEquals(Arrays.asList(6L,	"Kimi Madhow",            "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("15 09 2018")),  r.next());
      assertEquals(Arrays.asList(7L,	"Toffee Madhow",          "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 12 2018")),   r.next());
      assertEquals(Arrays.asList(8L,	"Misha Madhow",           "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 09 2019")),   r.next());
      assertEquals(Arrays.asList(9L,	"Mika Madhow",            "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 09 2019")),   r.next());
      assertEquals(Arrays.asList(10L,	"Swiss Roll Madhow",      "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 09 2019")),   r.next());
      assertEquals(Arrays.asList(11L,	"Sinead Madhow",          "121-2121",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("10 05 2019")),  r.next());
      assertEquals(Arrays.asList(12L,	"Bono Madhow",            "121-2122",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("10 05 2019")),  r.next());
      assertEquals(Arrays.asList(13L,	"Doloress Madhow",        "121-2123",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 01 2018")),   r.next());
      assertEquals(Arrays.asList(14L,	"Pepper Madhow",          "121-2124",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 03 2018")),   r.next());
      assertEquals(Arrays.asList(15L,	"Oreo Madhow",            "121-2125",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 12 2021")),   r.next());
      assertEquals(Arrays.asList(16L,	"Solero Madhow",          "121-2126",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 10 2021")),   r.next());
      assertFalse(r.hasNext());
    }
  }

  @Test
  public void readDelimitedTextStructuredWithErrors() throws Exception {
    try (DelimitedTextLineReader r = new DelimitedTextLineReader()) {
      r.open(new File(DelimitedTextTest.class.getResource("/data/test_data_err.csv").toURI()),
             "test_data.csv",
             Structure.newBuilder()
                      .columnSeparatorChars('\t')
                      .column(new Column("id",      "long"))
                      .column(new Column("name" ,   "string"))
                      .column(new Column("phone",   "string"))
                      .column(new Column("address", "string"))
                      .column(new Column("dob",     "date"))
                      .build());

      assertEquals(Arrays.asList(1L,  "Vikash Madhow",           "1234567",   "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("17 05 1977")),  r.next());
      assertEquals(Arrays.asList(2L,	"Avish Madhow",           "12844-4343",	"59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 02 2001")),   r.next());
      assertEquals(Arrays.asList(3L,	"Hemisha Madhow",         "323232",	    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("11 08 2002")),  r.next());
      assertEquals(Arrays.asList(4L,	"Ashveena Madhow",        "3232",	      "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("22 08 1978")),  r.next());
      assertEquals(Arrays.asList(5L,	"Light Madhow",           "21212121",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 01 2010")),   r.next());
      assertEquals(Arrays.asList(6L,	"Kimi Madhow",            "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("15 09 2018")),  r.next());
      assertEquals(Arrays.asList(7L,	"Toffee Madhow",          "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 12 2018")),   r.next());
      assertEquals(Arrays.asList(8L,	"Misha Madhow",           "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 09 2019")),   r.next());
      assertEquals(Arrays.asList(9L,	"Mika Madhow",            "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 09 2019")),   r.next());
      assertEquals(Arrays.asList("##","Swiss Roll Madhow",      "1212121",    "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 09 2019")),   r.next());
      assertEquals(Arrays.asList(11L,	"Sinead Madhow",          "121-2121",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("10 05 2019")),  r.next());
      assertEquals(Arrays.asList(12L,	"Bono Madhow",            "121-2122",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("10 05 2019")),  r.next());
      assertEquals(Arrays.asList(13L,	"Doloress Madhow",        "121-2123",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 01 2018")),   r.next());
      assertEquals(Arrays.asList(14L,	"Pepper Madhow",          "121-2124",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("1 03 2018")),   r.next());
      assertEquals(Arrays.asList(15L,	"Oreo Madhow",            "121-2125",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 12 2021")),   r.next());
      assertEquals(Arrays.asList(16L,	"Solero Madhow",          "121-2126",	  "59, Avenue Telfair, Quatre Bornes", Convert.convertDate("5 10 2021")),   r.next());
      assertFalse(r.hasNext());
    }
  }
}