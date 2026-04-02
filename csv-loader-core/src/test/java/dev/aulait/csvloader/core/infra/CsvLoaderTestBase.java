package dev.aulait.csvloader.core.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.aulait.csvloader.core.application.CsvLoader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;

public class CsvLoaderTestBase {

  private static final String NULL_MARKER = "[null]";
  private static final String EMPTY_MARKER = "[empty]";
  private static final String TABLE_LIST_FILE = "table-list.txt";
  private static final String ORDER_FILE = "ORDER.csv";

  private final CSVFormat expectedCsvFormat =
      CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();

  protected Connection connection;

  protected CsvLoader loader = new CsvLoader();

  protected LogCallback log = new LogCallbackImpl();

  @BeforeEach
  void init() throws Exception {
    Properties prop = new Properties();
    prop.load(getClass().getResourceAsStream("/connection.properties"));

    connection =
        DriverManager.getConnection(
            prop.getProperty("url"), prop.getProperty("user"), prop.getProperty("password"));

    connection.createStatement().execute("DELETE FROM \"ORDER\"");
  }

  protected void loadInputCsv(String tableName, String inputResource)
      throws IOException, SQLException {
    String resourceName = getClass().getSimpleName() + "/" + inputResource + ".csv";
    URL inputUrl = getClass().getResource(resourceName);

    prepareLoaderResources(tableName, inputUrl);
    loader.load(this, connection, log);
  }

  private void prepareLoaderResources(String tableName, URL inputUrl) throws IOException {
    try {
      URL ownerResourceUrl = getClass().getResource(getClass().getSimpleName() + "/");

      Path ownerResource = Path.of(ownerResourceUrl.toURI());
      Files.writeString(ownerResource.resolve(TABLE_LIST_FILE), tableName, StandardCharsets.UTF_8);
      Files.copy(
          Path.of(inputUrl.toURI()),
          ownerResource.resolve(ORDER_FILE),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (URISyntaxException e) {
      throw new IOException("Failed to prepare loader resources", e);
    }
  }

  protected void assertExpected(String expectedResource) throws IOException, SQLException {
    String resourceName = getClass().getSimpleName() + "/" + expectedResource + ".csv";
    URL expectedUrl = getClass().getResource(resourceName);
    assertNotNull(expectedUrl, "Expected resource not found: " + resourceName);

    String q = connection.getMetaData().getIdentifierQuoteString();
    String sql = "SELECT * FROM " + q + "ORDER" + q;

    try (CSVParser parser =
            CSVParser.parse(expectedUrl, StandardCharsets.UTF_8, expectedCsvFormat);
        ResultSet rs = connection.createStatement().executeQuery(sql)) {

      for (CSVRecord record : parser) {
        assertTrue(rs.next());
        for (String col : parser.getHeaderNames()) {
          String expected = record.get(col);
          if (NULL_MARKER.equals(expected)) {
            assertNull(rs.getObject(col));
          } else if (EMPTY_MARKER.equals(expected)) {
            assertEquals("", rs.getString(col));
          } else if (!expected.isEmpty()) {
            assertEquals(expected, rs.getString(col));
          }
        }
      }
      assertFalse(rs.next());
    }
  }
}
