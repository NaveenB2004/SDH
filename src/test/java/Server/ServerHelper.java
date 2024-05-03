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

package Server;

import Common.SampleObject;
import com.naveenb2004.SocketDataHandler.DataHandler;
import com.naveenb2004.SocketDataHandler.DataProcessor;
import com.naveenb2004.SocketDataHandler.PreUpdateHandler.PreDataHandler;
import com.naveenb2004.SocketDataHandler.PreUpdateHandler.PreUpdateHandler;
import com.naveenb2004.SocketDataHandler.PreUpdateHandler.PreUpdateWatcher;
import com.naveenb2004.SocketDataHandler.SocketDataHandler;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class ServerHelper extends SocketDataHandler implements PreUpdateWatcher {

    public ServerHelper(@NonNull final Socket SOCKET) {
        super(SOCKET);
        PreUpdateHandler preUpdateHandler = getPRE_UPDATE_HANDLER();
        preUpdateHandler.addWatcher(this);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(@NonNull DataHandler update) {
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
    public void onDisconnected(DataProcessor.@NonNull DisconnectStatus status, Exception exception) {
        System.out.println("Server : " + status);
        if (exception != null) {
            throw new RuntimeException("Server : " + exception);
        }
    }

    @Override
    public void onPreUpdateSeen(@NonNull PreDataHandler preUpdate) {
        System.out.println("Server : Pre update received!");
        System.out.println("Server : Request = " + preUpdate.getRequest());
        System.out.println("Server : Update method = " + preUpdate.getMethod());
        System.out.println("Server : DataType = " + preUpdate.getDataType());
        System.out.println("Server : Total data size = " + preUpdate.getTotalDataSize());
        System.out.println("Server : Transferred data size = " + preUpdate.getTransferredDataSize());
        System.out.println("Server : Is completed = " + preUpdate.isCompleted());
        System.out.println();
    }

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
