package com.naveenb2004.SocketDataHandler.PreUpdateHandler;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PreUpdateHandler {
    protected final List<PreUpdateWatcher> watchers = new ArrayList<>();

    public void addWatcher(PreUpdateWatcher watcher) {
        watchers.add(watcher);
    }

    public void removeWatcher(PreUpdateWatcher watcher) {
        watchers.remove(watcher);
    }

    protected void update(@NonNull PreDataHandler preUpdate) {
        for (PreUpdateWatcher watcher : watchers) {
            watcher.onPreUpdateSeen(preUpdate);
        }
    }
}
