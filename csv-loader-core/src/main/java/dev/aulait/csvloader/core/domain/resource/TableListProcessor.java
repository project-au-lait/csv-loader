package dev.aulait.csvloader.core.domain.resource;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TableListProcessor {

  public List<TableDataResource> processs(Object owner, List<String> tablePaths) {

    List<TableDataResource> resources = new ArrayList<>();

    for (String tablePath : tablePaths) {
      resources.add(read(owner, tablePath));
    }

    return resources;
  }

  TableDataResource read(Object owner, String tablePath) {
    TableDataResource res = new TableDataResource();

    int lastIndexOfSlash = tablePath.lastIndexOf("/");

    if (lastIndexOfSlash < 0) {
      res.setTableName(tablePath);
    } else {
      res.setTableName(tablePath.substring(lastIndexOfSlash + 1));
    }

    String resName = owner.getClass().getSimpleName() + "/" + tablePath + ".csv";
    URL resUrl = owner.getClass().getResource(resName);

    res.setCsvUrl(resUrl);

    return res;
  }
}
