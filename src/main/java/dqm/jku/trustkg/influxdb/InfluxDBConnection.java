package dqm.jku.trustkg.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

public class InfluxDBConnection {
  private static final String URL = "http://localhost:8086";
  private static final String USER = "root";
  private static final String PW = "root";
  private static final String DEF_DB = "testSeries";
  private static final String DEF_RET = "testRetention";

  private InfluxDB instance;
  private String dbName;
  private String retentionPolicyName;
  
  public InfluxDBConnection() {
    instance = InfluxDBFactory.connect(URL, USER, PW);
    this.dbName = DEF_DB;
    this.retentionPolicyName = DEF_RET;
    createDB();
  }

  /**
   * @return the dbName
   */
  public String getDbName() {
    return dbName;
  }

  /**
   * @return the retentionPolicyName
   */
  public String getRetentionPolicyName() {
    return retentionPolicyName;
  }

  private void createDB() {
    if (isInitialized()) return;
    instance.query(new Query("CREATE DATABASE " + dbName));
    instance.query(new Query("CREATE RETENTION POLICY " + retentionPolicyName + " ON " + dbName + " DURATION 30h REPLICATION 2 SHARD DURATION 30m DEFAULT"));
  }
  
  public boolean isInitialized() {
    QueryResult dbs = instance.query(new Query("SHOW DATABASES"));
    for (Result r : dbs.getResults()) {
      for (Series s : r.getSeries()) {
        if (s.toString().equals(dbName)) return true;
      }
    }
    return false;
  }
  
  public void write(Point value) {
    this.instance.write(dbName, retentionPolicyName, value);
  }
  
  public QueryResult query(Query query) {
    return this.instance.query(query);
  }

  public void close() {
    this.instance.close();    
  }
  
  public void deleteDB() {
    instance.query(new Query("DROP RETENTION POLICY " + getRetentionPolicyName() + " ON " + getDbName()));
    instance.query(new Query("DROP DATABASE " + getDbName()));
  }
  
  public void printQuery(QueryResult qr) {
    for (Result r : qr.getResults()) {
      if (r.getSeries() == null) return;
      for (Series s : r.getSeries()) {
        System.out.println(s.toString());
      }
    }
  }


}
