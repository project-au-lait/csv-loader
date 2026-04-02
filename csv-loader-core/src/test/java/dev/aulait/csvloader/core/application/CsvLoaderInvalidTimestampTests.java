package dev.aulait.csvloader.core.application;

import static org.junit.jupiter.api.Assertions.*;

import dev.aulait.csvloader.core.infra.CsvLoaderTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CsvLoaderInvalidTimestampTests extends CsvLoaderTestBase {

  @ParameterizedTest
  @CsvSource({
    "Invalid timestamp: not-a-date",
  })
  void invalidTimestampTest(String expectedErrorMsg) throws Exception {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> loadInputCsv());
    assertEquals(expectedErrorMsg, ex.getMessage());
  }
}
