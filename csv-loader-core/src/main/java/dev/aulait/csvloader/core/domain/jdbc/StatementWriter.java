package dev.aulait.csvloader.core.domain.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class StatementWriter {
  private static final String STRING_TO_CONVERT_TO_NULL = "[null]";

  public void write(
      Connection connection, String statement, CSVParser csvParser, TableMetaData metaData)
      throws SQLException {
    try (PreparedStatement pstmt = connection.prepareStatement(statement)) {

      Iterator<CSVRecord> itr = csvParser.iterator();

      while (itr.hasNext()) {

        CSVRecord csvRecord = itr.next();
        int i = 1;

        for (String columnName : csvParser.getHeaderNames()) {
          String cellValue = csvRecord.get(columnName);
          int columnIndex = i++;

          setParam(pstmt, columnIndex, columnName, connection, cellValue, metaData);
        }
        pstmt.addBatch();
      }
      pstmt.executeBatch();
    }
  }

  void setParam(
      PreparedStatement pstmt,
      int columnIndex,
      String columnName,
      Connection connection,
      String cellValue,
      TableMetaData metaData)
      throws SQLException {

    int dataType = metaData.getDataType(columnName);

    if (STRING_TO_CONVERT_TO_NULL.equals(cellValue)) {
      pstmt.setNull(columnIndex, dataType);
      return;
    }

    switch (dataType) {
      case Types.SMALLINT:
      case Types.INTEGER:
      case Types.BIGINT:
        pstmt.setLong(columnIndex, Long.parseLong(cellValue));
        break;
      case Types.NUMERIC:
      case Types.DECIMAL:
        pstmt.setBigDecimal(columnIndex, new BigDecimal(cellValue));
        break;
      case Types.DOUBLE:
      case Types.FLOAT:
        pstmt.setDouble(columnIndex, Double.parseDouble(cellValue));
        break;
      case Types.DATE:
        pstmt.setDate(columnIndex, Date.valueOf(LocalDate.parse(cellValue)));
        break;
      case Types.TIMESTAMP:
        pstmt.setTimestamp(columnIndex, Timestamp.valueOf(LocalDateTime.parse(cellValue)));
        break;
      case Types.TIME:
        pstmt.setTime(columnIndex, Time.valueOf(LocalTime.parse(cellValue)));
        break;
      case Types.BINARY:
        pstmt.setBytes(columnIndex, cellValue.getBytes());
        break;
      case Types.BOOLEAN:
      case Types.BIT:
        pstmt.setBoolean(columnIndex, Boolean.valueOf(cellValue));
        break;
      case Types.OTHER:
        if (isPgJsonColumn(
            connection.getMetaData().getDatabaseProductName(), metaData.getTypeName(columnName))) {
          pstmt.setObject(columnIndex, cellValue, Types.OTHER);
        } else {
          pstmt.setString(columnIndex, cellValue);
        }
        break;
      default:
        pstmt.setString(columnIndex, cellValue);
    }
  }

  boolean isPgJsonColumn(String databaseName, String columnTypeName) {
    return ("PostgreSQL".equalsIgnoreCase(databaseName)
        && ("json".equalsIgnoreCase(columnTypeName) || "jsonb".equalsIgnoreCase(columnTypeName)));
  }
}
