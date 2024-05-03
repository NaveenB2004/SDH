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

package Client;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public void connect(int port) {
        try {
            Socket socket = new Socket("localhost", port);
            System.out.println("Client : Connected to server on " + socket.getRemoteSocketAddress());

            ClientHelper clientHelper = new ClientHelper(socket);

            clientHelper.sendText();
            Thread.sleep(3000);

            clientHelper.sendFile();
            Thread.sleep(3000);

            clientHelper.sendObject();
            Thread.sleep(3000);

            clientHelper.close();
            System.out.println("Client : Disconnected from server " + socket.getRemoteSocketAddress());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
