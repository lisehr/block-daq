package dqm.jku.trustkg.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.ConnectorPartialCSV;

public class FileSelectionUtil {
  private static final String PREFIX = "src/main/java/dqm/jku/trustkg/resources/";
  private static final String CSV = "csv/";
  private static final String PATTERNS = "patterns/";

  public static ConnectorCSV connectToCSV(int index) throws IOException {
    // walk resources package to make a selection on which csv file should be used
    // for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX + CSV));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();

    return new ConnectorCSV(files.get(index).toString(), ",", "\n", FilenameUtils.removeExtension(files.get(index).getFileName().toString()), true);

  }

  public static ConnectorCSV connectToCSV(int index, String name) throws IOException {
    // walk resources package to make a selection on which csv file should be used
    // for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX + CSV));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();

    return new ConnectorCSV(files.get(index).toString(), ",", "\n", name, false);

  }

  public static ConnectorPartialCSV connectToCSVPartial(int index, int offset, int noRecords) throws IOException {
    // walk resources package to make a selection on which csv file should be used
    // for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX + CSV));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();

    return new ConnectorPartialCSV(files.get(index).toString(), ",", "\n", FilenameUtils.removeExtension(files.get(index).getFileName().toString()), true, offset, noRecords);

  }

  public static ConnectorPartialCSV connectToCSVPartial(int index, int offset, int noRecords, String name) throws IOException {
    // walk resources package to make a selection on which csv file should be used
    // for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX + CSV));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();

    return new ConnectorPartialCSV(files.get(index).toString(), ",", "\n", name, true, offset, noRecords);

  }

  public static List<String> readAllPatternsOfFile(int index) throws IOException {
    // walk resources package to make a selection on which pattern file should be
    // used for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX + PATTERNS));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();

    if(files.size() > 1) {
    	return Files.readAllLines(files.get(index));
    } else {
    	// return empty list if no patterns found
    	return new ArrayList<String>();
    }
  }

}
