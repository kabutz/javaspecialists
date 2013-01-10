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

package eu.javaspecialists.tjsn.examples.issue196;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class DemoFrame extends JFrame {
    public DemoFrame() {
        super("DemoFrame");
        JButton button = new JButton("Cause Exception");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                throw new NullPointerException("brain is null");
            }
        });
        add(button);

        // This timer will run in the EDT
        javax.swing.Timer timer1 = new javax.swing.Timer(3000,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        throw new IllegalStateException("forgotten name");
                    }
                });
        timer1.start();

        // This timer will run in a normal thread
        java.util.Timer timer2 = new java.util.Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                throw new IllegalArgumentException("stop arguing!");
            }
        }, 6000);
    }

    public static void create() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DemoFrame frame = new DemoFrame();
                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                frame.setSize(500, 200);
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
            }
        });
    }
}

