package dev.aulait.csvloader.core.domain.resource;

import dev.aulait.csvloader.core.infra.LogCallback;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TableListReader {

  private String tableListFineName = "table-list.txt";

  public List<String> read(Object owner, LogCallback log) throws IOException {
    String resName = owner.getClass().getSimpleName() + "/" + tableListFineName;
    URL resUrl = owner.getClass().getResource(resName);

    if (resUrl == null) {
      String message =
          "Resouce: "
              + tableListFineName
              + " is not found in classpath of owner: "
              + owner.getClass();
      throw new FileNotFoundException(message);
    }

    log.info("Reading table list: " + resUrl);

    List<String> lines = new ArrayList<>();

    try (InputStream is = resUrl.openStream()) {
      try (Scanner scanner = new Scanner(is)) {
        while (scanner.hasNextLine()) {
          lines.add(scanner.nextLine());
        }
      }
    }

    return lines;
  }
}
