package dev.aulait.csvloader.core.domain.resource;

import java.net.URL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDataResource {
  private String tableName;
  private URL csvUrl;
}
