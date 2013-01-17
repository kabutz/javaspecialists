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

import java.io.*;

class SocketReaderThread extends Thread {
    private final String studentName;
    private final ObjectInputStream in;
    private final TeacherServer server;

    public SocketReaderThread(
            String studentName,
            ObjectInputStream in,
            TeacherServer server) {
        super("Reader from " + studentName);
        this.studentName = studentName;
        this.in = in;
        this.server = server;
    }

    public void run() {
        while (true) {
            try {
                byte[] img = (byte[]) in.readObject();
                System.out.println("Received screenshot of " +
                        img.length + " bytes from " + studentName);
                server.showScreenShot(img);
            } catch (Exception ex) {
                System.out.println("Exception occurred: " + ex);
                ex.printStackTrace();
                server.shutdown();
                return;
            }
        }
    }

    public void close() {
        try {
            in.close();
        } catch (IOException ignore) {
        }
    }
}
