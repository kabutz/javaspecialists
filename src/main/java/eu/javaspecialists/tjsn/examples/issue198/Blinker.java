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

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Blinker shows how we can use the Java 7 Phaser to coordinate threads and
 * also
 * at the same time count how many phases we have been through (hence the
 * name).
 * <p/>
 * Demo class from http://www.javaspecialists.eu/archive/Issue198.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class Blinker extends JFrame {
  public Blinker() {
    setLayout(new GridLayout(0, 4));
  }

  private void addButtons(int buttons, final int blinks) {
    final Phaser phaser = new Phaser(buttons) {
      protected boolean onAdvance(int phase, int parties) {
        return phase >= blinks - 1 || parties == 0;
      }
    };
    for (int i = 0; i < buttons; i++) {
      final JComponent comp = new JButton("Button " + i);
      comp.setOpaque(true);
      final Color defaultColor = comp.getBackground();
      changeColor(comp, defaultColor);
      add(comp);
      new Thread() {
        public void run() {
          Random rand = ThreadLocalRandom.current();
          try {
            Thread.sleep(1000);
            do {
              Color newColor = new Color(rand.nextInt());
              changeColor(comp, newColor);
              Thread.sleep(500 + rand.nextInt(3000));
              changeColor(comp, defaultColor);
              Toolkit.getDefaultToolkit().beep();
              Thread.sleep(2000);
              phaser.arriveAndAwaitAdvance();
            } while (!phaser.isTerminated());
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      }.start();
    }
  }

  private void changeColor(
      final JComponent comp, final Color color) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        comp.setBackground(color);
        invalidate();
        repaint();
      }
    });
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Blinker blinker = new Blinker();
        blinker.addButtons(20, 3);
        blinker.pack();
        blinker.setVisible(true);
        blinker.setDefaultCloseOperation(
            WindowConstants.EXIT_ON_CLOSE);
      }
    });
  }
}