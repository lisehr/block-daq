package dqm.jku.trustkg.quality.profilingmetrics;

import java.util.List;
import java.util.Objects;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.records.RecordSet;
import dqm.jku.trustkg.quality.DataProfile;
@RDFNamespaces({ 
  "foaf = http://xmlns.com/foaf/0.1/",
})
@RDFBean("foaf:ProfileMetric")
public abstract class ProfileMetric {
  private String label; // the naming of the metric
  private Class<?> valClass; // the class of the value
  private Object value; // the value itself
  private DataProfile refProf; // reference profile for calculations

  public ProfileMetric(String label, DataProfile refProf) {
    if (label == null || refProf == null) throw new IllegalArgumentException("Parameters cannot be null!");
    this.label = label;
    this.refProf = refProf;
    value = null;
  }

  /**
   * Gets the label
   * 
   * @return label of metric
   */
  @RDF("foaf:label")
  public String getLabel() {
    return label;
  }

  /**
   * Gets the value
   * 
   * @return value of metric
   */
  @RDF("foaf:value")
  public Object getValue() {
    return value;
  }

  /**
   * Gets the class object of a value
   * 
   * @return class of value
   */
  @RDF("foaf:valueClass")
  public Class<?> getValueClass() {
    return valClass;
  }

  /**
   * Sets the class of the value
   * 
   * @param cls the new value class to be set
   */
  protected void setValueClass(Class<?> cls) {
    this.valClass = cls;
  }

  /**
   * Sets the value of the metric
   * 
   * @param value the new value to be set
   */
  protected void setValue(Object value) {
    this.value = value;
  }

  /**
   * Gets the reference DataProfile
   * 
   * @return the reference profile
   */
  public DataProfile getRefProf() {
    return refProf;
  }
  
  /**
   * Gets the reference dsd element, used for calculation
   * 
   * @return the reference element
   */
  public DSDElement getRefElem() {
    return refProf.getRefElem();
  }

  /**
   * Method for calculating the profile metric, overridden by each metric
   * 
   * @param oldVal a oldValue to be updated, null for initial calculation
   * @param rs     the recordset used for calculation
   */
  public abstract void calculation(RecordSet rs, Object oldVal);
  
  /**
   * Method for calculating the profile metric, overridden by each metric
   * 
   * @param oldVal a oldValue to be updated, null for initial calculation
   * @param list     a sorted list, containing all values
   */
  public abstract void calculationNumeric(List<Number> list, Object oldVal);
  
  /**
   * Method for updating the metric value, overriden by each metric
   * @param rs the recordset used for updating
   */
  public abstract void update(RecordSet rs);
  
  /**
   * Returns a string representation of the metic value
   * @return string repr of value
   */
  protected abstract String getValueString();


  @Override
  public String toString() {
    if (value == null) return String.format("%s\tnull", label);
    else return String.format("%s\t%s", label, getValueString());
  }


  @Override
  public int hashCode() {
    return Objects.hash(label, valClass, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ProfileMetric)) return false;
    ProfileMetric other = (ProfileMetric) obj;
    return Objects.equals(label, other.label) && Objects.equals(valClass, other.valClass) && Objects.equals(value, other.value);
  }

}
