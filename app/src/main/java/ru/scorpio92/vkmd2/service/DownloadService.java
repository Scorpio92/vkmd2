package ru.scorpio92.vkmd2.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.widget.RemoteViews;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.repository.db.base.old.AppDatabase;
import ru.scorpio92.vkmd2.data.repository.network.DownloadAudioRepo;
import ru.scorpio92.vkmd2.presentation.view.activity.DownloadManagerActivity;
import ru.scorpio92.vkmd2.tools.Logger;
import ru.scorpio92.vkmd2.tools.VkmdUtils;

import static ru.scorpio92.vkmd2.App.APP_DIR;
import static ru.scorpio92.vkmd2.BuildConfig.MUSIC_FOLDER;


public class DownloadService extends Service {

    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final int FILE_NAME_LENGTH_LIMIT = 255 - 7;
    public static final String MP3_EXT = ".mp3";
    private static final int DOWNLOAD_PROGRESS_UPDATE_INTERVAL = 1000; //ms

    //Event from Service to Activity or Notification////////////////////////////////////////////////
    public final static String SERVICE_BROADCAST = "VKMD2.DOWNLOAD_SERVICE.BROADCAST";
    public final static String SERVICE_EVENT = "EVENT";

    public enum EVENT {
        GENERAL_START_DOWNLOAD,
        DOWNLOAD_PROGRESS_UPDATE,
        TRACK_DOWNLOAD_ERROR,
        GENERAL_DOWNLOAD_ERROR,
        NOTHING_DOWNLOAD,
        TRACK_DOWNLOAD_COMPLETE,
        GENERAL_DOWNLOAD_COMPLETE,
        DOWNLOAD_ACTIVE,
        DOWNLOAD_INACTIVE
    }

    public final static String AUDIO_TRACK_NAME_PARAM = "TRACK_NAME";
    public final static String AUDIO_TRACK_ARTIST_PARAM = "TRACK_ARTIST";
    public final static String AUDIO_TRACK_PROGRESS_PARAM = "TRACK_PROGRESS";
    public final static String AUDIO_TRACK_ID_PARAM = "TRACK_ID";
    ////////////////////////////////////////////////////////////////////////////////////////////////


    //ACTIONS from Notification or Activity to Service//////////////////////////////////////////////
    public final static String SERVICE_ACTION = "ACTION";

    public enum ACTION {
        START_DOWNLOAD,
        GET_INFO,
        START_PAUSE,
        STOP
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int NOTIFICATION_ID = 6662;
    public final static String NOTIFICATION_ACTION_START_PAUSE = "VKMD2.DOWNLOAD_SERVICE.START_PAUSE";
    public final static String NOTIFICATION_ACTION_STOP = "VKMD2.DOWNLOAD_SERVICE.STOP";

    private String downloadTrackId;
    private CachedTrack downloadTrack;
    private int percentCurrent;

    private boolean downloadPause;
    private Disposable disposable;

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
                    case START_DOWNLOAD:
                        checkForNeedDownload();
                        break;
                    case GET_INFO:
                        sendBroadcastToActivity(!downloadPause && startId != 1 ? EVENT.DOWNLOAD_ACTIVE : EVENT.DOWNLOAD_INACTIVE);

