package videos.religious.platform;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class Subscriptions extends Fragment implements
        FragmentManager.OnBackStackChangedListener{

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewrelated;
    private Subscriptions.OnFragmentInteractionListener mListener;
    private Box<Subscriptioned> SubscriptionsBox = ObjectBox.getBoxStore().boxFor(Subscriptioned.class);
    private List SubscriptionsList = SubscriptionsBox.getAll();
    private ArrayList<Related> relatedList = new ArrayList<>();
    private Context context = this.getContext();
    private SubscriptionsAdapter adapter;
    private RelatedAdapter radapter;
    private SwipeRefreshLayout swipeLayout;
    private static View inflate;
    private String videoid,videolike,thisvideoviews,thisvideodesc;
    private static int tvisibility = 0;
    public RelativeLayout Tv;
    private TextView videodesc;
    private int ScrollState;
    private boolean LAST_FETCH_ONPROGRESS = true;
    private int LAST_AD_INDEX = 5;
    private FirebaseFirestore db;
    private String LAST_ITEM_ID = "0";
    private LinearLayout nv;
    private static final int NUMBER_OF_ADS = 1;

    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static Subscriptions newInstance(String str, String str2) {
        return new Subscriptions();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this.getContext(), "" + R.string.admob_app_id);
        db = FirebaseFirestore.getInstance();
        flushOutDatedTrends();
    }
    private void flushOutDatedTrends(){
        db.collection("subscriptions")
                .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().isEmpty()) {
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if(System.currentTimeMillis() - Long.parseLong(documentSnapshot.get("timestamp")+"") >= 7) {
                            db.collection("subscriptions").document(documentSnapshot.getId()).delete();
                        }
                    }
                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        inflate = layoutInflater.inflate(R.layout.fragment_subscriptions, viewGroup, false);
        recyclerView = inflate.findViewById(R.id.recycler_view);
        swipeLayout = inflate.findViewById(R.id.swipesubscriptions);
        attachData();
        fetchJSON();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchJSON();
                SubscriptionsList.clear();
                mNativeAds.clear();
                loadNativeAds();
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                    }
                }, 1000);
            }
        });
        swipeLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDiluted,
                R.color.colorPrimaryDarkVideo,
                R.color.colorPrimaryDark);
        return inflate;
    }
    @SuppressLint("StaticFieldLeak")
    private void DownloadVideo(String DownloadUrl) {
    }
    private String subscribed = "0";
    private String liked = "0";
    private String favourited = "0";
    private String subscriptions = "0";
    @SuppressLint("StaticFieldLeak")
    private void fetchJSON() {
        try {
            db.collection("subscriptions").whereEqualTo("subscriber", Constants.myId).orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LAST_ITEM_ID = document.get("timestamp") + "";
                            String channelId = document.get("subscribed") + "";
                            db.collection("videos").whereEqualTo("channelId", channelId).limit(3).get()
                                    .addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                SubscriptionsBox.removeAll();
                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                    Map<String, Object> post = doc.getData();
                                                    final Subscriptioned videopost = new Subscriptioned();
                                                    final String postdate = post.get("time") + "";
                                                    final String poster = post.get("poster") + "";
                                                    final String channelid = post.get("channelId") + "";
                                                    String videotext = post.get("videotext") + "";
                                                    final String likes = post.get("likes") + " Likes";
                                                    final String video = post.get("video") + "";
                                                    final String duration = post.get("duration") + "";
                                                    final String onlineid = doc.getId();
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
                                                                SubscriptionsBox.put(videopost);
                                                            }
                                                            swipeLayout.setRefreshing(false);
                                                            attachData();
                                                        }
                                                    });
                                                }
                                            }
                                            LAST_FETCH_ONPROGRESS = false;
                                        }
                                    });
                        }
                    }
                }
            });
        }catch(Exception m){

        }
    }
    @Override
    public void onViewCreated(@Nullable final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadNativeAds();
    }
    private void attachData() {
        SubscriptionsList = SubscriptionsBox.getAll();
        adapter = new SubscriptionsAdapter(this.getContext(), SubscriptionsList);
        recyclerView.setItemViewCacheSize(20);
        adapter.setOnRecyclerViewItemClickListener(new SubscriptionsAdapter.OnRecyclerViewItemClickListener() {
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
            @Override
            public void onFavouritesClicked(String id){
                addFavourites(id);
            }
        });
        recyclerView.setHasFixedSize(true);
        adapter.setHasStableIds(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int state) {
                super.onScrollStateChanged(recyclerView, state);
                boolean hasStarted = state == SCROLL_STATE_DRAGGING;
                if (state == SCROLL_STATE_IDLE) {
                    ScrollState = state;
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
                        LAST_AD_INDEX= SubscriptionsList.size()-1;
                        FetchMoreJson();
                    }
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext().getApplicationContext(), RecyclerView.VERTICAL, false));
        swipeLayout.setRefreshing(false);
    }
    @SuppressLint("StaticFieldLeak")
    private void addFavourites(final String videoidtolike) {
        db.collection("favourites").whereEqualTo("favouriter",Constants.myId).whereEqualTo("favourited",videoidtolike)
                .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().isEmpty() || task.getResult().equals(null)) {
                    db.collection("videos").document(videoidtolike).update("favourites", FieldValue.increment(1));
                    Toast.makeText(getContext(), "Added To Your Favourites", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "No Need, Already In Your Favourites", Toast.LENGTH_SHORT).show();
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
        }catch(Exception e) {
        }
    }
    @SuppressLint("StaticFieldLeak")
    private void FetchMoreJson() {
        try {
            db.collection("subscriptions").whereEqualTo("subscriber", Constants.myId)
                    .whereLessThan("timestamp", LAST_ITEM_ID).orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(5).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LAST_ITEM_ID = document.get("timestamp") + "";
                            String channelId = document.get("videoid") + "";
                            db.collection("videos").whereEqualTo("channelId", channelId)
                                    .orderBy("time", Query.Direction.DESCENDING).limit(5).get()
                                    .addOnCompleteListener(getActivity(),task1 -> {
                                        if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot vdoc : task1.getResult()) {
                                                Map<String, Object> post = vdoc.getData();
                                                final Subscriptioned videopost = new Subscriptioned();
                                                final String postdate = post.get("time") + "";
                                                final String poster = post.get("poster") + "";
                                                final String channelid = post.get("channelId") + "";
                                                String videotext = post.get("videotext") + "";
                                                final String likes = post.get("likes") + " Likes";
                                                final String video = post.get("video") + "";
                                                final String duration = post.get("duration") + "";
                                                final String onlineid = vdoc.getId();
                                                final String mbs = post.get("videoSize") + "mbs";
                                                final String downloads = post.get("downloads") + "";
                                                final String comments = post.get("views") + " Views";
                                                //get channelinfo

                                                db.collection("subscriptions").whereEqualTo("subscriber", Constants.myId).whereEqualTo("subscribed", channelid)
                                                        .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                        if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                                            subscribed = "1";
                                                        }
                                                    }
                                                });
                                                db.collection("favourites").whereEqualTo("favouriter", Constants.myId).whereEqualTo("favourited", channelid)
                                                        .get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                        if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                                            favourited = "1";
                                                        }
                                                    }
                                                });
                                                db.collection("likes").whereEqualTo("liker", Constants.myId).whereEqualTo("liked", channelid)
                                                        .limit(1).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                        if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                                            liked = "1";
                                                        }
                                                    }
                                                });
                                                db.collection("subscriptions").whereEqualTo("subscribed", channelid).get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                        if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                                            subscriptions = task1.getResult().getDocuments().size() + "";
                                                        }
                                                    }
                                                });
                                                String finalVideotext = videotext;
                                                db.collection("channels").document(channelid).get()
                                                        .addOnCompleteListener(getActivity(),new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                                                if (task1.isSuccessful() && !task1.getResult().getData().isEmpty()) {
                                                                    Map<String, Object> cpost = task1.getResult().getData();
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
                                                                    SubscriptionsBox.put(videopost);
                                                                    SubscriptionsList.add(videopost);
                                                                    adapter.notifyItemInserted(SubscriptionsList.size() - 1);
                                                                    LAST_FETCH_ONPROGRESS = false;
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            LAST_FETCH_ONPROGRESS = false;
                                            Toast.makeText(getContext(), "end", Toast.LENGTH_SHORT).show();
                                        }
                                        LAST_FETCH_ONPROGRESS = false;
                                    });
                        }
                    } else {
                    }
                    LAST_FETCH_ONPROGRESS = false;
                }
            });
        }catch(Exception l){

        }
    }
    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        private GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Subscriptions.OnFragmentInteractionListener) {
            this.mListener = (Subscriptions.OnFragmentInteractionListener) context;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.toString());
        sb.append(" must implement OnFragmentInteractionListener");
        throw new RuntimeException(sb.toString());
    }
    public void onDestroy(){
        super.onDestroy();
        db.terminate();
    }
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    private void sendNotificationUpload(Context context, String title, String button) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "1")
                .setContentTitle(title)
                .setContentText("")
                .setAutoCancel(true)
                .setContentIntent(null)
                .setContentInfo(title)
                .addAction(R.drawable.ic_home_black_24dp, button, null)
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
        if (SubscriptionsList.size() <= 0) {
            return;
        }
        int offset = (SubscriptionsList.size() / mNativeAds.size())+1;
        int index = 2;
        for (UnifiedNativeAd ad : mNativeAds) {
            SubscriptionsList.add(index, ad);
            index = index + offset;
            adapter.notifyItemInserted(index);
        }
    }
    private void loadNativeAds(){
        AdLoader.Builder builder = new AdLoader.Builder(getContext().getApplicationContext(), getString(R.string.ad_unit_id));
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
        AdLoader.Builder builder = new AdLoader.Builder(getContext().getApplicationContext(), getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        try {
                            int index = LAST_AD_INDEX;
                            SubscriptionsList.add(index, unifiedNativeAd);
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
                        view.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.RubberBand).duration(400).playOn(view);
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
