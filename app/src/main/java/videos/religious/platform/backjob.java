package videos.religious.platform;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.InspectableProperty;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.TranscoderListener;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy;
import com.otaliastudios.transcoder.resize.AspectRatioResizer;
import com.otaliastudios.transcoder.resize.AtMostResizer;
import com.otaliastudios.transcoder.resize.ExactResizer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.objectbox.model.EntityFlags;


public class backjob extends Service {
    private String duration;
    private String videoToDecode = "";
    private String videoToUpload = "";
    private String posterToUpload = "", firestoreposterurl;
    private String postersmall = "", firestorepostersmallurl;
    private String videoText = "", firestorevideourl;
    private String videodescription = "";
    private ArrayList pendingUploading = new ArrayList();
    private FirebaseStorage store;
    private FirebaseFirestore db;
    private String lastMparam;
    private static boolean isUploading = false;

    public backjob() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        store = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
            this.startForeground(1,new Notification());
    }
    private Intent n;
    private int idn = 1;
    private int flg;

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        try {
            n = intent;
            flg = flags;
            String videoToDecodeChecker = intent.getStringExtra("videotodecode");
            if(videoToDecodeChecker.equals(lastMparam)){
                Toast.makeText(this, " Video Already In Queue For Upload", Toast.LENGTH_SHORT).show();
                return START_STICKY;
            }
            else if(!isUploading) {
                posterToUpload = n.getStringExtra("postertoupload");
                postersmall = n.getStringExtra("postersmall");
                videoToDecode = intent.getStringExtra("videotodecode");
                videodescription = intent.getStringExtra("videodescription");
                videoText = intent.getStringExtra("videotext");
                decodeVideo(videoToDecode);
                lastMparam = videoToDecode;
                return START_STICKY;
            }
            else  {
                String postertopend,postersmallpend,videotopend,videodescpend,videotextpend;
                postertopend = n.getStringExtra("postertoupload");
                postersmallpend = n.getStringExtra("postersmall");
                videotopend = intent.getStringExtra("videotodecode");
                videodescpend = intent.getStringExtra("videodescription");
                videotextpend = intent.getStringExtra("videotext");
                Map<String, Object> pendUpload = new HashMap<>();
                pendUpload.put("posterToUpload",postertopend);
                pendUpload.put("postersmall",postersmallpend);
                pendUpload.put("videoToDecode",videotopend);
                pendUpload.put("videodescription",videodescpend);
                pendUpload.put("videoText",videotextpend);
                pendingUploading.add(pendUpload);
                Toast.makeText(this, "Your Upload Has Been Queued ", Toast.LENGTH_SHORT).show();
                lastMparam = videotopend;
                return START_STICKY;
            }
        }
        catch (Exception ex) {
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isUploading = false;
    }

    private String decodeVideo(String videoToDecode) {
        isUploading = true;
        final String VideotoDecode = this.getCacheDir() + "/" + "VID_CHTUBE_" + (System.currentTimeMillis()) + ".mp4";
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
        String[] d = duration.split(":");
        if (d[0].equals("00")) {
            duration = d[1] + ":" + d[2];
        }
        int finalwidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int finalheight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Float wf, hf;
        if (finalwidth > finalheight && finalwidth > 640 && getFileSizeMegaBytes(new File(videoToDecode)) > 50) {
            finalwidth = 640;
            finalheight = 360;
            wf = 16f;
            hf = 9f;
        } else if (finalheight > finalwidth && finalheight > 960 && getFileSizeMegaBytes(new File(videoToDecode)) > 50) {
            finalwidth = 540;
            finalheight = 960;
            hf = 9f;
            wf = 16f;
        } else if (finalheight / finalwidth == 1 && finalheight > 400 && getFileSizeMegaBytes(new File(videoToDecode)) > 50) {
            finalheight = 400;
            finalwidth = 400;
            wf = 1f;
            hf = 1f;
        } else {
            uploadVideo(videoToDecode);
            return videoToDecode;
        }
        DefaultVideoStrategy strategy = new DefaultVideoStrategy.Builder()
                .addResizer(new AspectRatioResizer(wf / hf))
                .addResizer(new ExactResizer(finalwidth, finalheight))
                .addResizer(new AtMostResizer(960))
                .frameRate(25)
                .bitRate(DefaultVideoStrategy.BITRATE_UNKNOWN)
                .build();
        Transcoder.into(VideotoDecode)
                .setVideoTrackStrategy(strategy)
                .setVideoRotation(0)
                .addDataSource(videoToDecode)
                .setListener(new TranscoderListener() {
                    public void onTranscodeProgress(double progress) {
                        Log.d("kkkkkk", progress + "");
                    }

                    public void onTranscodeCompleted(int successCode) {
                        uploadVideo(VideotoDecode);
                        Log.d("dddddddd", successCode + "");

                    }

                    public void onTranscodeCanceled() {
                        Log.d("deccccccc", "cancelled");
                    }

                    public void onTranscodeFailed(@NonNull Throwable exception) {
                        Log.d("eeeeee", exception.getMessage());

                    }
                }).transcode();
        return VideotoDecode;
    }

    private void zipFile(String filePath) {
        File fileToZip = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(filePath + ".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            System.err.format("The file % does not exist", filePath);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex);
        }
        Log.d("ziiiiiiiiip", "" + new File(filePath + ".zip").getAbsolutePath().length());
    }

    private int getFileSizeMegaBytes(File file) {
        return (int) file.length() / (1024 * 1024);
    }

    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder mBuilder;
    private StorageReference pRefs, psRefs;
    private int videosize;

    private void uploadVideo(String UploadVideo) {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(backjob.this, "1");
        mBuilder.setContentTitle(videoText)
                .setContentText("uploading in progress")
                .setOngoing(true)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_cloud_upload_black_24dp)
                .setOnlyAlertOnce(true);
        mNotifyManager.notify(1, mBuilder.build());
        videosize = getFileSizeMegaBytes(new File(UploadVideo));
        if (videosize > 50) {
            Toast.makeText(this, "MAXIMUM SIZE PER VIDEO IS 50MB", Toast.LENGTH_LONG).show();
            return;
        }
        try {
                        final Uri vfile = Uri.fromFile(new File(UploadVideo));
            videofiles = System.currentTimeMillis() + "";
            sRef = store.getReference();
                        final StorageReference vRefs = sRef.child("videos/" + videofiles + "/" + vfile.getLastPathSegment());
                        UploadTask uploadTaskv = vRefs.putFile(vfile);
                        uploadTaskv.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull final UploadTask.TaskSnapshot taskSnapshot) {
                                double incr = ((float)100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                final int incrr = (int) incr;
                                Log.d("proooooo", incrr + "/100");
                                // Sets the progress indicator to a max value, the current completion percentage and "determinate" state
                                mBuilder.setProgress(100, incrr, false)
                                        .setSound(null);
                                mNotifyManager.notify(1, mBuilder.build());
                            }
                        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    return null;
                                }
                                return vRefs.getDownloadUrl();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mBuilder.setProgress(0, 0, false)
                                        .setOngoing(false)
                                        .setContentTitle("Upload Failed")
                                        .setSmallIcon(R.drawable.ic_error_red_24dp)
                                        .setSound(null);
                                mNotifyManager.notify(1, mBuilder.build());
                                Log.d("errrr", "" + e.getMessage());
                                isUploading=false;
                                vRefs.delete();
                            }
                        }).addOnCompleteListener(task -> {
                            mBuilder.setContentText("Upload completed")
                                    .setSmallIcon(R.drawable.ic_cloud_done_black_24dp)
                                    .setOngoing(false)
                                    .setProgress(0, 0, false);
                            mNotifyManager.notify(1, mBuilder.build());
                            if (task.isSuccessful()) {
                                uploadposter();
                                firestorevideourl = task.getResult().toString();
                                Log.d("proooooo", "videooooo succ");
                                if (!firestorevideourl.equals(null) || !firestoreposterurl.equals(null) || !firestorepostersmallurl.equals(null)) {

                                }
                            } else {
                                isUploading = false;
                                Log.d("errrr url", task.getException().getMessage() + " e");
                            }
                        });
        } catch (
                Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }
