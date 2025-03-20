package videos.religious.platform;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Trending> TrendingList;
    private List<UnifiedNativeAd> mNativeAds;
    private int hashtag=0;
    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener mListener;
    private String currentVideoId;
    // A menu item view type.
    private static final int POST_ITEM_VIEW_TYPE = 0;

    // The unified native ad view type.
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(String[] videoinfos);
        void onItemShareClicked(String videourlshare);
        void onFavouritesClicked(String videoid);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView channelname,subs,postdownloads,videosize,duration,channelid, totalvideos,amen,download,channelowner,postdate,videodesc,videoviews,videolikes,thischannelownerid,thisvideoid,thisvideourl;
        ImageView overflow,share,channelprofile,poster;

        private MyViewHolder(View view) {
            super(view);
            channelname = view.findViewById(R.id.channelname);
            channelid = view.findViewById(R.id.channelid);
            totalvideos = view.findViewById(R.id.totalvideos);
            thischannelownerid = view.findViewById(R.id.thischannelownerid);
            thisvideoid = view.findViewById(R.id.thisvideoid);
            subs = view.findViewById(R.id.subscriptions);
            duration = view.findViewById(R.id.duration);
            postdownloads = view.findViewById(R.id.postdownloads);
            videosize = view.findViewById(R.id.mbs);
            thisvideourl = view.findViewById(R.id.thisvideourl);
            channelowner = view.findViewById(R.id.channelOwner);
            postdate = view.findViewById(R.id.postdate);
            videodesc = view.findViewById(R.id.videodesc);
            videoviews = view.findViewById(R.id.videoviews);
            videolikes = view.findViewById(R.id.videolikes);
            poster = view.findViewById(R.id.poster);
            channelprofile = view.findViewById(R.id.channelprofile);
            overflow = view.findViewById(R.id.overflow);
            share = view.findViewById(R.id.share);
        }
    }
    public TrendingAdapter(Context mContext, List<Trending> TrendingList) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.TrendingList = TrendingList;
    }
    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MyViewHolder holder;
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = inflater.inflate(R.layout.ad_unified,
                        parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case POST_ITEM_VIEW_TYPE:
                View itemView = inflater.inflate(R.layout.trending_card, parent, false);
               holder = new MyViewHolder(itemView);

                holder.poster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // send the text to the listener, i.e Activity.
                        String videourl = holder.thisvideourl.getText().toString();
                        String videoid = holder.thisvideoid.getText().toString();
                        String videolike = holder.videolikes.getText().toString();
                        String thisvideoviews = holder.videoviews.getText().toString();
                        String thisvideodesc = holder.videodesc.getText().toString();
                        String[] videoinfo = {videourl,videoid,videolike,thisvideoviews,thisvideodesc};
                        mListener.onItemClicked(videoinfo);
                    }
                });
                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // send the text to the listener, i.e Activity.
                        String videoidshare = holder.thisvideoid.getText().toString();
                        YoYo.with(Techniques.RubberBand).repeat(0).duration(600).playOn(holder.share);
                        mListener.onItemShareClicked(videoidshare);

                    }
                });
                holder.overflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentVideoId = holder.thisvideoid.getText().toString();
                        showPopupMenu(holder.overflow);
                    }
                });
                return holder;
            default:
                 itemView = inflater.inflate(R.layout.trending_card, parent, false);
                 holder = new MyViewHolder(itemView);
                return holder;
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder2, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                List TrendingList2 = this.TrendingList;
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) TrendingList2.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder2).getAdView());
                break;
            case POST_ITEM_VIEW_TYPE:
                final MyViewHolder holder = (MyViewHolder) holder2;
                holder.postdownloads.setText("#"+ ++hashtag+"trending");
                holder.channelname.setText( TrendingList.get(position).getChannelName());
                holder.postdate.setText( TrendingList.get(position).getPostDate());
                holder.channelid.setText(TrendingList.get(position).getChannelId());
                holder.videodesc.setText(TrendingList.get(position).getVideoDesc());
                holder.thischannelownerid.setText(TrendingList.get(position).getChannelownerid());
                holder.thisvideoid.setText(TrendingList.get(position).getVideoId());
                holder.thisvideourl.setText(TrendingList.get(position).getVideourl());
                holder.channelowner.setText(TrendingList.get(position).getPosterName());
                holder.totalvideos.setText(TrendingList.get(position).getTotalVideos());
                holder.videolikes.setText(TrendingList.get(position).getNumOfBlesses());
                holder.videoviews.setText(TrendingList.get(position).getNumOfComments());
                holder.duration.setText(TrendingList.get(position).getVDuration());
                holder.channelname.setTag(TrendingList.get(position).getChannelCover());
                holder.channelid.setTag(TrendingList.get(position).getTotalVideos());
                String issubscribed = TrendingList.get(position).getSubscribed();
                // loading video poster using Glide library
                Glide.with(holder.poster).load(TrendingList.get(position).getPoster()).into(holder.poster);
                Glide.with(holder.channelprofile).load(TrendingList.get(position).getChannelProfile()).placeholder(R.drawable.gallerysmall).circleCrop().into(holder.channelprofile);
        }
    }
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }
    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setGravity(Gravity.CENTER_VERTICAL);
        inflater.inflate(R.menu.menu_post, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_add_favourite) {
                menuItem.setTitle("Added...");
                mListener.onFavouritesClicked(currentVideoId);
                return true;
            }
            return false;
        }
    }
    @Override
    public long getItemId(int position) {
        Object recyclerViewItem = TrendingList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return position;
        }
        return TrendingList.get(position).getId();
    }
    @Override
    public int getItemCount() {
        if(TrendingList != null) {
            return TrendingList.size();
        }
        else {
            return 10;
        }
    }
    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = TrendingList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return POST_ITEM_VIEW_TYPE;
    }
    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();
        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            Glide.with(adView).load(icon.getDrawable()).transform(new CircleCrop()).into((ImageView) adView.getIconView());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }
}