package dev.aulait.csvloader.core.application;

import static org.junit.jupiter.api.Assertions.*;

import dev.aulait.csvloader.core.infra.CsvLoaderTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CsvLoaderDataVariationTests extends CsvLoaderTestBase {

  private static final String ORDER_TABLE = "ORDER";

  @ParameterizedTest
  @CsvSource({"ORDER_input_variation, ORDER_expected_variation"})
  void variationTest(String inputResource, String expectedResource) throws Exception {
    loadInputCsv(ORDER_TABLE, inputResource);
    assertExpected(expectedResource);
  }

  @ParameterizedTest
  @CsvSource({
    "ORDER_input_invalid_timestamp, Invalid timestamp: not-a-date",
  })
  void invalidTimestampTest(String inputResource, String expectedErrorMsg) throws Exception {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class, () -> loadInputCsv(ORDER_TABLE, inputResource));
    assertEquals(expectedErrorMsg, ex.getMessage());
  }
}
