package videos.religious.platform;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import static android.net.wifi.WifiConfiguration.Status.strings;

public class FragviewActivity extends AppCompatActivity implements IOnBackPressed,TrendingVideos.OnFragmentInteractionListener,
        Subscriptions.OnFragmentInteractionListener,Uploading.OnFragmentInteractionListener,Preferrences.OnFragmentInteractionListener,
        Downloads.OnFragmentInteractionListener,terms.OnFragmentInteractionListener,EditAccount.OnFragmentInteractionListener,AboutPage.OnFragmentInteractionListener,popularVideos.OnFragmentInteractionListener,AllWatchedVideos.OnFragmentInteractionListener{
    private static final String UPLOAD_RATE = "20";
    public static String videoTobeuploaded;
    private double Prosessing = 0;
    private static int fragint;
    public static String posterTobeuploaded;
    public void onFragmentInteraction(Uri uri) {
    }
    @Override
    public void setCurrentFragment(int i) {
        if (i == 3) {
        }
    }
    @Override
    public void setNewFragment(int fragid){
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);   if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDiluted));
        setAnimations();
        setContentView(R.layout.activity_fragview);
        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest)
                .setReplaceAll(true)
                .setEmojiSpanIndicatorEnabled(true)
                .setEmojiSpanIndicatorColor(Color.GREEN)
                .registerInitCallback(new EmojiCompat.InitCallback() {
                    @Override
                    public void onInitialized() {
                        super.onInitialized();
                    }
                    @Override
                    public void onFailed(@Nullable Throwable throwable) {
                        super.onFailed(throwable);
                    }
                });
        EmojiCompat.init(config);
        fragint = getIntent().getIntExtra("fragId",0);
        final Fragment newFragment;
        switch(fragint){
            case 1:
            newFragment = new popularVideos();
            break;
            case 7:
            newFragment = new Subscriptions();
            break;
            case 3:
            newFragment = new AllWatchedVideos();
            break;
            case 6:
            newFragment = new terms();
            break;
            case 5:
            newFragment = new Downloads();
            break;
            case 2:
                if(!Constants.myId.equals("")){
                    newFragment = new EditAccount();
                    break;
                }
            newFragment = new Preferrences();
            break;
            case 8:
            newFragment = new AboutPage();
            break;
            case 9:
            newFragment = new TrendingVideos();
            break;
            case 15:
                final String videoUri = getIntent().getStringExtra("uri");
                newFragment = Uploading.newInstance(videoUri,"");
                break;
            case 16:
                final String videouri = getIntent().getStringExtra("videouri");
                final String videoid = getIntent().getStringExtra("videoid");
                final String thisvideoviews = getIntent().getStringExtra("thisvideoviews");
                final String thisvideodesc = getIntent().getStringExtra("thisvideodesc");

            return;
            default:
                newFragment =new Downloads();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragview, newFragment);
        transaction.commit();
    }
    private void v() {
        try {
            MultipartUploadRequest uploadRequest = new MultipartUploadRequest(FragviewActivity.this,  "/videos.php");
            for (int i = 0; i < strings.length; i++) {
                Toast.makeText(FragviewActivity.this, "file" + i + " is " + strings[i], Toast.LENGTH_SHORT).show();
                uploadRequest.addFileToUpload(strings[i], "file");
            }
            uploadRequest
                    .setMaxRetries(Integer.MAX_VALUE)
                    .addParameter("filescount", strings.length + "")
                    .addParameter("myid", "1")
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .addParameter("channel", "2")
                    .addParameter("text", "helowwwwwwwwwwwwww")
                    .startUpload();
        } catch (
                FileNotFoundException exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
        AndroidNetworking.upload("/videos.php")
                .addMultipartFile("file", new File(videoTobeuploaded))
                .addMultipartParameter("myid", "1")
                .addMultipartParameter("channel", "2")
                .addMultipartParameter("text", "helowwwwwwwwwwwwww")
                .setTag("uploadFile")
                .setPriority(Priority.HIGH)
                .setTag("uploadFile")
                .setPriority(Priority.HIGH)
                .build() // setting an executor to get response or completion on that executor thread
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        Log.d("ssssssss", bytesUploaded + " / " + totalBytes);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(FragviewActivity.this, "annnnnn" + response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(FragviewActivity.this, "annnnnnnnnn" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setAnimations(){
        Explode out = new Explode();
        out.setDuration(100);
        this.getWindow().setReturnTransition(out);
    }
    @Override
    public void onBackPressed() {
        if(!AllowBack()){
            Toast.makeText(this, "Sync Data Before Going Back", Toast.LENGTH_LONG).show();
        }
        else
            super.onBackPressed();
    }
    public boolean AllowBack(){
        if(allow.equals("No")) {
            return false;
        }
        else
            return true;
    }
    private String allow = "";
    public void check(String no){
        allow = no;
    }
    @Override
    public void setRefresh(int i){

    }
}