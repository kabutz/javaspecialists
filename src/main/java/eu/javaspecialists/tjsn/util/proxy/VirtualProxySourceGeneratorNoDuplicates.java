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

package eu.javaspecialists.tjsn.util.proxy;

import java.io.*;

class VirtualProxySourceGeneratorNoDuplicates
        extends VirtualProxySourceGenerator {

    public VirtualProxySourceGeneratorNoDuplicates(
            Class subject, Class realSubject) {
        super(subject, realSubject, Concurrency.NO_DUPLICATES);
    }

    protected void addImports(PrintWriter out) {
        out.println("import java.util.concurrent.locks.*;");
        out.println();
    }

    protected void addRealSubjectCreation(PrintWriter out,
                                          String name,
                                          String realName) {
        out.printf("  private volatile %s realSubject;%n", name);
        out.println("  private final Lock initializationLock = " +
                "new ReentrantLock();");
        out.println();
        out.printf("  private %s realSubject() {%n", name);
        out.printf("    %s result = realSubject;%n", name);
        out.printf("    if (result == null) {%n");
        out.printf("      initializationLock.lock();%n");
        out.printf("      try {%n");
        out.printf("        result = realSubject;%n");
        out.printf("        if (result == null) {%n");
        out.printf("          result = realSubject = new %s();%n",
                realName);
        out.printf("        }%n");
        out.printf("      } finally {%n");
        out.printf("        initializationLock.unlock();%n");
        out.printf("      }%n");
        out.printf("    }%n");
        out.printf("    return result;%n");
        out.println("  }");
    }
}
