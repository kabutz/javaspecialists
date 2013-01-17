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

package eu.javaspecialists.tjsn.jmatia.actions;

import java.awt.*;
import java.awt.event.*;

public class ClickMouse implements RobotAction {
    private final int mouseButton;
    private final int clicks;

    public ClickMouse(int mouseButton, int clicks) {
        this.mouseButton = mouseButton;
        this.clicks = clicks;
    }

    public ClickMouse(MouseEvent event) {
        this(event.getModifiers(), event.getClickCount());
    }

    public Object execute(Robot robot) {
        for (int i = 0; i < clicks; i++) {
            robot.mousePress(mouseButton);
            robot.mouseRelease(mouseButton);
        }
        return null;
    }

    public String toString() {
        return "ClickMouse: " + mouseButton + ", " + clicks;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ClickMouse)) return false;
        ClickMouse cm = (ClickMouse) o;
        return clicks == cm.clicks && mouseButton == cm.mouseButton;
    }

    public int hashCode() {
        return 31 * mouseButton + clicks;
    }
}
