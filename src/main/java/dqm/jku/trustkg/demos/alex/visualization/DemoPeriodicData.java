package dqm.jku.trustkg.demos.alex.visualization;

import java.io.IOException;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.influxdb.InfluxDBConnection;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.util.FileSelectionUtil;

/**
 * Idea: This demo should update the data profile after any fixed amount of
 * records (let's say after every set of 1000 records in a 100k record set) to
 * show how the data profile has changed over time
 * 
 * Implementation: - batch updates via partial csv connector - update methods in
 * data profile
 * 
 * @author optimusseptim
 *
 */
public class DemoPeriodicData {
  private static final int FILEINDEX = 21;
  private static final boolean DEBUG = false;
  private static final int AMOUNT = 500;
  private static final String NAME = "supplychain";

  public static void main(String args[]) throws IOException, InterruptedException {
    InfluxDBConnection influx = new InfluxDBConnection();
    int noRecs = 0;
    int offset = 0;
    ConnectorCSV conn = FileSelectionUtil.connectToCSV(FILEINDEX, NAME);

    if (DEBUG) {
      influx.deleteDB();
      influx = new InfluxDBConnection();
    }

    Concept testCon = null;

    Datasource ds = conn.loadSchema();
    for (Concept c : ds.getConcepts()) {
      testCon = c;
      RecordSet rs = conn.getRecordSet(c);
      for (Attribute a : c.getSortedAttributes()) {
        a.annotateProfile(rs);
        a.printAnnotatedProfile();
      }
    }
    noRecs = conn.getNrRecords(testCon);

    ds.addProfileToInflux(influx);

    for (offset = AMOUNT + 1; offset < noRecs; offset += AMOUNT) {
      conn = FileSelectionUtil.connectToCSVPartial(FILEINDEX, offset, AMOUNT, NAME);
      RecordSet rs = conn.getRecordSet(testCon);
      for (Attribute a : testCon.getSortedAttributes()) {
        DataProfile dp = a.createDataProfile(rs);
        dp.printProfile();
        influx.storeProfile(dp);
      }
    }

  }

}
