package com.naveenb2004.UpdateHandler;

import java.util.ArrayList;
import java.util.List;

public class UpdateHandler {
    protected final List<UpdateWatcher> watchers = new ArrayList<>();

    public void addWatcher(UpdateWatcher watcher) {
        watchers.add(watcher);
    }

    public void removeWatcher(UpdateWatcher watcher) {
        watchers.remove(watcher);
    }

    protected void update() {
        for (UpdateWatcher watcher : watchers) {
            watcher.onUpdateSeen();
        }
    }
}
