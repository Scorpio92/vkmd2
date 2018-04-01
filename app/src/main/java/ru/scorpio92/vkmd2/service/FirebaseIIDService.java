package ru.scorpio92.vkmd2.service;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ru.scorpio92.vkmd2.tools.LocalStorage;
import ru.scorpio92.vkmd2.tools.Logger;

public class FirebaseIIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Logger.log("Refreshed token: " + refreshedToken);
        try {
            LocalStorage.setDataInFile(this, LocalStorage.FCM_TOKEN, refreshedToken);
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
