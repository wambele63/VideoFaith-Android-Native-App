package videos.religious.platform;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.*;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.circularreveal.CircularRevealRelativeLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.*;
import com.google.common.io.Files;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ixuea.android.downloader.callback.DownloadManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.objectbox.Box;
import io.objectbox.query.Query;
import ir.mehdiyari.fallery.imageLoader.FalleryImageLoader;
import ir.mehdiyari.fallery.main.fallery.Fallery;
import ir.mehdiyari.fallery.main.fallery.FalleryBuilder;
import ir.mehdiyari.fallery.main.fallery.FalleryOptions;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements Notifications.OnChangeCurrentView, Dashboard.OnFragmentInteractionListener,
        Videos.OnFragmentInteractionListener,Notifications.OnFragmentInteractionListener,
        NewsPage.OnFragmentInteractionListener, TrendingVideos.OnFragmentInteractionListener,
        Preferrences.OnFragmentInteractionListener,AboutPage.OnFragmentInteractionListener,
        Subscriptions.OnFragmentInteractionListener,
        Downloads.OnFragmentInteractionListener,RemoveAds.OnFragmentInteractionListener, IOnBackPressed {
    private static final int MY_REQUEST_CODE = 123;
    private TextView mTextMessage;
    private ExtendedViewPager extendedViewPager;
    private String userid;
    private FloatingActionButton floatingActionButton,addnewsbtn;
    private File source,dest;
    private static int countBackClicks = 0;
    private static int FILE_REQUEST_CODE = 100;
    private LinearLayout navView;
    FragmentPagerAdapter adapterViewPager;
    private TextView uploadvideopro;
    private LinearLayout uploadvideobtn;
    private Button christian;
    private Button muslim;
    private Button other;
    private CircularRevealRelativeLayout rewardopt;
    private String videoToUpload;
    private FirebaseAuth mAuth;
    private int NUM_ITEMS = 4;
    private int updateType = AppUpdateType.IMMEDIATE;
    private AppUpdateManager appUpdateManager;

    private class Wambura extends FragmentPagerAdapter {

        private Wambura(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public int getCount() {
            return NUM_ITEMS;
        }
        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return Videos.newInstance(MainActivity.this.userid, "Church");
                case 1:
                    return videos.religious.platform.NewsPage.newInstance(MainActivity.this.userid, "Hopetv");
                case 2:
                    return videos.religious.platform.Notifications.newInstance(MainActivity.this.userid, "Church");
                case 3:
                    return videos.religious.platform.Dashboard.newInstance(MainActivity.this.userid, "Church");
                default:
                    return Videos.newInstance(userid,"");
            }
        }
        public CharSequence getPageTitle(int i) {
            StringBuilder sb = new StringBuilder();
            sb.append("Page ");
            sb.append(i);
            return sb.toString();
        }
    }
    private void securityAlgorithmString(){
        Task<DocumentSnapshot> d = db.collection("serverApp").document("serverApp").get();
        assert d != null;
        d.addOnSuccessListener(MainActivity.this,n->{
            Constants.securechar = (List<String>) n.get("secureChar");
            Constants.currenttime = encryptDecrypt.decryptString(preferences.getString("currenttime",""));
            Constants.currentime = encryptDecrypt.decryptString(preferences.getString("currentime",""));
        }).addOnFailureListener(n->{
            securityAlgorithmString();
        });
    }
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.material);
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            MobileAds.initialize(getApplicationContext(), "" + R.string.admob_app_id);$.$(this);
            okhttp3.OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(28800, TimeUnit.SECONDS)
                    .writeTimeout(28800, TimeUnit.SECONDS)
                    .readTimeout(28800, TimeUnit.SECONDS)
                    .build();
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(firebaseFirestoreSettings);
            securityAlgorithmString();
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
            loadRewardedVideoAd();
           db.collection("serverApp").document("serverApp").get().addOnSuccessListener(MainActivity.this, serverAppDoc-> {
                  if(Objects.requireNonNull(serverAppDoc.getData()).size() > 0){
                      String serverkey = serverAppDoc.getString("serverkey");
                      if (Objects.equals(serverAppDoc.get("updateType"), "IMMEDIATE")) {
                            updateType = AppUpdateType.IMMEDIATE;
                        }
                       if(Objects.equals(serverAppDoc.get("updateType"), "FLEXIBLE")){
                           updateType = AppUpdateType.FLEXIBLE;
                        }
                        startUpdateRequest();
                    }
                });
            preferences = getSharedPreferences("channelInfo", Context.MODE_PRIVATE);
            //Assigning local details to variables
            Constants.myId = preferences.getString("channelid", "");
            Constants.myChannelCover = preferences.getString("ccover", "");
            Constants.myFullname = preferences.getString("accountname", "");
            Constants.myChannelName = preferences.getString("channelname", "");
            Constants.myChannelProfile = preferences.getString("cprofile", "");
            Constants.myCountry = preferences.getString("clocation", "");
            Constants.FIREBASE_TOKEN = preferences.getString("firebase_token","");
            Constants.reserveprofile = preferences.getString("reserveprofile","");
            Constants.reservechannelname = preferences.getString("reservename","");
            Constants.PRO_USER = preferences.getBoolean("PRO_USER",false);
            Constants.STATUS= preferences.getBoolean("status",false);
            Constants.phone= preferences.getString("phone","");
            Constants.myReligion= preferences.getString("religion","");
            if(Constants.myReligion.equals("other")) {
                if(preferences.getString("other","").equals("muslim")) {
                    preferences.edit().putString("other", "christian").apply();
                    Constants.myReligion = "christian";
                }
                else {
                    preferences.edit().putString("other", "muslim").apply();
                    Constants.myReligion = "muslim";
                }
            }
            rewardopt = findViewById(R.id.rewardopt);
            christian = findViewById(R.id.christian);
            other = findViewById(R.id.other);
            muslim = findViewById(R.id.islam);
            FirebaseMessaging.getInstance().subscribeToTopic("videofaith");
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.myReligion);
            if(Constants.myReligion.equals("")) {
                rewardopt.setVisibility(View.VISIBLE);
            }
            SharedPreferences preferences = getSharedPreferences("channelInfo", Context.MODE_PRIVATE);
            christian.setOnClickListener(view -> {
                YoYo.with(Techniques.SlideOutLeft).duration(600).onEnd(animator -> rewardopt.setVisibility(View.GONE)).playOn(rewardopt);
                preferences.edit().putString("religion",christian.getText().toString().toLowerCase()).apply();
                Constants.myReligion = christian.getText().toString().toLowerCase();
                FirebaseMessaging.getInstance().subscribeToTopic("christian");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("muslim");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("other");
                Toast.makeText(this, "Welcome Dear Christian", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(this::restart,3000);
            });
            muslim.setOnClickListener(view -> {
                YoYo.with(Techniques.SlideOutLeft).duration(600).onEnd(animator -> rewardopt.setVisibility(View.GONE)).playOn(rewardopt);
                preferences.edit().putString("religion",muslim.getText().toString().toLowerCase()).apply();
                Constants.myReligion = muslim.getText().toString().toLowerCase();
                FirebaseMessaging.getInstance().subscribeToTopic("muslim");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("christian");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("other");
                Toast.makeText(this, "Welcome Fellow Muslim!", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(this::restart,3000);
            });
            other.setOnClickListener(view -> {
                YoYo.with(Techniques.SlideOutLeft).duration(600).onEnd(animator -> rewardopt.setVisibility(View.GONE)).playOn(rewardopt);
                preferences.edit().putString("religion",other.getText().toString().toLowerCase()).apply();
                Constants.myReligion = other.getText().toString();
                FirebaseMessaging.getInstance().subscribeToTopic("other");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("muslim");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("christian");
                Toast.makeText(this, "Welcome Aboard!", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(this::restart,3000);
            });
            extendedViewPager = findViewById(R.id.vpPager);
            this.adapterViewPager = new Wambura(getSupportFragmentManager());
            navView = findViewById(R.id.nav_view_home);
            Box<User> userBox = ObjectBox.getBoxStore().boxFor(User.class);
            Query userQuery = userBox.query().build();
            long currentUsers = userQuery.count();
            if (currentUsers <= 0) {
                User firstUser = new User();
                userBox.put(firstUser);
            }
            AndroidNetworking.initialize(this, okHttpClient);
            FontRequest fontRequest = new FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "Noto Color Emoji Compat", R.array.com_google_android_gms_fonts_certs);
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
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                Tv = inflater.inflate(R.layout.maintv, findViewById(R.id.Tvmain));
                parent = findViewById(R.id.container2);
                parent.addView(Tv);
            this.userid = getApplicationContext().getSharedPreferences("wambura", MODE_PRIVATE).getString("user_id", null);
            mTextMessage = findViewById(R.id.thisviews);
            extendedViewPager.setAdapter(this.adapterViewPager);
            extendedViewPager.setPageTransformer(true, (ViewPager.PageTransformer) new AccordionTransformer());
            extendedViewPager.setOffscreenPageLimit(5);
            final ImageButton homeButton = findViewById(R.id.buttonhome);
            final ImageButton newsButton = findViewById(R.id.buttonnews);
            final ImageButton Buttonme = findViewById(R.id.buttonme);
            final ImageButton notifyButton = findViewById(R.id.buttonnotify);
            final ImageButton searchButton = findViewById(R.id.buttonsearch);
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    extendedViewPager.setCurrentItem(0);
                }
            });
            newsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    extendedViewPager.setCurrentItem(1);
                }
            });
            notifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    extendedViewPager.setCurrentItem(2);
                }
            });
            Buttonme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    extendedViewPager.setCurrentItem(3);
                }
            });
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent searchintent = new Intent(MainActivity.this, searchActivity.class);
                    startActivity(searchintent);
                }
            });
            floatingActionButton = findViewById(R.id.add_video);
            addnewsbtn = findViewById(R.id.addnews);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.RubberBand).duration(100).playOn(floatingActionButton);
                    startPicker();
                }
            });
            addnewsbtn.setOnClickListener(v -> {
                startNewsUpdater();
            });
            extendedViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    AppBarLayout appBarLayout = findViewById(R.id.appbar);
                }

                @Override
                public void onPageSelected(int position) {
                    if(position == 1){
                        YoYo.with(Techniques.SlideInUp).onStart(o->{addnewsbtn.setVisibility(View.VISIBLE);
                        floatingActionButton.setVisibility(View.GONE);}).duration(600).playOn(addnewsbtn);
                    }else {
                        YoYo.with(Techniques.SlideOutDown).onEnd(o->{addnewsbtn.setVisibility(View.GONE); floatingActionButton.setVisibility(View.VISIBLE);}).duration(600).playOn(addnewsbtn);
                    }
                    switch (position) {
                        case 0:
                            homeButton.setImageDrawable(getDrawable(R.drawable.ic_home_icon_active));
                            newsButton.setImageDrawable(getDrawable(R.drawable.ic_radio));
                            notifyButton.setImageDrawable(getDrawable(R.drawable.ic_notification));
                            Buttonme.setImageDrawable(getDrawable(R.drawable.ic_person_pin_black_24dp));
                            YoYo.with(Techniques.Pulse).duration(300).playOn(homeButton);
                            break;
                        case 1:
                            homeButton.setImageDrawable(getDrawable(R.drawable.ic_home_icon));
                            newsButton.setImageDrawable(getDrawable(R.drawable.ic_radio_active));
                            notifyButton.setImageDrawable(getDrawable(R.drawable.ic_notification));
                            Buttonme.setImageDrawable(getDrawable(R.drawable.ic_person_pin_black_24dp));
                            YoYo.with(Techniques.Pulse).duration(300).playOn(newsButton);
                            break;
                        case 2:
                            homeButton.setImageDrawable(getDrawable(R.drawable.ic_home_icon));
                            newsButton.setImageDrawable(getDrawable(R.drawable.ic_radio));
                            notifyButton.setImageDrawable(getDrawable(R.drawable.ic_notification_active));
                            Buttonme.setImageDrawable(getDrawable(R.drawable.ic_person_pin_black_24dp));
                            YoYo.with(Techniques.Pulse).duration(300).playOn(notifyButton);
                            break;
                        case 3:
                            homeButton.setImageDrawable(getDrawable(R.drawable.ic_home_icon));
                            newsButton.setImageDrawable(getDrawable(R.drawable.ic_radio));
                            notifyButton.setImageDrawable(getDrawable(R.drawable.ic_notification));
                            Buttonme.setImageDrawable(getDrawable(R.drawable.ic_person_pin_active));
                            YoYo.with(Techniques.Pulse).duration(300).playOn(Buttonme);
                            break;
                        default:
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }catch (Exception exeption){
            Log.d("failedlaunching", exeption.getMessage());

        }
        //process updates
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(listener);
        durl = "https://firebasestorage.googleapis.com/v0/b/christiantube-7c50c.appspot.com/o/videos%2Fpromovideo.mp4?alt=media&token=6e312bf1-e0e4-4af5-a73e-c36ff4a6dec0";$.$$();
        downloadedname = "promovideo";
    }
    private void restart(){
        Constants.triggerRebirth(this, new Intent());
    }
    private void startUpdateRequest(){
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            Log.d("appUpdateInfo :", "packageName :"+appUpdateInfo.packageName()+ ", "+ "availableVersionCode :"+ appUpdateInfo.availableVersionCode() +", "+"updateAvailability :"+ appUpdateInfo.updateAvailability() +", "+ "installStatus :" + appUpdateInfo.installStatus() );

            if (appUpdateManager.getAppUpdateInfo().getResult().updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateManager.getAppUpdateInfo().getResult().isUpdateTypeAllowed(updateType)){
                requestUpdate(appUpdateManager.getAppUpdateInfo().getResult());
                Log.d("UpdateAvailable","update is there ");
            }
            else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                Log.d("Update","3");
                notifyUser();
            }
            else
            {
                Log.d("NoUpdateAvailable","update is not there ");
            }
        });

    }
    private void requestUpdate(AppUpdateInfo appUpdateInfo){
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, updateType,MainActivity.this,MY_REQUEST_CODE);
            resume();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
    InstallStateUpdatedListener listener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED){
            Log.d("InstallDownloded","InstallStatus sucsses");
            notifyUser();
        }
    };
    private void startPicker() {
        try {
            final FalleryOptions falleryOptions = new FalleryBuilder()
                    .build();

            Fallery.startFalleryFromActivityWithOptions(
                    MainActivity.this, 1, falleryOptions
            );
        }
        catch (Exception exeption){
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }
    }

    private FalleryImageLoader YourImageLoader(FalleryImageLoader FalleryImageLoader) {
        return FalleryImageLoader;
    }

    public void HideAnimation(final View view){
        view.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        YoYo.with(Techniques.SlideOutRight).duration(300).playOn(navView);
                    } @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }
    private void ShowAnimation(final View view){
        view.animate()
                .alpha(1.0f)
                .setDuration(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        YoYo.with(Techniques.SlideInRight).duration(300).playOn(navView);
                    } @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }
    public void hide(){
          HideAnimation(navView);
          YoYo.with(Techniques.RubberBand).duration(300).playOn(floatingActionButton);
    }
    public void show(){
        ShowAnimation(navView);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case 1:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        ArrayList<String> videoPaths = new ArrayList<>(Arrays.asList(Fallery.getResultMediasFromIntent(data)));
                        String video = videoPaths.get(0);
                        startUpload(video);
                    }
                    break;
                case Activity.RESULT_OK:
                    if(resultCode != RESULT_OK){
                        Log.d("RESULT_OK  :",""+resultCode);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    if (resultCode != RESULT_CANCELED){
                        Log.d("RESULT_CANCELED  :",""+resultCode);
                    }
                    break;
            }
        } catch (Exception exeption) {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void startNewsUpdater() {
        try {
            if (!Constants.myId.equals("")) {
                Intent intent = new Intent(this, addnews.class);
                startActivity(intent);
            }
            else {
                Snackbar d = Snackbar.make(this.getWindow().getDecorView(),"Please Login To Post News", BaseTransientBottomBar.LENGTH_INDEFINITE);
                d.setActionTextColor(Color.BLUE);
                d.setTextColor(Color.WHITE);
                d.setBackgroundTint(Color.BLACK).setBackgroundTintMode(PorterDuff.Mode.SRC_OVER);
                d.setAction(
                        "Ok", view -> {
                            d.setDuration(BaseTransientBottomBar.LENGTH_SHORT);
                            Intent intent = new Intent(this, FragviewActivity.class);
                            intent.putExtra("fragId", 2);
                            startActivity(intent);
                        }
                );
                d.show();
            }
        } catch (Exception exeption) {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void startUpload(String video) {
        try {
            if (!Constants.myId.equals("")) {
                Intent intent = new Intent(this, FragviewActivity.class);
                intent.putExtra("fragId", 15).putExtra("uri", video);
                startActivity(intent);
            }
            else {
                    Snackbar d = Snackbar.make(this.getWindow().getDecorView(),"Please Login To Post Videos", BaseTransientBottomBar.LENGTH_INDEFINITE);
                    d.setActionTextColor(Color.BLUE);
                    d.setTextColor(Color.WHITE);
                    d.setBackgroundTint(Color.BLACK).setBackgroundTintMode(PorterDuff.Mode.SRC_OVER);
                    d.setAction(
                            "Ok", view -> {
                                d.setDuration(BaseTransientBottomBar.LENGTH_SHORT);
                                Intent intent = new Intent(this, FragviewActivity.class);
                                intent.putExtra("fragId", 2);
                                startActivity(intent);
                            }
                    );
                    d.show();
            }
        } catch (Exception exeption) {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }
    }
    public void onFragmentInteraction(Uri uri) {
    }
    public void OnChangeCurrentView(int page){
        OnChangeViewPager(page);
    }
    @Override
    public void OnChangeViewPager(int pageno){
        extendedViewPager.setCurrentItem(pageno);
    }
    @Override
    public void setRefresh(int i){
        adapterViewPager.notifyDataSetChanged();
    }
    @Override
    public boolean AllowBack(){
        return true;
    }
    @Override
    public void setCurrentFragment(int i) {
        if (i == 3) {
            this.NUM_ITEMS = 4;
            adapterViewPager.notifyDataSetChanged();
            extendedViewPager.setCurrentItem(i);
        } else {
            this.NUM_ITEMS = 5;
            adapterViewPager.notifyDataSetChanged();
            extendedViewPager.setCurrentItem(i);
        }
    }
    @Override
    public void setNewFragment(int fragid){
        Intent intent = new Intent(this,FragviewActivity.class);
        intent.putExtra("fragId",fragid);
        startActivity(intent);
    }
    private int s  =1;
    private static long contentPosition = 0;
    @Override
    protected void onStop() {
        if(null != exoPlayer) {
            contentPosition = exoPlayer.getContentPosition();
            exoPlayer.setPlayWhenReady(false);
        }
        super.onStop();
    }
    @Override
    protected void onPause() {
        if(null != exoPlayer) {
            contentPosition = exoPlayer.getContentPosition();
            exoPlayer.setPlayWhenReady(false);
        }
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != exoPlayer) {
            exoPlayer.release();
            exoPlayer = null;
        }
        db.terminate();
        appUpdateManager.unregisterListener(listener);
    }
    private void notifyUser() {
        Snackbar snackbar =
                Snackbar.make(getWindow().getDecorView(),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
    private void resume(){
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                    notifyUser();

                }

            }
        });
    }
    @Override
    protected void onResume() {
        if(null != exoPlayer) {
            exoPlayer.setPlayWhenReady(true);
        }
        super.onResume();
    }
    @Override
    public void onBackPressed() {
      if(AllowBack2()){
          super.onBackPressed();
      }
        }
        private void startMakingTvFloat(){
            YoYo.with(Techniques.SlideOutDown).duration(300).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    makeTvFloating();
                }
            }).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    Tv.setVisibility(View.GONE);
                }
            }).playOn(Tv);
        }
        @SuppressLint("SourceLockedOrientationActivity")
        public boolean AllowBack2() {
        if(oriented) {
            RelativeLayout.LayoutParams fullparams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (getWindow().getDecorView().getHeight() / 1.778));
            videoViewCont.setLayoutParams(fullparams);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Tvbottom.setVisibility(View.VISIBLE);
            Tvtop.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            oriented = false;
            return false;
        }
        else if(Tv.getVisibility() == View.VISIBLE){
            startMakingTvFloat();
            lastUri = videouri;
            return false;
        }
            else if (extendedViewPager.getCurrentItem() != 0) {
                s=1;
                extendedViewPager.setCurrentItem(0);
                return false;
            }
            else if (s == 1) {
                s=0;
                Snackbar.make(getWindow().getDecorView(),"Click Again to Exit",Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).setBackgroundTint(getResources().getColor(R.color.colorPrimaryDarkVideo)).setTextColor(Color.WHITE).show();
                return false;
            }
            else
                return true;
        }
    private ViewGroup createFloatBox() {
        Activity topActivity = this;
        ViewGroup topActivityBox = topActivity.findViewById(android.R.id.content);
        ViewGroup floatBox = (ViewGroup) LayoutInflater.from(topActivity.getApplication()).inflate(R.layout.floatbox, null);
        floatBox.setBackgroundColor(Color.BLACK);

        float newparentwidth = getWindow().getDecorView().getWidth()/2;
        newparentwidth = newparentwidth + newparentwidth/2;
        float newparentheight = newparentwidth/(float) 1.778;
        FrameLayout.LayoutParams floatBoxParams = new FrameLayout.LayoutParams((int)newparentwidth, (int)newparentheight);
        floatBoxParams.gravity = Gravity.BOTTOM | Gravity.END;
        floatBoxParams.bottomMargin = 0;
        floatBoxParams.rightMargin = 0;
        topActivityBox.addView(floatBox,floatBoxParams);


        floatBox.setOnTouchListener(new View.OnTouchListener() {
            float ry;
            float oy;

            float rx;
            float ox;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
//                System.out.println("MotionEvent:action:"+event.getAction()+",raw:["+event.getRawX()+","+event.getRawY()+"],xy["+event.getX()+","+event.getY()+"]");

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ry = event.getRawY();
                        oy = v.getTranslationY();

                        rx = event.getRawX();
                        ox = v.getTranslationX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float y = oy + event.getRawY() - ry;
                        if (y > 0) {
//                            y = 0;
                        }
                        v.setTranslationY(y);

                        float x = ox + event.getRawX() - rx;
                        if (x > 0) {
//                            x = 0;
                        }
                        v.setTranslationX(x);
                        break;
                }
                return true;
            }
        });
        return floatBox;
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setFloat(){
        videoViewCont.setOnTouchListener(new View.OnTouchListener() {
            float ry;
            float oy;

            float rx;
            float ox;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(float_control.getVisibility()==View.INVISIBLE) {
                    float_control.setVisibility(View.VISIBLE);
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ry = event.getRawY();
                        oy = v.getTranslationY();

                        rx = event.getRawX();
                        ox = v.getTranslationX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float y = oy + event.getRawY() - ry;
                        if (y > 0) {
//                            y = 0;
                        }
                        v.setTranslationY(y);

                        float x = ox + event.getRawX() - rx;
                        if (x > 0) {
//                            x = 0;
                        }
                        v.setTranslationX(x);
                        break;
                }
                return true;
            }
        });
    }
    private void videoNormalListener(View view) {
        final TextView bright = findViewById(R.id.brightness);
        view.setOnTouchListener(new View.OnTouchListener() {
            float ry;
            float oy;

            float rx;
            float ox;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                YoYo.with(Techniques.FadeIn).duration(500).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        bright.setVisibility(View.VISIBLE);
                    }
                }).playOn(bright);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        YoYo.with(Techniques.FadeIn).duration(500).onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                bright.setVisibility(View.INVISIBLE);
                            }
                        }).playOn(bright);
                    }
                },1000);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ry = event.getRawY();
                        oy = v.getTranslationY();

                        rx = event.getRawX();
                        ox = v.getTranslationX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float y = oy + event.getRawY() - ry;
                        if (y > 0) {
//                            y = 0;
                        }
                        float percent = y / videoViewCont.getHeight();
                        setbrightness(percent);
                        float x = ox + event.getRawX() - rx;
                        if (x > 0) {
//                            x = 0;
                        }
                        break;
                }
                return true;
            }
        });
    }
    private void  setbrightness(float percent){
        Window window = (getWindow());
        final TextView bright = findViewById(R.id.brightness);
        float brightness = getWindow().getAttributes().screenBrightness;
        if (brightness < 0) {
            brightness = window.getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
        WindowManager.LayoutParams lpa = window.getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        bright.setText(((int) (lpa.screenBrightness * 100)) + "%");
        window.setAttributes(lpa);
    }
    private void removeFloatContainer() {
        videoViewCont.setElevation(0);
        floatingActionButton.setTranslationY(0);
        normal_control.setVisibility(View.VISIBLE);
        float_control.setVisibility(View.GONE);
        double returnheight =  getWindow().getDecorView().getWidth()/1.778;
        RelativeLayout.LayoutParams floatBoxParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)returnheight);
        floatBoxParams.topMargin = 0;
        floatBoxParams.leftMargin= 0;
        ((ViewGroup)videoViewCont.getParent()).removeView(videoViewCont);
        videoViewCont.setTranslationX(0);
        videoViewCont.setTranslationY(0);
        parentTv.addView(videoViewCont,floatBoxParams);
        videoView.setUseController(true);
        videoViewCont.setOnTouchListener(null);
        DISPLAY_MODE_FLOAT=false;
    }
    private void makeTvFloating() {
        normal_control = findViewById(R.id.normal_control);
        float_control = findViewById(R.id.float_control);
        normal_control.setVisibility(View.GONE);
        float_control.setVisibility(View.VISIBLE);
        parentTv = (ViewGroup) videoViewCont.getParent();
        ViewGroup parentmain = findViewById(R.id.container2);
        float newparentwidth = getWindow().getDecorView().getWidth()/2;
        newparentwidth = newparentwidth + newparentwidth/2;
        float newparentheight = newparentwidth/(float) 1.778;
        RelativeLayout.LayoutParams floatBoxParams = new RelativeLayout.LayoutParams((int)newparentwidth, (int)newparentheight);
        floatBoxParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        floatBoxParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        floatBoxParams.bottomMargin = navView.getHeight();
        floatBoxParams.rightMargin = 0;
        parentTv.removeView(videoViewCont);
        videoViewCont.setElevation(20);
        parentmain.addView(videoViewCont,floatBoxParams);
        floatingActionButton.setTranslationY(videoViewCont.getHeight());
        setFloat();
        videoView.setUseController(false);
        DISPLAY_MODE_FLOAT=true;
    }

        private String allow = "";
        public void check(String no){
          allow = no;
        }
        //Running Playing Methods
        private ImageView chanpro,downloadnow;
        private ImageButton amen,playfloat,pausefloat,fullwindow,smallWindow,download,unmuteVideo,unloopVideo,muteVideo,loopVideo,share,favourite;
        private PlayerView videoView;
        private FrameLayout videoViewCont;
        private FrameLayout videoViewBright;
        private RelativeLayout normal_control;
        private RelativeLayout float_control;
        static SimpleExoPlayer exoPlayer;
        private boolean REWARD_ON_COMPLETE = false;
        private Button subscribe;
        private RelativeLayout chanpad;
        private View Tv;
        private LinearLayout Tvbottom;
        private LinearLayout Tvtop;
        private RelatedAdapter radapter;
        private String proxyUrl = "";
        private RewardedVideoAd mRewardedVideoAd;
        private static String[] videoinfo;
        private FrameLayout adContainerView;
        private AdView adView;
        private TextView tvchan, videoviews, subt, videodesc, loadmorerelated;
        private ProgressBar progressBarr;
        private FirebaseFirestore db;
        private WeakReference<? extends ViewGroup> boxContainerRef;
        private static String videouri = "";
        private static String lastUri = "";
        private RecyclerView recyclerViewrelated;
        private String videoid, videolike,LAST_RELATED_ITEM_ID, thisvideoviews, chanid="xyzxyzxyzxyz", thisvideodesc, ChannelName, downloadedname, chanprourl, durl;
        private ArrayList myVideoRelated = new ArrayList<>();
        private String LAST_ITEM_ID = "0";
        public static int currentpos;
        private ViewGroup parent;
        private DownloadManager downloadManager;
        private boolean DISPLAY_MODE_FLOAT = false;
        private  void prepareControl(){
            TextView qualityoption = findViewById(R.id.qualityoption);
            ImageButton returntv = findViewById(R.id.returntv);
            ImageButton closefloat = findViewById(R.id.closefloat);
            returntv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFloatContainer();
                    YoYo.with(Techniques.SlideInUp).duration(300).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            Tv.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.SlideInUp).duration(250).playOn(videoViewCont);
                        }
                    }).playOn(Tv);
                }
            });
            closefloat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                            removeFloatContainer();
                            recyclerViewrelated.setAdapter(null);
                            Tv.setVisibility(View.GONE);
                            contentPosition = exoPlayer.getContentPosition();
                            lastUri = "";
                            exoPlayer.release();
                }
            });
            qualityoption.setOnClickListener(this::showPopupQuality);
            download.setOnClickListener(this::showPopupDownload);
            Glide.with(download).load(getResources().getDrawable(R.drawable.downloadbig)).fitCenter().into(download);
            downloadnow.setOnClickListener(this::showPopupDownload);
        }
        public void prepareVideoView(String[] videoinfos, String str2) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            commentsDialog comments = commentsDialog.newInstance("","","","");
            comments.show(fragmentManager, "comments");
            videouri = str2;
            videoinfo = videoinfos;
            videoView = findViewById(R.id.video_view);
            videoViewCont = findViewById(R.id.video_view_cont);
            double returnheight =  getWindow().getDecorView().getWidth()/1.778;
            RelativeLayout.LayoutParams floatBoxParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)returnheight);
            videoViewCont.setLayoutParams(floatBoxParams);
            videoViewBright = findViewById(R.id.videoviewb);
            Tvtop = findViewById(R.id.Tvtop);
            download = findViewById(R.id.exodownload);
            downloadnow = findViewById(R.id.downloadnow);
            YoYo.with(Techniques.Bounce).repeat(10).duration(250).playOn(downloadnow);
            prepareControl();
            initializePlayerAds();
            Tv.setVisibility(View.INVISIBLE);
            YoYo.with(Techniques.SlideInUp).duration(300).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    Tv.setVisibility(View.VISIBLE);
                }
            }).playOn(Tv);
            adContainerView = findViewById(R.id.ad_viewtv_container);
            // Step 1 - Create an AdView and set the ad unit ID on it.
            adView = new AdView(this);
            adView.setAdUnitId(getString(R.string.NewsBannerAd_unit_id));
            adContainerView.addView(adView);
            loadBanner();
            chanpro = findViewById(R.id.channpro);
            recyclerViewrelated = findViewById(R.id.recycler_view_related);
            Tvbottom = findViewById(R.id.Tvbottom);
            loadmorerelated = findViewById(R.id.loadmorerelated);
            progressBarr = findViewById(R.id.progressmorerelated);
            chanpad = findViewById(R.id.channelpad);
            subt = findViewById(R.id.sub);
            videoviews = findViewById(R.id.thisviews);
            radapter = new RelatedAdapter(MainActivity.this, myVideoRelated);
            videodesc = findViewById(R.id.thisvideodesc);
            tvchan = findViewById(R.id.tvchannel);
            TextView videolikes = findViewById(R.id.thislikes);
            TextView videodownloads = findViewById(R.id.thisdownloads);
            TextView videofav = findViewById(R.id.thisfav);
            TextView videosid = findViewById(R.id.thisvideoids);
            TextView videourl = findViewById(R.id.tvvideourl);
            amen = findViewById(R.id.thislikesimg);
            fullwindow = findViewById(R.id.fullvideo);
            smallWindow = findViewById(R.id.floatvideo);
            playfloat = findViewById(R.id.playfloat);
            pausefloat = findViewById(R.id.pausefloat);
            muteVideo = findViewById(R.id.mutevideo);
            unmuteVideo = findViewById(R.id.unmutevideo);
            unloopVideo = findViewById(R.id.unloopvideo);
            loopVideo = findViewById(R.id.loopvideo);
            subscribe = findViewById(R.id.subscribe);
            favourite = findViewById(R.id.thisfavourites);
            share = findViewById(R.id.thisshare);
            videoid = videoinfo[1];
            videolike = videoinfo[2];
            thisvideoviews = videoinfo[3];
            thisvideodesc = videoinfo[4];
            chanid = videoinfo[6];
            String subsc = videoinfo[7];
            String channame = videoinfo[8];
            String chancover = videoinfo[9];
            String totalvids = videoinfo[10];
            String isliked = videoinfo[11];
            String isfavoured = videoinfo[12];
            String issubscribed = videoinfo[13];
            subt.setText("SUBSCRIBERS " + subsc);
            tvchan.setText(channame);
            final String chanprourl = videoinfo[5];
            String[] strlike = videolike.split(" ");
            String strlk = strlike[0];
            String[] strviews = thisvideoviews.split(" ");
            String strvws = strviews[0];
            videolikes.setText(strlk);
            videodownloads.setText("Down..");
            videofav.setText("Favo..");
            videoviews.setText(strvws);
            videolikes.setTag(isliked);
            videodownloads.setTag(isfavoured);
            videoviews.setTag(issubscribed);
            videosid.setText(videoid);
            videourl.setText(videouri);
            videodesc.setText(thisvideodesc);
            videodesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (videodesc.getMaxLines() == 1) {
                        videodesc.setMaxLines(6);
                        videodesc.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp), null);
                        return;
                    }
                    videodesc.setMaxLines(1);
                    videodesc.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp), null);
                }
            });
            addViews(videoid);
            amen.setEnabled(true);
            chanpad.setTag(chanid);
            tvchan.setTag(chancover);
            subt.setTag(totalvids);
            videodesc.setTag(subsc);
            if(chanpad.getTag().toString() == Constants.myId){
                isSubscribed();
            }
            if (issubscribed.equals("1")) {
                isSubscribed();
            }
            Glide.with(chanpro.getContext()).load( chanprourl).circleCrop().into(chanpro);
            if (isliked.equals("1")) {
                Glide.with(amen.getContext()).load(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp)).into(amen);
                amen.setEnabled(false);
            }
            if(isliked.equals("0")){
                Glide.with(amen.getContext()).load(getResources().getDrawable(R.drawable.ic_like)).into(amen);
                amen.setEnabled(true);
            }
            if (isfavoured.equals("1")) {
                Glide.with(favourite.getContext()).load(getResources().getDrawable(R.drawable.ic_star_active2)).into(favourite);
                favourite.setEnabled(false);
            }
            if(isfavoured.equals(0)){
                Glide.with(favourite.getContext()).load(getResources().getDrawable(R.drawable.ic_star)).into(favourite);
                favourite.setEnabled(true);
            }
            chanpad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String channelpadid = chanpad.getTag().toString();
                    String channelcover = tvchan.getTag().toString();
                    String subsc = videodesc.getTag().toString();
                    String totalvid = subt.getTag().toString();
                    String issub = videoviews.getTag().toString();
                    TextView channlname = findViewById(R.id.tvchannel);
                    String cname = channlname.getText().toString();
                    Intent intent = new Intent(MainActivity.this, VisitChannel.class);
                    intent.putExtra("fragid", channelpadid);
                    intent.putExtra("cname", cname);
                    intent.putExtra("ccover", channelcover);
                    intent.putExtra("chanpro", chanprourl);
                    intent.putExtra("videos", totalvid);
                    intent.putExtra("subsc", subsc);
                    intent.putExtra("issub", issub);
                    startActivity(intent);
                }
            });
            fullwindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exoPlayer.setPlayWhenReady(false);
                    rotatePlayer();
                }
            });
            smallWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            playfloat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pausefloat.setVisibility(View.VISIBLE);
                    playfloat.setVisibility(View.GONE);
                    exoPlayer.setPlayWhenReady(true);
                }
            });
            pausefloat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pausefloat.setVisibility(View.GONE);
                    playfloat.setVisibility(View.VISIBLE);
                    exoPlayer.setPlayWhenReady(false);
                }
            });
            loopVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loopVideo.setVisibility(View.INVISIBLE);
                    unloopVideo.setVisibility(View.VISIBLE);
                }
            });
            unloopVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loopVideo.setVisibility(View.VISIBLE);
                    unloopVideo.setVisibility(View.INVISIBLE);
                }
            });
            muteVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    muteVideo.setVisibility(View.INVISIBLE);
                    unmuteVideo.setVisibility(View.VISIBLE);
                }
            });
            unmuteVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    muteVideo.setVisibility(View.VISIBLE);
                    unmuteVideo.setVisibility(View.INVISIBLE);
                }
            });
            initmoretvtools();
            fetchRelatedJSON("");
            loadNativeAds();
        }
    @SuppressLint("SourceLockedOrientationActivity")
    private void rotatePlayer(){
            if(!oriented) {
                RelativeLayout.LayoutParams fullparams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                videoViewCont.setLayoutParams(fullparams);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                Tvbottom.setVisibility(View.GONE);
                Tvtop.setVisibility(View.GONE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                oriented = true;
                exoPlayer.setPlayWhenReady(true);
            }else {
                RelativeLayout.LayoutParams fullparams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (getWindow().getDecorView().getHeight() / 1.778));
                videoViewCont.setLayoutParams(fullparams);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Tvbottom.setVisibility(View.VISIBLE);
                Tvtop.setVisibility(View.VISIBLE);
                oriented = false;
                exoPlayer.setPlayWhenReady(true);
            }
        }
        private ViewGroup parentTv;
        private boolean oriented = false;
        private void initializePlayerAds() {
            try {
                if(null != exoPlayer) {
                    if (exoPlayer.isPlaying() || exoPlayer.isLoading()) {
                        if (lastUri.equals(videouri) && DISPLAY_MODE_FLOAT) {
                            removeFloatContainer();
                            return;
                        } else if (lastUri.equals(videouri) && !DISPLAY_MODE_FLOAT) {
                            Toast.makeText(this, "Video ALready Playing", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    exoPlayer.release();
                }
                if (DISPLAY_MODE_FLOAT) {
                    removeFloatContainer();
                }
                exoPlayer = setupPlayer();
                exoPlayer.setPlayWhenReady(true);
                lastUri = videouri;
                exoPlayer.addListener(new Player.EventListener() {
                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                        exoPlayer.retry();
                            Toast.makeText(MainActivity.this, "network error retrying...", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch(Exception dc) {
            }
        }

    private boolean checkCachedState() {
        return true;
    }

    private void updateVideoProgress() {
        long videoProgress = exoPlayer.getCurrentPosition() * 100 / exoPlayer.getDuration();
    }

    void seekVideo() {
        long videoPosition = exoPlayer.getDuration();
        exoPlayer.seekTo(videoPosition);
    }
    private SimpleExoPlayer setupPlayer() {
        videoView.setUseController(true);

        exoPlayer = newSimpleExoPlayer();
        videoView.setPlayer(exoPlayer);

        MediaSource videoSource = newVideoSource(videouri);
        exoPlayer.prepare(videoSource);
        videoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        return exoPlayer;
    }

    private SimpleExoPlayer newSimpleExoPlayer() {
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        return new SimpleExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .build();
    }
    private MediaSource newVideoSource(String url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();
        String userAgent = Util.getUserAgent(this, "AndroidVideoCache sample");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, userAgent, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    }
    private void initmoretvtools(){
            final TextView videoidt = findViewById(R.id.thisvideoids);
            amen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glide.with(amen.getContext()).load(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp)).centerInside().into(amen);
                    amen.setEnabled(false);
                    TextView videolikeadd = findViewById(R.id.thislikes);
                    String vlk = videolikeadd.getText().toString();
                    String tvid = videoidt.getText().toString();
                    int myvlks = 0;
                    try {
                        myvlks = Integer.parseInt(vlk);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Could not parse likes" + nfe);
                    }
                    myvlks++;
                    String newvlk = "" + myvlks;
                    videolikeadd.setText(newvlk);
                    YoYo.with(Techniques.SlideInUp).duration(300).playOn(videolikeadd);
                    amenPost(tvid);
                }
            });
            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glide.with(favourite).load(getResources().getDrawable(R.drawable.ic_star_active2)).into(favourite);
                    favourites(videoid);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share();
                }
            });
            subscribe.setOnClickListener(view -> {
                String chanid = chanpad.getTag().toString();
                subscribeNow(chanid);
            });
            loadmorerelated.setOnClickListener(view -> {
                loadmorerelated.setVisibility(View.GONE);
                progressBarr.setVisibility(View.VISIBLE);
                FetchMoreRelated("");
            });
        }
        private QuerySnapshot morequery;
        @SuppressLint("StaticFieldLeak")
        private void FetchMoreRelated(String channelId) {
            String listsearchshort="";
            listsearchshort = thisvideodesc.toLowerCase();
            if(listsearchshort.length() > 6){
                listsearchshort = listsearchshort.substring(2,6).replace("-", "");
            }
            String[] listsearch = listsearchshort.split(" ");
            Task<QuerySnapshot> taskrelated = null;
            if(!channelId.equals("")) {
                 taskrelated = db.collection("videos")
                         .whereEqualTo("religion",Constants.myReligion)
                        .whereArrayContainsAny("keywords", Arrays.asList(listsearch))
                        .whereLessThan("time", Long.parseLong(LAST_ITEM_ID))
                        .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(5).get();
            } else {
                taskrelated = db.collection("videos")
                        .whereEqualTo("channelId", channelId)
                        .whereLessThan("time", Long.parseLong(LAST_ITEM_ID))
                        .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(5).get();
            }
            taskrelated.addOnCompleteListener(MainActivity.this,task -> {
                        if(task.isSuccessful() && Objects.requireNonNull(task.getResult()).isEmpty()) {
                            morequery = task.getResult();
                            prepareMoreRelatedDocs();
                        }else {
                            FetchMoreRelated(chanid);
                        }
                    });
        }
        private void loopthroughMore() {
            for (QueryDocumentSnapshot document : morequery) {
                Map<String, Object> post = document.getData();
                final Related videopost = new Related();
                final String postdate = post.get("time") + "";
                final String poster = post.get("poster") + "";
                final String channelid = post.get("channelId") + "";
                String videotext = post.get("videotext") + "";
                final String likes = post.get("likes") + " Likes";
                final String video = post.get("video") + "";
                final String duration = post.get("duration") + "";
                final String onlineid = document.getId();
                LAST_ITEM_ID = postdate;
                final String mbs = post.get("videoSize") + "mbs";
                final String downloads = post.get("downloads") + "";
                final String comments = post.get("views") + " Views";
                //get channelinfo

                db.collection("subscriptions").whereEqualTo("subscriber", Constants.myId).whereEqualTo("subscribed", channelid)
                        .get().addOnCompleteListener(MainActivity.this, task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                subscribed = "1";
                            }
                        });
                db.collection("favourites").whereEqualTo("favouriter", Constants.myId).whereEqualTo("favourited", channelid)
                        .get().addOnCompleteListener(MainActivity.this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            favourited = "1";
                        }
                    }
                });
                db.collection("likes").whereEqualTo("liker", Constants.myId).whereEqualTo("liked", channelid)
                        .limit(1).get().addOnCompleteListener(MainActivity.this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            liked = "1";
                        }
                    }
                });
                db.collection("subscriptions").whereEqualTo("subscribed", channelid).get().addOnCompleteListener(MainActivity.this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            subscriptions = task.getResult().getDocuments().size() + "";
                        }
                    }
                });
                String finalVideotext = videotext;
                db.collection("channels").document(channelid).get()
                        .addOnCompleteListener(MainActivity.this,new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().getData().isEmpty()) {
                                    Map<String, Object> cpost = task.getResult().getData();
                                    String channelname = cpost.get("channelname") + "";
                                    String channelprofile = cpost.get("profile") + "";
                                    String totalvideos = cpost.get("videos") + "";
                                    String ownername = cpost.get("accountname") + "";
                                    String channelcover = cpost.get("cover") + "";
                                    //calculate no of days past
                                    String daysagodate = "Hot";
                                    long daysago = (System.currentTimeMillis() / (24 * 60 * 60 * 1000)) - (Long.parseLong(postdate) / (24 * 60 * 60 * 1000));
                                    if (daysago > 1 && daysago <= 7) {
                                        daysagodate = daysago + " days ago";
                                    }
                                    if (daysago > 7 && daysago <= 30) {
                                        daysagodate = (int) daysago / 7 + " weeks ago";
                                        if ((int) daysago / 7 < 2) {
                                            daysagodate = "Last week";
                                        }
                                    }
                                    if (daysago > 30 && daysago <= 360) {
                                        daysagodate = daysago + " months ago";
                                        if ((int) daysago / 30 < 2) {
                                            daysagodate = "Last month";
                                        }
                                    }
                                    if (daysago > 360) {
                                        daysagodate = daysago + " years ago";
                                        if ((int) daysago / 360 < 2) {
                                            daysagodate = "Last year";
                                        }
                                    }
                                    videopost.setChannelName(channelname);
                                    videopost.setVideosize(mbs);
                                    videopost.setChannelProfile(channelprofile);
                                    videopost.setPostDate(daysagodate);
                                    videopost.setPoster(poster);
                                    videopost.setSubscriptions(subscriptions);
                                    videopost.setVDuration(duration);
                                    videopost.setChannelId(channelid);
                                    videopost.setVideoDesc(finalVideotext);
                                    videopost.setLikeda(liked);
                                    videopost.setSubscribed(subscribed);
                                    videopost.setFavoured(favourited);
                                    videopost.setNumOfBlesses(likes);
                                    videopost.setChannelownerid(channelid);
                                    videopost.setTotalVideos(totalvideos);
                                    videopost.setVideourl(video);
                                    videopost.setDownloads(downloads);
                                    videopost.setVideoId(onlineid);
                                    videopost.setChannelCover(channelcover);
                                    videopost.setPosterName(ownername);
                                    videopost.setNumOfComments(comments);
                                    myVideoRelated.add(videopost);
                                    radapter.notifyItemInserted(myVideoRelated.size() - 1);
                                    morerelateddocs++;
                                }
                            }
                        });
            }
        }
        public void share() {
            try {
                File directory = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File pathx = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + "promovideo" + ".mp4");
                if (!videouri.equals("")) {
                    pathx = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + thisvideodesc + ".mp4");
                    Files.copy(new File(videouri), new File(pathx.getAbsolutePath()));
                }
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("video/mp4");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(pathx.getAbsolutePath())));
                    share.putExtra(Intent.EXTRA_TEXT, "Install The Largest Religious Free Video Platform To Download Amazing Videos Like This " + "https://play.google.com/store/apps/details?id=" + getPackageName());
                    startActivity(Intent.createChooser(share, "SHARE VIDEO"));
            }catch(Exception e) {
                Log.d("share error", e.getMessage());
            }
        }
        private void showPopupDownload(View view) {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_download, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyDownloadItemClickListener());
            popup.setGravity(Gravity.CENTER_HORIZONTAL);
            popup.show();
        }
        private class MyDownloadItemClickListener implements PopupMenu.OnMenuItemClickListener {

            private MyDownloadItemClickListener() {
            }
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.downloadoption) {
                    downloadedname = videodesc.getText().toString();
                    durl = videouri;
                    startRewardedVideoForDownload();
                }
                return false;
            }
        }
        private boolean instanceDownload = false;

        private void startRewardedVideoForDownload(){
            if(mRewardedVideoAd.isLoaded()){
                mRewardedVideoAd.show();
                instanceDownload =true;
                userRewarded=false;
            }
            else if(!mRewardedVideoAd.isLoaded()){
                REWARD_ON_COMPLETE=true;
                userRewarded = false;
                instanceDownload = true;
            }
        }
        private void startDownload(){
            File pathx = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + downloadedname + ".mp4");
            if(pathx.exists()){
                return;
            }
            if(!videouri.equals("")){
                try {
                    durl = proxyUrl;
                    File directory = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    pathx = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + downloadedname + ".mp4");
                    if(pathx.exists()){
                        Toast.makeText(MainActivity.this, "Video ALready Downloaded", Toast.LENGTH_LONG).show();
                        return;
                    }
                    File path = File.createTempFile(downloadedname, ".mp4", directory);
                    proxyUrl = proxyUrl.replace("file:/", "/");
                    try{
                        Files.copy(new File(proxyUrl), new File(path.getAbsolutePath()));
                    }catch (Exception g){
                        Toast.makeText(MainActivity.this, " Error Saving Video To Device "+g.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(MainActivity.this, "Saved To Device", Toast.LENGTH_LONG).show();
                }catch (Exception n){
                }
                return;
            }
            Intent intentd = new Intent(MainActivity.this,DownloadBackAgent.class);
            intentd.putExtra("downloadname",downloadedname).putExtra("videotodownload",durl);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(intentd);
            }
            else {
                getApplicationContext().startService(intentd);
            }
        }
        private void showPopupQuality(View view) {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_quality, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyQualityItemClickListener());
            popup.setGravity(Gravity.CENTER_HORIZONTAL);
            popup.show();
        }
        private class MyQualityItemClickListener implements PopupMenu.OnMenuItemClickListener {

            private MyQualityItemClickListener() {
            }
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.downloadoption) {
                    TextView videourl = findViewById(R.id.thisvideourl);
                    downloadedname = videodesc.getText().toString() + "_" + videoid;
                    durl = videouri;
                    startRewardedVideoForDownload();
                    return true;
                }
                return false;
            }
        }
        private void isSubscribed() {
            subscribe.setTextColor(Color.GREEN);
            subscribe.setText("SUBSCRIBED");
            subscribe.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_notifications_active_black_24dp), null);
            subscribe.setEnabled(false);
        }
        private void subscribeNow(final String idchan){
            isSubscribed();
            if(idchan.equals(Constants.myId)){
                return;
            }
            db.collection("subscribers").whereEqualTo("subscriber",Constants.myId)
                    .whereEqualTo("subscribed",idchan)
                    .get().addOnCompleteListener(MainActivity.this, task -> {
                        if (task.isSuccessful() && task.getResult().isEmpty()) {
                            Map<String, Object> subscribe = new HashMap<>();
                            subscribe.put("subscriber",Constants.myId);
                            subscribe.put("subscribed",idchan);
                            subscribe.put("timestamp",System.currentTimeMillis());
                            db.collection("subscriptions").add(subscribe);
                            db.collection("channels").document(idchan).update("subscriptions", FieldValue.increment(1));
                            FirebaseMessaging.getInstance().subscribeToTopic(idchan)
                                    .addOnCompleteListener(MainActivity.this,task1 -> {
                                        String msg = "success";
                                        if (!task1.isSuccessful()) {
                                            msg = "failed subscriptions";
                                        }
                                    });
                        }
                    });
        }
        @SuppressLint("StaticFieldLeak")
        private void amenPost(final String videoidtolike) {
            if(!Constants.myId.equals("")) {
                db.collection("likes").whereEqualTo("liker", Constants.myId)
                        .whereEqualTo("liked", videoidtolike)
                        .get().addOnCompleteListener(MainActivity.this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().isEmpty()) {
                            Map<String, Object> favour = new HashMap<>();
                            favour.put("liker", Constants.myId);
                            favour.put("liked", videoidtolike);
                            favour.put("timestamp", System.currentTimeMillis());
                            db.collection("likes").add(favour);
                            db.collection("videos").document(videoidtolike).update("likes", FieldValue.increment(1));
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Login To Post Anything", Toast.LENGTH_SHORT).show();
            }
        }
        @SuppressLint("StaticFieldLeak")
        private void favourites(final String videoidtolike) {
            try {
                if (!Constants.myId.equals("")) {
                    db.collection("favourites").whereEqualTo("favouriter", Constants.myId).whereEqualTo("favourited", videoidtolike)
                            .get().addOnCompleteListener(MainActivity.this, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult().isEmpty() || task.getResult().equals(null)) {
                                Map<String, Object> favour = new HashMap<>();
                                favour.put("favouriter", Constants.myId);
                                favour.put("favourited", videoidtolike);
                                favour.put("timestamp", System.currentTimeMillis());
                                db.collection("favourites").add(favour);
                                db.collection("videos").document(videoidtolike).update("favourites", FieldValue.increment(1));
                                Toast.makeText(MainActivity.this, "Added To Your Favourites", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "No Need, Already In Your Favourites", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Login To Post Anything", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception k){}
        }
        @SuppressLint("StaticFieldLeak")
        private void addViews(final String videovid) {
            try {
                db.collection("videos").document(videoid).update("views", FieldValue.increment(1));
                db.collection("trendings").whereEqualTo("videoid", videovid).limit(1)
                        .get().addOnCompleteListener(MainActivity.this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getDocuments().isEmpty()) {
                            Map<String, Object> trend = new HashMap<>();
                            trend.put("timestamp", System.currentTimeMillis());
                            trend.put("videoid", videovid);
                            trend.put("religion", Constants.myReligion);
                            trend.put("trend", 0);
                            db.collection("trendings").add(trend);
                        }
                        else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("trendings").document(document.getId()).update("trend", FieldValue.increment(1), "timestamp", System.currentTimeMillis());
                            }
                        }
                    }
                });
            }catch (Exception h){}
        }
        private String subscribed = "0";
        private String liked = "0";
        private String favourited = "0";
        private int preparecontroller=0;
    private QuerySnapshot querydocs;
    private String subscriptions = "0";
        @SuppressLint("StaticFieldLeak")
        private void fetchRelatedJSON(String channelId) {
            try {
            loadmorerelated.setVisibility(View.GONE);
            progressBarr.setVisibility(View.VISIBLE);
            String listsearchshort="";
                listsearchshort = thisvideodesc.toLowerCase();
                if (listsearchshort.length() > 6) {
                    listsearchshort = listsearchshort.substring(0, 6);
                }
                String[] listsearch = listsearchshort.split(" ");
                Task<QuerySnapshot> taskrelated;
                if(channelId.equals("")) {
                    taskrelated =
                            db.collection("videos")
                                    .whereEqualTo("religion",Constants.myReligion)
                                    .whereArrayContainsAny("keywords",Arrays.asList(listsearch))
                                    .limit(8).get();
                }
                else {
                    taskrelated = db.collection("videos")
                            .whereEqualTo("channelId", channelId)
                            .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
                            .limit(8).get();
                }
                       taskrelated.addOnSuccessListener(MainActivity.this,queryDocumentSnapshots -> {
                                myVideoRelated.clear();
                                radapter.notifyDataSetChanged();
                                if(queryDocumentSnapshots.size() > 0){
                                    querydocs = queryDocumentSnapshots;
                                    prepareRelatedDocs();
                            } else {
                                    fetchRelatedJSON(chanid);
                                    progressBarr.setVisibility(View.GONE);
                                    loadmorerelated.setVisibility(View.VISIBLE);
                                }
                        }).addOnFailureListener(d-> {
                           Toast.makeText(this, ""+d.getMessage(), Toast.LENGTH_SHORT).show();
                       });
            }catch (Exception e){
                Log.e("ccccccc",e.getMessage());
            }
        }
        private int relateddocs=0;
        private int morerelateddocs=0;
        private void prepareRelatedDocs(){
                if (relateddocs <= querydocs.size() - 1) {
                    loopThroughRelated();
                    return;
                }
            progressBarr.setVisibility(View.GONE);
            loadmorerelated.setVisibility(View.VISIBLE);
            attachRelated();
        }
        private void prepareMoreRelatedDocs(){
            assert morequery != null;
                if (morerelateddocs <= morequery.size() - 1) {
                    loopthroughMore();
                    return;
                }
            progressBarr.setVisibility(View.GONE);
            loadmorerelated.setVisibility(View.VISIBLE);
        }
        private void loopThroughRelated(){
            DocumentSnapshot document = querydocs.getDocuments().get(relateddocs);
            Map<String, Object> post = document.getData();
            final Related videopost = new Related();
            assert post != null;
            final String postdate = post.get("time") + "";
            final String poster = post.get("poster") + "";
            final String channelid = post.get("channelId") + "";
            String videotext = post.get("videotext") + "";
            final String likes = post.get("likes") + " Likes";
            final String video = post.get("video") + "";
            final String duration = post.get("duration") + "";
            final String onlineid = document.getId();
            LAST_ITEM_ID = postdate;
            Log.d("lastrelated",LAST_ITEM_ID);
            final String mbs = post.get("videoSize") + "mbs";
            final String downloads = post.get("downloads") + "";
            final String comments = post.get("views") + " Views";
            //get channelinfo

            db.collection("subscriptions").whereEqualTo("subscriber", Constants.myId).whereEqualTo("subscribed", onlineid)
                    .get().addOnCompleteListener(MainActivity.this,new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        subscribed = "1";
                    }
                }
            });
            db.collection("favourites").whereEqualTo("favouriter", Constants.myId).whereEqualTo("favourited", onlineid)
                    .get().addOnCompleteListener(MainActivity.this,new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        favourited = "1";
                    }
                }
            });
            db.collection("likes").whereEqualTo("liker", Constants.myId).whereEqualTo("liked", onlineid)
                    .limit(1).get().addOnCompleteListener(MainActivity.this,new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        liked = "1";
                    }
                }
            });
            db.collection("subscriptions").whereEqualTo("subscribed", channelid).get().addOnCompleteListener(MainActivity.this,new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        subscriptions = task.getResult().getDocuments().size() + "";
                    }
                }
            });
            String finalVideotext = videotext;
            db.collection("channels").document(channelid).get().addOnCompleteListener(MainActivity.this,new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().getData().isEmpty()) {
                        Map<String, Object> cpost = task.getResult().getData();
                        String channelname = cpost.get("channelname") + "";
                        String channelprofile = cpost.get("profile") + "";
                        String totalvideos = cpost.get("videos") + "";
                        String ownername = cpost.get("accountname") + "";
                        String channelcover = cpost.get("cover") + "";
                        //calculate no of days past
                        String daysagodate = "Hot";
                        long daysago = (System.currentTimeMillis() / (24 * 60 * 60 * 1000)) - Long.parseLong(postdate) / (24 * 60 * 60 * 1000);
                        if (daysago > 1 && daysago <= 7) {
                            daysagodate = daysago + " days ago";
                        }
                        if (daysago > 7 && daysago <= 30) {
                            daysagodate = (int) daysago / 7 + " weeks ago";
                            if ((int) daysago / 7 < 2) {
                                daysagodate = "Last week";
                            }
                        }
                        if (daysago > 30 && daysago <= 360) {
                            daysagodate = daysago + " months ago";
                            if ((int) daysago / 30 < 2) {
                                daysagodate = "Last month";
                            }
                        }
                        if (daysago > 360) {
                            daysagodate = daysago / 360 + " years ago";
                            if ((int) daysago / 360 < 2) {
                                daysagodate = "Last year";
                            }
                        }
                        videopost.setChannelName(channelname);
                        videopost.setVideosize(mbs);
                        videopost.setChannelProfile(channelprofile);
                        videopost.setPostDate(daysagodate);
                        videopost.setPoster(poster);
                        videopost.setSubscriptions(subscriptions);
                        videopost.setVDuration(duration);
                        videopost.setChannelId(channelid);
                        videopost.setVideoDesc(finalVideotext);
                        videopost.setLikeda(liked);
                        videopost.setSubscribed(subscribed);
                        videopost.setFavoured(favourited);
                        videopost.setNumOfBlesses(likes);
                        videopost.setChannelownerid(channelid);
                        videopost.setTotalVideos(totalvideos);
                        videopost.setVideourl(video);
                        videopost.setDownloads(downloads);
                        videopost.setVideoId(onlineid);
                        videopost.setChannelCover(channelcover);
                        videopost.setPosterName(ownername);
                        videopost.setNumOfComments(comments);
                        myVideoRelated.add(videopost);
                        relateddocs++;
                        prepareRelatedDocs();
                    }
                }
            });
        }
        private void attachRelated() {
            recyclerViewrelated.setAdapter(null);
            radapter = new RelatedAdapter(MainActivity.this,myVideoRelated);
            recyclerViewrelated.setItemViewCacheSize(20);
            radapter.setOnRecyclerViewItemClickListener(new RelatedAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemRelatedClicked(String[] videoinfo) {
                    YoYo.with(Techniques.SlideInUp).duration(300).repeat(0).playOn(videoView);
                    videouri = videoinfo[0];
                    if(videouri == lastUri){
                        Toast.makeText(MainActivity.this, "Video Already Playing", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    initializePlayerAds();
                    TextView videolikes = findViewById(R.id.thislikes);
                    TextView videodownloads = findViewById(R.id.thisdownloads);
                    TextView videosid = findViewById(R.id.thisvideoids);
                    TextView videourl = findViewById(R.id.tvvideourl);
                    videoid = videoinfo[1];
                    videolike = videoinfo[2];
                    thisvideoviews = videoinfo[3];
                    thisvideodesc = videoinfo[4];
                    chanprourl = videoinfo[5];
                    chanid = videoinfo[6];
                    String subsc = videoinfo[7];
                    String channame = videoinfo[8];
                    String chancover = videoinfo[9];
                    String totalvids = videoinfo[10];
                    String isliked = videoinfo[11];
                    String isfavoured = videoinfo[12];
                    String issubscribed = videoinfo[13];
                    subt.setText("SUBSCRIBERS " + subsc);
                    tvchan.setText(channame);
                    String[] strlike = videolike.split(" ");
                    String strlk = strlike[0];
                    String[] strviews = thisvideoviews.split(" ");
                    String strvws = strviews[0];
                    videolikes.setText(strlk);
                    videodownloads.setText(videodownloads.getText().toString());
                    videoviews.setText(strvws);
                    videolikes.setTag(isliked);
                    videodownloads.setTag(isfavoured);
                    videoviews.setTag(issubscribed);
                    videosid.setText(videoid);
                    videourl.setText(videouri);
                    videodesc.setText(thisvideodesc);
                    addViews(videoid);
                    amen.setEnabled(true);
                    chanpad.setTag(chanid);
                    tvchan.setTag(chancover);
                    subt.setTag(totalvids);
                    videodesc.setTag(subsc);
                    if (issubscribed.equals("1")) {
                        isSubscribed();
                    }
                    Glide.with(chanpro.getContext()).load(chanprourl).circleCrop().into(chanpro);
                    if (isliked.equals("1")) {
                        Glide.with(amen.getContext()).load(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp)).into(amen);
                        amen.setEnabled(false);
                    } else {
                        Glide.with(amen.getContext()).load(getResources().getDrawable(R.drawable.ic_like)).into(amen);
                        amen.setEnabled(true);
                    }
                    if (isfavoured.equals("1")) {
                        Glide.with(favourite.getContext()).load(getResources().getDrawable(R.drawable.ic_star_active2)).into(favourite);
                        favourite.setEnabled(false);
                    } else {
                        Glide.with(favourite.getContext()).load(getResources().getDrawable(R.drawable.ic_star)).into(favourite);
                        favourite.setEnabled(true);
                    }
                }
            });
            recyclerViewrelated.setAdapter(radapter);
            recyclerViewrelated.setLayoutManager(new LinearLayoutManager(MainActivity.this.getApplicationContext(), RecyclerView.VERTICAL, false));
        }
        //Ads Managements

        private void loadRewardedVideoAd() {
            try {
                mRewardedVideoAd.loadAd(getResources().getString(R.string.RewardedAd_unit_id),
                        new AdRequest.Builder().build());
            }catch (Exception v){}
        }
        @SuppressLint("StaticFieldLeak")
        private void DownloadVideo(String DownloadUrl, final String fileName) {
        }

        private void loadBanner() {
            AdRequest adRequest =
                    new AdRequest.Builder()
                            .build();

            AdSize adSize = getAdSize();
            // Step 4 - Set the adaptive ad size on the ad view.
            adView.setAdSize(adSize);

            // Step 5 - Start loading the ad in the background.
            adView.loadAd(adRequest);
        }
        private AdSize getAdSize() {
            // Step 2 - Determine the screen width (less decorations) to use for the ad width.
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;

            int adWidth = (int) (widthPixels / density);

            // Step 3 - Get adaptive ad size and return for setting on the ad view.
            return AdSize.getCurrentOrientationBannerAdSizeWithWidth(MainActivity.this,adWidth);
        }
        private boolean userRewarded = false;
        private RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener () {
            @Override
            public void onRewarded(RewardItem reward) {
                // Reward the user.
                if(instanceDownload){
                    startDownload();
                    userRewarded = true;
                }
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
                if(instanceDownload && !userRewarded) {
                    Snackbar d = Snackbar.make(getWindow().getDecorView(), "Closed Ad " +
                            " Download Video Failed ", BaseTransientBottomBar.LENGTH_INDEFINITE);
                    d.setActionTextColor(Color.RED);
                    d.setTextColor(Color.BLACK);
                    d.setBackgroundTint(Color.WHITE).setBackgroundTintMode(PorterDuff.Mode.SRC_OVER);
                    d.setAction(
                            "Dismiss", view -> d.setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                    );
                    d.show();
                    loadRewardedVideoAd();
                }
            }
            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                new Handler().postDelayed(() -> loadRewardedVideoAd(),30000);
            }
            @Override
            public void onRewardedVideoAdLoaded() {
                if (REWARD_ON_COMPLETE) {
                    mRewardedVideoAd.show();
                    REWARD_ON_COMPLETE = false;
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Toast.makeText(MainActivity.this, "Whatch Until Download Start...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoStarted() {
                loadRewardedVideoAd();
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        };
        private void insertAdsInMenuItems() {
            try {
                int index = 0;
                for (UnifiedNativeAd ad : mNativeAds) {
                    myVideoRelated.add(index, ad);
                    radapter.notifyItemInserted(index);
                }
            }catch (Exception exp){
            }
        }
        private AdLoader adLoader;
        private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
        private void loadNativeAds() {
            mNativeAds.clear();
            // code goes here.
            AdLoader.Builder builder = new AdLoader.Builder(MainActivity.this, getString(R.string.ad_unit_id));
            adLoader = builder.forUnifiedNativeAd(
                    new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            // A native ad loaded successfully, check if the ad loader has finished loading
                            // and if so, insert the ads into the list.
                            mNativeAds.add(unifiedNativeAd);
                            if(!adLoader.isLoading()) {
                                insertAdsInMenuItems();
                            }
                        }
                    }).withAdListener(
                    new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            // A native ad failed to load, check if the ad loader has finished loading
                            // and if so, insert the ads into the list.
                            Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                    + " load another vv."+errorCode);
                            if(!adLoader.isLoading()) {
                                insertAdsInMenuItems();
                            }
                        }
                    }).build();
            // Load the Native Express ad.
            adLoader.loadAds(new AdRequest.Builder().build(), 1);
        }
}