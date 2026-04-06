package dev.aulait.csvloader.core.application;

import static org.junit.jupiter.api.Assertions.*;

import dev.aulait.csvloader.core.domain.converter.DateTimeParser;
import java.time.LocalDateTime;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class DateTimeParserTests {

  @ParameterizedTest
  @CsvSource({
    "2020-12-31T10:00:00, 2020-12-31T10:00:00",
    "2020-12-31 10:00:00, 2020-12-31T10:00:00",
    "2020-12-31, 2020-12-31T00:00:00"
  })
  void successTest(String input, String expected) {
    assertEquals(LocalDateTime.parse(expected), DateTimeParser.toTimestamp(input));
  }

  @ParameterizedTest
  @CsvSource({"not-a-date"})
  void failureTest(String input) {
    assertThrows(IllegalArgumentException.class, () -> DateTimeParser.toTimestamp(input));
  }
}
