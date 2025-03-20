package videos.religious.platform;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.io.Files;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class sfragmentdialog extends DialogFragment {
    private ImageView chanpro,downloadnow;
    private ImageButton amen,fullwindow,smallWindow,unmuteVideo,download,unloopVideo,muteVideo,loopVideo,share,favourite;
    private PlayerView videoView;
    private boolean REWARD_ON_COMPLETE = false;
    private Button subscribe;
    private RelativeLayout Tv, chanpad;
    private boolean oriented = false;
    private LinearLayout Tvbottom,Tvtop;
    private RelatedAdapter radapter;
    private RewardedVideoAd mRewardedVideoAd;
    private static String videoid,nvideoview,nvideodesc;
    private FrameLayout adContainerView;
    private TextView tvchan, videoviews, subt, videodesc, loadmorerelated;
    private ProgressBar progressBarr;
    private AdView adView;
    private SimpleExoPlayer player;
    private FirebaseFirestore db;
    private static String videouri;
    private SimpleExoPlayer exoPlayer;
    private int currentpos;
    private RecyclerView recyclerViewrelated;
    private String videolike,LAST_RELATED_ITEM_ID,channelprofile, thisvideoviews, chanid="xyzxyzxyz", thisvideodesc, ChannelName, downloadedname, chanprourl, durl;
    private ArrayList myVideoRelated = new ArrayList<>();
    private String LAST_ITEM_ID = "0";
    private View rootView;
    private FrameLayout videoViewCont;
    private static String lastUri = "";

    public static sfragmentdialog newInstance(String nvideourl, String nvideoid,String nvideoviews,String ndesc) {
        videouri = nvideourl;
        videoid = nvideoid;
        nvideoview = nvideoviews;
        nvideodesc = ndesc;
        return new sfragmentdialog();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return dialog;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadNativeAds();
        db = FirebaseFirestore.getInstance();
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
        loadRewardedVideoAd();
        thisvideodesc = nvideodesc;
        fetchJSON(videoid);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.maintv, container, false);
        Tv = rootView.findViewById(R.id.Tvmain);
        Tv.setVisibility(View.VISIBLE);
        adContainerView = rootView.findViewById(R.id.ad_viewtv_container);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(getContext());
        adView.setAdUnitId(getString(R.string.NewsBannerAd_unit_id));
        adContainerView.addView(adView);
        loadBanner();
        chanpro = rootView.findViewById(R.id.channpro);
        recyclerViewrelated = rootView.findViewById(R.id.recycler_view_related);
        videoView = rootView.findViewById(R.id.video_view);
        Tvbottom = rootView.findViewById(R.id.Tvbottom);
        Tvtop = rootView.findViewById(R.id.Tvtop);
        loadmorerelated = rootView.findViewById(R.id.loadmorerelated);
        progressBarr = rootView.findViewById(R.id.progressmorerelated);
        chanpad = rootView.findViewById(R.id.channelpad);
        subt = rootView.findViewById(R.id.sub);
        videoviews = rootView.findViewById(R.id.thisviews);
        radapter = new RelatedAdapter(getContext(), myVideoRelated);
        videodesc = rootView.findViewById(R.id.thisvideodesc);
        tvchan = rootView.findViewById(R.id.tvchannel);
        amen = rootView.findViewById(R.id.thislikesimg);
        fullwindow = rootView.findViewById(R.id.fullvideo);
        smallWindow = rootView.findViewById(R.id.floatvideo);
        muteVideo = rootView.findViewById(R.id.mutevideo);
        unmuteVideo = rootView.findViewById(R.id.unmutevideo);
        unloopVideo = rootView.findViewById(R.id.unloopvideo);
        loopVideo = rootView.findViewById(R.id.loopvideo);
        subscribe = rootView.findViewById(R.id.subscribe);
        favourite = rootView.findViewById(R.id.thisfavourites);
        share = rootView.findViewById(R.id.thisshare);
        videoView =rootView.findViewById(R.id.video_view);
        videoViewCont =rootView.findViewById(R.id.video_view_cont);
        double returnheight =  getActivity().getWindow().getDecorView().getWidth()/1.778;
        RelativeLayout.LayoutParams floatBoxParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)returnheight);
        videoViewCont.setLayoutParams(floatBoxParams);
        fetchRelatedJSON("");
        return rootView;
    }
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
                // code goes here.
                AdLoader.Builder builder = new AdLoader.Builder(getContext(), getString(R.string.ad_unit_id));
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
    private  void prepareControl(){
        TextView qualityoption =rootView.findViewById(R.id.qualityoption);
        qualityoption.setOnClickListener(this::showPopupQuality);
        download.setOnClickListener(this::showPopupDownload);
        downloadnow.setOnClickListener(this::showPopupDownload);
    }
    @Override
    public void onViewCreated(@Nullable final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            Tvtop =rootView.findViewById(R.id.Tvtop);
            download =rootView.findViewById(R.id.exodownload);
            downloadnow =rootView.findViewById(R.id.downloadnow);
        YoYo.with(Techniques.Bounce).repeat(5).duration(250).playOn(downloadnow);
        prepareControl();
        initializePlayerAds();
        Tvbottom.setVisibility(View.GONE);
        Tvtop.setVisibility(View.GONE);
        videodesc.setText(nvideodesc);
        videoviews.setText(nvideoview);
    }
    private void initializePlayerAds() {
        Glide.with(download).load(getResources().getDrawable(R.drawable.download)).circleCrop().into(download);
        try {
            if(null != exoPlayer) {
                if (exoPlayer.isPlaying() || exoPlayer.isLoading()) {
                    if(lastUri.equals(videouri)) {
                        Toast.makeText(getContext(), "Video ALready Playing", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                exoPlayer.release();
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
                    new Handler().postDelayed(() -> exoPlayer.retry(),5000);
                    Toast.makeText(getContext(), "Failed to Play - Retrying ", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception ignored){
        }
    }
    private void updateVideoProgress() {
        long videoProgress = exoPlayer.getCurrentPosition() * 100 / exoPlayer.getDuration();
    }

    void seekVideo() {
        long videoPosition = exoPlayer.getDuration();
        exoPlayer.seekTo(videoPosition);
    }
    private void setCachedState(boolean cached) {
        int statusIconId = cached ? R.drawable.ic_cloud_done_black_24dp : R.drawable.ic_cloud_download_black_24dp;
    }
    private SimpleExoPlayer setupPlayer() {
        videoView.setUseController(true);
        Log.d("usevideouri", "Use proxy url " + videouri + " instead of original url " + videouri);

        exoPlayer = newSimpleExoPlayer();
        videoView.setPlayer(exoPlayer);

        MediaSource videoSource = newVideoSource(videouri);
        exoPlayer.setMediaSource(videoSource);

        return exoPlayer;
    }
    private SimpleExoPlayer newSimpleExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        return new SimpleExoPlayer.Builder(getActivity())
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .build();
    }

    private MediaSource newVideoSource(String url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(getActivity()).build();
        String userAgent = Util.getUserAgent(getContext(), "AndroidVideoCache sample");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), userAgent, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    }
    
    @SuppressLint("SourceLockedOrientationActivity")
    private void initmoretvtools(){
        amen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.SlideInUp).repeat(0).duration(600).playOn(amen);
                Glide.with(amen.getContext()).load(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp)).fitCenter().into(amen);
                amen.setEnabled(false);
                TextView videoidt = rootView.findViewById(R.id.thisvideoids);
                TextView videolikeadd = rootView.findViewById(R.id.thislikes);
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
                YoYo.with(Techniques.RollIn).duration(400).playOn(videolikeadd);
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
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chanid = chanpad.getTag().toString();
                subscribeNow(chanid);
            }
        });
        loadmorerelated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarr.setVisibility(View.VISIBLE);
                FetchMoreRelated("");
            }
        });
        chanpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String channelpadid = chanpad.getTag().toString();
                String channelcover = tvchan.getTag().toString();
                String subsc = videodesc.getTag().toString();
                String totalvid = subt.getTag().toString();
                String issub = videoviews.getTag().toString();
                TextView channlname = rootView.findViewById(R.id.tvchannel);
                String cname = channlname.getText().toString();
                Intent intent = new Intent(getContext(), VisitChannel.class);
                intent.putExtra("fragid", channelpadid);
                intent.putExtra("cname", cname);
                intent.putExtra("ccover", channelcover);
                intent.putExtra("chanpro", channelprofile);
                intent.putExtra("videos", totalvid);
                intent.putExtra("subsc", subsc);
                intent.putExtra("issub", issub);
                startActivity(intent);
            }
        });
        fullwindow.setOnClickListener(view -> {
            try {
                if (!oriented) {
                    RelativeLayout.LayoutParams fullparams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    videoViewCont.setLayoutParams(fullparams);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    Tvbottom.setVisibility(View.GONE);
                    Tvtop.setVisibility(View.GONE);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    oriented = true;
                } else {
                    RelativeLayout.LayoutParams fullparams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (getActivity().getWindow().getDecorView().getHeight() / 1.778));
                    videoViewCont.setLayoutParams(fullparams);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Tvbottom.setVisibility(View.VISIBLE);
                    Tvtop.setVisibility(View.VISIBLE);
                    oriented = false;
                }
            }catch (Exception c){
            }
        });
        smallWindow.setOnClickListener(view -> getDialog().dismiss());
        loopVideo.setOnClickListener(view -> {
            loopVideo.setVisibility(View.INVISIBLE);
            unloopVideo.setVisibility(View.VISIBLE);
        });
        unloopVideo.setOnClickListener(view -> {
            loopVideo.setVisibility(View.VISIBLE);
            unloopVideo.setVisibility(View.INVISIBLE);
        });
        muteVideo.setOnClickListener(view -> {
            muteVideo.setVisibility(View.INVISIBLE);
            unmuteVideo.setVisibility(View.VISIBLE);
        });
        unmuteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muteVideo.setVisibility(View.VISIBLE);
                unmuteVideo.setVisibility(View.INVISIBLE);
            }
        });
    }
    private String subscribed = "0";
    private String liked = "0";
    private String favourited = "0";
    private String subscriptions = "0";
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
        taskrelated.addOnCompleteListener(getActivity(),task -> {
            if(task.isSuccessful() && Objects.requireNonNull(task.getResult()).isEmpty()) {
                morequery = task.getResult();
                prepareMoreRelatedDocs();
            }else {
                FetchMoreRelated(chanid);
            }
        });
    }
    public void share() {
        try { File directory = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File pathx = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + "promovideo" + ".mp4");
            if (!videouri.equals("")) {
                pathx = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + thisvideodesc + ".mp4");
                videouri = videouri.replace("file:/", "/");
                Files.copy(new File(videouri), new File(pathx.getAbsolutePath()));
            }
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("video/mp4");
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(pathx.getAbsolutePath())));
            share.putExtra(Intent.EXTRA_TEXT, "Install The Largest Religious Free Video Platform " + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
            startActivity(Intent.createChooser(share, "SHARE VIDEO"));
        } catch (Exception e) {
        }
    }
    private void showPopupQuality(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
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
                TextView videourl = rootView.findViewById(R.id.thisvideourl);
                downloadedname = videodesc.getText().toString() + "_" + videoid;
                durl = videouri;
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                    REWARD_ON_COMPLETE = false;
                    userRewarded = false;
                    return true;
                } else if (!adLoader.isLoading()) {
                    loadRewardedVideoAd();
                    userRewarded = false;
                    REWARD_ON_COMPLETE = true;
                    return true;
                }
                REWARD_ON_COMPLETE = true;
                return true;
            }
            return false;
        }
    }
    private void showPopupDownload(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
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
                TextView videourl = rootView.findViewById(R.id.thisvideourl);
                downloadedname = videodesc.getText().toString();
                durl = videouri;
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                    REWARD_ON_COMPLETE = false;
                    userRewarded = false;
                    return true;
                } else if (!adLoader.isLoading()) {
                    loadRewardedVideoAd();
                    userRewarded = false;
                    REWARD_ON_COMPLETE = true;
                    return true;
                }
                REWARD_ON_COMPLETE = true;
                return true;
            }
            return false;
        }
    }
    private void isSubscribed() {
        subscribe.setTextColor(Color.GREEN);
        subscribe.setText("SUBSCRIBED");
        subscribe.setBackground(null);
        subscribe.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_notifications_active_black_24dp), null);
        subscribe.setEnabled(false);
    }
    private void subscribeNow(final String idchan){
        isSubscribed();
        if(idchan.equals(Constants.myId)){
            return;
        }
        db.collection("subscribers").whereEqualTo("subscriber",Constants.myId).whereEqualTo("subscribed",idchan)
                .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().isEmpty()) {
                    Map<String, Object> subscribe = new HashMap<>();
                    subscribe.put("subscriber",Constants.myId);
                    subscribe.put("subscribed",idchan);
                    subscribe.put("timestamp",System.currentTimeMillis());
                    db.collection("subscriptions").add(subscribe);
                    db.collection("channels").document(idchan).update("subscriptions", FieldValue.increment(1));
                    FirebaseMessaging.getInstance().subscribeToTopic(idchan)
                            .addOnCompleteListener(getActivity(),new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "success";
                                    if (!task.isSuccessful()) {
                                        msg = "failed subscriptions";
                                    }
                                }
                            });
                }
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    private void amenPost(final String videoidtolike) {
        if(!Constants.myId.equals("")) {
            db.collection("likes").whereEqualTo("liker", Constants.myId).whereEqualTo("liked", videoidtolike)
                    .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
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
        else{
            Toast.makeText(getContext(), "Login To Post Anything", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("StaticFieldLeak")
    private void favourites(final String videoidtolike) {
        if(!Constants.myId.equals("")) {
            db.collection("favourites").whereEqualTo("favouriter", Constants.myId).whereEqualTo("favourited", videoidtolike)
                    .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult().isEmpty() || task.getResult().equals(null)) {
                        Map<String, Object> favour = new HashMap<>();
                        favour.put("favouriter", Constants.myId);
                        favour.put("favourited", videoidtolike);
                        favour.put("timestamp", System.currentTimeMillis());
                        db.collection("favourites").add(favour);
                        db.collection("videos").document(videoidtolike).update("favourites", FieldValue.increment(1));
                        Toast.makeText(getContext(), "Added To Your Favourites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "No Need, Already In Your Favourites", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(getContext(), "Login To Post Anything", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("StaticFieldLeak")
    private void addViews(final String videovid) {
        db.collection("videos").document(videoid).update("views", FieldValue.increment(1));
        db.collection("trendings").whereEqualTo("videoid",videovid).limit(1)
                .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().isEmpty()) {
                    Map<String, Object> trend = new HashMap<>();
                    trend.put("timestamp",System.currentTimeMillis());
                    trend.put("videoid",videovid);
                    trend.put("religion",Constants.myReligion);
                    trend.put("trend", 0);
                    db.collection("trendings").add(trend);
                }else {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection("trendings").document(document.getId()).update("trend", FieldValue.increment(1));
                    }
                }
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    private void fetchRelatedJSON( String channelId) {
        try {
            loadmorerelated.setVisibility(View.GONE);
            progressBarr.setVisibility(View.VISIBLE);
            String listsearchshort="";
            listsearchshort = thisvideodesc.toLowerCase();
            if (listsearchshort.length() > 6) {
                listsearchshort = listsearchshort.substring(0, 6);
            }
            String[] listsearch = listsearchshort.split(" ");
            Task<QuerySnapshot> taskrelated = null;
            if(channelId.equals("")) {
                taskrelated =
                        db.collection("videos")
                                .whereEqualTo("religion",Constants.myReligion)
                                .whereArrayContainsAny("keywords",Arrays.asList(listsearch))
                                .orderBy("time", Query.Direction.DESCENDING)
                                .limit(8).get();
            }
            else {
                taskrelated = db.collection("videos")
                        .whereEqualTo("channelId", channelId)
                        .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(8).get();
            }
            taskrelated.addOnSuccessListener(getActivity(),queryDocumentSnapshots -> {
                myVideoRelated.clear();
                radapter.notifyDataSetChanged();
                if(queryDocumentSnapshots.size() > 0){
                    querydocs = queryDocumentSnapshots;
                    prepareRelatedDocs();
                }
                else {
                    fetchRelatedJSON(chanid);
                    progressBarr.setVisibility(View.GONE);
                    loadmorerelated.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(get->{
            });
        }catch (Exception e){
            Log.e("ccccccc",e.getMessage());
        }
    }
    private QuerySnapshot querydocs;
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
                    .get().addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    subscribed = "1";
                }
            });
            db.collection("favourites").whereEqualTo("favouriter", Constants.myId).whereEqualTo("favourited", channelid)
                    .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        favourited = "1";
                    }
                }
            });
            db.collection("likes").whereEqualTo("liker", Constants.myId).whereEqualTo("liked", channelid)
                    .limit(1).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        liked = "1";
                    }
                }
            });
            db.collection("subscriptions").whereEqualTo("subscribed", channelid).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        subscriptions = task.getResult().getDocuments().size() + "";
                    }
                }
            });
            String finalVideotext = videotext;
            db.collection("channels").document(channelid).get()
                    .addOnCompleteListener(getActivity(),new OnCompleteListener<DocumentSnapshot>() {
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
    private QuerySnapshot morequery;
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
                .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    subscribed = "1";
                }
            }
        });
        db.collection("favourites").whereEqualTo("favouriter", Constants.myId).whereEqualTo("favourited", onlineid)
                .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    favourited = "1";
                }
            }
        });
        db.collection("likes").whereEqualTo("liker", Constants.myId).whereEqualTo("liked", onlineid)
                .limit(1).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    liked = "1";
                }
            }
        });
        db.collection("subscriptions").whereEqualTo("subscribed", channelid).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    subscriptions = task.getResult().getDocuments().size() + "";
                }
            }
        });
        String finalVideotext = videotext;
        db.collection("channels").document(channelid).get().addOnCompleteListener(getActivity(),new OnCompleteListener<DocumentSnapshot>() {
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

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
            layoutParams.dimAmount = 0;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER_VERTICAL);
            getDialog().getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onResume() {
        if(null != exoPlayer){
            exoPlayer.setPlayWhenReady(true);
        }
        super.onResume();
    }
    @Override
    public void onStop() {
        if(null != exoPlayer){
            exoPlayer.setPlayWhenReady(false);
        }
        super.onStop();
    }
    @Override
    public void onPause() {
        if(null != exoPlayer){
            exoPlayer.setPlayWhenReady(false);
        }
        super.onPause();
    }
    @Override
    public void onDetach() {
        if(null != exoPlayer){
            exoPlayer.setPlayWhenReady(false);
        }
        super.onDetach();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != exoPlayer){
            exoPlayer.release();
        }
        db.terminate();
    }
