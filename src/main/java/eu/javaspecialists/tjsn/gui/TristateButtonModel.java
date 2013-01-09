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

package eu.javaspecialists.tjsn.gui;

import javax.swing.JToggleButton.*;
import java.awt.event.*;

/**
 * From http://www.javaspecialists.eu/archive/Issue145.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class TristateButtonModel extends ToggleButtonModel {
  private TristateState state = TristateState.DESELECTED;

  public TristateButtonModel(TristateState state) {
    setState(state);
  }

  public TristateButtonModel() {
    this(TristateState.DESELECTED);
  }

  public void setIndeterminate() {
    setState(TristateState.INDETERMINATE);
  }

  public boolean isIndeterminate() {
    return state == TristateState.INDETERMINATE;
  }

  // Overrides of superclass methods
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    // Restore state display
    displayState();
  }

  public void setSelected(boolean selected) {
    setState(selected ?
        TristateState.SELECTED : TristateState.DESELECTED);
  }

  // Empty overrides of superclass methods
  public void setArmed(boolean b) {
  }

  public void setPressed(boolean b) {
  }

  void iterateState() {
    setState(state.next());
  }

  private void setState(TristateState state) {
    //Set internal state
    this.state = state;
    displayState();
    if (state == TristateState.INDETERMINATE && isEnabled()) {
      // force the events to fire

      // Send ChangeEvent
      fireStateChanged();

      // Send ItemEvent
      int indeterminate = 3;
      fireItemStateChanged(new ItemEvent(
          this, ItemEvent.ITEM_STATE_CHANGED, this,
          indeterminate));
    }
  }

  private void displayState() {
    super.setSelected(state != TristateState.DESELECTED);
    super.setArmed(state == TristateState.INDETERMINATE);
    super.setPressed(state == TristateState.INDETERMINATE);
  }

  public TristateState getState() {
    return state;
  }
}
