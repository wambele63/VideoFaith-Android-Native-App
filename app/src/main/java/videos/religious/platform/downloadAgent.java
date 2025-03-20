package videos.religious.platform;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadListener;
import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.config.Config;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;

import java.io.File;

public class downloadAgent {
    private static DownloadManager downloadManager;
    private static DownloadInfo downloadInfo;
    public static void Agent(final Context context, final String downloadname, final String downloadUrl){

        final NotificationManager[] mNotifyManager = new NotificationManager[1];
        final NotificationCompat.Builder[] mBuilder = new NotificationCompat.Builder[1];
        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/");

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File path = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + downloadname + ".mp4");
            if(path.exists()){
                Toast.makeText(context, "Video ALready Downloaded", Toast.LENGTH_SHORT).show();
                return;
            }
            path.createNewFile();

            Config config = new Config();
            //set database path
            config.setDownloadThread(3);

            //set each download info thread number
            config.setEachDownloadThread(3);

            // set connect timeout,unit millisecond
            config.setConnectTimeout(300000);

            // set read data timeout,unit millisecond
            config.setReadTimeout(300000);
            downloadManager = DownloadService.getDownloadManager(context, config);
            downloadInfo = new DownloadInfo.Builder()
                    .setUrl(downloadUrl)
                    .setPath(path.toString())
                    .build();
            //set download callback.
            downloadInfo.setDownloadListener(new DownloadListener() {

                @Override
                public void onStart() {
                    Toast.makeText(context, "started download", Toast.LENGTH_SHORT).show();
                    mNotifyManager[0] = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder[0] = new NotificationCompat.Builder(context, "1");
                    mBuilder[0].setContentTitle("Download Video")
                            .setContentText("Downloading in progress")
                            .setOngoing(true)
                            .setOnlyAlertOnce(true)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_cloud_download_black_24dp);
                    mNotifyManager[0].notify(3, mBuilder[0].build());
                }
                @Override
                public void onWaited() {
                }

                @Override
                public void onPaused() {
                }

                @Override
                public void onDownloading(long progress, long size) {
                    Log.d("downloading ", FileUtil.formatFileSize(progress) + "/" + FileUtil
                            .formatFileSize(size));
                    double incr = (100.0 * progress) / size;
                    final int incrr = (int) incr;
                    // Sets the progress indicator to a max value, the current completion percentage and "determinate" state
                    mBuilder[0].setProgress(100, incrr, false)
                            .setContentTitle("Downloading Video...")
                            .setSound(null);
                    mNotifyManager[0].notify(3, mBuilder[0].build());
                }

                @Override
                public void onRemoved() {
                    downloadInfo = null;
                }

                @Override
                public void onDownloadSuccess() {
                    Toast.makeText(context, "sucess", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDownloadFailed(DownloadException e) {
                    Toast.makeText(context, "error downloading " + e.getCause().getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
//submit download info to download manager.
            downloadManager.download(downloadInfo);
        }catch(Exception exp){
            //handles error
        }
    }
}
