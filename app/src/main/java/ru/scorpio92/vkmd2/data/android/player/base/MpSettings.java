package ru.scorpio92.vkmd2.data.android.player.base;

import java.util.Map;

/**
 * Настройки плеера
 */
public class MpSettings {

    private Map<Integer, MpFeature> settings;

    public MpSettings(Map<Integer, MpFeature> settings) {
        this.settings = settings;
    }

    public Map<Integer, MpFeature> getSettings() {
        return settings;
    }

    public void changeFeature(MpFeature mpFeature) {
        if (settings.containsKey(mpFeature.getId())) {
            settings.remove(mpFeature.getId());
            settings.put(mpFeature.getId(), mpFeature);
        }
    }
}
