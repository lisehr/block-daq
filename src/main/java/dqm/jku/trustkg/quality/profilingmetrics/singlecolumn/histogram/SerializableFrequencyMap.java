package dqm.jku.trustkg.quality.profilingmetrics.singlecolumn.histogram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFContainer;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

/**
 * Data structure for a serializable map for histogram bins, processable by
 * RDFBeans
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:SerializableFrequencyMap")
public class SerializableFrequencyMap {
  private Set<FrequencyClass> classes = new HashSet<>();
  private String uri;

  public SerializableFrequencyMap() {

  }

  public SerializableFrequencyMap(String uri) {
	  this.uri = uri + "/frequencies";
  }
  
  @RDFSubject
  public String getURI() {
	  return this.uri;
  }
  
  public void setURI(String uri) {
	  this.uri = uri;
  }


/**
   * Puts a new Frequency class into the map
   * 
   * @param key   the classno
   * @param value the frequency
   */
  public void put(int key, int value) {
    classes.add(new FrequencyClass(key, value, this.uri));
  }

  /**
   * Increments the frequency via the classno
   * 
   * @param key the classno
   */
  public void incrementFrequency(int key) {
    for (FrequencyClass f : classes) {
      if (f.getClassNo() == key) f.incrementFrequency();
    }
  }

  /**
   * Gets the classes
   * 
   * @return the classes
   */
  @RDF("foaf:hasClass")
  @RDFContainer
  public Set<FrequencyClass> getClasses() {
    return classes;
  }

  /**
   * Sets the classes (security threat but needed by rdfbeans)
   * 
   * @param classes the classes to set
   */
  public void setClasses(Set<FrequencyClass> classes) {
    this.classes = classes;
  }

  /**
   * Creates a List of all values (frequencies)
   * 
   * @return frequency list
   */
  public List<Integer> values() {
    List<Integer> list = new ArrayList<>();
    for (FrequencyClass f : classes) {
      list.add(f.getFrequency());
    }
    return list;
  }

}
