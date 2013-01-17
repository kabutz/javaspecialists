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

public class MoveMouse implements RobotAction {
    private final int x;
    private final int y;

    public MoveMouse(Point to) {
        x = (int) to.getX();
        y = (int) to.getY();
    }

    public MoveMouse(MouseEvent event) {
        this(event.getPoint());
    }

    public Object execute(Robot robot) {
        robot.mouseMove(x, y);
        return null;
    }

    public String toString() {
        return "MoveMouse: x=" + x + ", y=" + y;
    }

    public boolean equals(Object o) {
        if (!(o instanceof MoveMouse)) return false;
        MoveMouse mm = (MoveMouse) o;
        return x == mm.x && y == mm.y;
    }

    public int hashCode() {
        return (x << 16) + y;
    }
}
