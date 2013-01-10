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

package eu.javaspecialists.tjsn.examples.issue145;

import eu.javaspecialists.tjsn.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * From http://www.javaspecialists.eu/archive/Issue145.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class TristateCheckBoxTest {
    public static void main(String args[]) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("TristateCheckBoxTest");
                frame.setLayout(new GridLayout(0, 1, 15, 15));
                UIManager.LookAndFeelInfo[] lfs =
                        UIManager.getInstalledLookAndFeels();
                for (UIManager.LookAndFeelInfo lf : lfs) {
                    System.out.println("Look&Feel " + lf.getName());
                    try {
                        UIManager.setLookAndFeel(lf.getClassName());
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                    frame.add(makePanel());
                }
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private static JPanel makePanel() {
        final TristateCheckBox tristateBox = new TristateCheckBox(
                "Tristate checkbox");
        tristateBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                switch (tristateBox.getState()) {
                    case SELECTED:
                        System.out.println("Selected");
                        break;
                    case DESELECTED:
                        System.out.println("Not Selected");
                        break;
                    case INDETERMINATE:
                        System.out.println("Tristate Selected");
                        break;
                }
            }
        });
        tristateBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(e);
            }
        });
        final JCheckBox normalBox = new JCheckBox("Normal checkbox");
        normalBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(e);
            }
        });

        final JCheckBox enabledBox = new JCheckBox("Enable", true);
        enabledBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                tristateBox.setEnabled(enabledBox.isSelected());
                normalBox.setEnabled(enabledBox.isSelected());
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel(UIManager.getLookAndFeel().getName()));
        panel.add(tristateBox);
        panel.add(normalBox);
        panel.add(enabledBox);
        return panel;
    }
}
