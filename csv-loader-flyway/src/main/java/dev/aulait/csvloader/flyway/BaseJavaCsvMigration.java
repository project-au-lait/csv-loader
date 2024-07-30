package dev.aulait.csvloader.flyway;

import dev.aulait.csvloader.core.application.CsvLoader;
import org.flywaydb.core.api.logging.Log;
import org.flywaydb.core.api.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class BaseJavaCsvMigration extends BaseJavaMigration {

  protected CsvLoader loader = new CsvLoader();

  protected Log log = LogFactory.getLog(getClass());

  @Override
  public void migrate(Context ctx) throws Exception {
    loader.load(this, ctx.getConnection(), log::info);
  }
}
