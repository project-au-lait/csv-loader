package dev.aulait.csvloader.core.application;

import dev.aulait.csvloader.core.infra.CsvLoaderTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CsvLoaderDataVariationTests extends CsvLoaderTestBase {

  @ParameterizedTest
  @CsvSource({"ORDER_expected_variation"})
  void variationTest(String expectedResource) throws Exception {
    loadInputCsv();
    assertExpected(expectedResource);
  }
}
