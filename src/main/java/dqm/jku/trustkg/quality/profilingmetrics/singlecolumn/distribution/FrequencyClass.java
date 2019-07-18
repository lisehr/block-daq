package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.distribution;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:FrequencyClass")
public class FrequencyClass implements Comparable<FrequencyClass> {
  private int classNo;
  private int frequency;

  public FrequencyClass() {
    
  }
  
  public FrequencyClass(int key, int value) {
    this.classNo = key;
    this.frequency = value;
  }

  /**
   * @return the classNo
   */
  @RDF("foaf:classNo")
  public int getClassNo() {
    return classNo;
  }

  /**
   * @param classNo the classNo to set
   */
  public void setClassNo(int classNo) {
    this.classNo = classNo;
  }

  /**
   * @return the frequency
   */
  @RDF("foaf:frequency")
  public int getFrequency() {
    return frequency;
  }

  /**
   * @param frequency the frequency to set
   */
  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public void incrementFrequency() {
    frequency++;
  }

  @Override
  public int compareTo(FrequencyClass other) {
    if (classNo < other.classNo) return -1;
    else if (classNo > other.classNo) return 1;
    else return 0;
  }

}
