package videos.religious.platform;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.provider.FontRequest;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class
MainFavourites extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ChannelVideos.OnFragmentInteractionListener,Subscriptions.OnFragmentInteractionListener, Notifications.OnChangeCurrentView,Preferrences.OnFragmentInteractionListener,MyAllVideos.OnFragmentInteractionListener,Downloads.OnFragmentInteractionListener, AllWatchedVideos.OnFragmentInteractionListener, Favouritesfragment.OnFragmentInteractionListener,Likedfragment.OnFragmentInteractionListener {
    private ExtendedViewPager extendedViewPager;
    private FragmentPagerAdapter adapterViewPager;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private MenuItem itemmenu;
    private CardView appsettings;
    public static boolean auto_play = false;
    private Switch autoplay;
    private FirebaseAuth fAuth;
    private class WamburaFavourites extends FragmentPagerAdapter {
        private int NUM_ITEMS = 6;

        private WamburaFavourites(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public int getCount() {
            return this.NUM_ITEMS;
        }

        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return videos.religious.platform.Downloads.newInstance("", "Hopetv");
                case 1:
                    return videos.religious.platform.Favouritesfragment.newInstance("", "Hopetv");
                case 2:
                    return videos.religious.platform.AllWatchedVideos.newInstance("", "Church");
                case 3:
                    return videos.religious.platform.Likedfragment.newInstance("", "Hopetv");
                case 4:
                    return videos.religious.platform.MyAllVideos.newInstance("", "Hopetv");
                case 5:
                    return videos.religious.platform.Subscriptions.newInstance("", "Hopetv");
                default:
                    return videos.religious.platform.Downloads.newInstance("", "Hopetv");
            }
        }
        public CharSequence getPageTitle(int i) {
            StringBuilder sb = new StringBuilder();
            sb.append("Page ");
            sb.append(i);
            return sb.toString();
        }
    }

    private void plainChar(){
        Task<DocumentSnapshot> d = db.collection("serverApp").document("serverApp").get();
        assert d != null;
        d.addOnSuccessListener(n->{
            Constants.plainchar = (List<String>) n.get("plainChar");
        });
    }
    private void securityAlgorithmString(){
        Task<DocumentSnapshot> d = db.collection("serverApp").document("serverApp").get();
        assert d != null;
        d.addOnSuccessListener(n->{
            Constants.securechar = (List<String>) n.get("secureChar");
            Constants.currenttime = encryptDecrypt.decryptString(preferences.getString("currenttime",""));
            Constants.currentime = encryptDecrypt.decryptString(preferences.getString("currentime",""));
        });
    }
    private FirebaseFirestore db;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_favourites);
        db = FirebaseFirestore.getInstance();
        plainChar();
        preferences = getSharedPreferences("channelInfo", Context.MODE_PRIVATE);
        //Assigning local details to variables
        setTitle(Constants.myChannelName);
        fAuth = FirebaseAuth.getInstance();
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
        Constants.myReligion= preferences.getString("religion","");
        Constants.phone= preferences.getString("phone","");
        extendedViewPager =  findViewById(R.id.vpPagerFavourites);
        this.adapterViewPager = new WamburaFavourites(getSupportFragmentManager());
        extendedViewPager.setAdapter(this.adapterViewPager);
        extendedViewPager.setPageTransformer(true, (ViewPager.PageTransformer) new CubeOutTransformer());
        extendedViewPager.setOffscreenPageLimit(10);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_favourites);
        View view = navigationView.getHeaderView(0);
        ImageView header = view.findViewById(R.id.imageView);
        Glide.with(header).load(Constants.myChannelProfile).circleCrop().into(header);
        TextView name = view.findViewById(R.id.name);
        TextView name2 = view.findViewById(R.id.textView);
        name.setText(Constants.myChannelName);
        name2.setText(Constants.myFullname);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Dowloads");
        autoplay = findViewById(R.id.autoplay);
        extendedViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        toolbar.setTitle("Downloads");
                        YoYo.with(Techniques.SlideInRight).duration(300).onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                autoplay.setVisibility(View.VISIBLE);
                            }
                        }).playOn(autoplay);
                        break;
                    case 1:
                        toolbar.setTitle("Favourites");
                        YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                autoplay.setVisibility(View.GONE);
                            }
                        }).playOn(autoplay);
                        break;
                    case 2:
                        toolbar.setTitle("Loved & Liked");
                        YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                autoplay.setVisibility(View.GONE);
                            }
                        }).playOn(autoplay);
                        break;
                    case 3:
                        toolbar.setTitle("Liked Videos");
                        YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            autoplay.setVisibility(View.GONE);
                        }
                    }).playOn(autoplay);
                        break;
                    case 4:
                        toolbar.setTitle("My Videos");
                        YoYo.with(Techniques.SlideOutRight).duration(300).onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                autoplay.setVisibility(View.GONE);
                            }
                        }).playOn(autoplay);
                        break;
                    case 5:
                        toolbar.setTitle("Subscriptions");
                        YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                autoplay.setVisibility(View.GONE);
                            }
                        }).playOn(autoplay);
                        break;
                        default:
                            toolbar.setTitle("Downloads");
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        appsettings = findViewById(R.id.appsettings);
        final Button shortcutb = findViewById(R.id.homescreeniconok);
        shortcutb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShourcut();
                HideAnimation(appsettings);
            }
        });
        autoplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemmenu == null) {
                    if (auto_play) {
                        auto_play = false;
                        autoplay.setChecked(false);
                        return;
                    }
                    autoplay.setChecked(true);
                    auto_play = true;
                    return;
                }
                configAutoPlay();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        MobileAds.initialize(this, "" + R.string.admob_app_id);
        okhttp3.OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(28800, TimeUnit.SECONDS)
                . writeTimeout(28800, TimeUnit.SECONDS)
                . readTimeout(28800, TimeUnit.SECONDS)
                .build();
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
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        final ImageView floatingActionButton = findViewById(R.id.addnewvideo);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.RubberBand).duration(100).playOn(floatingActionButton);
                startPicker();
            }
        });
        securityAlgorithmString();
    }
    private void configAutoPlay(){
        if (auto_play) {
            auto_play = false;
            autoplay.setChecked(false);
            itemmenu.setIcon(R.drawable.ic_remove_red_eye_black_24dp);
            itemmenu.setTitle("Enable AutoPlay");
            return;
        }
        auto_play = true;
        autoplay.setChecked(true);
        itemmenu.setIcon(R.drawable.ic_visibility_off_black_24dp);
        itemmenu.setTitle("Disable AutoPlay");
    }
    private void startPicker() {
        ArrayList<String> filepaths = new ArrayList<>();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode)
        {
            case 1:
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    ArrayList<String> videoPaths = new ArrayList<>();
                    videoPaths.addAll(data.getStringArrayListExtra("2"));
                    String video = videoPaths.get(0);
                    startUpload(video);
                }
                break;
        }
    }
    private void startUpload(String video){
        if(Constants.STATUS) {
            Intent intent = new Intent(this, FragviewActivity.class);
            intent.putExtra("fragId", 15).putExtra("uri", video);
            startActivity(intent);
        } else {
            Snackbar d = Snackbar.make(this.getWindow().getDecorView(),"Please Go To Your Channel And Verify " +
                    "Your Email To Start Uploading Videos", BaseTransientBottomBar.LENGTH_INDEFINITE);
            d.setActionTextColor(Color.RED);
            d.setTextColor(Color.WHITE);
            d.setBackgroundTint(Color.BLACK).setBackgroundTintMode(PorterDuff.Mode.SRC_OVER);
            d.setAction(
                    "Ok", view -> d.setDuration(BaseTransientBottomBar.LENGTH_SHORT)
            );
            d.show();
        }
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent homeintent = new Intent(this, MainActivity.class);
            startActivity(homeintent);
        } else if (id == R.id.nav_myvideos) {
            extendedViewPager.setCurrentItem(4);
        } else if (id == R.id.nav_downloads) {
            extendedViewPager.setCurrentItem(0);
        } else if (id == R.id.nav_favorites) {
            extendedViewPager.setCurrentItem(1);
        } else if (id == R.id.nav_liked) {
            extendedViewPager.setCurrentItem(3);
        } else if (id == R.id.nav_viewed) {
            extendedViewPager.setCurrentItem(2);
        } else if (id == R.id.nav_subscriptions) {
            extendedViewPager.setCurrentItem(5);
        } else if (id == R.id.nav_business) {
            extendedViewPager.setCurrentItem(5);
        } else if (id == R.id.nav_balance) {
            extendedViewPager.setCurrentItem(5);
        } else if (id == R.id.nav_settings) {
            ShowAnimation(appsettings);
        } else if (id == R.id.nav_account) {
            Intent intent = new Intent(this, FragviewActivity.class);
            intent.putExtra("fragId", 2);
            startActivity(intent);
        } else if (id == R.id.nav_change_account_email) {
            Intent email = new Intent(this, PasswordAndEmailReset.class);
            email.putExtra("activityType", "changeEmail");
            startActivity(email);
        } else if (id == R.id.nav_change_account_password) {
            Intent pass = new Intent(this, PasswordAndEmailReset.class);
            pass.putExtra("activityType", "changePassword");
            startActivity(pass);
        } else if (id == R.id.nav_verify_email) {
            if (Constants.STATUS) {
                Toast.makeText(this, "No Need Account Is Already Verified", Toast.LENGTH_SHORT).show();
                return false;
            }
            Intent verify = new Intent(this, PasswordAndEmailReset.class);
            verify.putExtra("activityType", "verifyEmail");
            startActivity(verify);
        } else if (id == R.id.nav_swap_history) {
        } else if (id == R.id.disableauto) {
            this.itemmenu = item;
            configAutoPlay();
        } else if (id == R.id.logout) {
            Toast.makeText(this, "Loging Out....", Toast.LENGTH_LONG).show();
            this.itemmenu = item;
            SharedPreferences channelinfo = getSharedPreferences("channelInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = channelinfo.edit();
            editor.remove("channelid")
                    .remove("accountname")
                    .remove("ccover")
                    .remove("cprofile")
                    .remove("channelname")
                    .remove("email")
                    .remove("clocation");
            editor.apply();
            new Handler().postDelayed(() -> {
                final ComponentName compName =
                        new ComponentName(getApplicationContext(),
                                MainFavourites.class);
                getPackageManager().setComponentEnabledSetting(compName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                finishAffinity();
                System.exit(0);
            }, 3000);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onFragmentInteraction(Uri uri) {
    }
    public void OnChangeCurrentView(int page){
        OnChangeViewPager(page);
    }
    public void ShowAnimation(final View view){
        view.animate()
                .alpha(1.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        YoYo.with(Techniques.Wobble).duration(1000).playOn(view);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }
    public void HideAnimation(final View view){
        view.animate()
                .alpha(0.0f)
                .setDuration(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        YoYo.with(Techniques.RubberBand).duration(300).playOn(view);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }
    private void addShourcut(){
    }
    @Override
    public void OnChangeViewPager(int pageno){
        extendedViewPager.setCurrentItem(pageno);
    }
}