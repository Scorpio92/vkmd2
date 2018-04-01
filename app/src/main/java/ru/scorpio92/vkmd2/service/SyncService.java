package ru.scorpio92.vkmd2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.observers.DisposableObserver;
import ru.scorpio92.vkmd2.domain.usecase.GetAccountTracksUsecase;
import ru.scorpio92.vkmd2.tools.LocalStorage;
import ru.scorpio92.vkmd2.tools.Logger;

import static ru.scorpio92.vkmd2.BuildConfig.GET_AUDIO_OFFSET;


public class SyncService extends Service {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private final static long SYNC_DIFF = 24 * 3600 * 1000; // 24 * 3600 * 1000

    private final static int SYNC_START_DELAY = 1000;

    //ACTIONS from Notification or Activity to Service//////////////////////////////////////////////
    public final static String SERVICE_ACTION = "ACTION";

    public enum ACTION {
        START,
        STOP
    }

    public final static String IS_AUTO_SYNC = "IS_AUTO_SYNC";
    ////////////////////////////////////////////////////////////////////////////////////////////////


    //Event from Service to Activity or Notification////////////////////////////////////////////////
    public final static String SERVICE_BROADCAST = "VKMD2.SYNC_SERVICE.BROADCAST";
    public final static String SERVICE_EVENT = "EVENT";

    public enum EVENT {
        SYNC_WAS_COMPLETED,
        SYNC_START,
        SYNC_FINISH,
        SYNC_ERROR
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private GetAccountTracksUsecase usecase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log(LOG_TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.log(LOG_TAG, "onStartCommand, startId: " + startId);

        if (intent != null) {
            if (intent.getStringExtra(SERVICE_ACTION) != null) {
                ACTION action = Enum.valueOf(ACTION.class, intent.getStringExtra(SERVICE_ACTION));

                Logger.log(LOG_TAG, "onStartCommand, SERVICE_ACTION: " + action.name());

                switch (action) {
                    case START:
                        if (startId == 1)
                            try {
                                if (intent.getBooleanExtra(IS_AUTO_SYNC, false)) {
                                    if (checkSync(this)) {
                                        runSync();
                                    } else {
                                        sendBroadcastToActivity(EVENT.SYNC_WAS_COMPLETED);
                                    }
                                } else {
                                    runSync();
                                }
                            } catch (Exception e) {
                                Logger.error(e);
                                sendBroadcastToActivity(EVENT.SYNC_ERROR);
                            }
                        break;
                    case STOP:
                        finish();
                        break;
                }
            } else {
                sendBroadcastToActivity(EVENT.SYNC_ERROR);
            }
        } else {
            sendBroadcastToActivity(EVENT.SYNC_ERROR);
        }
        return Service.START_NOT_STICKY;
    }

    private void runSync() throws Exception {
        String cookie = LocalStorage.getDataFromFile(this, LocalStorage.COOKIE_STORAGE);
        int count = GET_AUDIO_OFFSET;
        try {
            String str = LocalStorage.getDataFromFile(this, LocalStorage.SYNC_TRACKS_COUNT_STORAGE);
            count = Integer.valueOf(str);
        } catch (Exception e) {
            Logger.error(e);
        }
        int finalCount = count;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                usecase = new GetAccountTracksUsecase(cookie, finalCount);
                usecase.execute(new DisposableObserver<String>() {
                    @Override
                    protected void onStart() {
                        sendBroadcastToActivity(EVENT.SYNC_START);
                    }

                    @Override
                    public void onNext(String uid) {
                        try {
                            LocalStorage.setDataInFile(SyncService.this, LocalStorage.USER_ID_STORAGE, uid);
                        } catch (Exception e) {
                            Logger.error(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        sendBroadcastToActivity(EVENT.SYNC_ERROR);
                    }

                    @Override
                    public void onComplete() {
                        try {
                            LocalStorage.setDataInFile(SyncService.this, LocalStorage.SYNC_LAST_TIME_STORAGE, String.valueOf(System.currentTimeMillis()));
                        } catch (Exception e) {
                            Logger.error(e);
                        }
                        sendBroadcastToActivity(EVENT.SYNC_FINISH);
                    }
                });
            }
        }, SYNC_START_DELAY);

    }


    private void sendBroadcastToActivity(EVENT event) {
        try {
            Intent intent = new Intent(SERVICE_BROADCAST);
            intent.putExtra(SERVICE_EVENT, event.name());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            if (event != EVENT.SYNC_START)
                finish();
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public static boolean checkSync(Context context) throws Exception {
        long lastSyncTime = Long.valueOf(LocalStorage.getDataFromFile(context, LocalStorage.SYNC_LAST_TIME_STORAGE));
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastSyncTime > SYNC_DIFF);
    }

    private void finish() {
        if (usecase != null) {
            usecase.cancel();
            usecase = null;
        }
        stopSelf();
    }
}