String videofiles;
    StorageReference sRef;
    private void uploadposter() {
        try {
            final Uri poster = Uri.fromFile(new File(posterToUpload));
            pRefs = sRef.child("videos/" + videofiles + "/" + poster.getLastPathSegment());
            UploadTask uploadTask = pRefs.putFile(poster);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                        return null;
                    }
                    return pRefs.getDownloadUrl();
                }
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("proooooo", "posoooo succ");
                    firestoreposterurl = task.getResult().toString();
                    uploadthumb();
                } else {
                    isUploading = false;
                }
            });
        }
        catch (Exception n){
            pRefs.delete();
        }
}
    private void uploadthumb() {
        Uri postersmal = Uri.fromFile(new File(postersmall));
        psRefs = sRef.child("videos/" + videofiles + "/" + postersmal.getLastPathSegment());
        UploadTask uploadTasks = psRefs.putFile(postersmal);
        uploadTasks.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                return null;
            }
            return psRefs.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("proooooo", "psmalloooo succ");
                firestorepostersmallurl = task.getResult().toString();
                if (!firestorevideourl.equals(null) || !firestoreposterurl.equals(null) || !firestorepostersmallurl.equals(null)) {
                    saveToFirestore();
                }
            }else {
                isUploading = false;
            }
        });
}
    private void saveToFirestore(){
        db = FirebaseFirestore.getInstance();
        long timestamp = System.currentTimeMillis();
        Map<String, Object> newVideo = new HashMap<>();
        String[] Videotext = videoText.toLowerCase().split(" ");
        newVideo.put("video", firestorevideourl);
        newVideo.put("poster", firestoreposterurl);
        newVideo.put("postersmall", firestorepostersmallurl);
        newVideo.put("channelId", Constants.myId);
        newVideo.put("videoSize", videosize+"");
        newVideo.put("videotext", videoText);
        newVideo.put("keywords", Arrays.asList(Videotext));
        newVideo.put("duration", duration);
        newVideo.put("likes", 0);
        newVideo.put("favourites", 0);
        newVideo.put("views", 0);
        newVideo.put("downloads", 0);
        newVideo.put("time", timestamp);
        newVideo.put("religion", videodescription);
        CollectionReference dbRef = db.collection("videos");
        dbRef.add(newVideo)
                .addOnSuccessListener(documentReference -> {
                    startPushNotification(videoText, firestoreposterurl);
                    Map<String,Object> notify = new HashMap<>();
                    notify.put("body",videoText);
                    notify.put("contentid",documentReference.getId());
                    notify.put("image",firestorepostersmallurl);
                    notify.put("type","video");
                    notify.put("timestamp",System.currentTimeMillis());
                    notify.put("contentl",0);
                    notify.put("contentd",0);
                    notify.put("contentv",0);
                    notify.put("religion",videodescription);
                    notify.put("contenturl",firestorevideourl);
                    db.collection("notifications").add(notify);
                    File file = new File(videoToUpload).getAbsoluteFile();
                    file.delete();
                    isUploading = false;
                    if(pendingUploading.size() > 0){
                        try {
                            JSONArray uploadarray = new JSONArray(pendingUploading);
                                JSONObject uploadobject = uploadarray.getJSONObject(0);
                                posterToUpload = uploadobject.getString("posterToUpload");
                                postersmall = uploadobject.getString("postersmall");
                                videoToDecode = uploadobject.getString("videoToDecode");
                                videodescription = uploadobject.getString("videodescription");
                                videoText = uploadobject.getString("videoText");
                                decodeVideo(videoToDecode);
                                pendingUploading.remove(0);
                        } catch (Exception g){
                            Toast.makeText(backjob.this, ""+g.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isUploading = false;
                    }
                });
    }
    private void startPushNotification(String videoText1, String store){
        db.collection("serverApp").document("serverApp").get().addOnSuccessListener(serverkeydoc-> {
            if(Objects.requireNonNull(serverkeydoc.getData()).size() > 0){
                String serverkey = serverkeydoc.getString("serverkey");
                String title = "New Video: " + videoText1;
                if(pendingUploading.size() > 0){
                    title = pendingUploading.size() + " Faith Videos Posted";
                }
                    Constants.sendFCMPush(Objects.requireNonNull(this.getApplicationContext()).getApplicationContext(), title, videoText1, store, videodescription, serverkey);
                }
        });
    }
}