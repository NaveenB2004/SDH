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

import io.github.naveenb2004.SampleProject.Common.SampleObject;
import io.github.naveenb2004.SocketDataHandler.DataHandler;
import io.github.naveenb2004.SocketDataHandler.DataProcessor;
import io.github.naveenb2004.SocketDataHandler.PreDataHandler;
import io.github.naveenb2004.SocketDataHandler.SocketDataHandler;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class ServerHelper extends SocketDataHandler {

    public ServerHelper(final Socket SOCKET) {
        super(SOCKET);
    }

    @Override
    public void onUpdateReceived(DataHandler update) {
        // handle completed update
        System.out.println("Server : Update received!");
        System.out.println("Server : Request = " + update.getRequest());
        System.out.println("Server : Timestamp = " + update.getTimestamp());
        System.out.println("Server : DataType = " + update.getDataType());

        if (update.getDataType() == DataHandler.DataType.FILE) {
            try {
                Desktop.getDesktop().open(update.getFile());
                System.out.println("Server : File opened!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (update.getDataType() == DataHandler.DataType.OBJECT) {
            SampleObject object = (SampleObject) update.getData();
            System.out.println("Server : Object Details = " + object.toString());
        }

        System.out.println();
    }

    @Override
    public void onPreUpdateReceived(PreDataHandler preUpdate) {
        // handle pre-updates
        System.out.println("Server : Pre update received!");
        System.out.println("Server : Request = " + preUpdate.getRequest());
        System.out.println("Server : Update method = " + preUpdate.getMethod());
        System.out.println("Server : DataType = " + preUpdate.getDataType());
        System.out.println("Server : Total data size = " + preUpdate.getTotalDataSize());
        System.out.println("Server : Transferred data size = " + preUpdate.getTransferredDataSize());
        System.out.println("Server : Is completed = " + preUpdate.isCompleted());
        System.out.println();
    }

    @Override
    public void onDisconnected(DataProcessor.DisconnectStatus status, Exception exception) {
        // process disconnections
        System.out.println("Server : " + status);
        if (exception != null) {
            throw new RuntimeException("Server : " + exception);
        }
    }

    // send text from server, to client
    protected void sendText() {
        System.out.println("Server : Sending Text...");
        DataHandler dataHandler = new DataHandler("/SendTextByServer");
        try {
            send(dataHandler);
            System.out.println("Server : Text sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }
}
