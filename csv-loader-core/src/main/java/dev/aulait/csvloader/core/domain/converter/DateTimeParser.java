package dev.aulait.csvloader.core.domain.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateTimeParser {

  private static final List<DateTimeFormatter> FORMATTERS =
      List.of(
          DateTimeFormatter.ISO_LOCAL_DATE_TIME,
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
          DateTimeFormatter.ISO_DATE);

  private static DateTimeFormatter cachedFormatter;

  static {
    init();
  }

  static void init() {
    cachedFormatter = null;
  }

  public static LocalDateTime toTimestamp(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    String trimmedValue = value.trim();

    if (cachedFormatter != null) {
      try {
        return parse(trimmedValue, cachedFormatter);
      } catch (Exception e) {
        /* If the formats do not match, the analysis will be performed using all formats. */
      }
    }

    for (DateTimeFormatter formatter : FORMATTERS) {
      try {
        LocalDateTime dateTime = parse(trimmedValue, formatter);
        cachedFormatter = formatter;
        return dateTime;
      } catch (Exception e) {
        /* If the format doesn't match, try the following formatter. */
      }
    }

    throw new IllegalArgumentException("Invalid timestamp: " + value);
  }

  private static LocalDateTime parse(String value, DateTimeFormatter formatter) {
    try {
      return LocalDateTime.parse(value, formatter);
    } catch (Exception e) {
      return LocalDate.parse(value, formatter).atStartOfDay();
    }
  }
}
