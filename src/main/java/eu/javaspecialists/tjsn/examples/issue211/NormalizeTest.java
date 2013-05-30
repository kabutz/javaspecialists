package eu.javaspecialists.tjsn.examples.issue211;

public class NormalizeTest {
  public static void main(String[] args) {
    Normalize norm = new Normalize();
    norm.testEquals("Genève", "Gene\u0300ve");
    norm.testEquals("ha\u0301ček", "hác\u030cek");
  }
}
