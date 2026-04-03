package dev.aulait.csvloader.core.application;

import static org.junit.jupiter.api.Assertions.*;

import dev.aulait.csvloader.core.domain.converter.DateTimeParser;
import dev.aulait.csvloader.core.infra.CsvLoaderTestBase;
import java.time.LocalDateTime;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class DateTimeParserTests extends CsvLoaderTestBase {

  LocalDateTime expectedTimeSlot = LocalDateTime.of(2020, 12, 31, 10, 0, 0);
  LocalDateTime expectedNoTime = LocalDateTime.of(2020, 12, 31, 00, 0, 0);

  @ParameterizedTest
  @CsvSource({"2020-12-31T10:00:00", "2020/12/31 10:00:00", "2020-12-31", "2020/12/31"})
  void successTest(String input) {
    LocalDateTime actualDateTime = DateTimeParser.toTimestamp(input);

    if (input.contains("T") || input.contains(" ")) {
      assertEquals(expectedTimeSlot, actualDateTime);
    } else {
      assertEquals(expectedNoTime, actualDateTime);
    }
  }

  @ParameterizedTest
  @CsvSource({"not-a-date"})
  void failureTest(String input) {
    assertThrows(IllegalArgumentException.class, () -> DateTimeParser.toTimestamp(input));
  }
}
