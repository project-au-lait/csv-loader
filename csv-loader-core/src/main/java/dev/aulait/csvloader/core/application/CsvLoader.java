package dev.aulait.csvloader.core.application;

import dev.aulait.csvloader.core.domain.jdbc.InsertStatementProcessor;
import dev.aulait.csvloader.core.domain.jdbc.MetaDataReader;
import dev.aulait.csvloader.core.domain.jdbc.StatementWriter;
import dev.aulait.csvloader.core.domain.jdbc.TableMetaData;
import dev.aulait.csvloader.core.domain.resource.TableDataResource;
import dev.aulait.csvloader.core.domain.resource.TableListProcessor;
import dev.aulait.csvloader.core.domain.resource.TableListReader;
import dev.aulait.csvloader.core.infra.LogCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

public class CsvLoader {

  private CSVFormat csvFormat =
      CSVFormat.DEFAULT
          .builder()
          .setRecordSeparator(System.lineSeparator())
          .setSkipHeaderRecord(true)
          .setHeader()
          .build();

  private TableListReader tableListReader = new TableListReader();

  private TableListProcessor tableListProcessor = new TableListProcessor();

  private MetaDataReader metaDataReader = new MetaDataReader();

  private InsertStatementProcessor insertStatementProcessor = new InsertStatementProcessor();

  private StatementWriter statementWriter = new StatementWriter();

  public void load(Object owner, Connection connection, LogCallback log)
      throws IOException, SQLException {

    List<String> tablePaths = tableListReader.read(owner, log);

    List<TableDataResource> tableDataResources = tableListProcessor.processs(owner, tablePaths);

    String identifierQuoteString = connection.getMetaData().getIdentifierQuoteString();

    for (TableDataResource tableDataResource : tableDataResources) {

      TableMetaData metaData =
          metaDataReader.read(connection, tableDataResource.getTableName(), log);

      log.info("Loading csv file : " + tableDataResource.getCsvUrl());

      try (CSVParser csvParser =
          CSVParser.parse(tableDataResource.getCsvUrl(), StandardCharsets.UTF_8, csvFormat)) {

        String insertStatement =
            insertStatementProcessor.process(
                tableDataResource.getTableName(),
                csvParser.getHeaderNames(),
                identifierQuoteString);

        statementWriter.write(connection, insertStatement, csvParser, metaData);
      }
    }
  }
}
