package dev.aulait.csvloader.core.application;

import static org.junit.jupiter.api.Assertions.*;

import dev.aulait.csvloader.core.infra.CsvLoaderTestBase;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class CsvLoaderDateTimeParserTests extends CsvLoaderTestBase {

  @Test
  void dataTimeFormatTests() throws IOException, SQLException {
    loader.load(this, connection, log);

    String selectFromOrder =
        "SELECT * FROM \"ORDER\""
            .replace("\"", connection.getMetaData().getIdentifierQuoteString());
    ResultSet rs = connection.createStatement().executeQuery(selectFromOrder);

    assertTrue(rs.next());
    assertEquals(1, rs.getInt("FROM"));
    assertEquals("one", rs.getString("COL_VARCHAR"));
    assertEquals("2020-12-31 10:00:00", rs.getString("COL_TIMESTAMP"));
    assertEquals(true, rs.getBoolean("COL_BOOLEAN"));
    assertEquals("00000000-0000-0000-0000-000000000001", rs.getString("COL_UUID"));

    assertTrue(rs.next());
    assertEquals(2, rs.getInt("FROM"));
    assertEquals("two", rs.getString("COL_VARCHAR"));
    assertEquals("2021-01-01 00:00:00", rs.getString("COL_TIMESTAMP"));
    assertEquals(true, rs.getBoolean("COL_BOOLEAN"));
    assertEquals("00000000-0000-0000-0000-000000000002", rs.getString("COL_UUID"));

    assertTrue(rs.next());
    assertEquals(3, rs.getInt("FROM"));
    assertEquals("three", rs.getString("COL_VARCHAR"));
    assertEquals("2021-01-02 00:00:00", rs.getString("COL_TIMESTAMP"));
    assertEquals(true, rs.getBoolean("COL_BOOLEAN"));
    assertEquals("00000000-0000-0000-0000-000000000003", rs.getString("COL_UUID"));

    assertFalse(rs.next());
  }

  private static class InvalidDateTimeTests {}

  @Test
  void invalidDateTimeFormatTests() throws IOException, SQLException {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> loader.load(new InvalidDateTimeTests(), connection, log));
    assertEquals("Invalid timestamp: not-a-date", ex.getMessage());
  }
}
