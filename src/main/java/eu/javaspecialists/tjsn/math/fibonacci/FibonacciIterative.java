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

package eu.javaspecialists.tjsn.math.fibonacci;

import java.math.*;

/**
 * This calculation loops from 0 to n and adds up all the numbers in O(n).
 * However, BigInteger.add() is also linear, so the complete algorithm is
 * quadratic.
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciIterative extends NonCachingFibonacci {
  public BigInteger calculate(int n) throws InterruptedException {
    if (n < 0) throw new IllegalArgumentException();
    BigInteger n0 = BigInteger.ZERO;
    BigInteger n1 = BigInteger.ONE;
    for (int i = 0; i < n; i++) {
      if (Thread.interrupted()) throw new InterruptedException();
      BigInteger temp = n1;
      n1 = n1.add(n0);
      n0 = temp;
    }
    return n0;
  }
}