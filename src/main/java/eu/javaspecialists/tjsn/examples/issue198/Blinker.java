package eu.javaspecialists.tjsn.examples.issue198;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

/**
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