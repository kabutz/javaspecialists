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

package eu.javaspecialists.tjsn.examples.issue198;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * Test class for code from http://www.javaspecialists.eu/archive/Issue198.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class FindRandomTest {
  @Test
  public void testThatMinBiggerOrEqualToZero() {
    double minDouble = FindRandom.findMinDouble();
    assertTrue(minDouble >= 0.0);
    assertTrue(minDouble < 1.0);
  }

  @Test
  public void testThatMaxLessThanOne() {
    double maxDouble = FindRandom.findMaxDouble();
    assertTrue(maxDouble < 1.0);
  }
}