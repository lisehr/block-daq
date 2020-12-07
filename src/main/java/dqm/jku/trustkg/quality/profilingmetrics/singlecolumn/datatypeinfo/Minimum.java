package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.datatypeinfo;

import java.util.List;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;
import dqm.jku.trustkg.dsd.records.RecordList;
import dqm.jku.trustkg.quality.DataProfile;
import dqm.jku.trustkg.quality.profilingmetrics.MetricTitle;
import dqm.jku.trustkg.quality.profilingmetrics.ProfileMetric;
import dqm.jku.trustkg.util.Constants;
import dqm.jku.trustkg.util.numericvals.NumberComparator;

import static dqm.jku.trustkg.quality.profilingmetrics.MetricTitle.*;
import static dqm.jku.trustkg.quality.profilingmetrics.MetricCategory.*;


/**
 * Describes the metric Minimum, which is the minimum value of all values of an
 * Attribute.
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:quality/structures/metrics/dataTypeInfo/Minimum")
public class Minimum extends ProfileMetric {
  public Minimum() {

  }

  public Minimum(DataProfile d) {
    super(min, dti, d);
  }

  @Override
  public void calculation(RecordList rs, Object oldVal) {
    Attribute a = (Attribute) super.getRefElem();
    Object val = null;
    if (oldVal == null) val = getBasicInstance();
    else val = oldVal;
    for (Record r : rs) {
      Object field = r.getField(a);
      val = getMinimum(val, field, false);
    }
    this.setValue(val);
    this.setNumericVal(((Number) val).doubleValue());
    this.setValueClass(a.getDataType());
  }

  /**
   * Creates a basic instance used as a reference (in this case the maximum value)
   * 
   * @return the reference value
   */
  private Object getBasicInstance() {
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.valueOf(Long.MAX_VALUE);
    else if (a.getDataType().equals(Double.class)) return Double.valueOf(Double.MAX_VALUE);
    else return Integer.MAX_VALUE;
  }

  /**
   * Checks the minimum value of two objects
   * 
   * @param current       the current minimum value
   * @param toComp        the new value to compare
   * @param isNumericList check, if calculation happens with numeric list
   * @return the new minimum value
   */
  private Object getMinimum(Object current, Object toComp, boolean isNumericList) {
    if (toComp == null) return current;
    Attribute a = (Attribute) super.getRefElem();
    if (a.getDataType().equals(Long.class)) return Long.min(((Number) current).longValue(), ((Number) toComp).longValue());
    else if (a.getDataType().equals(Double.class)) return Double.min(((Number) current).doubleValue(), ((Number) toComp).doubleValue());
    else if (a.getDataType().equals(String.class) && !isNumericList) return Integer.min((int) current, ((String) toComp).length());
    else return Integer.min(((Number) current).intValue(), ((Number) toComp).intValue());
  }

  @Override
  public void update(RecordList rs) {
    calculation(rs, super.getValue());
  }

  @Override
  public void calculationNumeric(List<Number> list, Object oldVal) {
    if (list == null || list.isEmpty()) {
      if (oldVal != null) return;
      else this.setValue(null);
    } else {
      list.sort(new NumberComparator());
      Object val = null;
      if (oldVal == null) val = getMinimum(list.get(0), getBasicInstance(), true);
      else val = getMinimum(list.get(0), oldVal, true);
      this.setValue(val);
      this.setNumericVal(((Number) val).doubleValue());
    }
    Attribute a = (Attribute) super.getRefElem();
    this.setValueClass(a.getDataType());
  }

  @Override
  protected String getValueString() {
    return super.getSimpleValueString();
  }

	@Override
	public boolean checkConformance(ProfileMetric m, double threshold) {
		Number rdpVal = (Number) this.getNumericVal();
		Number dpValue = (Number) m.getValue();
		if(rdpVal.doubleValue() < 0) {	// shift by threshold
			rdpVal = rdpVal.doubleValue() / threshold;
		} else {
			rdpVal = rdpVal.doubleValue() * threshold;
		}
		boolean conf = dpValue.doubleValue() >= rdpVal.doubleValue();
		if(!conf && Constants.DEBUG) System.out.println(MetricTitle.min + " exceeded: " + dpValue + " < " + rdpVal);
		return conf;
	}
}
