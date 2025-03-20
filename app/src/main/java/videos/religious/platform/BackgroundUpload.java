package videos.religious.platform;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.TranscoderListener;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy;
import com.otaliastudios.transcoder.resize.AspectRatioResizer;
import com.otaliastudios.transcoder.resize.AtMostResizer;
import com.otaliastudios.transcoder.resize.ExactResizer;

import net.gotev.uploadservice.data.UploadInfo;
import net.gotev.uploadservice.data.UploadNotificationConfig;
import net.gotev.uploadservice.data.UploadNotificationStatusConfig;
import net.gotev.uploadservice.network.ServerResponse;
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import kotlin.jvm.functions.Function2;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BackgroundUpload extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_DECODE = "videos.religious.platform.action.DECODE";
    private static final String ACTION_DOWNLOAD = "videos.religious.platform.action.DOWNLOAD";

    private String videoToDecode = "";
    private String videoToUpload = "";
    private String posterToUpload = "";
    private String videoText = "";
    private String videodescription = "";
    private String uid = "";
    private Handler mHandler;
    private String duration = "";
    private double videosize;
    public static String videochannel = "";
    private static Context contexts;
    public static String videoduration = "";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "videos.religious.platform.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "videos.religious.platform.extra.PARAM2";

    public BackgroundUpload() {
        super("BackgroundUpload");
    }

    /**
     * Starts this service to perform action DECODE with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionDECODE(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackgroundUpload.class);
        intent.setAction(ACTION_DECODE);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        Log.d("backkkkkkkk",param1);
        contexts = context;
        context.startService(intent);
    }

    /**
     * Starts this service to perform action DOWNLOAD with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionDOWNLOAD(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackgroundUpload.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        contexts = context;
        context.startService(intent);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("backkkkkkkk","fffffffffffff");
            final String action = intent.getAction();
            if (ACTION_DECODE.equals(action)) {
                posterToUpload = intent.getStringExtra("postertoupload");
                videoToDecode = intent.getStringExtra("videotodecode");
                videodescription = intent.getStringExtra("videotodescription");
                videoText = intent.getStringExtra("videotext");
                handleActionDECODE();
            } else if (ACTION_DOWNLOAD.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionDOWNLOAD(param1, param2);
            }
        }
    }

    /**
     * Handle action DECODE in the provided background thread with the provided
     * parameters.
     */

    private double getFileSizeMegaBytes(File file) {
        return file.length() / (1024 * 1024);
    }
    private void handleActionDECODE() {
        // TODO: Handle action DECODE
        try {
            decodeVideo(videoToDecode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Handle action DOWNLOAD in the provided background thread with the provided
     * parameters.
     */

    private void handleActionDOWNLOAD(String param1, String param2) {
        // TODO: Handle action DOWNLOAD
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private String decodeVideo(String videoToDecode) throws IOException {
        final String VideotoDecode = contexts.getCacheDir() + "/" + "VID_CHTUBE_" + (System.currentTimeMillis()) + ".mp4";
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoToDecode);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long millis = Long.parseLong(time);
        duration = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
        int finalwidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int finalheight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        retriever.release();
        Float wf, hf;
        if (finalwidth > finalheight && finalwidth > 640) {
            finalwidth = 640;
            finalheight = 360;
            wf = 16f;
            hf = 9f;
        } else if (finalheight > finalwidth && finalheight > 960) {
            finalwidth = 540;
            finalheight = 960;
            hf = 9f;
            wf = 16f;
        } else if (finalheight / finalwidth == 1) {
            finalheight = 400;
            finalwidth = 400;
            wf = 1f;
            hf = 1f;
        } else {
            return videoToDecode;
        }
        DefaultVideoStrategy strategy = new DefaultVideoStrategy.Builder()
                .addResizer(new AspectRatioResizer(wf / hf))
                .addResizer(new ExactResizer(finalwidth, finalheight))
                .addResizer(new AtMostResizer(960))
                .frameRate(25)
                .bitRate(DefaultVideoStrategy.BITRATE_UNKNOWN)
                .build();
        Log.d("duuuuuu",duration);
        Transcoder.into(VideotoDecode)
                .setVideoTrackStrategy(strategy)
                .setVideoRotation(0)
                .addDataSource(videoToDecode)
                .setListener(new TranscoderListener() {
                    public void onTranscodeProgress(double progress) {
                        try {
                            uploadVideo(VideotoDecode);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d("pppppppp",""+progress);
                    }
                    public void onTranscodeCompleted(int successCode) {
                        Log.d("deccccccc","completed");
                    }
                    public void onTranscodeCanceled() {
                        Log.d("deccccccc","cancelled");
                    }
                    public void onTranscodeFailed(@NonNull Throwable exception) {
                        Log.d("deccccccc",exception.getMessage());
                    }
                }).transcode();
        return VideotoDecode;
    }
    private void uploadDecodedVideo(){
        try {
            String[] filestrings = new String[]{videoToUpload, posterToUpload};
            MultipartUploadRequest uploadRequest = new MultipartUploadRequest(contexts,  "/videos.php");
            for (int i = 0; i < filestrings.length; i++) {
                uploadRequest.addFileToUpload(filestrings[i], "file[]");
            }
            uploadRequest
                    .setMaxRetries(20)
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .addParameter("uid", "1")
                    .addParameter("duration", duration)
                    .addParameter("videosize", videosize+"")
                    .addParameter("description", videodescription)
                    .addParameter("channel", "3")
                    .addParameter("text", videoText)
                    .startUpload();
        } catch (
                FileNotFoundException exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }
    private void uploadVideo(String UploadVideo) throws MalformedURLException {
        videosize = getFileSizeMegaBytes(  new File(UploadVideo));
        if (videosize > 1024) {
            Log.d("unsupported video size",videosize+"");
            return;
        }
        try {
            String[] filestrings = new String[]{UploadVideo, posterToUpload};
            MultipartUploadRequest uploadRequest = new MultipartUploadRequest(contexts, "/videos.php");
            for (int i = 0; i < filestrings.length; i++) {
                Log.d("sssssssssssss",filestrings[i]);
                uploadRequest.addFileToUpload(filestrings[i], "file[]");
            }
            uploadRequest
                    .setMaxRetries(20)
                    .addParameter("uid", "2")
                    .addParameter("duration", duration)
                    .addParameter("videosize", videosize+"")
                    .addParameter("description", videodescription)
                    .addParameter("channel", "4")
                    .addParameter("text", videoText)
                    .startUpload();
        } catch (
                FileNotFoundException exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }
}