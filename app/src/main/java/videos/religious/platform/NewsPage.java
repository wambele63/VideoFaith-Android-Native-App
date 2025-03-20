package videos.religious.platform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class NewsPage extends Fragment implements
        FragmentManager.OnBackStackChangedListener{

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewrelated;
    private FirebaseFirestore db;
    private NewsPage.OnFragmentInteractionListener mListener;
    private Box<News> newsBox = ObjectBox.getBoxStore().boxFor(News.class);
    private List newsList = newsBox.getAll();
    private ArrayList<RelatedNews> relatedList = new ArrayList<>();
    private Context context = this.getContext();
    private String ChannelName;
    private NewsAdapter nadapter;
    private RelatedNewsAdapter radapter;
    private String ChannelProfile;
    private String poster;
    private String postDate;
    private SwipeRefreshLayout swipeLayout;
    private View inflate;

    private static final int NUMBER_OF_ADS = 5;
    private static String LAST_ITEM_ID;

    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static NewsPage newInstance(String str, String str2) {
        return new NewsPage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        inflate = layoutInflater.inflate(R.layout.activity_news, viewGroup, false);
        recyclerView = inflate.findViewById(R.id.recycler_view_news);
        recyclerViewrelated = inflate.findViewById(R.id.recycler_view);
        attachNews();
        final LinearLayout nv = NewsPage.this.getActivity().findViewById(R.id.nav_view_home);
        initCollapsingToolbar();
        loadNativeAds();
        swipeLayout = inflate.findViewById(R.id.swipenews);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNews();
                if(!Constants.PRO_USER) {
                    loadNativeAds();
                }
            }
        });
        swipeLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDiluted,
                R.color.colorPrimaryDarkVideo,
                R.color.colorPrimaryDark);
        fetchNews();
        return inflate;
    }
    private void resetItemsAndIndeces(){
        newsBox.removeAll();
        index = 3;
        mNativeAds.clear();
    }
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = inflate.findViewById(R.id.collapsing_toolbarnews);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = inflate.findViewById(R.id.appbarnews);
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
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    private void DownloadVideo(String DownloadUrl) {
    }
    @SuppressLint("StaticFieldLeak")
    private void fetchNews() {
            try {
                swipeLayout.setRefreshing(true);
                db.collection("news")
                        .whereEqualTo("religion", Constants.myReligion)
                        .orderBy("timestamp", Query.Direction.DESCENDING).limit(20)
                        .get()
                        .addOnCompleteListener(getActivity(), task -> {
                            newsBox.removeAll();
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                resetItemsAndIndeces();
                                for (QueryDocumentSnapshot newsdoc : task.getResult()) {
                                    News news = new News();
                                    news.setread(newsdoc.get("read") + "");
                                    news.setPoster(newsdoc.get("image") + "");
                                    news.setVideoId(newsdoc.getId());
                                    String head = newsdoc.get("head") + "";
                                    news.setHead(head);
                                    news.setLocation(newsdoc.get("location") + "");
                                    news.setVideoDesc(newsdoc.get("body") + "");
                                    String postdate = newsdoc.get("timestamp") + "";
                                    String daysagodate = "Breaking";
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
                                    news.setPostDate(daysagodate);
                                    newsBox.put(news);
                                }
                            }
                            swipeLayout.setRefreshing(false);
                            attachNews();
                        });
            } catch (Exception b) {

            }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = new NewsPage().getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationBannerAdSizeWithWidth(new NewsPage().getActivity(),adWidth);
    }
    private void attachNews() {
        recyclerView.setAdapter(null);
        newsList = newsBox.getAll();
       nadapter = new NewsAdapter(getContext(), newsList);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHasFixedSize(true);
        nadapter.setHasStableIds(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            Activity act = getActivity();
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
       nadapter.setOnRecyclerViewItemClickListener(new NewsAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemShareClicked( String image,String videodesctext, String newheader,String time, String location) {
                try {
                    Intent news = new Intent(getContext(),NewsActivity.class);
                news.putExtra("image",image).putExtra("time",time).putExtra("location",location)
                        .putExtra("videodesctext",videodesctext).putExtra("head",newheader);
                String transitionName = "appname";
                   ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
                   startActivity(news, options.toBundle());
                }catch (NullPointerException ne){
                    Log.d("not attached yet","true");
                }
            }
            @Override
           public AdSize getAdSize(){
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
        });
        SlideInRightAnimationAdapter alphaAdapter = new SlideInRightAnimationAdapter(nadapter);
        SlideInUpAnimator animator = new SlideInUpAnimator();
        recyclerView.setItemAnimator(animator);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
        if(newsList.size() > 0 && mNativeAds.size() != 0){
            insertAdsInMenuItems();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext().getApplicationContext(), RecyclerView.VERTICAL, false));
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewsPage.OnFragmentInteractionListener) {
            this.mListener = (NewsPage.OnFragmentInteractionListener) context;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.toString());
        sb.append(" must implement OnFragmentInteractionListener");
        throw new RuntimeException(sb.toString());
    }
    public void onDetach() {
        super.onDetach();
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

    private static Fragment getCurrentFragment(MainActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            String lastFragmentName = fragmentManager.getBackStackEntryAt(
                    fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(lastFragmentName);
        }
        return null;
    }
    public void onBackStackChanged(){
    }
    private void showp(){
        final ProgressBar progressBar = inflate.findViewById(R.id.progress_barnews);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 3500);
    }
    private int index = 1;
    private void insertAdsInMenuItems() {
            int offset = (newsList.size() / mNativeAds.size());
            for (UnifiedNativeAd ad : mNativeAds) {
                if(newsList.size()-1 > index) {
                    newsList.add(index, ad);
                    index = index + offset;
                    nadapter.notifyItemInserted(index);
                }
            }
            new Handler().postDelayed(() -> {
                mNativeAds.clear();
            }, 5000);
    }
    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getString(R.string.imageAd_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another."+errorCode);
                        if(!adLoader.isLoading()) {
                        }
                    }
                }).build();
        // Load the Native Express ad.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }
}