package videos.religious.platform;

import android.content.Context;
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
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.List;

import io.objectbox.Box;

public class ChannelVideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Box<ChannelVideo> videobox = ObjectBox.getBoxStore().boxFor(ChannelVideo.class);
    private List<ChannelVideo> postList;
    private List<UnifiedNativeAd> mNativeAds;
    private LayoutInflater inflater;
    private String videoiddownload,currentVideoId;
    private OnRecyclerViewItemClickListener mListener;
    // A menu item view type.
    private static final int POST_ITEM_VIEW_TYPE = 0;

    // The unified native ad view type.

    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(String[] videoinfos);
        void onItemShareClicked(String videourlshare);
        void onItemDownloadClicked(String videourl);
        void onFavouritesClicked(String videoid);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView duration,postdownloads,channelid,subs, totalvideos,amen,videosize,download,channelowner,postdate,videoviews,videolikes,thischannelownerid,thisvideoid,thisvideourl;
        ImageView overflow,poster,share;
        EmojiAppCompatTextView channelname,videodesc;

        private MyViewHolder(View view) {
            super(view);
            channelname = view.findViewById(R.id.channelname);
            channelid = view.findViewById(R.id.channelid);
            totalvideos = view.findViewById(R.id.totalvideos);
            thischannelownerid = view.findViewById(R.id.thischannelownerid);
            thisvideoid = view.findViewById(R.id.thisvideoid);
            thisvideourl = view.findViewById(R.id.thisvideourl);
            channelowner = view.findViewById(R.id.channelOwner);
            postdate = view.findViewById(R.id.postdate);
            download = view.findViewById(R.id.download);
            subs = view.findViewById(R.id.subscriptions);
            duration = view.findViewById(R.id.duration);
            postdownloads = view.findViewById(R.id.postdownloads);
            videosize = view.findViewById(R.id.mbs);
            videodesc = view.findViewById(R.id.videodesc);
            videoviews = view.findViewById(R.id.videoviews);
            videolikes = view.findViewById(R.id.videolikes);
            poster = view.findViewById(R.id.poster);
            overflow = view.findViewById(R.id.overflow);
            share = view.findViewById(R.id.share);
        }
    }
    public ChannelVideosAdapter(Context mContext, List postlist) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.postList = postlist;
    }
    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = inflater.inflate(R.layout.ad_unified_mini,
                        parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case POST_ITEM_VIEW_TYPE:
                final MyViewHolder holder;
                View itemView = inflater.inflate(R.layout.channel_card, parent, false);
               holder = new MyViewHolder(itemView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.RubberBand).duration(600).playOn(holder.itemView);
                        // send the text to the listener, i.e Activity.
                        String videourl = holder.thisvideourl.getText().toString();
                        String videoid = holder.thisvideoid.getText().toString();
                        String videolike = holder.videolikes.getText().toString();
                        String chanid = holder.channelid.getText().toString();
                        String thisvideoviews = holder.videoviews.getText().toString();
                        String thisvideodesc = holder.videodesc.getText().toString();
                        String thissubs = holder.subs.getText().toString();
                        String channame = holder.postdate.getText().toString();
                        String isfavoured = postList.get(holder.getAdapterPosition()).getFavoured();
                        String isliked = postList.get(holder.getAdapterPosition()).getLikeda();
                        String issubscribed = postList.get(holder.getAdapterPosition()).getSubscribed();
                        String[] videoinfo = {videourl,videoid,videolike,thisvideoviews,thisvideodesc,chanid,thissubs,channame,isliked,isfavoured,issubscribed};
                        mListener.onItemClicked(videoinfo);
                    }
                });
                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // send the text to the listener, i.e Activity.
                        String videoidshare = postList.get(holder.getAdapterPosition()).getPoster();
                        YoYo.with(Techniques.RubberBand).repeat(0).duration(600).playOn(holder.share);
                        mListener.onItemShareClicked(videoidshare);
                    }
                });
                holder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // send the text to the listener, i.e Activity.
                        videoiddownload = holder.thisvideourl.getText().toString();
                        YoYo.with(Techniques.RubberBand).repeat(0).duration(600).playOn(holder.download);
                        showPopupDownload(holder.download);
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
                 itemView = inflater.inflate(R.layout.channel_card, parent, false);
                 holder = new MyViewHolder(itemView);
                return holder;
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder2, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                List postList2 = postList;
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) postList2.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder2).getAdView());
                break;
            case POST_ITEM_VIEW_TYPE:
                MyViewHolder holder = (MyViewHolder) holder2;
                holder.channelname.setText( postList.get(position).getVideoDesc());
                holder.channelid.setText(postList.get(position).getChannelId());
                holder.videodesc.setText(postList.get(position).getVideoDesc());
                holder.postdate.setText(postList.get(position).getChannelName());
                holder.thischannelownerid.setText(postList.get(position).getChannelownerid());
                holder.thisvideoid.setText(postList.get(position).getVideoId());
                holder.thisvideourl.setText(postList.get(position).getVideourl());
                holder.channelowner.setText(postList.get(position).getPosterName());
                holder.totalvideos.setText(postList.get(position).getTotalVideos());
                holder.videolikes.setText(postList.get(position).getNumOfBlesses());
                holder.postdownloads.setText(postList.get(position).getNumOfComments());
                holder.videoviews.setText(postList.get(position).getNumOfComments());
                holder.duration.setText(postList.get(position).getVDuration());
                holder.videosize.setText(postList.get(position).getVideosize());
                holder.subs.setText(postList.get(position).getSubscriptions());
                String issubscribed = postList.get(position).getSubscribed();
                if(issubscribed.equals("1")) {
                }
                // loading video poster using Glide library
                Glide.with(holder.itemView).load(postList.get(position).getPoster()).fitCenter().into(holder.poster);
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
        inflater.inflate(R.menu.menu_post, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }
private void showPopupDownload(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_download, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyDownloadItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    mListener.onFavouritesClicked(currentVideoId);
                    return true;
                default:
            }
            return false;
        }
    }

    private class MyDownloadItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyDownloadItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.downloadoption:
                    mListener.onItemDownloadClicked(videoiddownload);
                    return true;
                default:
            }
            return false;
        }
    }
    @Override
    public long getItemId(int position) {
        Object recyclerViewItem = postList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return position;
        }
        return postList.get(position).getId();    }

    @Override
    public int getItemCount() {
        if(postList != null) {
            return postList.size();
        }
        else {
            return 10;
        }
    }
    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = postList.get(position);
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
            adView.getIconView().setVisibility(View.GONE);
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
