package test;

import java.util.concurrent.TimeUnit;

public class PerformanceTest {

  public static void main(String[] args) {
    for(int i = 0 ; i < 100 ; i++) {
      evaluate();
    }
  }

  private static void evaluate() {
    long start = System.currentTimeMillis();
    ToBuild build = null; // TODO create an instance using a Builder
    long elapsed = System.currentTimeMillis() - start;

    System.out.println(elapsed + "ms / " + TimeUnit.MILLISECONDS.toSeconds(elapsed) + "s => " + build);
  }
}
