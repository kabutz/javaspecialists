package eu.javaspecialists.tjsn.examples.issue211;

import java.text.*;

public class Normalize {
  public boolean normeq_broken(String w1, String w2) {
    if (w1.length() != w2.length()) {
      w1 = Normalizer.normalize(w1, Normalizer.Form.NFD);
      w2 = Normalizer.normalize(w2, Normalizer.Form.NFD);
    }
    return w1.equals(w2);
  }

  public boolean normeq(String w1, String w2) {
    if (w1.equals(w2)) {
      return true;
    } else {
      w1 = Normalizer.normalize(w1, Normalizer.Form.NFD);
      w2 = Normalizer.normalize(w2, Normalizer.Form.NFD);
      return w1.equals(w2);
    }
  }

  public void testEquals(String w1, String w2) {
    System.out.println(w1 + " equals " + w2 + " " + w1.equals(w2));
    System.out.println(w1 + " normeq " + w2 + " " + normeq(w1, w2));
  }
}
