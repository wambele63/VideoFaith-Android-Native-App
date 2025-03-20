package videos.religious.platform;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
public class AllWatchedVideos extends Fragment implements
        FragmentManager.OnBackStackChangedListener{
    private RecyclerView recyclerViewAllWatched;
    private AllWatchedVideos.OnFragmentInteractionListener mListener;
    private Box<AllWatched> allWatchedbox = ObjectBox.getBoxStore().boxFor(AllWatched.class);
    private List AllWatchedList = allWatchedbox.getAll();
    private String ChannelName;
    private AllWatchedAdapter radapter;
    private View inflate;
    private int tvisibility = 0;
    private RelativeLayout Tv;
    private FirebaseFirestore db;
    private LinearLayout Tvtop;
    private int ScrollState;
    private ViewGroup sc;
    private static final int NUMBER_OF_ADS = 2;

    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static AllWatchedVideos newInstance(String str, String str2) {
        return new AllWatchedVideos();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getContext(), "" + R.string.admob_app_id);
        db = FirebaseFirestore.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        inflate = layoutInflater.inflate(R.layout.allwatched_fragment, viewGroup, false);
        fetchAllWatchedJSON();
        return inflate;
    }
    private Map<String,String> allmap=new HashMap<>();
    @SuppressLint("StaticFieldLeak")
    private void fetchAllWatchedJSON() {
        try {
            db.collection("likes").whereEqualTo("liker", Constants.myId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().addOnCompleteListener(getActivity(),task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("favourites")
                                        .whereEqualTo("favouriter", document.get("liker"))
                                        .whereEqualTo("favourited", document.get("liked"))
                                        .orderBy("timestamp", Query.Direction.DESCENDING).limit(3)
                                        .get().addOnCompleteListener(getActivity(), task1 -> {
                                            if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                                allWatchedbox.removeAll();
                                                for (final QueryDocumentSnapshot document1 : task1.getResult()) {
                                                    db.collection("videos").document(document1.get("favourited") + "")
                                                            .get().addOnCompleteListener(getActivity(),new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                                            if (task1.isSuccessful()) {
                                                                final Map<String, Object> post = task1.getResult().getData();
                                                                final AllWatched videopost = new AllWatched();
                                                                final String poster = post.get("postersmall") + "";
                                                                final String video = post.get("video") + "";
                                                                String videotext = post.get("videotext") + "";
                                                                videotext = videotext.replace("[", "")
                                                                        .replace("]", "").replace(",", "");
                                                                final String videoid = task1.getResult().getId();
                                                                final String likes = post.get("likes") + "";
                                                                final String views = post.get("views") + "";
                                                                final String channelid = post.get("channelId") + "";
                                                                String finalVideotext = videotext;
                                                                db.collection("channels").document(channelid).get().addOnCompleteListener(getActivity(),new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                                                        if (task1.isSuccessful()) {
                                                                            Map<String, Object> cpost = task1.getResult().getData();
                                                                            String channelname = cpost.get("channelname") + "";
                                                                            channelname = channelname.replace("[", "")
                                                                                    .replace("]", "").replace(",", "");
                                                                            String channelprofile = cpost.get("profile") + "";
                                                                            String totalvideos = cpost.get("videos") + "";
                                                                            String channelcover = cpost.get("cover") + "";
                                                                            //calculate no of days past
                                                                            videopost.setChannelName(channelname);
                                                                            videopost.setChannelProfile(channelprofile);
                                                                            videopost.setPoster(poster);
                                                                            videopost.setChannelId(channelid);
                                                                            videopost.setVideoDesc(finalVideotext);
                                                                            videopost.setNumOfBlesses(likes);
                                                                            videopost.setVideourl(video);
                                                                            videopost.setVideoId(videoid);
                                                                            videopost.setNumOfAmens(likes + " Amens");
                                                                            videopost.setNumOfComments(views + " Views");
                                                                            allWatchedbox.put(videopost);
                                                                        }
                                                                        attachAllWatched();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }catch (Exception d){

        }
    }
    @Override
    public void onViewCreated(@Nullable final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewAllWatched = view.findViewById(R.id.recycler_view_allwatched);
        attachAllWatched();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sc = new ViewGroup(getActivity()) {
            @Override
            protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
            }
        };
    }
    private void attachAllWatched() {
        AllWatchedList = allWatchedbox.getAll();
        radapter = new AllWatchedAdapter(this.getContext(), AllWatchedList);
        radapter.setOnRecyclerViewItemClickListener(new AllWatchedAdapter.OnRecyclerViewItemClickListener()
        {
            @Override
            public void onItemAllWatchedClicked(String[] videoinfo) {
                String videouri = videoinfo[0];
                String videoid = videoinfo[1];
                String videolike = videoinfo[2];
                String thisvideoviews = videoinfo[3];
                String thisvideodesc = videoinfo[4];
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = sfragmentdialog.newInstance(videouri, videoid, thisvideoviews, thisvideodesc);
                newFragment.setCancelable(true);
                newFragment.setAllowEnterTransitionOverlap(true);
                newFragment.setAllowReturnTransitionOverlap(true);
                newFragment.show(ft, "dialog");
            }
        });
        recyclerViewAllWatched.setHasFixedSize(true);
        recyclerViewAllWatched.setItemViewCacheSize(20);
        recyclerViewAllWatched.setDrawingCacheEnabled(true);
        recyclerViewAllWatched.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        SlideInRightAnimationAdapter alphaAdapter = new SlideInRightAnimationAdapter(radapter);
        recyclerViewAllWatched.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
        recyclerViewAllWatched.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                boolean endHasBeenReached = lastVisible + 1 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(false);
        recyclerViewAllWatched.setLayoutManager(mLayoutManager);
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
        if (context instanceof AllWatchedVideos.OnFragmentInteractionListener) {
            this.mListener = (AllWatchedVideos.OnFragmentInteractionListener) context;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.toString());
        sb.append(" must implement OnFragmentInteractionListener");
        throw new RuntimeException(sb.toString());
    }
    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }
    @Override
    public void onDestroy() {
        this.mListener = null;
        db.terminate();
        super.onDestroy();
    }
    private void sendNotificationUpload(Context context, String title,String button){
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
    public void onBackStackChanged(){
    }
    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }
        int offset = (AllWatchedList.size() / mNativeAds.size())+1;
        int index = 0;
        for (UnifiedNativeAd ad: mNativeAds) {
            AllWatchedList.add(index, ad);
            index = index + offset;
        }
    }
    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                        insertAdsInMenuItems();
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        insertAdsInMenuItems();
                    }
                }).build();
        // Load the Native Express ad.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }
}
