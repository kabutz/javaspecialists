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
 * Same as the FibonacciRecursiveNonCaching, except that this algorithm does
 * cache previous values.  It suffers from the same problems as
 * FibonacciIterative, in that the BigInteger.add() method is linear to the
 * size
 * of the number.  In addition, because it is still recursive in nature, the
 * stack depth is a limiting factor in which numbers we can calculate.
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciRecursive extends Fibonacci {
  public BigInteger doActualCalculate(int n) throws InterruptedException {
    if (Thread.interrupted()) throw new InterruptedException();
    if (n < 0) throw new IllegalArgumentException();
    if (n == 0) return BigInteger.ZERO;
    if (n == 1) return BigInteger.ONE;
    return calculate(n - 1).add(calculate(n - 2));
  }
}