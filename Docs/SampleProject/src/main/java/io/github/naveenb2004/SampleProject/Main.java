/*
 * Copyright 2024 Naveen Balasooriya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.naveenb2004.SampleProject;

import io.github.naveenb2004.SampleProject.Client.Client;
import io.github.naveenb2004.SampleProject.Server.Server;
import io.github.naveenb2004.SocketDataHandler.SocketDataHandler;

import java.io.File;

public class Main {

    // socket port
    public static final int port = 2004;

    public static void main(String[] args) {
        // set default maximum buffer size for communications
        SocketDataHandler.setDefaultBufferSize(1024);
        // set default temporary folder for files downloading
        SocketDataHandler.setTempFolder(new File("Temp"));

        // open server socket
        Server.connect(port);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // connect with the server
        Client.connect(port);
    }

}
