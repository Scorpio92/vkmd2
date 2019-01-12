package ru.scorpio92.vkmd2.data.android.player.base;

/**
 * Настройка плеера
 */
public class MpFeature {

    /**
     * зацикленное проигрываение текущего трека
     */
    public static int FEATURE_LOOP = 0;
    /**
     * рандомное проигрывание списка
     */
    public static int FEATURE_RANDOM = 1;

    private int id;
    private boolean enabled;

    public MpFeature(int id, boolean enabled) {
        this.id = id;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
