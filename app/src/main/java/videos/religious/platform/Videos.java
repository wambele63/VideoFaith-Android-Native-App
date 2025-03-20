package videos.religious.platform;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.circularreveal.CircularRevealRelativeLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.objectbox.Box;

public class Videos extends Fragment implements
        FragmentManager.OnBackStackChangedListener {
    private RecyclerView recyclerView;
    private boolean t;
    private Videos.OnFragmentInteractionListener mListener;
    private Box<Post> videobox = ObjectBox.getBoxStore().boxFor(Post.class);
    private List postList = new ArrayList();
    private List MoreJson;
    private ArrayList<Related> myVideoRelated = new ArrayList<>();
    private IOnBackPressed iOnBackPressed;
    private PostsAdapter adapter;
    private Context mContext;
    private String downloadedname,durl;
    private View inflate;
    public RelativeLayout Tv,chanpad;
    private LinearLayout nv;
    private SwipeRefreshLayout swipeLayout;
    private static int ScrollState;
    private int LAST_AD_INDEX=0;
    private RewardedAd rewardedAd;
    private RewardedAd rewardedAdForDownload;
    private static int tvisibility = 0;
    private boolean REWARD_ON_COMPLETE=false,LAST_FETCH_ONPROGRESS = true;
    private static final int NUMBER_OF_ADS = 1;
    private String LAST_ITEM_ID = "0";
    private int index = 0;
    private boolean adsinserted;
    private FirebaseAuth fAuth;
    private static boolean showed= false;
    private ImageButton topscroll;
    private String lastNewPost="0";
    private CircularRevealRelativeLayout rewardopt;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private List<UnifiedNativeAd> mMoreNativeAds = new ArrayList<>();

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    public static Videos newInstance(String str, String str2) {
        return new Videos();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this.getActivity(), initializationStatus -> {
        });
        toast= Toast.makeText(getContext(),"",Toast.LENGTH_SHORT);
        db = FirebaseFirestore.getInstance();
        rewardedAdForDownload = createAndLoadRewardedAd();
    }
    private RewardedAd createAndLoadRewardedAd() {
        rewardedAd = new RewardedAd(getActivity(),
                getResources().getString(R.string.RewardedAd_unit_id));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                if(REWARD_ON_COMPLETE) {
                    if (!instanceDownload) {
                    } else {
                        startDownloadad();
                    }
                }
            }
            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                rewardedAd = createAndLoadRewardedAd();
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }
    private boolean instanceDownload = false;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        inflate = layoutInflater.inflate(R.layout.activity_videos, viewGroup, false);
        swipeLayout = inflate.findViewById(R.id.swipehome);
        recyclerView = inflate.findViewById(R.id.recycler_view);
        swipeLayout.setOnRefreshListener(() -> {
            fetchJSON();
        });
        attachData();
        //TextView change = inflate.findViewById(R.id.changereligion);
        //change.setOnClickListener(h->{
         //   rewardopt = getActivity().findViewById(R.id.rewardopt);
         //   YoYo.with(Techniques.SlideInLeft).duration(600).onStart(j->{
         //       rewardopt.setVisibility(View.VISIBLE);
         //   }).playOn(rewardopt);
       // });
        swipeLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDiluted,
                R.color.colorPrimaryDarkVideo,
                R.color.colorPrimaryDark);
            fetchJSON();
        return inflate;
    }
    @SuppressLint("StaticFieldLeak")
    private void DownloadVideo(String DownloadUrl, final String fileName) {
    }
    private boolean userRewarded=false;
    private String subscribed = "0";
    private String liked = "0";
    private String favourited = "0";
    private String subscriptions = "0";
    private boolean offlineFlashed = false;
    private String channelname,channelprofile,totalvideos,ownername,channelcover;
    private void resetItemsAndIndeces(){
        LAST_AD_INDEX = 0;
        firstpost = 0;
        lastpost = 5;
        indoc = 0;
        postList.clear();
    }
    private void fetchJSON() {
        try {
            loadNativeAds();
            LAST_FETCH_ONPROGRESS = true;
            swipeLayout.setRefreshing(true);
            db.collection("videos")
                    .whereEqualTo("religion", Constants.myReligion)
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(5)
                    .get().addOnCompleteListener(getActivity(),task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    videobox.removeAll();
                    queryDocumentSnapshotss = task.getResult();
                    loopdocuments();
                } else {
                    LAST_FETCH_ONPROGRESS = false;
                        swipeLayout.setRefreshing(false);
                }
            }).addOnFailureListener(f->{
            });
        } catch (Exception g){

        }
    }
    private QuerySnapshot queryDocumentSnapshotss;
    private void loopdocuments(){
        if(indoc <= queryDocumentSnapshotss.size()-1) {
            loopthroughdocs(queryDocumentSnapshotss.getDocuments().get(indoc));
            return;
        }
        LAST_FETCH_ONPROGRESS=false;
        swipeLayout.setRefreshing(false);
        attachData();
    }
    private int indoc= 0;
    private void loopthroughdocs(@NotNull DocumentSnapshot document){
            Map<String,Object> post = document.getData();
            final Post videopost = new Post();
            final String postdate = post.get("time")+ "";
            final String poster = post.get("poster")+ "";
            final String channelid = post.get("channelId")+ "";
            String videotext = post.get("videotext")+ "";
            final String likes = post.get("likes") + " Likes";
            final String video = post.get("video")+ "";
            final String duration = post.get("duration")+ "";
            final String onlineid = document.getId();
            LAST_ITEM_ID = postdate;
            final String mbs = post.get("videoSize")+ "mbs";
            final String downloads = post.get("downloads")+ "";
            String comments = post.get("views")+"";
            int parsedview = Integer.parseInt(comments);
            if(parsedview > 999 && parsedview < 1000000){
                comments = new BigDecimal(((double) parsedview/1000)+"").setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "K views";
            }
            else if(parsedview > 999999) {
                comments = new BigDecimal(((double) parsedview/1000000)+"").setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "M views";
            }
            else {
                comments = comments + " views";
            }
            final String commentsvalue = comments;

            //get channelinfo

            db.collection("subscriptions").whereEqualTo("subscriber",Constants.myId).whereEqualTo("subscribed",onlineid)
                    .get().addOnCompleteListener(getActivity(),task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            subscribed = "1";
                            videopost.setSubscribed(subscribed);
                        }
                    });
            db.collection("favourites").whereEqualTo("favouriter",Constants.myId).whereEqualTo("favourited",onlineid)
                    .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        favourited = "1";
                        videopost.setFavoured(favourited);
                    }
                }
            });
            db.collection("likes").whereEqualTo("liker",Constants.myId).whereEqualTo("liked",onlineid)
                    .limit(1).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        liked = "1";
                        videopost.setLikeda(liked);
                    }
                }
            });
            db.collection("subscriptions").whereEqualTo("subscribed",channelid).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        subscriptions = task.getResult().getDocuments().size()+"";
                    }
                }
            });
        String finalVideotext = videotext;
        db.collection("channels").document(channelid).get()
                    .addOnSuccessListener(getActivity(), documentSnapshot -> {
                        Map<String, Object> cpost = documentSnapshot.getData();
                        channelname = cpost.get("channelname") + "";
                        channelprofile = cpost.get("profile") + "";
                        totalvideos = cpost.get("videos") + "";
                        ownername = cpost.get("accountname") + "";
                        channelcover = cpost.get("cover") + "";
                        //calculate no of days past
                        String daysagodate = "Hot";
                        long daysago = (System.currentTimeMillis()/(24*60*60*1000))-Long.parseLong(postdate)/(24*60*60*1000);
                        if(daysago > 1 && daysago <= 7 ){
                            daysagodate = daysago+" days ago";
                        }
                        if(daysago > 7 && daysago <= 30 ){
                            daysagodate = (int)daysago/7 + " weeks ago";
                            if((int)daysago/7 < 2){
                                daysagodate = "Last week";
                            }
                        }
                        if(daysago > 30 && daysago <= 360 ){
                            daysagodate = daysago+" months ago";
                            if((int)daysago/30 < 2){
                                daysagodate = "Last month";
                            }
                        }
                        if(daysago > 360 ){
                            daysagodate = daysago/360+" years ago";
                            if((int)daysago/360 < 2){
                                daysagodate = "Last year";
                            }
                        }
                        videopost.setChannelName(channelname);
                        videopost.setVideosize(mbs);
                        videopost.setChannelProfile(channelprofile);
                        videopost.setPostDate(daysagodate);
                        videopost.setPoster(poster);
                        videopost.setFavoured(favourited);
                        videopost.setSubscriptions(subscriptions);
                        videopost.setVDuration(duration);
                        videopost.setChannelId(channelid);
                        videopost.setVideoDesc(finalVideotext);
                        videopost.setNumOfBlesses(likes);
                        videopost.setChannelownerid(channelid);
                        videopost.setTotalVideos(totalvideos);
                        videopost.setVideourl(video);
                        videopost.setSubscribed(subscribed);
                        videopost.setLikeda(liked);
                        videopost.setDownloads(downloads);
                        videopost.setVideoId(onlineid);
                        videopost.setChannelCover(channelcover);
                        videopost.setPosterName(ownername);
                        videopost.setNumOfComments(commentsvalue);
                        videobox.put(videopost);
                        indoc++;
                        if(postList.size() < 6) {
                            postList.add(0,videopost);
                            adapter.notifyItemInserted(0);
                        }
                        loopdocuments();
                    });
    }
    private int moreint=1;
    @SuppressLint("StaticFieldLeak")
    private void FetchMoreJson() {
        try {
            LAST_FETCH_ONPROGRESS = true;
            db.collection("videos")
                    .whereEqualTo("religion", Constants.myReligion)
                    .whereLessThan("time", Long.parseLong(LAST_ITEM_ID))
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(15).get().addOnCompleteListener(getActivity(),task -> {
                if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {
                    hidep();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> post = document.getData();
                        final Post videopost = new Post();
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
                        String comments = post.get("views") + "";
                        int parsedview = Integer.parseInt(comments);
                        if (parsedview > 999 && parsedview < 1000000) {
                            comments = new BigDecimal(((double) parsedview / 1000) + "").setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "K views";
                        } else if (parsedview > 999999) {
                            comments = new BigDecimal(((double) parsedview / 1000000) + "").setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "M views";
                        } else {
                            comments = comments + " views";
                        }
                        final String commentsvalue = comments;
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
                        db.collection("channels").document(channelid).get()
                                .addOnCompleteListener(getActivity(),new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                        if (task2.isSuccessful() && !Objects.requireNonNull(task2.getResult()).getData().isEmpty()) {
                                            Map<String, Object> cpost = task2.getResult().getData();
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
                                            videopost.setNumOfComments(commentsvalue);
                                            videobox.put(videopost);
                                            if (moreint >= task.getResult().size()) {
                                                LAST_FETCH_ONPROGRESS = false;
                                            }
                                            moreint++;
                                        }
                                    }
                                });
                    }
                } else {
                        hidep();
                        LAST_FETCH_ONPROGRESS = false;
                        toast.setText("end");
                        toast.show();
                }
            });
        }catch (Exception k) {

        }
    }
    @Override
    public void onViewCreated(@Nullable final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nv = getActivity().findViewById(R.id.nav_view_home);
        setFloatingActionButton();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private int firstpost=0;
    private int lastpost=5;
    private Toast toast=null;
    private void attachMoreData(){
        try {
            showp();
            List postList2 = videobox.getAll().subList(firstpost, lastpost);
            int pos = postList.size();
            Collections.shuffle(postList2);
            postList.addAll(pos,postList2);
            adapter.notifyItemRangeInserted(pos,postList2.size());
            firstpost = lastpost;
            lastpost = lastpost + 5;
            insertMoreAdsInMenuItems();
            if(!LAST_FETCH_ONPROGRESS) {
                FetchMoreJson();
            }
        }
        catch (Exception outbond){
            hidep();
            if(!LAST_FETCH_ONPROGRESS){
                Toast.makeText(mContext, "Fetching More Videos...", Toast.LENGTH_LONG).show();
                FetchMoreJson();
            }
        }
    }
    private void attachData(){
        try {
                resetItemsAndIndeces();
            postList = videobox.getAll().subList(firstpost, lastpost);
            firstpost = lastpost;
            lastpost = lastpost + 5;
        } catch (Exception outbond){
            Log.d("outbond","outofbound exception");
        }
        recyclerView.setAdapter(null);
        Collections.shuffle(postList);
            adapter = new PostsAdapter(mContext, postList);
            adapter.setOnRecyclerViewItemClickListener(new PostsAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClicked(String[] videoinfo) {
                    String s = "f";
                    ((MainActivity) getActivity()).prepareVideoView(videoinfo, videoinfo[0]);
                }
                @Override
                public void onItemShareClicked(String videoshare) {
                    share();
                }

                @Override
                public void onFavouritesClicked(String id) {
                    addFavourites(id);
                }

                @Override
                public void onItemDownloadClicked(String videourl, String downloadname) {
                    downloadedname = downloadname;
                    durl = videourl;
                    startDownloadad();
                }
            });
        insertAdsInMenuItems();
        loadMoreNativeAds();
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                Activity act = getActivity();
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                    boolean endHasBeenReached = lastVisible + 1 == totalItemCount;
                    if (totalItemCount > 0 && endHasBeenReached) {
                            attachMoreData();
                    }
                    if (dy < dx) {
                        if (topscroll.getVisibility() != View.VISIBLE) {
                            topscroll.setVisibility(View.VISIBLE);
                            return;
                        }
                        YoYo.with(Techniques.RubberBand).duration(600).playOn(topscroll);
                    } else {
                        if (topscroll.getVisibility() != View.INVISIBLE) {
                            topscroll.setVisibility(View.INVISIBLE);
                            return;
                        }
                        YoYo.with(Techniques.RubberBand).duration(600).playOn(topscroll);
                    }
                }
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
            ((LinearLayoutManager) mLayoutManager).setStackFromEnd(false);
            recyclerView.setLayoutManager(mLayoutManager);
    }
    private void startDownloadad(){
        if (!rewardedAdForDownload.isLoaded()) {
            rewardedAdForDownload = createAndLoadRewardedAd();
        } else {
            Activity activityContext = getActivity();
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                    Toast.makeText(activityContext, "Do Not Close The Video", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                    if(!userRewarded) {
                        Snackbar d = Snackbar.make(getActivity().getWindow().getDecorView(), "Termi " +
                                "Download Video Failed", BaseTransientBottomBar.LENGTH_INDEFINITE);
                        d.setActionTextColor(Color.RED);
                        d.setTextColor(Color.BLACK);
                        d.setBackgroundTint(Color.WHITE).setBackgroundTintMode(PorterDuff.Mode.SRC_OVER);
                        d.setAction(
                                "Dismiss", view -> d.setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                        );
                        d.show();
                    }
                    rewardedAdForDownload = createAndLoadRewardedAd();
                }
                @Override
                public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem reward) {
                    startDownload();
                    userRewarded = true;
                    // User earned reward.
                }
                @Override
                public void onRewardedAdFailedToShow(int errorCode) {
                    // Ad failed to display.
                    rewardedAdForDownload = createAndLoadRewardedAd();
                }
            };
            rewardedAdForDownload.show(activityContext, adCallback);
        }
        REWARD_ON_COMPLETE = true;
    }
    private void startDownload(){
        if(!downloadedname.equals("") && !durl.equals("")) {
            Intent intentd = new Intent(getActivity(), DownloadBackAgent.class);
            intentd.putExtra("downloadname", downloadedname).putExtra("videotodownload", durl);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContext().getApplicationContext().startForegroundService(intentd);
            } else {
                getContext().getApplicationContext().startService(intentd);
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    private void addFavourites(final String videoidtolike) {
        db.collection("favourites").whereEqualTo("favouriter",Constants.myId).whereEqualTo("favourited",videoidtolike)
                .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().isEmpty() || task.getResult().equals(null)) {
                    Map<String, Object> favour = new HashMap<>();
                    favour.put("favouriter",Constants.myId);
                    favour.put("favourited",videoidtolike);
                    favour.put("timestamp",System.currentTimeMillis());
                    db.collection("favourites").add(favour);
                    db.collection("videos").document(videoidtolike).update("favourites", FieldValue.increment(1));
                    Toast.makeText(mContext, "Added To Your Favourites", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext, "No Need, Already In Your Favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setFloatingActionButton() {
        topscroll = inflate.findViewById(R.id.scroll);
        topscroll.setOnClickListener(v -> {
            recyclerView.smoothScrollToPosition(0);
            topscroll.setVisibility(View.GONE);
        });
    }
    public void share() {
        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File pathx = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/" + "promovideo" + ".mp4");
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("video/mp4");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(pathx.getAbsolutePath())));
        share.putExtra(Intent.EXTRA_TEXT, "Install The Largest Religious Free Video Platform For Your Spiritual Health" + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        startActivity(Intent.createChooser(share, "SHARE VIDEO"));
    }catch(Exception e) {
    }
    }
    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Videos.OnFragmentInteractionListener) {
            this.mListener = (Videos.OnFragmentInteractionListener) context;
            mContext = context;
            iOnBackPressed = (IOnBackPressed) context;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.toString());
        sb.append(" must implement OnFragmentInteractionListener");
        throw new RuntimeException(sb.toString());
    }
    @Override
