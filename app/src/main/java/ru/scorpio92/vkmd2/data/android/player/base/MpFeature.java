package ru.scorpio92.vkmd2.data.android.player.base;

/**
 * Настройка плеера
 */
public class MpFeature {

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

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(obj instanceof MpFeature) {
            MpFeature feature = (MpFeature) obj;
            return feature.getId() == this.getId();
        } else {
            return false;
        }
    }
}
