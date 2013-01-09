/*
 * Copyright (C) 2000-2013 Heinz Max Kabutz
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Heinz Max Kabutz licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.javaspecialists.tjsn.examples.issue187;

import java.util.*;

/**
 * Demonstrates how array bound exceptions eventually are eliminated by the
 * JVM.
 *
 * @author Dr Heinz M. Kabutz
 */
public class ArrayBoundsTest extends DuplicateExceptionChecker {
  private static final Object[] randomObjects =
      new Object[1000 * 1000];

  private static final int[] randomIndexes =
      new int[1000 * 1000];

  private static final String[] randomStrings =
      new String[1000 * 1000];


  public static void main(String[] args) {
    ArrayBoundsTest test = new ArrayBoundsTest();
    test.fillArrays(0.01);
    test.test();
  }

  public void notifyOfDuplicate(Exception e) {
    super.notifyOfDuplicate(e);
    System.exit(1);
  }

  private void fillArrays(double probabilityIndexIsOut) {
    Random random = new Random(0);
    for (int i = 0; i < randomObjects.length; i++) {
      randomObjects[i] = new Integer(i);
      randomIndexes[i] = random.nextInt(i);
      if (random.nextDouble() < probabilityIndexIsOut) {
        randomIndexes[i] = -randomIndexes[i];
      }
    }
    Arrays.fill(randomStrings, null);
  }

  private void test() {
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < randomObjects.length; j++) {
        try {
          int index = randomIndexes[j];
          randomStrings[index] = randomObjects[index].toString();
        } catch (ArrayIndexOutOfBoundsException e) {
          randomStrings[j] = null;
          handleException(e);
        }
      }
    }
  }
}