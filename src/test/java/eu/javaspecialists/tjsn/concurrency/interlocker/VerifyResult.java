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

package eu.javaspecialists.tjsn.concurrency.interlocker;

/**
 * Used by the InterleavedNumberTestingStrategy to return either success or
 * failure from each run.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class VerifyResult {
  private final boolean success;
  private final String failReason;

  private VerifyResult(boolean success, String failReason) {
    this.success = success;
    this.failReason = failReason;
  }

  public VerifyResult(String failReason) {
    this(false, failReason);
  }

  public VerifyResult() {
    this(true, null);
  }

  public boolean isSuccess() {
    return success;
  }

  public String getFailReason() {
    return failReason;
  }

  public String toString() {
    return success ? "Success" : "Failure - " + failReason;
  }
}