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

package eu.javaspecialists.tjsn.examples.issue076;

import javax.sound.midi.*;

/**
 * This program plays the first few notes of "As Time Goes By"
 * from the movie Casablanca.
 */
public class AsTimeGoesBy extends OldSong {
  public AsTimeGoesBy() throws MidiUnavailableException,
      InvalidMidiDataException {
    super(65, 20, 8);
  }

  public void makeSong(int key) throws InvalidMidiDataException {
    addSilence(7);
    add(key);
    add(key + 1);
    add(key);
    add(key - 2);
    add(key - 4);
    add(key - 2, 3);
    add(key);
    add(key + 3);
    add(key + 1);
    add(key);
    add(key - 2);
    add(key + 1, 3);
    add(key + 3);
    add(key + 8);
    add(key + 7);
    add(key + 5);
    add(key + 3);
    add(key + 5, 4);
    add(key + 2, 4);
    add(key + 3, 2);
    addSilence(1);
    add(key + 7);
    add(key + 10);
    add(key + 8);
    add(key + 7);
    add(key + 5);
    add(key + 7, 2);
    add(key + 8, 2);
    add(key + 3, 2);
    add(key + 3, 2);
    add(key - 4, 2);
    add(key - 2, 2);
    add(key - 4, 2);
  }

  public static void main(String[] args)
      throws MidiUnavailableException,
      InvalidMidiDataException {
    OldSong asTime = new AsTimeGoesBy();
    asTime.start();
  }
}