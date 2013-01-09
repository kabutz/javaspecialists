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

import eu.javaspecialists.tjsn.math.numbers.*;

import java.math.*;

/**
 * Based on Dijkstra's sum of the squares, available here:
 * http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Fibonacci/fibFormula.html
 * <p/>
 * However, instead of the slow BigInteger multiply(), we use Karatsuba's
 * algorithm.  Even faster is JScience's class, which uses a combination of
 * algorithms, depending on the size of the number being multiplied.
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciRecursiveDijkstraKaratsuba
    extends FibonacciRecursiveDijkstra {
  private final Karatsuba karatsuba = new BasicKaratsuba();

  protected BigInteger multiply(BigInteger bi0, BigInteger bi1) {
    return karatsuba.multiply(bi0, bi1);
  }
}