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

package com.naveenb2004.SocketDataHandler.PreUpdateHandler;

import com.naveenb2004.SocketDataHandler.DataHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class PreDataHandler {
    public enum Method {
        SEND,
        RECEIVE
    }

    private final PreUpdateHandler preUpdateHandler;
    private final long UUID;
    private final String request;
    private final Method method;
    private final DataHandler.DataType dataType;
    @Setter
    private long totalDataSize;
    private long transferredDataSize;
    private boolean isCompleted;

    public PreDataHandler(@NonNull PreUpdateHandler preUpdateHandler, long UUID, @NonNull String request,
                          @NonNull Method method, @NonNull DataHandler.DataType dataType) {
        this.preUpdateHandler = preUpdateHandler;
        this.UUID = UUID;
        this.request = request;
        this.method = method;
        this.dataType = dataType;

        preUpdateHandler.update(this);
    }

    public void setTransferredDataSize(long transferredDataSize) {
        this.transferredDataSize = transferredDataSize;
        preUpdateHandler.update(this);
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
        preUpdateHandler.update(this);
    }
}
