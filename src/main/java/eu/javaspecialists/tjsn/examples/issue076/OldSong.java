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
 * This class represents an Old Song that we might want to record
 * for our listening pleasure.
 */
public abstract class OldSong {
  private final Sequencer sequencer;
  private final Track track;
  private final int resolution;
  private int pos;

  /**
   * @param key   is the note that this starts with.  60 is middle C.
   * @param tempo is measured in beats per second
   */
  public OldSong(int key, int tempo, int resolution)
      throws MidiUnavailableException,
      InvalidMidiDataException {
    this.resolution = resolution;
    Sequence sequence = new Sequence(Sequence.PPQ, resolution);
    track = sequence.createTrack();
    makeSong(key);
    sequencer = MidiSystem.getSequencer();
    sequencer.open();
    sequencer.setSequence(sequence);
    sequencer.setTempoInBPM(tempo);
  }

  public void start() {
    sequencer.start();
  }

  protected abstract void makeSong(int key)
      throws InvalidMidiDataException;

  protected void add(int note) throws InvalidMidiDataException {
    add(note, 1);
  }

  protected synchronized void add(int note, int length)
      throws InvalidMidiDataException {
    addStartEvent(note);
    pos += length;
    addStopEvent(note);
  }

  protected synchronized void addSilence(int length) {
    pos += length;
  }

  /**
   * A piano teacher once told me that the first note in a bar
   * should be emphasized.
   * <p/>
   * We assume that resolution has already been set and that we
   * have the "this" monitor.
   */
  private int volume() {
    assert Thread.holdsLock(this);
    assert pos != 0;
    return ((pos % resolution) == 0) ? 100 : 70;
  }

  /**
   * We assume that we are holding the "this" monitor
   */
  private void addStartEvent(int note)
      throws InvalidMidiDataException {
    assert Thread.holdsLock(this);
    ShortMessage message = new ShortMessage();
    message.setMessage(ShortMessage.NOTE_ON, 0, note, volume());
    track.add(new MidiEvent(message, pos));
  }

  /**
   * We assume that we are holding the "this" monitor
   */
  private void addStopEvent(int note)
      throws InvalidMidiDataException {
    assert Thread.holdsLock(this);
    ShortMessage message = new ShortMessage();
    message.setMessage(ShortMessage.NOTE_OFF, 0, note, 0);
    track.add(new MidiEvent(message, pos));
  }
}