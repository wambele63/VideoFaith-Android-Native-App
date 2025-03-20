package videos.religious.platform;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadListener;
import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.config.Config;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DownloadBackAgent extends Service {
    private String duration;
    private String downloadname = "";
    private String videotodownload="";
    private FirebaseStorage store;
    private FirebaseFirestore db;
    static boolean isDownloadRunning=false;
    private int Nid = 3;
    public DownloadBackAgent() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @Override
    public void onCreate() {
        store = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        this.startForeground(Nid,new Notification());
    }
    private static Intent n;
    private int idn = 1;
    private int flg;
    private ArrayList pendingList = new ArrayList();

    private DownloadManager downloadManager;
    private DownloadInfo downloadInfo;
    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        try {
                n = intent;
            flg = flags;
            downloadname = intent.getStringExtra("downloadname");
            videotodownload = intent.getStringExtra("videotodownload");
            if(!isDownloadRunning) {
                downloadVideo(videotodownload, downloadname);
            } else {
                Map<String, Object> pendingDownload = new HashMap<>();
                pendingDownload.put("downloadname", downloadname);
                pendingDownload.put("videotodownload", videotodownload);
                pendingList.add(pendingDownload);
                Toast.makeText(this, "Your Download Has Been Queed", Toast.LENGTH_LONG).show();
                return START_STICKY;
            }
        }catch (Exception ex){
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        DownloadBackAgent.isDownloadRunning=false;
    }
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private StorageReference pRefs,psRefs;
    private int videosize;
    private File path;
    private void downloadVideo(String videourl,String name){
        DownloadBackAgent.isDownloadRunning=true;
        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/");

            if (!directory.exists()) {
                directory.mkdirs();
            }
            path = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + name + ".mp4");
            if(path.exists()){
                Toast.makeText(DownloadBackAgent.this, "Video aLready downloaded", Toast.LENGTH_SHORT).show();
                return;
            }
            path.createNewFile();

            Config config = new Config();
            //set database path
            config.setDownloadThread(3);

            //set each download info thread number
            config.setEachDownloadThread(3);

            // set connect timeout,unit millisecond
            config.setConnectTimeout(120000);
            config.setRetryDownloadCount(2);
            // set read data timeout,unit millisecond
            config.setReadTimeout(120000);
            downloadManager = DownloadService.getDownloadManager(DownloadBackAgent.this, config);
            downloadInfo = new DownloadInfo.Builder()
                    .setUrl(videourl)
                    .setPath(path.toString())
                    .build();
            //set download callback.
            downloadInfo.setDownloadListener(new DownloadListener() {

                @Override
                public void onStart() {
                    mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(DownloadBackAgent.this, "1");
                    mBuilder.setContentTitle(name)
                            .setContentText("Downloading in progress")
                            .setOngoing(true)
                            .setAllowSystemGeneratedContextualActions(true)
                            .setGroup("downloading")
                            .setPriority(1)
                            .setOnlyAlertOnce(true)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_cloud_download_black_24dp);
                    mNotifyManager.notify(Nid,mBuilder.build());
                }
                @Override
                public void onWaited() {
                }

                @Override
                public void onPaused() {
                }

                @Override
                public void onDownloading(long progress, long size) {
                    double incr = (100.0 * progress) / size;
                    final int incrr = (int) incr;
                    mBuilder.setProgress(100, incrr, false)
                            .setSound(null);
                    mNotifyManager.notify(Nid,mBuilder.build());
                }

                @Override
                public void onRemoved() {
                    isDownloadRunning=false;
                    downloadInfo = null;
                }
                @Override
                public void onDownloadSuccess() {
                    mNotifyManager.cancel(Nid);
                    isDownloadRunning = false;
                    if(pendingList.size() > 0) {
                        try {
                            JSONArray f;
                                f = new JSONArray(pendingList);
                                JSONObject pendObject = f.getJSONObject(0);
                                String downloadname = pendObject.getString("downloadname");
                                String videotodownload = pendObject.getString("videotodownload");
                                downloadVideo(videotodownload, downloadname);
                                pendingList.remove(0);
                        } catch (Exception f){
                        }
                    }
                }
                @Override
                public void onDownloadFailed(DownloadException e) {
                    mBuilder.setOngoing(false);
                    mNotifyManager.cancel(Nid);
                    isDownloadRunning = false;
                    path.getAbsoluteFile().delete();
                }
            });
//submit download info to download manager.
            downloadManager.download(downloadInfo);
        }catch(Exception exp){
            //handles error
            isDownloadRunning=false;
        }
    }
}