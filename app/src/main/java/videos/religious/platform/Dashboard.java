package videos.religious.platform;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Dashboard extends Fragment {

    private RecyclerView recyclerviewDash;
    private RecyclerView recyclerViewrelated;
    private Dashboard.OnFragmentInteractionListener mListener;
    private ArrayList<Dash> DashList = new ArrayList<>();
    private ArrayList<Related> relatedList = new ArrayList<>();
    private DashboardAdapter nadapter;
    private View inflate;
    private static int CHANNEL_ID = 0;
    private LinearLayout nv;
    private static int tvisibility;
    private Context context;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    public static Dashboard newInstance(String str, String str2) {
        return new Dashboard();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = Dashboard.this.getContext();
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        inflate = layoutInflater.inflate(R.layout.fragment_dashboard, viewGroup, false);
        recyclerviewDash = inflate.findViewById(R.id.list_recyclerview);
        TextView notifyheader = inflate.findViewById(R.id.notifyheader);
        if(!Constants.myFullname.equals("")) {
            notifyheader.setText(Constants.myFullname);
        }
        recyclerViewrelated = inflate.findViewById(R.id.recycler_view_related);
         nv = Dashboard.this.getActivity().findViewById(R.id.nav_view_home);
        return inflate;
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
            DashList = prepareDash();
            nadapter = new DashboardAdapter(Dashboard.this.getContext(), DashList);
            recyclerviewDash.setItemViewCacheSize(20);
            recyclerviewDash.setHasFixedSize(true);
            nadapter.setOnRecyclerViewItemClickListener(new DashboardAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClicked(int wheretogo) {
                    if(wheretogo == 0){
                        if(Constants.myId.equals("")){
                            Intent intent = new Intent(getActivity(), FragviewActivity.class);
                            intent.putExtra("fragId", 2);
                            startActivity(intent);
                            return;
                        }
                        Toast.makeText(context, "Entering Channel Manager", Toast.LENGTH_SHORT).show();
                        final ComponentName compName =
                                new ComponentName(getContext(),
                                        MainFavourites.class);
                            getContext().getPackageManager().setComponentEnabledSetting(compName,
                                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                            Intent intent = new Intent(getActivity(), MainFavourites.class);
                        intent.putExtra("fragId", wheretogo);
                        startActivity(intent);
                    }
                    else if(wheretogo == 4){
                        if(!Constants.myId.equals("")) {
                            Intent billing = new Intent(getContext(), InAppBilling.class);
                            startActivity(billing);
                        }
                        else {
                            Intent intent = new Intent(getActivity(), FragviewActivity.class);
                            intent.putExtra("fragId", 2);
                            startActivity(intent);
                        }
                    }
                    else {
                        Intent intent = new Intent(Dashboard.this.getActivity(), FragviewActivity.class);
                        intent.putExtra("fragId", wheretogo);
                        startActivity(intent);
                    }
                    }
                @Override @TargetApi(21)
                public void onItemShareClicked(String image,String videodesctext, String newheader) {
                    Intent Dashing = new Intent(Dashboard.this.getContext(),MainActivity.class);
                }
            });
            recyclerviewDash.setAdapter(nadapter);
            recyclerviewDash.setLayoutManager(new GridLayoutManager(Dashboard.this.getContext(),2));
    }
    private ArrayList<Dash> prepareDash() {
        ArrayList<Dash> MyDashList = new ArrayList<>();
            int[] icons = new int[]{
                    R.drawable.ic_person_pin_black_24dp,
                    R.drawable.ic_filter_2_black_24dp,
                    R.drawable.ic_perm_data_setting_black_24dp,
                    R.drawable.ic_star_active,
                    R.drawable.ic_block_black_24dp,
                    R.drawable.ic_cloud_download_black_24dp,
                    R.drawable.ic_assignment_black_24dp,
                    R.drawable.ic_subscriptions_black_24dp,
                    R.drawable.ic_info_black_24dp,
                    R.drawable.ic_trending_up_black_24dp,
            };
            int[] DashItem = new int[]{
                    R.string.your_channel,
                    R.string.switch_channel,
                    R.string.settings,
                    R.string.favorites,
                    R.string.remove_ads,
                    R.string.downloads,
                    R.string.terms,
                    R.string.subscriptions,
                    R.string.about,
                    R.string.trending,
            };
            for (int i = 0; i < icons.length; i++) {
                    Dash dash = new Dash();
                    dash.setContentId(DashItem[i]);
                    dash.setDashImage(icons[i]);
                    MyDashList.add(dash);
                    nadapter = new DashboardAdapter(this.getContext(), DashList);
                    nadapter.notifyItemChanged(i);
            }
        return MyDashList;
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
        if (context instanceof Dashboard.OnFragmentInteractionListener) {
            this.mListener = (Dashboard.OnFragmentInteractionListener) context;
            try {
            }catch (ClassCastException cl){

            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.toString());
        sb.append(" must implement OnFragmentInteractionListener");
        throw new RuntimeException(sb.toString());
    }
    @Override public void onStart() {
        super.onStart();
    }

    @Override public void onStop() {
        super.onStop();
    }

    public void onDetach() {
        super.onDetach();
        this.mListener = null;
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
            else {
                return true;
            }
    }
}
