package ma.vi.datalines;

import ma.vi.base.util.Convert;
import ma.vi.datalines.xl.XlsLineReader;
import ma.vi.datalines.xl.XlsxLineReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import static ma.vi.datalines.DelimitedTextTest.asMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class XlsTest {
   @Test
  public void readXlsxStructuredWithErrors() throws Exception {
    try (XlsLineReader r = new XlsLineReader()) {
      r.open(new File(XlsTest.class.getResource("/data/test_data_err.xls").toURI()),
             "test_data_err.xls",
             Format.newBuilder()
                   .columnSeparatorChars('\t')
                   .column(new Column("id",      "long"))
                   .column(new Column("name" ,   "string"))
                   .column(new Column("phone",   "string"))
                   .column(new Column("address", "string"))
                   .column(new Column("dob",     "date"))
                   .build());

      assertEquals(asMap(Arrays.asList(1L,  "Vikash Madhow",     "1234567",    "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("17 05 1977"), LocalTime.of(0, 0, 0, 0)))), r.next());
      assertEquals(asMap(Arrays.asList(2L,	"Avish Madhow",      "12844-4343", "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("1 02 2001"),  LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(3L,	"Hemisha Madhow",    "323232",	   "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("11 08 2002"), LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(4L,	"Ashveena Madhow",   "3232",	     "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("22 08 1978"), LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(5L,	"Light Madhow",      "21212121",	 "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("1 01 2010"),  LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(6L,	"Kimi Madhow",       "1212121",    "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("15 09 2018"), LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(7L,	"Toffee Madhow",     "1212121",    "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("1 12 2018"),  LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(8L,	"Misha Madhow",      "1212121",    "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("5 09 2019"),  LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(9L,	"Mika Madhow",       "1212121",    "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("5 09 2019"),  LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList("##","Swiss Roll Madhow", "1212121",    "59, Avenue Telfair, Quatre Bornes",                     "31 09 2019" )),  r.next());
      assertEquals(asMap(Arrays.asList(11L,	"Sinead Madhow",     "121-2121",	 "59, Avenue Telfair, Quatre Bornes",                     "10 13 2019" )),  r.next());
      assertEquals(asMap(Arrays.asList(12L,	"Bono Madhow",       "121-2122",	 "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("10 05 2019"), LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(13L,	"Doloress Madhow",   "121-2123",	 "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("1 01 2018" ), LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(14L,	"Pepper Madhow",     "121-2124",	 "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("1 03 2018" ), LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(15L,	"Oreo Madhow",       "121-2125",	 "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("5 12 2021" ), LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertEquals(asMap(Arrays.asList(16L,	"Solero Madhow",     "121-2126",	 "59, Avenue Telfair, Quatre Bornes", LocalDateTime.of(Convert.convertDate("5 10 2021" ), LocalTime.of(0, 0, 0, 0)))),  r.next());
      assertFalse(r.hasNext());
    }
  }
}