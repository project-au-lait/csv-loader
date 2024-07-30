package dev.aulait.csvloader.core.domain.jdbc;

import java.util.List;
import java.util.StringJoiner;

public class InsertStatementProcessor {

  public String process(String tableName, List<String> columnNames, String idenfifierQuateString) {

    StringJoiner columns = new StringJoiner(",");
    StringJoiner values = new StringJoiner(",");

    for (String columnName : columnNames) {
      String r = idenfifierQuateString + columnName + idenfifierQuateString;
      columns.add(r);
      values.add("?");
    }

    return String.format(
        "INSERT INTO %1$s%2$s%1$s (%3$s) VALUES (%4$s)",
        idenfifierQuateString, tableName, columns.toString(), values.toString());
  }
}
