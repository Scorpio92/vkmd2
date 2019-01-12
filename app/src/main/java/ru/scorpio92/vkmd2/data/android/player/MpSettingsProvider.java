package ru.scorpio92.vkmd2.data.android.player;

import ru.scorpio92.vkmd2.data.android.player.base.IMpSettingsProvider;
import ru.scorpio92.vkmd2.data.android.player.base.MpSettings;
import ru.scorpio92.vkmd2.data.datasource.internal.base.AbstractLocalDataSource;
import ru.scorpio92.vkmd2.tools.JsonWorker;

public class MpSettingsProvider extends AbstractLocalDataSource implements IMpSettingsProvider {

    @Override
    protected String provideStoreName() {
        return "mp_features";
    }

    @Override
    protected boolean enableEncryption() {
        return false;
    }

    @Override
    public void saveSettings(MpSettings mpSettings) throws Exception {
        saveData(JsonWorker.getSerializeJson(mpSettings));
    }

    @Override
    public MpSettings loadSettings() throws Exception {
        return JsonWorker.getDeserializeJson(getData(), MpSettings.class);
    }
}
