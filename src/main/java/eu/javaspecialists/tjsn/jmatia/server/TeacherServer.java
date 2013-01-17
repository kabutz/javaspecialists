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

import javax.swing.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;

public class TeacherServer {
    public static final int PORT = 5555;

    private final SocketWriterThread writer;
    private final TeacherFrame frame;
    private final SocketReaderThread reader;

    public TeacherServer(Socket socket)
            throws IOException, ClassNotFoundException,
            InvocationTargetException, InterruptedException {
        ObjectOutputStream out = new ObjectOutputStream(
                socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(
                        socket.getInputStream()));
        System.out.println("waiting for student name ...");
        final String studentName = (String) in.readObject();

        reader = new SocketReaderThread(studentName, in, this);
        writer = new SocketWriterThread(studentName, out);

        final TeacherFrame[] temp = new TeacherFrame[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                temp[0] = new TeacherFrame(studentName,
                        TeacherServer.this, writer);
            }
        });
        frame = temp[0];

        reader.start();
        writer.start();

        System.out.println("finished connecting to " + socket);
    }

    public void showScreenShot(byte[] bytes) throws IOException {
        frame.showScreenShot(bytes);
    }

    public void shutdown() {
        writer.interrupt();
        reader.close();
    }

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(PORT);
        while (true) {
            Socket socket = ss.accept();
            System.out.println("Connection From " + socket);
            new TeacherServer(socket);
        }
    }
}
