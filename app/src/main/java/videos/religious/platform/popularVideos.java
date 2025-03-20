package videos.religious.platform;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;

public class popularVideos extends Fragment implements
        FragmentManager.OnBackStackChangedListener {
    private RecyclerView recyclerView;
    private boolean t;
    private popularVideos.OnFragmentInteractionListener mListener;
    private Box<Popular> popularbox = ObjectBox.getBoxStore().boxFor(Popular.class);
    private List postList=new ArrayList();
    private List MoreJson;
    private ArrayList<Related> myVideoRelated = new ArrayList<>();
    private IOnBackPressed iOnBackPressed;
    private popularAdapter adapter;
    private Context mContext;
    private String downloadedname,durl;
    private View inflate;
    public RelativeLayout Tv,chanpad;
    private LinearLayout nv;
    private SwipeRefreshLayout swipeLayout;
    private static int ScrollState;
    private int LAST_AD_INDEX=5;
    private RewardedAd rewardedAd;
    private RewardedAd rewardedAdForDownload;
    private static int tvisibility = 0;
    private boolean REWARD_ON_COMPLETE,LAST_FETCH_ONPROGRESS = true;
    private static final int NUMBER_OF_ADS = 1;
    private String LAST_ITEM_ID = "0";
    private boolean adsinserted;
    private FirebaseAuth fAuth;
    private static boolean showed= false;
    private String videoid,videolike,thisvideoviews,thisvideodesc;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private List<UnifiedNativeAd> mMoreNativeAds = new ArrayList<>();

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    public static popularVideos newInstance(String str, String str2) {
        return new popularVideos();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this.getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        db = FirebaseFirestore.getInstance();
        fetchJSON();
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        inflate = layoutInflater.inflate(R.layout.fragment_popular, viewGroup, false);
        swipeLayout = inflate.findViewById(R.id.swipepopular);
        recyclerView = inflate.findViewById(R.id.recycler_view);
        attachData();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LAST_ITEM_ID="0";
                fetchJSON();
                LAST_AD_INDEX=5;
            }
        });
        swipeLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDiluted,
                R.color.colorPrimaryDarkVideo,
                R.color.colorPrimaryDark);
        return inflate;
    }
    @SuppressLint("StaticFieldLeak")
    private void DownloadVideo(String DownloadUrl, final String fileName) {
    }
    private String subscribed = "0";
    private String liked = "0";
    private String favourited = "0";
    private String subscriptions = "0";
    private void fetchJSON() {
        try {
            db.collection("videos")
                    .whereEqualTo("religion",Constants.myReligion)
                    .orderBy("views", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        popularbox.removeAll();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> post = document.getData();
                            final Popular videopost = new Popular();
                            final String postdate = post.get("time") + "";
                            final String poster = post.get("poster") + "";
                            final String channelid = post.get("channelId") + "";
                            String videotext = post.get("videotext") + "";
                            final String likes = post.get("likes") + " Likes";
                            final String video = post.get("video") + "";
                            final String duration = post.get("duration") + "";
                            final String onlineid = document.getId();
                            LAST_ITEM_ID = post.get("views") + "";
                            final String mbs = post.get("videosize") + "mbs";
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
                                        popularbox.put(videopost);
                                    }
                                    LAST_FETCH_ONPROGRESS = false;
                                    swipeLayout.setRefreshing(false);
                                    attachData();
                                }
                            });
                        }
                    }
                    LAST_FETCH_ONPROGRESS = false;
                }
            });
        }catch (Exception f){

        }
    }
    @SuppressLint("StaticFieldLeak")
    private void FetchMoreJson() {
        try {
            db.collection("videos")
                    .whereEqualTo("religion",Constants.myReligion)
                    .whereLessThan("views", Long.parseLong(LAST_ITEM_ID)).orderBy("views", Query.Direction.DESCENDING)
                    .limit(5).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> post = document.getData();
                            final Popular videopost = new Popular();
                            final String postdate = post.get("time") + "";
                            final String poster = post.get("poster") + "";
                            final String channelid = post.get("channelId") + "";
                            String videotext = post.get("videotext") + "";
                            final String likes = post.get("likes") + " Likes";
                            final String video = post.get("video") + "";
                            final String duration = post.get("duration") + "";
                            final String onlineid = document.getId();
                            LAST_ITEM_ID = post.get("views") + "";
                            final String mbs = post.get("videosize") + "mbs";
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
                                                popularbox.put(videopost);
                                                postList.add(videopost);
                                                adapter.notifyItemInserted(postList.size() - 1);
                                                LAST_FETCH_ONPROGRESS = false;
                                            }
                                        }
                                    });
                        }
                    } else {
                        LAST_FETCH_ONPROGRESS = false;
                        Toast.makeText(mContext, "end", Toast.LENGTH_SHORT).show();
                    }
                    LAST_FETCH_ONPROGRESS = false;
                }
            });
        }catch(Exception g){

        }
    }
    @Override
    public void onViewCreated(@Nullable final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nv = getActivity().findViewById(R.id.nav_view_home);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private void attachData(){
        postList = popularbox.getAll();
        Collections.shuffle(postList);
        adapter = new popularAdapter(mContext, postList);
        adapter.setOnRecyclerViewItemClickListener(new popularAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClicked(String[] videoinfo) {
                String videouri = videoinfo[0];
                videoid = videoinfo[1];
                thisvideoviews = videoinfo[2];
                thisvideodesc = videoinfo[3];
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = sfragmentdialog.newInstance(videouri, videoid, thisvideoviews, thisvideodesc);
                newFragment.show(ft, "dialog");
            }
            @Override
            public void onItemShareClicked(String popularVideoshare) {
                share();
            }
            @Override
            public void onFavouritesClicked(String id){
                addFavourites(id);
            }
        });
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
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        ScrollState=1;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        ScrollState=1;
                        if (act instanceof MainActivity) {
                            ((MainActivity) act).hide();
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        ScrollState=0;
                        if(!adsinserted && mNativeAds != null) {
                            insertAdsInMenuItems();
                        }
                        if (act instanceof MainActivity) {
                            ((MainActivity) act).show();
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
                if(dy < dx){
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

// Set the SnapType
        layoutManager.canScrollVertically();

//Change the durations for snap animation.
        layoutManager.setSmoothScrollbarEnabled(true);

//Change the interpolator for the snaping animation.

//Change the padding for the end parts of the view.

// Attach layout manager to the RecyclerView:
        recyclerView.setLayoutManager(layoutManager);
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
        } catch (Exception e) {
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
        if (context instanceof popularVideos.OnFragmentInteractionListener) {
            this.mListener = (popularVideos.OnFragmentInteractionListener) context;
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
        recyclerView.setAdapter(null);
    }
    @Override
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
    private void insertAdsInMenuItems() {
        if (postList.size() >= 5) {
            int index = LAST_AD_INDEX;
            for (UnifiedNativeAd ad : mNativeAds) {
                postList.add(index, ad);
                adapter.notifyItemInserted(index);
            }
            adsinserted = true;
        }
    }
    private void loadNativeAds(){
        AdLoader.Builder builder = new AdLoader.Builder(mContext, getString(R.string.ad_unit_id));
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
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }
    private void loadMoreNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(mContext, getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        try {
                            int index = postList.size()-1;
                            postList.add(index, unifiedNativeAd);
                            adapter.notifyItemInserted(index);
                        }catch (Exception exp){
                            Log.d("MOREADSEXP",exp.toString());
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