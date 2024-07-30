package dev.aulait.csvloader.core.domain.jdbc;

import dev.aulait.csvloader.core.infra.LogCallback;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MetaDataReader {

  public TableMetaData read(Connection connection, String tableName, LogCallback log)
      throws SQLException {
    TableMetaData metaData = new TableMetaData(tableName);

    try (ResultSet rs =
        connection.getMetaData().getColumns(null, connection.getSchema(), tableName, "%")) {

      while (rs.next()) {
        metaData.addDataType(
            rs.getString("COLUMN_NAME"),
            metaData.new TypeDetail(rs.getInt("DATA_TYPE"), rs.getString("TYPE_NAME")));
      }
    }

    log.info("Extracted " + tableName + " : " + metaData);
    return metaData;
  }
}
