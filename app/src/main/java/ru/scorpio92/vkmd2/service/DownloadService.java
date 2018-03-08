package ru.scorpio92.vkmd2.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.data.repository.network.DownloadTrackRepo;
import ru.scorpio92.vkmd2.data.repository.network.core.INetworkRepository;
import ru.scorpio92.vkmd2.data.repository.network.specifications.DownloadTrack;
import ru.scorpio92.vkmd2.presentation.view.activity.DownloadManagerActivity;
import ru.scorpio92.vkmd2.tools.Logger;
import ru.scorpio92.vkmd2.tools.VkmdUtils;


public class DownloadService extends Service {

    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String DEFAULT_DOWNLOAD_FOLDER = "vkmd";
    public static final String DEFAULT_DOWNLOAD_PATH = System.getenv("EXTERNAL_STORAGE") + "/" + DEFAULT_DOWNLOAD_FOLDER;
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
    private boolean stopService;

    private INetworkRepository downloadTrackRepo;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log(LOG_TAG, "onCreate");
        stopService = false;
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
                            if(downloadPause) {
                                markFile(CachedTrack.TRACK_NOT_DOWNLOADED);
                                if (downloadTrackRepo != null)
                                    downloadTrackRepo.cancel();
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
        File file = new File(DEFAULT_DOWNLOAD_PATH);
        return file.exists() || file.mkdir();
    }

    private void startDownload() {
        new Thread(() -> {
            try {
                downloadTrack = AppDatabase.getInstance().cacheDAO().getTrackByTrackId(downloadTrackId);

                Logger.log(LOG_TAG, "start for download downloadTrack: " + downloadTrack.getName());

                String urlString = VkmdUtils.decode(downloadTrack.getUrlAudio(), Integer.valueOf(downloadTrack.getUserId()));

                final long[] lastTime = {System.currentTimeMillis()};
                percentCurrent = 0;

                downloadTrackRepo = new DownloadTrackRepo(new DownloadTrackRepo.Callback() {
                    @Override
                    public void onProgressUpdate(int progress) {
                        percentCurrent = progress;
                        if (System.currentTimeMillis() - lastTime[0] > DOWNLOAD_PROGRESS_UPDATE_INTERVAL) {
                            lastTime[0] = System.currentTimeMillis();
                            sendBroadcastToActivity(EVENT.DOWNLOAD_PROGRESS_UPDATE);
                        }
                    }

                    @Override
                    public void onComplete(byte[] bytes) {
                        OutputStream output = null;
                        try {
                            String savedPath = DEFAULT_DOWNLOAD_PATH + "/" + getAudioFileName(downloadTrack);
                            downloadTrack.setSavedPath(savedPath);
                            output = new FileOutputStream(savedPath);
                            output.write(bytes);
                            markFile(CachedTrack.TRACK_DOWNLOADED);
                            sendBroadcastToActivity(EVENT.TRACK_DOWNLOAD_COMPLETE);
                        } catch (Exception e) {
                            Logger.error(e);
                            sendBroadcastToActivity(EVENT.TRACK_DOWNLOAD_ERROR);
                            markFile(CachedTrack.TRACK_DOWNLOAD_ERROR);
                        } finally {
                            if (output != null) {
                                try {
                                    output.flush();
                                    output.close();
                                } catch (Exception e) {
                                    Logger.error(e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Logger.error(e);
                        sendBroadcastToActivity(EVENT.TRACK_DOWNLOAD_ERROR);
                        markFile(CachedTrack.TRACK_DOWNLOAD_ERROR);
                    }
                });
                downloadTrackRepo.execute(new DownloadTrack(urlString));

                if (stopService)
                    return;

                while (downloadPause) {
                    try {
                        Logger.log(LOG_TAG, "pause");
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        Logger.error(e);
                    }
                }


                //проверяем - качать дальше или закрывать сервис
                if (checkFileForDownload()) {
                    startDownload();
                } else {
                    sendBroadcastToActivity(EVENT.GENERAL_DOWNLOAD_COMPLETE);
                    finish();
                }
            } catch (Exception e) {
                Logger.error(e);
                sendBroadcastToActivity(EVENT.TRACK_DOWNLOAD_ERROR);
                markFile(CachedTrack.TRACK_DOWNLOAD_ERROR);
            }
        }).start();
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

    private void sentNotificationInForeground() {
        try {
            Intent notificationIntent = new Intent(this, DownloadManagerActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            PendingIntent pendingIntentStartPause = PendingIntent.getBroadcast(this, 0, new Intent().setAction(NOTIFICATION_ACTION_START_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 0, new Intent().setAction(NOTIFICATION_ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

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
            builder.setSmallIcon(R.mipmap.ic_file_download_black_24dp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            }
            builder.setPriority(Notification.PRIORITY_MAX);

            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);

        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private void finish() {
        stopService = true;

        if (downloadTrackRepo != null)
            downloadTrackRepo.cancel();

        Logger.log(LOG_TAG, "finish");

        stopForeground(true);
        stopSelf();
    }
}

