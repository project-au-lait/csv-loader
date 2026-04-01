package dev.aulait.csvloader.core.domain.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ValueConverter {

  private static final List<Function<String, LocalDateTime>> PARSERS =
      List.of(
          v -> LocalDateTime.parse(v, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
          v -> LocalDateTime.parse(v, DateTimeFormatter.ISO_DATE),
          v -> LocalDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
          v -> LocalDate.parse(v).atStartOfDay());

  public static LocalDateTime toTimestamp(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    return PARSERS.stream()
        .map(parser -> tryConvert(parser, value.trim()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid timestamp: " + value));
  }

  private static <T> Optional<T> tryConvert(Function<String, T> parser, String value) {
    try {
      return Optional.of(parser.apply(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
