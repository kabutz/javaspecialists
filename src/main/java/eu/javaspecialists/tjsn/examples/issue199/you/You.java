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

package eu.javaspecialists.tjsn.examples.issue199.you;

import eu.javaspecialists.tjsn.examples.issue199.clowns.*;

/**
 * @author Wouter Coekaerts
 */
public class You {
    public static void main(String args[]) {
        // TODO put 20 clowns into a Volkswagen
        Volkswagen vw = new Volkswagen();
        for (int i = 0; i < 20; i++) {
            vw.add(new Clown());
        }
        vw.done();
    }
}