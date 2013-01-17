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

package eu.javaspecialists.tjsn.jmatia.server;

import eu.javaspecialists.tjsn.jmatia.actions.*;

import java.awt.event.*;
import java.io.*;
import java.util.concurrent.*;

class SocketWriterThread extends Thread {
    private final RobotActionQueue jobs = new RobotActionQueue();
    private final String studentName;
    private final ObjectOutputStream out;
    private volatile boolean active = false;

    public SocketWriterThread(String studentName,
                              ObjectOutputStream out) {
        super("Writer to " + studentName);
        this.studentName = studentName;
        this.out = out;
    }

    public void setActive(boolean active) {
        this.active = active;
        askForScreenShot();
    }

    private double getZoomFactor() {
        return active ? 1.0 : 0.3;
    }

    public long getWaitTime() {
        return active ? 500 : 3000;
    }

    public void clickEvent(MouseEvent e) {
        if (active) {
            jobs.add(new MoveMouse(e));
            jobs.add(new ClickMouse(e));
        }
        active = true;
        askForScreenShot();
    }

    private void askForScreenShot() {
        jobs.add(new ScreenShot(getZoomFactor()));
    }

    public void run() {
        askForScreenShot();
        try {
            while (!isInterrupted()) {
                try {
                    RobotAction action = jobs.poll(
                            getWaitTime(),
                            TimeUnit.MILLISECONDS);
                    if (action == null) {
                        // we had a timeout, so do a screen capture
                        askForScreenShot();
                    } else {
                        System.out.println("sending " + action +
                                " to " + studentName);
                        out.writeObject(action);
                        out.reset();
                        out.flush();
                    }
                } catch (InterruptedException e) {
                    interrupt();
                    break;
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Connection to " + studentName +
                    " closed (" + e + ')');
        }
        System.out.println("Closing connection to " + studentName);
    }
}
