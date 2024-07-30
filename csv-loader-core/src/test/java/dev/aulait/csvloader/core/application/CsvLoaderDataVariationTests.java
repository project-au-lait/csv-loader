package dev.aulait.csvloader.core.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.aulait.csvloader.core.infra.CsvLoaderTestBase;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class CsvLoaderDataVariationTests extends CsvLoaderTestBase {

  @Test
  void test() throws IOException, SQLException {
    loader.load(this, connection, log);

    String selectFromOrder =
        "SELECT * FROM \"ORDER\""
            .replace("\"", connection.getMetaData().getIdentifierQuoteString());
    ResultSet rs = connection.createStatement().executeQuery(selectFromOrder);

    assertTrue(rs.next());

    assertEquals(1, rs.getInt("FROM"));
    assertEquals("one", rs.getString("COL_VARCHAR"));
    assertEquals("2020-12-29", rs.getString("COL_DATE"));
    assertEquals("12:30:00", rs.getString("COL_TIME"));
    assertEquals(true, rs.getBoolean("COL_BOOLEAN"));

    assertTrue(rs.next());

    assertEquals(2, rs.getInt("FROM"));
    assertEquals("", rs.getString("COL_VARCHAR"));
    assertEquals("2020-12-30", rs.getString("COL_DATE"));

    assertTrue(rs.next());

    assertEquals(null, rs.getObject("FROM"));
    assertEquals(null, rs.getObject("COL_DECIMAL"));
    assertEquals(null, rs.getObject("COL_VARCHAR"));
    assertEquals(null, rs.getObject("COL_DATE"));
    assertEquals(null, rs.getObject("COL_TIMESTAMP"));
    assertEquals(null, rs.getObject("COL_TIME"));
    assertEquals(null, rs.getObject("COL_JSON"));
    assertEquals(null, rs.getObject("COL_BOOLEAN"));

    assertFalse(rs.next());
  }
}
