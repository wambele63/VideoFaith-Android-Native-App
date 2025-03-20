package videos.religious.platform;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

public class VisitChannel extends AppCompatActivity implements ChannelVideos.OnFragmentInteractionListener{
    public static String chid,cpro,cname,ccover,cissub,videos,subs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_channel);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.channelv,ChannelVideos.newInstance("",""));
        transaction.commit();
        Intent intent = getIntent();
        chid = intent.getStringExtra("fragid");
        cname = intent.getStringExtra("cname");
        cpro = intent.getStringExtra("chanpro");
        ccover = intent.getStringExtra("ccover");
        cissub = intent.getStringExtra("issub");
        videos = intent.getStringExtra("videos")+ " videos";
        subs = intent.getStringExtra("subsc")+" subscribers";
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
    }

    public void onFragmentInteraction(Uri uri) {
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}