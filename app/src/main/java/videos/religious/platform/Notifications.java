package videos.religious.platform;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Objects;

import io.objectbox.Box;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

public class Notifications extends Fragment {

    private RecyclerView recyclerviewnotify;
    private RecyclerView recyclerViewrelated;
    private Notifications.OnFragmentInteractionListener mListener;
    private Box<Notify> notifyBox = ObjectBox.getBoxStore().boxFor(Notify.class);
    private List<Notify> NotifyList = notifyBox.getAll();
    private Context context;
    private NotifyAdapter nadapter;
    private Notify fullpost;
    private FirebaseFirestore db;
    private static View inflate;
    private int tvisibility;
    private SwipeRefreshLayout swipeLayout;
    private LinearLayout nv;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static Notifications newInstance(String str, String str2) {
        return new Notifications();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        context = Notifications.this.getContext();
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        inflate = layoutInflater.inflate(R.layout.fragment_notify, viewGroup, false);
        recyclerviewnotify = inflate.findViewById(R.id.recycler_view_notify);
        recyclerViewrelated = inflate.findViewById(R.id.recycler_view_related);
        attachNotifications();
         nv = getActivity().findViewById(R.id.nav_view_home);
        swipeLayout = inflate.findViewById(R.id.swipenotify);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNotify();
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
        fetchNotify();
        return inflate;
    }
    @SuppressLint("StaticFieldLeak")
    private void fetchNotify() {
            try {
                swipeLayout.setRefreshing(true);
                db.collection("notifications").whereEqualTo("religion", Constants.myReligion).orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(getActivity(), task -> {
                            notifyBox.removeAll();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot notdoc : task.getResult()) {
                                    Notify notify = new Notify();
                                    notify.setContentId(notdoc.get("contentid") + "");
                                    notify.setNotifyImage(notdoc.get("image") + "");
                                    notify.setNotificationId(notdoc.getId());
                                    notify.setNotification(notdoc.get("body") + "");
                                    notify.setNotifyType(notdoc.get("type") + "");
                                    notify.setContentl(notdoc.get("contentl") + "");
                                    notify.setContentv(notdoc.get("contentv") + "");
                                    notify.setContentd(notdoc.get("contentd") + "");
                                    notify.setContenturl(notdoc.get("contenturl") + "");
                                    String postdate = notdoc.get("timestamp") + "";
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
                                    notify.setNotifyDate(daysagodate);
                                    notifyBox.put(notify);
                                }
                            }
                            swipeLayout.setRefreshing(false);
                            attachNotifications();
                        });
            } catch (Exception d) {

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
    private void attachNotifications() {
        recyclerviewnotify.setAdapter(null);
        NotifyList = notifyBox.getAll();
        nadapter = new NotifyAdapter(Notifications.this.getContext(), NotifyList);
        recyclerviewnotify.setItemViewCacheSize(3);
        recyclerviewnotify.setHasFixedSize(true);
        nadapter.setOnRecyclerViewItemClickListener(new NotifyAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClicked(String[] videoinfo) {
                String videouri = videoinfo[0];
                String videoid = videoinfo[1];
                String contentl = videoinfo[2];
                String contentv = videoinfo[3];
                String thisvideodesc = videoinfo[4];
                String contentd = videoinfo[5];
                if(videoinfo[6].equals("video")){
                    if(null != MainActivity.exoPlayer ) {
                        if (MainActivity.exoPlayer.isPlaying()) {
                            MainActivity.exoPlayer.setPlayWhenReady(false);
                        }
                    }
                    FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    DialogFragment newFragment = sfragmentdialog.newInstance(videouri, videoid, contentv, thisvideodesc);
                    newFragment.show(ft, "dialog");
                }
                else if(videoinfo[6].equals("new")){
                    Intent news = new Intent(getActivity(),NewsActivity.class);
                    news.putExtra("image",videouri)
                            .putExtra("videodesctext",contentd)
                            .putExtra("head", thisvideodesc)
                            .putExtra("location", contentl);
                        Objects.requireNonNull(getActivity()).startActivity(news);
                        Log.d("not attached yet","true");
                }
            }
            @Override
            public void onItemAmenClicked(String videoids) {
            }
            @Override
            public void onItemDownloadClicked(String videourl) {
            }
            @Override @TargetApi(21)
            public void onItemShareClicked(String image,String videodesctext, String newheader) {
                Intent Notifying = new Intent(Notifications.this.getContext(),MainActivity.class);
            }
        });
        SlideInBottomAnimationAdapter alphaAdapter = new SlideInBottomAnimationAdapter(nadapter);
    recyclerviewnotify.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
        recyclerviewnotify.setAdapter(nadapter);
        recyclerviewnotify.setLayoutManager(new LinearLayoutManager(this.getContext().getApplicationContext(), RecyclerView.VERTICAL, false));
    }
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
    // ExoPlayerListener method
    private void onThumbImageViewReady(ImageView imageView) {
        Glide.with(this)
                .load("/videos/bestglobalgospel.jpg")
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Notifications.OnFragmentInteractionListener) {
            this.mListener = (Notifications.OnFragmentInteractionListener) context;
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
    public interface OnChangeCurrentView {
         void OnChangeViewPager(int viewpager);
    }
        public boolean AllowBack() {
            if (tvisibility == 1) {
                tvisibility = 0;
                return false;
            }
            return false;
    }
}
