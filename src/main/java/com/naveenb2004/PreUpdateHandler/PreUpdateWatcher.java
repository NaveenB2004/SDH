package com.naveenb2004.PreUpdateHandler;

import lombok.NonNull;

public interface PreUpdateWatcher {
    void onPreUpdateSeen(@NonNull PreDataHandler preUpdate);
}
