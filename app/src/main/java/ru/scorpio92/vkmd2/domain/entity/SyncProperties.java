package ru.scorpio92.vkmd2.domain.entity;

public class SyncProperties {

    /**
     * Признак синхронизации через экран синхронизации
     */
    private boolean manualSync;
    /**
     * кол-во треков для синхронизации
     */
    private int syncCount;
    /**
     * таймстамп. время последней синхронизации
     */
    private long lastSyncTime;

    public SyncProperties(boolean manualSync, int syncCount) {
        this(manualSync, syncCount, System.currentTimeMillis());
    }

    public SyncProperties(boolean manualSync, int syncCount, long lastSyncTime) {
        this.manualSync = manualSync;
        this.syncCount = syncCount;
        this.lastSyncTime = lastSyncTime;
    }

    public boolean isManualSync() {
        return manualSync;
    }

    public int getSyncCount() {
        return syncCount;
    }

    public long getLastSyncTime() {
        return lastSyncTime;
    }
}
