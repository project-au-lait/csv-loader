package dev.aulait.csvloader.core.infra;

import dev.aulait.csvloader.core.application.CsvLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;

public class CsvLoaderTestBase {

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
}