private void attachRelated () {
        recyclerViewrelated.setAdapter(null);
        radapter = new RelatedAdapter(getContext(),myVideoRelated);
        recyclerViewrelated.setItemViewCacheSize(20);
        radapter.setOnRecyclerViewItemClickListener(new RelatedAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemRelatedClicked(String[] videoinfo) {
                YoYo.with(Techniques.SlideInUp).duration(300).repeat(0).playOn(videoView);
                videouri = videoinfo[0];
                if(videouri == lastUri){
                    Toast.makeText(getContext(), "Video Already Playing", Toast.LENGTH_SHORT).show();
                    return;
                }
                initializePlayerAds();
                TextView videolikes = rootView.findViewById(R.id.thislikes);
                TextView videodownloads = rootView.findViewById(R.id.thisdownloads);
                TextView videosid = rootView.findViewById(R.id.thisvideoids);
                TextView videourl = rootView.findViewById(R.id.tvvideourl);
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
        recyclerViewrelated.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext(), RecyclerView.VERTICAL, false));
    }
    //Ads Managements
    @SuppressLint("StaticFieldLeak")
    private void fetchJSON(final String videoId) {
        try {
            db.collection("videos").document(videoId)
                    .get()
                    .addOnCompleteListener(getActivity(), task -> {
                        Map<String, Object> post = task.getResult().getData();
                        final String postdate = post.get("time") + "";
                        final String poster = post.get("poster") + "";
                        final String channelid = post.get("channelId") + "";
                        chanid = channelid;
                        String videotext = post.get("videotext") + "";
                        final String likes = post.get("likes") + " Likes";
                        final String video = post.get("video") + "";
                        final String duration = post.get("duration") + "";
                        LAST_ITEM_ID = postdate;
                        final String mbs = post.get("videoSize") + "mbs";
                        final String downloads = post.get("downloads") + "";
                        String comments = post.get("views") + "";
                        int parsedview = Integer.parseInt(comments);
                        if (parsedview > 999 && parsedview < 1000000) {
                            comments = new BigDecimal(((double) parsedview / 1000) + "").setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "K views";
                        } else if (parsedview > 999999) {
                            comments = new BigDecimal(((double) parsedview / 1000000) + "").setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "M views";
                        } else {
                            comments = comments + " views";
                        }

                        //get channelinfo

                        db.collection("subscriptions").whereEqualTo("subscriber", Constants.myId).whereEqualTo("subscribed", videoId)
                                .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    subscribed = "1";
                                }
                            }
                        });
                        db.collection("favourites").whereEqualTo("favouriter", Constants.myId).whereEqualTo("favourited", videoId)
                                .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    favourited = "1";
                                }
                            }
                        });
                        db.collection("likes").whereEqualTo("liker", Constants.myId).whereEqualTo("liked", videoId)
                                .limit(1).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    liked = "1";
                                }
                            }
                        });
                        db.collection("subscriptions").whereEqualTo("subscribed", channelid).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    subscriptions = task.getResult().getDocuments().size() + "";
                                }
                            }
                        });
                        String finalVideotext = videotext;
                        db.collection("channels").document(channelid).get().addOnCompleteListener(getActivity(),new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().getData().isEmpty()) {
                                    Tvbottom.setVisibility(View.VISIBLE);
                                    Tvtop.setVisibility(View.VISIBLE);
                                    initmoretvtools();
                                    Map<String, Object> cpost = task.getResult().getData();
                                    String channelname = cpost.get("channelname") + "";
                                    String channelprofile = cpost.get("profile") + "";
                                    String totalvideos = cpost.get("videos") + "";
                                    String ownername = cpost.get("accountname") + "";
                                    String channelcover = cpost.get("cover") + "";
                                    //calculate no of days past
                                    TextView videolikes = rootView.findViewById(R.id.thislikes);
                                    TextView videodownloads = rootView.findViewById(R.id.thisdownloads);
                                    TextView videofav = rootView.findViewById(R.id.thisfav);
                                    TextView videosid = rootView.findViewById(R.id.thisvideoids);
                                    TextView videourl = rootView.findViewById(R.id.tvvideourl);

                                    subt.setText("SUBSCRIBERS " + subscriptions);
                                    tvchan.setText(channelname);
                                    videolikes.setText(likes);
                                    videodownloads.setText("Down..");
                                    videofav.setText("Favo..");
                                    videolikes.setTag(liked);
                                    videodownloads.setTag(favourited);
                                    videoviews.setTag(subscribed);
                                    videosid.setText(videoId);
                                    videourl.setText(video);

                                    videodesc.setText(finalVideotext);
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
                                    addViews(videoId);
                                    chanpad.setTag(channelid);
                                    tvchan.setTag(channelcover);
                                    subt.setTag(totalvideos);
                                    videodesc.setTag(subscriptions);
                                    if (subscribed.equals("1")) {
                                        isSubscribed();
                                    }
                                    Glide.with(chanpro.getContext()).load(channelprofile).circleCrop().into(chanpro);
                                    if (liked.equals("1")) {
                                        Glide.with(amen.getContext()).load(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp)).into(amen);
                                        amen.setEnabled(false);
                                    } else {
                                        Glide.with(amen.getContext()).load(getResources().getDrawable(R.drawable.ic_like)).into(amen);
                                        amen.setEnabled(true);
                                    }
                                    if (favourited.equals("1")) {
                                        Glide.with(favourite.getContext()).load(getResources().getDrawable(R.drawable.ic_star_active2)).into(favourite);
                                        favourite.setEnabled(false);
                                    } else {
                                        Glide.with(favourite.getContext()).load(getResources().getDrawable(R.drawable.ic_star)).into(favourite);
                                        favourite.setEnabled(true);
                                    }
                                }
                            }
                        });
                    });
        }catch(Exception d){
        }
    }
    private void loadRewardedVideoAd() {
        try {
            mRewardedVideoAd.loadAd(getResources().getString(R.string.RewardedAd_unit_id),
                    new AdRequest.Builder().build());
        }catch (Exception v){}
    }
    private void sendNotificationUpload(Context context, String title, String button) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "1")
                .setContentTitle(title)
                .setContentText(button)
                .setAutoCancel(true)
                .setContentIntent(null)
                .setContentInfo(title)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setChannelId("1");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Notification Channel is required for Android O and above
        notificationManager.notify(1, notificationBuilder.build());
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
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationBannerAdSizeWithWidth(getContext(),adWidth);
    }

    private void startDownload(){
        if(!videouri.equals("")){
            try {
                durl = videouri;
                File directory = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File pathx = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + downloadedname + ".mp4");
                if(pathx.exists()){
                    Toast.makeText(getContext(), "Video ALready Downloaded", Toast.LENGTH_LONG).show();
                    return;
                }
                File path = File.createTempFile(downloadedname, ".mp4", directory);
                videouri = videouri.replace("file:/", "/");
                try{
                    Files.copy(new File(videouri), new File(path.getAbsolutePath()));
                }catch (Exception g){
                    Toast.makeText(getContext(), " Error Saving Video For Offline ", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getContext(), "Saved To Device", Toast.LENGTH_LONG).show();
            }catch (Exception n){
            }
            return;
        }
        Intent intentd = new Intent(getContext(),DownloadBackAgent.class);
        intentd.putExtra("downloadname",downloadedname).putExtra("videotodownload",durl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().getApplicationContext().startForegroundService(intentd);
        }
        else {
            getActivity().getApplicationContext().startService(intentd);
        }
    }
    private boolean userRewarded = false;
    private RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener () {
        @Override
        public void onRewarded(RewardItem reward) {
            // Reward the user.
            REWARD_ON_COMPLETE=false;
            startDownload();
            userRewarded = true;
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
        }

        @Override
        public void onRewardedVideoAdClosed() {
            if(!userRewarded) {
                Snackbar d = Snackbar.make(getActivity().getWindow().getDecorView(), "Ad Terminated " +
                        "Download Video Failed ", BaseTransientBottomBar.LENGTH_INDEFINITE);
                d.setActionTextColor(Color.BLUE);
                d.setTextColor(Color.WHITE);
                d.setBackgroundTint(Color.BLACK).setBackgroundTintMode(PorterDuff.Mode.SRC_OVER);
                d.setAction(
                        "Dismiss", view -> d.setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                );
                d.show();
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
                userRewarded = false;
                REWARD_ON_COMPLETE = false;
            }
        }
        @Override
        public void onRewardedVideoAdOpened() {
            Toast.makeText(getContext(),"Watch Util Download Start",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRewardedVideoStarted() {
            REWARD_ON_COMPLETE=false;
            loadRewardedVideoAd();
        }

        @Override
        public void onRewardedVideoCompleted() {

        }
    };
}