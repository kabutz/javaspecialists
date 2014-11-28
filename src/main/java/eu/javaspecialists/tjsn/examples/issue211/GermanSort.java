package eu.javaspecialists.tjsn.examples.issue211;

import java.text.*;
import java.util.*;

public class GermanSort implements Comparator<String> {
  private final RuleBasedCollator collator;

  public GermanSort() throws ParseException {
    collator = createCollator();
  }

  private RuleBasedCollator createCollator() throws ParseException {
    String german = "" +
        "= '-',''' " +
        "< A,a;ä,Ä< B,b< C,c< D,d< E,e< F,f< G,g< H,h< I,i< J,j" +
        "< K,k< L,l< M,m< N,n< O,o;Ö,ö< P,p< Q,q< R,r< S,s< T,t" +
        "< U,u;Ü,ü< V,v< W,w< X,x< Y,y< Z,z" +
        "& ss=ß";
    return new RuleBasedCollator(german);
  }

  public int compare(String s1, String s2) {
    return collator.compare(s1, s2);
  }

  public void sort(String[] strings) {
    Arrays.sort(strings, this);
  }
}

