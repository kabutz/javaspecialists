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

package eu.javaspecialists.tjsn.math.numbers;

import java.math.*;

/**
 * Some utility functions for adding an arbitrary number of BigIntegers and for
 * splitting a BigInteger at some position.
 *
 * @author Dr Heinz M. Kabutz
 */
class BigIntegerUtils {
  public static BigInteger add(BigInteger... ints) {
    BigInteger sum = ints[0];
    for (int i = 1; i < ints.length; i++) {
      sum = sum.add(ints[i]);
    }
    return sum;
  }

  public static BigInteger[] split(BigInteger x, int m) {
    BigInteger left = x.shiftRight(m);
    BigInteger right = x.subtract(left.shiftLeft(m));
    return new BigInteger[]{left, right};
  }
}