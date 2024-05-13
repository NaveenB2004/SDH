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

package io.github.naveenb2004.SampleProject.Client;

import io.github.naveenb2004.SampleProject.Common.SampleObject;
import io.github.naveenb2004.SocketDataHandler.DataHandler;
import io.github.naveenb2004.SocketDataHandler.DataProcessor;
import io.github.naveenb2004.SocketDataHandler.PreDataHandler;
import io.github.naveenb2004.SocketDataHandler.SocketDataHandler;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientHelper extends SocketDataHandler {

    public ClientHelper(final Socket SOCKET) {
        super(SOCKET);
    }

    @Override
    public void onUpdateReceived(DataHandler update) {
        // handle completed update
        System.out.println("Client : Update received!");
        System.out.println("Client : Request = " + update.getRequest());
        System.out.println("Client : Timestamp = " + update.getTimestamp());
        System.out.println("Client : DataType = " + update.getDataType());
        System.out.println();
    }

    @Override
    public void onPreUpdateReceived(PreDataHandler preUpdate) {
        // implement it!
    }

    @Override
    public void onDisconnected(DataProcessor.DisconnectStatus status, Exception exception) {
        // process disconnections
        System.out.println("Client : " + status);
        if (exception != null) {
            throw new RuntimeException("Client : " + exception);
        }
    }

    // send sample text
    protected void sendText() {
        System.out.println("Client : Sending Text...");
        DataHandler dataHandler = new DataHandler("/SendText");
        try {
            send(dataHandler);
            System.out.println("Client : Text sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    // send sample file
    protected void sendFile() {
        System.out.println("Client : Sending File...");
        File file = new File(
                "src/main/java/io/github/naveenb2004/SampleProject/Common/SampleFile.jpg");
        DataHandler dataHandler = new DataHandler("/SendFile", file);

        try {
            send(dataHandler);
            System.out.println("Client : File sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    // send sample serializable object
    protected void sendObject() {
        System.out.println("Client : Sending Object...");
        SampleObject object = SampleObject.builder()
                .id(123456L)
                .name("SampleObjectName")
                .age(80)
                .isMale(true)
                .build();
        DataHandler dataHandler = new DataHandler("/SendObject", object);

        try {
            send(dataHandler);
            System.out.println("Client : Object sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }
}
