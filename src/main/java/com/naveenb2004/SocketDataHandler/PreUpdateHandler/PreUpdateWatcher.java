package com.naveenb2004.SocketDataHandler.PreUpdateHandler;

import lombok.NonNull;

public interface PreUpdateWatcher {
    void onPreUpdateSeen(@NonNull PreDataHandler preUpdate);
}
