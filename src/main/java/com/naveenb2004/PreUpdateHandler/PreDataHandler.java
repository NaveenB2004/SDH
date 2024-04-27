package com.naveenb2004.PreUpdateHandler;

import com.naveenb2004.DataHandler;
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
