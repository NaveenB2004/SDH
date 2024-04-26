package com.naveenb2004.PreUpdateHandler;

import com.naveenb2004.DataHandler;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class PreDataHandler {
    private final PreUpdateHandler preUpdateHandler;
    private final long UUID;
    private final String request;
    private final DataHandler.DataType dataType;
    private long totalDataSize = -1L;
    private long transferredDataSize;
    private boolean isCompleted;

    public PreDataHandler(@NonNull PreUpdateHandler preUpdateHandler, long UUID, @NonNull String request,
                          @NonNull DataHandler.DataType dataType) {
        this.preUpdateHandler = preUpdateHandler;
        this.UUID = UUID;
        this.request = request;
        this.dataType = dataType;

        preUpdateHandler.update(this);
    }

    protected void setTotalDataSize(long totalDataSize) {
        this.totalDataSize = totalDataSize;
        preUpdateHandler.update(this);
    }

    protected void setTransferredDataSize(long transferredDataSize) {
        this.transferredDataSize = transferredDataSize;
        preUpdateHandler.update(this);
    }

    protected void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
        preUpdateHandler.update(this);
    }
}
