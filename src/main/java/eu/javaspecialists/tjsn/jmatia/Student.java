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

package eu.javaspecialists.tjsn.jmatia;

import eu.javaspecialists.tjsn.jmatia.actions.*;
import eu.javaspecialists.tjsn.jmatia.server.*;

import java.awt.*;
import java.io.*;
import java.net.*;

public class Student {
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final Robot robot;
    private final RobotActionQueue jobs = new RobotActionQueue();
    private final ProcessorThread processor;
    private final ReaderThread reader;

    public Student(String serverMachine, String studentName)
            throws IOException, AWTException {
        Socket socket = new Socket(
                serverMachine, TeacherServer.PORT);
        robot = new Robot();
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(
                new BufferedInputStream(socket.getInputStream()));
        out.writeObject(studentName);
        out.flush();
        processor = new ProcessorThread();
        reader = new ReaderThread();
    }

    private class ReaderThread extends Thread {
        public void run() {
            try {
                RobotAction action;
                while ((action = (RobotAction) in.readObject()) != null) {
                    if (!action.equals(jobs.peekLast())) {
                        jobs.add(action);
                        System.out.println("jobs = " + jobs);
                    } else {
                        System.out.println("Discarding duplicate request");
                    }
                }
            } catch (EOFException eof) {
                System.out.println("Connection closed");
            } catch (Exception ex) {
                System.out.println("Connection closed abruptly: " + ex);
            }
        }
    }

    private class ProcessorThread extends Thread {
        public ProcessorThread() {
            super("ProcessorThread");
            setDaemon(true);
        }

        public void run() {
            try {
                while (!isInterrupted()) {
                    try {
                        RobotAction action = jobs.take();
                        Object result = action.execute(robot);
                        if (result != null) {
                            out.writeObject(result);
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
                System.out.println("Connection closed (" + e + ')');
            }
        }
    }

    public void start() {
        processor.start();
        reader.start();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Parameters: server studentname");
            System.exit(1);
        }
        Student student = new Student(args[0], args[1]);
        student.start();
    }
}