public void onDestroy(){
        super.onDestroy();
        db.terminate();
    }
@Override
    public void onDetach() {
        super.onDetach();
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
    public void onBackStackChanged() {
    }
    private void showp(){
            final ProgressBar progressBar = inflate.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
    }
    private void hidep(){
            final ProgressBar progressBar = inflate.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.INVISIBLE);
    }
    private void insertAdsInMenuItems() {
        if(postList.size() < 5) {
            return;
        }
                for (UnifiedNativeAd ad : mNativeAds) {
                    postList.add(0, ad);
                    adapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                    LAST_AD_INDEX = LAST_AD_INDEX + 6;
                    mNativeAds.clear();
                }
    }
    private void insertMoreAdsInMenuItems() {
        if(postList.size()-1 >= LAST_AD_INDEX && LAST_AD_INDEX > 0 && mMoreNativeAds.size() > 0) {
            for (UnifiedNativeAd ad : mMoreNativeAds) {
                postList.add(LAST_AD_INDEX, ad);
                adapter.notifyItemInserted(LAST_AD_INDEX);
                LAST_AD_INDEX = LAST_AD_INDEX + 6;
            }
        mMoreNativeAds.clear();
            loadMoreNativeAds();
        }
    }
    private void loadNativeAds(){
        AdLoader.Builder builder = new AdLoader.Builder(mContext, getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                unifiedNativeAd -> {
                    // A native ad loaded successfully, check if the ad loader has finished loading
                    // and if so, insert the ads into the list.
                    mNativeAds.clear();
                    mNativeAds.add(unifiedNativeAd);
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another vv."+errorCode);
                        if(!adLoader.isLoading()) {
                        }
                    }
                }).build();
        // Load the Native Express ad
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
            }
    private void loadMoreNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(mContext, getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                unifiedNativeAd -> {
                    // A native ad loaded successfully, check if the ad loader has finished loading
                    // and if so, insert the ads into the list.
                    mMoreNativeAds.add(unifiedNativeAd);
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another vv."+errorCode);
                        if(!adLoader.isLoading()) {

                        }
                    }
                }).build();
        // Load the Native Express ad.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }
    public boolean AllowBack(){
        if (tvisibility == 1) {
            tvisibility = 0;
            return false;
        }
        return true;
    }
    private void HideAnimation(final View view){
        view.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        YoYo.with(Techniques.SlideOutUp).duration(300).playOn(view);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }
    private void ShowAnimation(final View view){
        view.animate()
                .alpha(1.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        YoYo.with(Techniques.SlideInDown).duration(300).playOn(view);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }
    private void ShowAnimationTv(final View view){
        view.animate()
                .alpha(1.0f)
                .setDuration(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        YoYo.with(Techniques.FlipInY).duration(300).playOn(view);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        YoYo.with(Techniques.RubberBand).duration(600).playOn(view);
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }
    private void HideAnimationTv(final View view){
        view.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        YoYo.with(Techniques.FlipOutY).duration(300).playOn(view);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }
}