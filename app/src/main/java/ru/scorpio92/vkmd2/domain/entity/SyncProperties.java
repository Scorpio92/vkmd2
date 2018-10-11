package ru.scorpio92.vkmd2.domain.entity;

public class SyncProperties {

    private boolean autoSync;
    /**
     * кол-во треков для синхронизации
     */
    private int syncCount;
    /**
     * таймстамп. время последней синхронизации
     */
    private long lastSyncTime;

    public SyncProperties(boolean autoSync, int syncCount) {
        this(autoSync, syncCount, System.currentTimeMillis());
    }

    public SyncProperties(boolean autoSync, int syncCount, long lastSyncTime) {
        this.autoSync = autoSync;
        this.syncCount = syncCount;
        this.lastSyncTime = lastSyncTime;
    }

    public int getSyncCount() {
        return syncCount;
    }

    public long getLastSyncTime() {
        return lastSyncTime;
    }
}
