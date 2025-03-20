package videos.religious.platform;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class ChristianTube extends Application {
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        ChristianTube app = (ChristianTube) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(Utils.getVideoCacheDir(this))
                .build();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
        UploadService.PROGRESS_REPORT_INTERVAL = 1000;
        UploadService.BACKOFF_MULTIPLIER = 2;
        UploadService.INITIAL_RETRY_WAIT_TIME = 15000;
        UploadService.MAX_RETRY_WAIT_TIME= 150000;
        UploadService.IDLE_TIMEOUT = 30000;
        UploadService.KEEP_ALIVE_TIME_IN_SECONDS = 360000;
        UploadService.HTTP_STACK = new OkHttpStack();
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                // you can add your own request interceptors to add authorization headers.
                // file contents in the log. Logging body is suitable only for small requests.
                .cache(null)
                .build();
    }

}