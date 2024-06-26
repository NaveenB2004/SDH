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

package io.github.naveenb2004.SampleProject.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void connect(int port) {
        new Thread(() -> {
            try {
                // create new server socket
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Server : Server started on port " + port);

                System.out.println("Server : Waiting for client...");
                // accept clients
                Socket socket = serverSocket.accept();
                System.out.println("Server : Client connected to server on  : " + socket.getRemoteSocketAddress());

                // connect socket with library
                ServerHelper serverHelper = new ServerHelper(socket);

                serverHelper.sendText();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
