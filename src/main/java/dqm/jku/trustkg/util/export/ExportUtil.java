package dqm.jku.trustkg.util.export;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.cardCat;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.depend;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.dti;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.histCat;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.avg;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.card;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.dec;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.dig;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.dt;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.histCR;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.histCls;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.histVal;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.keyCand;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.max;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.med;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.min;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.nullVal;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.nullValP;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.size;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.unique;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dqm.jku.trustkg.dsd.DSDKnowledgeGraph;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.MetricTitle;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.histogram.Histogram;

public class ExportUtil {
	private static final List<LabelTriple<MetricTitle, String, String>> LABELS = new ArrayList<>();

	private static final String EXPORT_PATH = "src/main/java/dqm/jku/trustkg/resources/export/";
	private static final String EXPORT_CSV = "csv/";
  private static final String EXPORT_REPORT = "report/";

	static {
		LABELS.add(new LabelTriple<>(size, cardCat.label(), "# Rows"));
		LABELS.add(new LabelTriple<>(nullVal, cardCat.label(), nullVal.label()));
		LABELS.add(new LabelTriple<>(nullValP, cardCat.label(), nullValP.label()));
		LABELS.add(new LabelTriple<>(card, cardCat.label(), card.label()));
		LABELS.add(new LabelTriple<>(unique, cardCat.label(), unique.label()));

		LABELS.add(new LabelTriple<>(dt, dti.label(), dt.label()));
		LABELS.add(new LabelTriple<>(min, dti.label(), min.label()));
		LABELS.add(new LabelTriple<>(max, dti.label(), max.label()));
		LABELS.add(new LabelTriple<>(avg, dti.label(), avg.label()));
		LABELS.add(new LabelTriple<>(med, dti.label(), med.label()));
		LABELS.add(new LabelTriple<>(dig, dti.label(), "# Digits"));
		LABELS.add(new LabelTriple<>(dec, dti.label(), "# Decimals"));

		LABELS.add(new LabelTriple<>(histCls, histCat.label(), "# Classes"));
		LABELS.add(new LabelTriple<>(histCR, histCat.label(), histCR.label()));
		LABELS.add(new LabelTriple<>(histVal, histCat.label(), histVal.label()));

		LABELS.add(new LabelTriple<>(keyCand, depend.label(), keyCand.label()));
	}

	/**
	 * Method for exporting a complete datasource to a csv-file. Currently only data
	 * profiles on attribute level are supported.
	 * 
	 * @param ds the datasource to be exported
	 */
	public static void exportToCSV(Datasource ds) {
		Map<String, ArrayList<Object>> metricValues = new HashMap<String, ArrayList<Object>>();
		List<String> elementLabels = new ArrayList<String>();

		for (Concept c : ds.getConcepts()) {
			for (Attribute a : c.getAttributes()) {
				if(a.hasProfile()) {
					elementLabels.add(a.getLabel());
					if (a.getProfile() != null) {
						DataProfile dp = a.getProfile();
						List<ProfileMetric> metrics = dp.getMetrics();
						for (ProfileMetric m : metrics) {
							String key = m.getLabel();
							if (!key.contains("Histogram")) {
								ArrayList<Object> list = new ArrayList<Object>();
								if (metricValues.containsKey(key)) list = metricValues.get(key);
								list.add(m.getValue());
								metricValues.put(key, list);
							} else { // implement special case for histogram: split into 3 lines
								Histogram hist = (Histogram) m;
								String s1 = histCls.label();
								String s2 = histCR.label();
								String s3 = histVal.label();
								ArrayList<Object> list1 = new ArrayList<Object>();
								ArrayList<Object> list2 = new ArrayList<Object>();
								ArrayList<Object> list3 = new ArrayList<Object>();

								if (metricValues.containsKey(s1)) {
									list1 = metricValues.get(s1);
									list2 = metricValues.get(s2);
									list3 = metricValues.get(s3);
								}
								list1.add(hist.getNumberOfClasses());
								list2.add(hist.getClassrange());
								list3.add(hist.getClassValuesCSV());
								metricValues.put(s1, list1);
								metricValues.put(s2, list2);
								metricValues.put(s3, list3);
							}
						}
					}
				}
			}
		}

		// Create header line
		StringBuilder sb = new StringBuilder();
		sb.append("Category;Metric");
		for (String el : elementLabels) {
			sb.append(";");
			sb.append(el);
		}
		sb.append("\n");

		// Create Value lines
		for (LabelTriple<MetricTitle, String, String> l : LABELS) {
			String label = l.getKey().label();
			ArrayList<Object> list = metricValues.get(label);
			sb.append(l.getCat(l.getKey()));
			sb.append(";");
			sb.append(l.getLabel(l.getKey()));
			for (Object o : list) {
				sb.append(";");
				if (o != null) {
					if (o instanceof Number) sb.append(formatFloat(o));
					else sb.append(o.toString());
				}
			}
			sb.append("\n");
		}

		Path path = Paths.get(EXPORT_PATH + EXPORT_CSV);
		if (Files.notExists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (PrintWriter writer = new PrintWriter(new File(EXPORT_PATH + EXPORT_CSV + "export_" + ds.getLabel() + ".csv"))) {
			writer.write(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void exportToCSV(List<Datasource> dss) {
		for (Datasource ds : dss) exportToCSV(ds);
	}
	
	public static void exportReport(DSDKnowledgeGraph kg) {
	  StringBuilder sb = new StringBuilder();
	  
    sb.append("Structure of KG: ");
    sb.append(kg.getLabel()).append('\n').append('\t');
    for (Datasource ds : kg.getDatasources().values()) {
      sb.append(ds.getStructureString().replace("\n", "\n\t"));
    }
    sb.append('\n');
    
    sb.append("Dataprofiles of KG: ");
    sb.append(kg.getLabel()).append('\n').append('\t');
    for (Datasource ds : kg.getDatasources().values()) {
      sb.append("Dataprofiles of Datasource: ");
      sb.append(ds.getLabel()).append('\n').append('\t').append('\t');
      for (Concept c : ds.getConcepts()) {
        sb.append("Dataprofiles of Concept: ");
        sb.append(c.getLabel()).append('\n').append('\t').append('\t').append('\t');
        for (Attribute a : c.getAttributes()) sb.append(a.getProfileString().replace("\n", "\n\t\t\t"));
        sb.append('\t').append('\t');
      }
      sb.delete(sb.length() - 5, sb.length() - 1);
    }    
	  
	  Path path = Paths.get(EXPORT_PATH + EXPORT_REPORT);
	    if (Files.notExists(path)) {
	      try {
	        Files.createDirectory(path);
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }

	    try (PrintWriter writer = new PrintWriter(new File(EXPORT_PATH + EXPORT_REPORT + "export_report_" + kg.getLabel() + ".txt"))) {
	      writer.write(sb.toString());
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    }

	}

	private static String formatFloat(Object num) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.0000");
		return decimalFormat.format(num);
	}
}
