package com.naveenb2004.UpdateHandler;

import com.naveenb2004.DataHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class PreDataHandler {
    private final long UUID;
    private final String request;
    @Setter(AccessLevel.PROTECTED)
    private DataHandler.DataType dataType;
    @Setter(AccessLevel.PROTECTED)
    private long totalDataSize = -1L;
    @Setter(AccessLevel.PROTECTED)
    private long transferredDataSize;
    @Setter(AccessLevel.PROTECTED)
    private boolean isCompleted;
    
    public PreDataHandler(long UUID, @NonNull String request) {
        this.UUID = UUID;
        this.request = request;
    }
}
