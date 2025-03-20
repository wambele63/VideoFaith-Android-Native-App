package videos.religious.platform;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class ChannelVideos extends Fragment implements
        FragmentManager.OnBackStackChangedListener, RewardedVideoAdListener {

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewrelated;
    private boolean t,adsinserted;
    private ChannelVideos.OnFragmentInteractionListener mListener;
    private Box<ChannelVideo> videobox = ObjectBox.getBoxStore().boxFor(ChannelVideo.class);
    private List postList = videobox.getAll();
    private List MoreJson;
    private ArrayList<Related> relatedList = new ArrayList<>();
    private ArrayList<Related> myVideoRelated;
    private String ChannelName,downloadedname,durl;
    private IOnBackPressed iOnBackPressed;
    private FirebaseFirestore db;
    private ChannelVideosAdapter adapter;
    private Context mContext;
    private String videoid,videolike,thisvideoviews,chanprourl,thisvideodesc;
    private View inflate;
    public RelativeLayout Tv,chanpad,chanpadtv;
    private SwipeRefreshLayout swipeLayout;
    private static int ScrollState;
    private int LAST_AD_INDEX= 4;
    private EmojiAppCompatTextView videodesc;
    private RewardedVideoAd mRewardedVideoAd;
    private static int tvisibility = 0;
    private static boolean REWARD_ON_COMPLETE = false;
    private static boolean LAST_FETCH_ONPROGRESS = true;
    private static final int NUMBER_OF_ADS = 1;
    private String LAST_ITEM_ID = "0";
    private String LAST_RELATED_ITEM_ID;
    private FrameLayout adContainerView;
    private TextView tvchan,appallbar,videourl,videoviews,subt,loadmorerelated;
    private ProgressBar progressBarr;
    private AdView adView;
    private Button subscribe;
    private ImageView chanpro;
    private ImageButton amen,download,favourite,share;
    private RelativeLayout collapsed;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private List<UnifiedNativeAd> mMoreNativeAds = new ArrayList<>();

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    public static   ChannelVideos newInstance(String str, String str2) {
        return new ChannelVideos();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        db = FirebaseFirestore.getInstance();
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
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        inflate = layoutInflater.inflate(R.layout.fragment_channelvideos, viewGroup, false);
        swipeLayout = inflate.findViewById(R.id.swipehome);
        chanpad = inflate.findViewById(R.id.chanpadc);
        chanpadtv = inflate.findViewById(R.id.channelpad);
        subscribe = inflate.findViewById(R.id.subscribec);
        videoviews = inflate.findViewById(R.id.thisviews);
        videourl = inflate.findViewById(R.id.tvvideourl);
        chanpro = inflate.findViewById(R.id.channpro);
        subt = inflate.findViewById(R.id.sub);
        tvchan = inflate.findViewById(R.id.tvchannel);
        fetchJSON();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchJSON();
                mNativeAds.clear();
                loadNativeAds();
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                    }
                }, 1000);
            }
        });
        adContainerView = inflate.findViewById(R.id.ad_viewtv_container);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(getContext());
        adView.setAdUnitId(getString(R.string.NewsBannerAd_unit_id));
        adContainerView.addView(adView);
        loadBanner();
        swipeLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDiluted,
                R.color.colorPrimaryDarkVideo,
                R.color.colorPrimaryDark);
        collapsed = inflate.findViewById(R.id.collapsedbar);
        appallbar = inflate.findViewById(R.id.appallbar);
        ImageView chanprofile1 = inflate.findViewById(R.id.profile);
        ImageView chanprofile = inflate.findViewById(R.id.collapsedbarimg);
        ImageView chancover = inflate.findViewById(R.id.channcover);
        TextView textView = inflate.findViewById(R.id.collapsedbart);
        TextView textView1 = inflate.findViewById(R.id.subtitle);
        TextView textView2 = inflate.findViewById(R.id.subtitlevids);
        TextView textView3 = inflate.findViewById(R.id.subtitlesub);
        chanpad.setTag(VisitChannel.chid);
        textView3.setText(VisitChannel.subs);
        textView2.setText(VisitChannel.videos);
        Glide.with(getContext()).load(VisitChannel.ccover).into(chancover);
        Glide.with(getContext()).load(VisitChannel.cpro).circleCrop().into(chanprofile1);
        Glide.with(getContext()).load(VisitChannel.cpro).circleCrop().into(chanprofile);
        textView.setText(VisitChannel.cname);
        textView1.setText(VisitChannel.cname);
        if(VisitChannel.cissub.equals("1")){
            isSubscribed();
        }
        return inflate;
    }
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = inflate.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = inflate.findViewById(R.id.appbarc);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d("scrooleed", "scrollsve");
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                    if(collapsed.getVisibility() != View.VISIBLE) {
                        YoYo.with(Techniques.FadeInLeft).onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                collapsed.setVisibility(View.VISIBLE);
                                appallbar.setVisibility(View.INVISIBLE);
                            }
                        }).duration(600).playOn(collapsed);
                    }
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                    YoYo.with(Techniques.FadeOutDown).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            collapsed.setVisibility(View.GONE);
                            appallbar.setVisibility(View.VISIBLE);
                        }
                    }).duration(300).playOn(collapsed);
                }
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    private void DownloadVideo(String DownloadUrl, final String fileName) {

    }
    private String subscribed = "0";
    private String liked = "0";
    private String favourited = "0";
    private String subscriptions = "0";
    @SuppressLint("StaticFieldLeak")
    private void fetchJSON() {
        try {
            db.collection("videos").whereEqualTo("channelId", VisitChannel.chid)
                    .orderBy("time", Query.Direction.DESCENDING).limit(3).get()
                    .addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                videobox.removeAll();
                                LAST_FETCH_ONPROGRESS = true;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> post = document.getData();
                                    final ChannelVideo videopost = new ChannelVideo();
                                    final String postdate = post.get("time") + "";
                                    final String poster = post.get("poster") + "";
                                    final String channelid = post.get("channelId") + "";
                                    String videotext = post.get("videotext") + "";
                                    final String likes = post.get("likes") + " Amens";
                                    final String video = post.get("video") + "";
                                    final String duration = post.get("duration") + "";
                                    final String onlineid = document.getId();
                                    LAST_ITEM_ID = postdate;
                                    final String mbs = post.get("videoSize") + "mbs";
                                    final String downloads = post.get("downloads") + "";
                                    final String comments = post.get("views") + " Views";
                                    //get channelinfo
                                    db.collection("subscriptions").whereEqualTo("subscriber", Constants.myId).whereEqualTo("subscribed", channelid)
                                            .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                subscribed = "1";
                                            }
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
                                    db.collection("channels").document(channelid).get().addOnCompleteListener(getActivity(),new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful() && !task.getResult().getData().isEmpty()) {
                                                Map<String, Object> cpost = task.getResult().getData();
                                                String channelname = cpost.get("channelname") + "";
                                                String channelprofile = cpost.get("profile") + "";
                                                String totalvideos = cpost.get("videos") + "";
                                                String ownername = cpost.get("accountname") + "";
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
                                                videopost.setVideoDesc(videotext);
                                                videopost.setLikeda(liked);
                                                videopost.setSubscribed(subscribed);
                                                videopost.setFavoured(favourited);
                                                videopost.setNumOfBlesses(likes);
                                                videopost.setChannelownerid(channelid);
                                                videopost.setTotalVideos(totalvideos);
                                                videopost.setVideourl(video);
                                                videopost.setDownloads(downloads);
                                                videopost.setVideoId(onlineid);
                                                videopost.setPosterName(ownername);
                                                videopost.setNumOfComments(comments);
                                                videobox.put(videopost);
                                            }
                                            swipeLayout.setRefreshing(false);
                                            attachData();
                                            LAST_FETCH_ONPROGRESS = false;
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
        catch (Exception f){

        }
    }
        @SuppressLint("StaticFieldLeak")
        private void FetchMoreJson() {
        try {
            showp();
            db.collection("videos").whereEqualTo("channelId", VisitChannel.chid).whereLessThan("time", Long.parseLong(LAST_ITEM_ID)).orderBy("time", Query.Direction.DESCENDING)
                    .limit(4).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> post = document.getData();
                            final ChannelVideo videopost = new ChannelVideo();
                            final String postdate = post.get("time") + "";
                            final String poster = post.get("poster") + "";
                            final String channelid = post.get("channelId") + "";
                            final String likes = post.get("likes") + " Amens";
                            final String video = post.get("video") + "";
                            final String videotext = post.get("videotext") + "";
                            final String duration = post.get("duration") + "";
                            final String onlineid = document.getId();
                            LAST_ITEM_ID = postdate;
                            final String mbs = post.get("videoSize") + "mbs";
                            final String downloads = post.get("downloads") + "";
                            final String comments = post.get("views") + " Views";
                            //get channelinfo
                            db.collection("subscriptions").whereEqualTo("subscriber", Constants.myId).whereEqualTo("subscribed", channelid)
                                    .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        subscribed = "1";
                                    }
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
                                                videopost.setVideoDesc(videotext);
                                                videopost.setLikeda(liked);
                                                videopost.setSubscribed(subscribed);
                                                videopost.setFavoured(favourited);
                                                videopost.setNumOfBlesses(likes);
                                                videopost.setChannelownerid(channelid);
                                                videopost.setTotalVideos(totalvideos);
                                                videopost.setVideourl(video);
                                                videopost.setDownloads(downloads);
                                                videopost.setVideoId(onlineid);
                                                videopost.setPosterName(ownername);
                                                videopost.setNumOfComments(comments);
                                                videobox.put(videopost);
                                                postList.add(postList.size(), videopost);
                                                adapter.notifyItemInserted(postList.size());
                                                LAST_FETCH_ONPROGRESS = false;
                                                hidep();
                                            }
                                        }
                                    });
                        }
                        loadMoreNativeAds();
                    } else {
                        LAST_FETCH_ONPROGRESS = false;
                        Toast.makeText(mContext, "end", Toast.LENGTH_SHORT).show();
                        hidep();
                    }
                }
            });
        }catch (Exception d){

        }
    }
    @SuppressLint("StaticFieldLeak")
    private void favourites(String videoidtolike) {
    }
    @Override
    public void onViewCreated(@Nullable final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Tv = view.findViewById(R.id.Tv);
        loadNativeAds();
        loadmorerelated = view.findViewById(R.id.loadmorerelated);
        progressBarr = inflate.findViewById(R.id.progressmorerelated);
        recyclerView = view.findViewById(R.id.recycler_view);
        initCollapsingToolbar();
        recyclerViewrelated = view.findViewById(R.id.recycler_view_related);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chanid = chanpad.getTag().toString();
                subscribeNow(chanid);
            }
        });
    }
    private boolean subscribeNow(String idchan){
        return t;
    }
    private void isSubscribed(){
        subscribe.setBackgroundColor(getResources().getColor(R.color.colorLightAccent));
        subscribe.setTextColor(Color.GREEN);
        subscribe.setText("SUBSCRIBED");
        subscribe.setCompoundDrawablesWithIntrinsicBounds(null,null,getContext().getResources().getDrawable(R.drawable.ic_notifications_active_black_24dp),null);
        subscribe.setEnabled(false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        attachData();
    }
    private void FetchedRecyclerviewsdata(String response) {
        postList = prepareposts(response);
        recyclerView.setAdapter(null);
        attachData();
        swipeLayout.setRefreshing(false);
    }
    private void FetchedMoreRecyclerviewsdata(String response) {
        prepareMoreposts(response);
    }
    private void attachData(){
        adapter = new ChannelVideosAdapter(mContext, postList);
        adapter.setOnRecyclerViewItemClickListener(new ChannelVideosAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClicked(String[] videoinfo) {
                String videouri = videoinfo[0];
                videoid = videoinfo[1];
                videolike = videoinfo[2];
                thisvideoviews = videoinfo[3];
                thisvideodesc = videoinfo[4];
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = sfragmentdialog.newInstance(videouri, videoid, thisvideoviews, thisvideodesc);
                newFragment.show(ft, "dialog");
            }
            @Override
            public void onItemShareClicked(String videoshare) {
                share();
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
            @Override
            public void onFavouritesClicked(String id){
                favourites(id);
            }
            @Override
            public void onItemDownloadClicked(String videourl) {
                downloadedname = "Test Download";
                durl = videourl;
                if(mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                    REWARD_ON_COMPLETE = false;
                    return;
                }
                REWARD_ON_COMPLETE = true;
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        adapter.setHasStableIds(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(adapter));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            Activity act = getActivity();
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        ScrollState = 1;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        ScrollState = 1;
                        if (act instanceof MainActivity) {
                            ((MainActivity) act).hide();
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        ScrollState = 0;
                        if (!adsinserted && mNativeAds != null) {
                            insertAdsInMenuItems();
                        }
                        break;
                    default:
                        if (act instanceof MainActivity) {
                            ((MainActivity) act).show();
                        }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,int dy) {
                LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible + 1 == totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    if(!LAST_FETCH_ONPROGRESS) {
                        LAST_FETCH_ONPROGRESS=true;
                        FetchMoreJson();
                    }
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(false);
        recyclerView.setLayoutManager(mLayoutManager);
    }
    private List<ChannelVideo> prepareposts(String response) {
        try {
            videobox.removeAll();
            JSONArray dataArray = new JSONArray(response);
            for (int i = 0; i < dataArray.length(); i++) {
                ChannelVideo videopost = new ChannelVideo();
                JSONObject post = dataArray.getJSONObject(i);
                String channelname = post.getString("channelname");
                String channelprofile = post.getString("channelprofile");
                String postdate = post.getString("posterdate");
                String poster = post.getString("poster");
                String channelid = post.getString("channelid");
                String videotext = post.getString("videodesc");
                String likes = post.getString("totalblesses") + " Amens";
                String channelownerid = post.getString("channelownerid");
                String totalvideos = post.getString("totalvideos");
                String video = post.getString("video");
                String duration = post.getString("duration");
                String onlineid = post.getString("videoid");
                String mbs = "("+post.getString("videosize")+")";
                String ownername = post.getString("postername");
                String subscriptions = post.getString("clikes");
                String favoured = post.getString("favoured");
                String subscribed = post.getString("subscribed");
                String liked = post.getString("liked");
                String channelcover = post.getString("channelcover");
                String amens = post.getString("numofamens") + " Amens";
                String comments = post.getString("numofcomments") + " Views";
                videopost.setChannelName(channelname);
                videopost.setVideosize(mbs);
                videopost.setChannelProfile(channelprofile);
                videopost.setPostDate(postdate);
                videopost.setPoster(poster);
                videopost.setSubscriptions(subscriptions);
                videopost.setVDuration(duration);
                videopost.setChannelId(channelid);
                videopost.setVideoDesc(videotext);
                videopost.setLikeda(liked);
                videopost.setSubscribed(subscribed);
                videopost.setFavoured(favoured);
                videopost.setNumOfBlesses(likes);
                videopost.setChannelownerid(channelownerid);
                videopost.setTotalVideos(totalvideos);
                videopost.setVideourl(video);
                videopost.setVideoId(onlineid);
                LAST_ITEM_ID = onlineid;
                videopost.setPosterName(ownername);
                videopost.setNumOfAmens(amens);
                videopost.setNumOfComments(comments);
                videobox.put(videopost);
            }
        } catch (Exception jsonerror) {
            jsonerror.printStackTrace();
        }
        return videobox.getAll();
    }
    private List<ChannelVideo> prepareMoreposts(String response) {
        List<ChannelVideo> MoreJson2 = new ArrayList<>();
        try {
            JSONArray dataArray = new JSONArray(response);
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    ChannelVideo videopost = new ChannelVideo();
                    JSONObject post = dataArray.getJSONObject(i);
                    String channelname = post.getString("channelname");
                    String channelprofile = post.getString("channelprofile");
                    String postdate = post.getString("posterdate");
                    String poster = post.getString("poster");
                    String channelid = post.getString("channelid");
                    String videotext = post.getString("videodesc");
                    String likes = post.getString("totalblesses") + " Amens";
                    String channelownerid = post.getString("channelownerid");
                    String totalvideos = post.getString("totalvideos");
                    String video = post.getString("video");
                    String duration = post.getString("duration");
                    String onlineid = post.getString("videoid");
                    String mbs = "("+post.getString("videosize")+")";
                    String ownername = post.getString("postername");
                    String subscriptions = post.getString("clikes");
                    String favoured = post.getString("favoured");
                    String subscribed = post.getString("subscribed");
                    String liked = post.getString("liked");
                    String channelcover = post.getString("channelcover");
                    String amens = post.getString("numofamens") + " Amens";
                    String comments = post.getString("numofcomments") + " Views";
                    videopost.setChannelName(channelname);
                    videopost.setVideosize(mbs);
                    videopost.setChannelProfile(channelprofile);
                    videopost.setPostDate(postdate);
                    videopost.setPoster(poster);
                    videopost.setSubscriptions(subscriptions);
                    videopost.setVDuration(duration);
                    videopost.setChannelId(channelid);
                    videopost.setVideoDesc(videotext);
                    videopost.setLikeda(liked);
                    videopost.setSubscribed(subscribed);
                    videopost.setFavoured(favoured);
                    videopost.setNumOfBlesses(likes);
                    videopost.setChannelownerid(channelownerid);
                    videopost.setTotalVideos(totalvideos);
                    videopost.setVideourl(video);
                    videopost.setVideoId(onlineid);
                    LAST_ITEM_ID = onlineid;
                    videopost.setPosterName(ownername);
                    videopost.setNumOfAmens(amens);
                    videopost.setNumOfComments(comments);
                    postList.add(postList.size()-1, videopost);
                    adapter.notifyItemInserted(postList.size()-1);
                } catch (JSONException jsonex) {
                }
            }
        } catch (JSONException jsonerror) {
            jsonerror.printStackTrace();
        }
        return MoreJson2;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChannelVideos.OnFragmentInteractionListener) {
            this.mListener = (ChannelVideos.OnFragmentInteractionListener) context;
            mContext = context;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.toString());
        sb.append(" must implement OnFragmentInteractionListener");
        throw new RuntimeException(sb.toString());
    }
    public void onDestroy(){
        super.onDestroy();
        recyclerView.setAdapter(null);
        recyclerViewrelated.setAdapter(null);
    }
    public void onDetach() {
        super.onDetach();
        recyclerView.setAdapter(null);
        this.mListener = null;
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
        progressBar.setVisibility(View.GONE);
    }
    private void insertAdsInMenuItems() {
            try {
                int index = 0;
                for (UnifiedNativeAd ad : mNativeAds) {
                    postList.add(index, ad);
                    adapter.notifyItemInserted(index);
                }
                adsinserted = true;
            }catch (Exception exp){
            }
    }
    private void loadNativeAds() {
                // code goes here.
                AdLoader.Builder builder = new AdLoader.Builder(mContext, getString(R.string.ad_unit_id));
                adLoader = builder.forUnifiedNativeAd(
                        new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                // A native ad loaded successfully, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.
                                mNativeAds.add(unifiedNativeAd);
                                if(!adLoader.isLoading()) {
                                    showp();
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
                adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }
    private void loadMoreNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(mContext, getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                unifiedNativeAd -> {
                    // A native ad loaded successfully, check if the ad loader has finished loading
                    // and if so, insert the ads into the list.
                    try {
                        postList.add(LAST_AD_INDEX, unifiedNativeAd);
                        adapter.notifyItemInserted(LAST_AD_INDEX);
                        LAST_AD_INDEX = LAST_AD_INDEX+3;
                    }catch (Exception exp){
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
                            loadMoreNativeAds();
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
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getResources().getString(R.string.RewardedAd_unit_id),
                new AdRequest.Builder().build());
    }
    @Override
    public void onRewarded(RewardItem reward) {
        // Reward the user.
        sendNotificationUpload(getContext(),"Download Started","Close");
        DownloadVideo(durl,downloadedname);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Snackbar.make(getActivity().getWindow().getDecorView(),"Watch Until Download Start",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if(REWARD_ON_COMPLETE){
            mRewardedVideoAd.show();
            REWARD_ON_COMPLETE = false;
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }
    @Override
    public void onRewardedVideoStarted() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}