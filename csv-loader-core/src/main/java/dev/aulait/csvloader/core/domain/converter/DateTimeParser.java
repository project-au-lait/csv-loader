package dev.aulait.csvloader.core.domain.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

public class DateTimeParser {

  private static final List<DateTimeFormatter> FORMATTERS =
      List.of(
          DateTimeFormatter.ISO_LOCAL_DATE_TIME,
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
          new DateTimeFormatterBuilder()
              .appendPattern("yyyy-MM-dd")
              .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
              .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
              .toFormatter());

  private static DateTimeFormatter detectedFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public static LocalDateTime toTimestamp(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    String trimmedValue = value.trim();

    try {
      return LocalDateTime.parse(trimmedValue, detectedFormatter);
    } catch (Exception e) {
      return detectAndParse(trimmedValue);
    }
  }

  private static LocalDateTime detectAndParse(String value) {
    for (DateTimeFormatter formatter : FORMATTERS) {
      try {
        LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
        detectedFormatter = formatter;
        return dateTime;
      } catch (Exception e) {
        /* If the format doesn't match, try the following formatter. */
      }
    }

    throw new IllegalArgumentException("Invalid timestamp: " + value);
  }
}
