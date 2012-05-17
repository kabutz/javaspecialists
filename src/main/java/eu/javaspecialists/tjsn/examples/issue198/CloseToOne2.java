package eu.javaspecialists.tjsn.examples.issue198;

public class CloseToOne2 {
  public static void main(String[] args) {
    double d = 0.999999993;
    System.out.println("d = " + d);
    int i = (int) (1 + d);
    System.out.println("i = " + i);
    int j = (int) (100_000_000 + d);
    System.out.println("j = " + j);
  }
}
