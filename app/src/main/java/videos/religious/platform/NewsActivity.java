package videos.religious.platform;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.transition.Explode;
import android.transition.Fade;
import android.view.Window;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.androidnetworking.AndroidNetworking;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class NewsActivity extends AppCompatActivity implements New.OnFragmentInteractionListener,addnews.OnFragmentInteractionListener {
    private TextView mTextMessage;
    private ExtendedViewPager extendedViewPager;
    private String userid;
    private String newStringText;
    private String newStringImage;
    private String newStringHeader;
    private String newsTime,newsLocation;
    FragmentPagerAdapter adapterViewPager;
    private InterstitialAd mInterstitialAd;

    private class Wambura extends FragmentPagerAdapter {
        private int NUM_ITEMS = 1;
        private Wambura(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public int getCount() {
            return this.NUM_ITEMS;
        }

        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return videos.religious.platform.New.newInstance(newStringImage, newStringText, newStringHeader,newsTime,newsLocation);
                default:
                    return videos.religious.platform.New.newInstance(newStringImage, newStringText, newStringHeader,newsTime,newsLocation);
            }
        }

        public CharSequence getPageTitle(int i) {
            StringBuilder sb = new StringBuilder();
            sb.append("Page ");
            sb.append(i);
            return sb.toString();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        Intent newdata = getIntent();
        Bundle extras = newdata.getExtras();
        newStringText = extras.getString("videodesctext");
        newStringImage = extras.getString("image");
        newStringHeader = extras.getString("head");
        newsTime = extras.getString("time");
        newsLocation = extras.getString("location");
        setContentView(R.layout.activity_news2);
        setupWindowAnimations();
        okhttp3.OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.initialize(this, okHttpClient);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        extendedViewPager = findViewById(R.id.vpPager);
        this.adapterViewPager = new Wambura(getSupportFragmentManager());
        extendedViewPager.setAdapter(this.adapterViewPager);   extendedViewPager.setOffscreenPageLimit(10);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        extendedViewPager.setPageTransformer(true, (ViewPager.PageTransformer) new AccordionTransformer());
    }
    @TargetApi(21)
    private void setupWindowAnimations() {
        Fade animator = new Fade();
        animator.setDuration(200);
        getWindow().setEnterTransition(animator);

        Explode ranimator = new Explode();
        ranimator.setDuration(200);
        this.getWindow().setReturnTransition(ranimator);
    }
    @Override
    public void onBackPressed(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            super.onBackPressed();
            return;
        }
        super.onBackPressed();
    }
    public void onFragmentInteraction(Uri uri) {
    }
}