                        if (startId == 1)
                            finish();
                        break;
                    case START_PAUSE:
                        if (startId == 1) {
                            checkForNeedDownload();
                        } else {
                            downloadPause = !downloadPause;
                            if (downloadPause) {
                                markFile(CachedTrack.TRACK_NOT_DOWNLOADED);
                                if (disposable != null)
                                    disposable.dispose();
                            } else {
                                checkForNeedDownload();
                            }

                            sendBroadcastToActivity(downloadPause ? EVENT.DOWNLOAD_INACTIVE : EVENT.DOWNLOAD_ACTIVE);

                            sentNotificationInForeground();
                        }
                        break;
                    case STOP:
                        finish();
                        break;
                }
            }
        }

        return Service.START_NOT_STICKY;
    }

    private void checkForNeedDownload() {
        if (checkDownloadFolder()) {
            if (checkFileForDownload()) {
                sendBroadcastToActivity(EVENT.GENERAL_START_DOWNLOAD);
                startDownload();
            } else {
                sendBroadcastToActivity(EVENT.NOTHING_DOWNLOAD);
                finish();
            }
        } else {
            sendBroadcastToActivity(EVENT.GENERAL_DOWNLOAD_ERROR);
            finish();
        }
    }

    private boolean checkDownloadFolder() {
        File file = new File(APP_DIR + "/" + MUSIC_FOLDER);
        return file.exists() || file.mkdirs();
    }

    //проверяем - качать дальше или закрывать сервис
    private void onTerminate() {
        if (checkFileForDownload()) {
            startDownload();
        } else {
            sendBroadcastToActivity(EVENT.GENERAL_DOWNLOAD_COMPLETE);
            finish();
        }
    }

    private void startDownload() {
        disposable = Observable.fromCallable(() -> {
            downloadTrack = AppDatabase.getInstance().cacheDAO().getTrackByTrackId(downloadTrackId);
            Logger.log(LOG_TAG, "start for download downloadTrack: " + downloadTrack.getName());
            String url = VkmdUtils.decode(downloadTrack.getUrlAudio(), Integer.valueOf(downloadTrack.getUserId()));
            String savePath = APP_DIR + "/" + MUSIC_FOLDER + "/" + getAudioFileName(downloadTrack);
            downloadTrack.setSavedPath(savePath);
            return new Pair<>(url, savePath);
        }).flatMap(pair -> new DownloadAudioRepo(pair.first, pair.second).downloadTrack())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Integer>() {

                    final long[] lastTime = {System.currentTimeMillis()};

                    @Override
                    public void onNext(Integer integer) {
                        percentCurrent = integer;
                        if (System.currentTimeMillis() - lastTime[0] > DOWNLOAD_PROGRESS_UPDATE_INTERVAL) {
                            lastTime[0] = System.currentTimeMillis();
                            sendBroadcastToActivity(EVENT.DOWNLOAD_PROGRESS_UPDATE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.error((Exception) e);
                        sendBroadcastToActivity(EVENT.TRACK_DOWNLOAD_ERROR);
                        markFile(CachedTrack.TRACK_DOWNLOAD_ERROR);
                        onTerminate();
                    }

                    @Override
                    public void onComplete() {
                        markFile(CachedTrack.TRACK_DOWNLOADED);
                        sendBroadcastToActivity(EVENT.TRACK_DOWNLOAD_COMPLETE);
                        onTerminate();
                    }
                });

    }

    private void markFile(int status) {
        try {
            switch (status) {
                case CachedTrack.TRACK_DOWNLOADED:
                    AppDatabase.getInstance().cacheDAO().markFileAsDownloaded(downloadTrack.getTrackId(), downloadTrack.getSavedPath());
                    break;
                case CachedTrack.TRACK_DOWNLOAD_ERROR:
                    AppDatabase.getInstance().cacheDAO().markFileAsError(downloadTrack.getTrackId());
                    break;
                case CachedTrack.TRACK_NOT_DOWNLOADED:
                    AppDatabase.getInstance().cacheDAO().markFileAsNotDownloaded(downloadTrack.getTrackId());
                    break;
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private boolean checkFileForDownload() {
        downloadTrackId = null;
        try {
            downloadTrackId = AppDatabase.getInstance().cacheDAO().getTrackIdForDownload();
        } catch (Exception e) {
            Logger.error(e);
        }
        return downloadTrackId != null;
    }

    private String getAudioFileName(CachedTrack track) throws Exception {
        String artist = track.getArtist();
        String name = track.getName();
        if (artist.concat(name).length() >= FILE_NAME_LENGTH_LIMIT) {
            if (artist.length() >= FILE_NAME_LENGTH_LIMIT) {
                return track.getTrackId().concat(MP3_EXT);
            } else {
                int maxLenghtName = FILE_NAME_LENGTH_LIMIT - artist.length();
                String cuttedName = name.substring(0, maxLenghtName);
                return artist.concat(" - ").concat(cuttedName).concat(MP3_EXT);
            }
        } else {
            return artist.concat(" - ").concat(name).concat(MP3_EXT);
        }
    }

    private void sendBroadcastToActivity(EVENT event) {
        try {
            Intent intent = new Intent(SERVICE_BROADCAST);
            intent.putExtra(SERVICE_EVENT, event.name());
            switch (event) {
                case GENERAL_START_DOWNLOAD:
                case GENERAL_DOWNLOAD_ERROR:
                case GENERAL_DOWNLOAD_COMPLETE:
                case NOTHING_DOWNLOAD:
                case DOWNLOAD_ACTIVE:
                case DOWNLOAD_INACTIVE:
                    break;
                case DOWNLOAD_PROGRESS_UPDATE:
                    intent.putExtra(AUDIO_TRACK_ID_PARAM, downloadTrack.getTrackId());
                    intent.putExtra(AUDIO_TRACK_ARTIST_PARAM, downloadTrack.getArtist());
                    intent.putExtra(AUDIO_TRACK_NAME_PARAM, downloadTrack.getName());
                    intent.putExtra(AUDIO_TRACK_PROGRESS_PARAM, percentCurrent);

                    sentNotificationInForeground();
                    break;
                case TRACK_DOWNLOAD_COMPLETE:
                case TRACK_DOWNLOAD_ERROR:
                    intent.putExtra(AUDIO_TRACK_ID_PARAM, downloadTrackId);
                    break;
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private static final String channelId = "vkmd2_download_channel";
    private static final String channelName = "Channel download VKMD2";
    private NotificationManager notificationManager;
    private NotificationChannel mChannel;

    private void sentNotificationInForeground() {
        try {
            Intent notificationIntent = new Intent(this, DownloadManagerActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            PendingIntent pendingIntentStartPause = PendingIntent.getBroadcast(this, 0, new Intent().setAction(NOTIFICATION_ACTION_START_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 0, new Intent().setAction(NOTIFICATION_ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT);

            if (notificationManager == null) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (mChannel == null) {
                    mChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                    mChannel.enableVibration(false);
                    mChannel.setVibrationPattern(null);
                    mChannel.enableLights(false);
                    mChannel.setSound(null, null);
                    notificationManager.createNotificationChannel(mChannel);
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_service_notification);

            contentView.setTextViewText(R.id.trackName, downloadTrack.getArtist() + " - " + downloadTrack.getName());

            contentView.setTextViewText(R.id.downloadProgress, String.valueOf(percentCurrent) + "%");

            contentView.setOnClickPendingIntent(R.id.sp, pendingIntentStartPause);
            if (downloadPause) {
                contentView.setImageViewResource(R.id.sp, R.mipmap.play);
            } else {
                contentView.setImageViewResource(R.id.sp, R.mipmap.pause);
            }

            contentView.setOnClickPendingIntent(R.id.stop, pendingIntentStop);

            builder.setContentIntent(pendingIntent);
            builder.setContent(contentView);
            builder.setCustomBigContentView(contentView);
            builder.setSmallIcon(R.mipmap.note);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            }
            builder.setPriority(Notification.PRIORITY_DEFAULT);
            builder.setOnlyAlertOnce(true);
            builder.setSound(null);
            builder.setVibrate(null);

            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);

        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private void finish() {
        if (disposable != null)
            disposable.dispose();

        Logger.log(LOG_TAG, "finish");

        stopForeground(true);
        stopSelf();
    }
}

