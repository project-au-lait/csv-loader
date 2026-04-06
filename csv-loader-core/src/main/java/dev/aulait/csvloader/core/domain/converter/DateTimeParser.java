package dev.aulait.csvloader.core.domain.converter;

import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class DateTimeParser {

  private static final String SEPARATOR_SLASH = "/";
  private static final String SEPARATOR_HYPHEN = "-";

  public static final String CUSTOM_FORMAT_PROPERTY = "dev.aulait.csvloader.timestamp.format";

  private static volatile List<Function<String, LocalDateTime>> PARSERS;

  static {
    reload();
  }

  @VisibleForTesting
  public static void reload() {
    List<Function<String, LocalDateTime>> parsers = new ArrayList<>();
    parsers.add(v -> LocalDateTime.parse(v, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    parsers.add(v -> LocalDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    parsers.add(v -> LocalDate.parse(v, DateTimeFormatter.ISO_DATE).atStartOfDay());
    parsers.add(v -> LocalDate.parse(v).atStartOfDay());

    String customFormat = System.getProperty(CUSTOM_FORMAT_PROPERTY);
    if (customFormat != null && !customFormat.isBlank()) {
      DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(customFormat);
      parsers.add(v -> LocalDateTime.parse(v, customFormatter));
    }

    PARSERS = List.copyOf(parsers);
  }

  public static LocalDateTime toTimestamp(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    String sanitizedValue = value.replace(SEPARATOR_SLASH, SEPARATOR_HYPHEN).trim();

    for (Function<String, LocalDateTime> parser : PARSERS) {
      Optional<LocalDateTime> result = tryParse(parser, sanitizedValue);
      if (result.isPresent()) {
        moveToFront(parser);
        return result.get();
      }
    }

    throw new IllegalArgumentException("Invalid timestamp: " + value);
  }

  private static <T> Optional<T> tryParse(Function<String, T> parser, String value) {
    try {
      return Optional.of(parser.apply(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private static void moveToFront(Function<String, LocalDateTime> parser) {
    if (PARSERS.get(0) == parser) {
      return;
    }
    List<Function<String, LocalDateTime>> reordered = new ArrayList<>(PARSERS);
    reordered.remove(parser);
    reordered.add(0, parser);
    PARSERS = List.copyOf(reordered);
  }
}
