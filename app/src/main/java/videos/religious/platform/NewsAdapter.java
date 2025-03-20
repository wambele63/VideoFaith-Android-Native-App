package videos.religious.platform;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<News> newsList;
    List newsList2;
    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener mListener;
    private ActivityCompat activity;
    private static final int POST_ITEM_VIEW_TYPE = 0;
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;
    public interface OnRecyclerViewItemClickListener {
        void onItemShareClicked(String image, String videodesctext,String newheader,String time,String location);
        AdSize getAdSize();
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView channelname,channelid, totalvideos,videodesc,amen,share,download,channelowner,postdate,videoviews,videolikes,thischannelownerid,thisvideoid,thisvideourl;
        ImageView nposter,channelprofile,overflow;
        FrameLayout adview;
        LinearLayoutCompat cardview;

        private MyViewHolder(View view) {
            super(view);
            channelname = view.findViewById(R.id.channelname);
            channelid = view.findViewById(R.id.channelid);
            totalvideos = view.findViewById(R.id.totalvideos);
            thischannelownerid = view.findViewById(R.id.thischannelownerid);
            thisvideoid = view.findViewById(R.id.thisvideoid);
            thisvideourl = view.findViewById(R.id.thisvideourl);
            channelowner = view.findViewById(R.id.channelOwner);
            postdate = view.findViewById(R.id.timeone);
            videoviews = view.findViewById(R.id.videoviews);
            videolikes = view.findViewById(R.id.videolikes);
            nposter= view.findViewById(R.id.poster);
            channelprofile = view.findViewById(R.id.channelprofile);
            videodesc = view.findViewById(R.id.videodesc);
            overflow = view.findViewById(R.id.overflow);
            amen = view.findViewById(R.id.amen);
            download = view.findViewById(R.id.download);
            cardview = view.findViewById(R.id.card_view);
            share = view.findViewById(R.id.share);
            adview  = view.findViewById(R.id.ad_view_container_new);
        }
    }
    public NewsAdapter(Context mContext, List NewsList) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.newsList = NewsList;
        newsList2 = this.newsList;
    }
    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = inflater.inflate(R.layout.ad_unified_news,
                        parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case POST_ITEM_VIEW_TYPE:
                View itemView = inflater.inflate(R.layout.new_card, parent, false);
                final MyViewHolder holder = new MyViewHolder(itemView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String posterimg = holder.thisvideourl.getText().toString();
                        final String image = posterimg;
                        String newheader = holder.channelname.getText().toString();
                        String videodesctext = holder.videodesc.getText().toString();
                        String time = holder.postdate.getText().toString();
                        String location = holder.thischannelownerid.getText().toString();
                     mListener.onItemShareClicked(image, videodesctext, newheader,time,location);
                    }
                });
                // Step 1 - Create an AdView and set the ad unit ID on it.
                AdView adView;
                adView = new AdView(mContext);
                adView.setAdUnitId(mContext.getString(R.string.NewsBannerAd_unit_id));
                holder.adview.addView(adView);
                    loadBanner(adView);
                return holder;
                default:
                itemView = inflater.inflate(R.layout.new_card, parent, false);
                MyViewHolder holder2 = new MyViewHolder(itemView);
                return holder2;
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder2, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) newsList2.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder2).getAdView());
                break;
            case POST_ITEM_VIEW_TYPE:
                final MyViewHolder holder = (MyViewHolder) holder2;
                holder.channelid.setText(newsList.get(position).getChannelId());
                holder.postdate.setText(newsList.get(position).getPostDate());
                holder.thischannelownerid.setText(newsList.get(position).getLocation());
                holder.thisvideoid.setText(newsList.get(position).getVideoId());
                holder.thisvideourl.setText(newsList.get(position).getPoster());
                holder.channelowner.setText(newsList.get(position).getPosterName());
                holder.totalvideos.setText(newsList.get(position).getTotalVideos());
                holder.videolikes.setText(newsList.get(position).getNumOfBlesses());
                holder.videoviews.setText(newsList.get(position).getNumOfComments());
                holder.videodesc.setText(newsList.get(position).getVideoDesc());
                holder.channelname.setText(newsList.get(position).getHead());
                // loading video nposterusing Glide library
                final String posterimg = newsList.get(position).getPoster();
                final String image = posterimg;
                Glide.with(holder.itemView).load(image).into(holder.nposter);
                break;
        }
    }
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder){
        super.onViewRecycled(holder);
        mListener = null;
    }
    /**a
     * Showing popup menu when tapping on 3 dots
    /**
     * Click listener for popup menu items
     */
    @Override
    public long getItemId(int position) {
        Object recyclerViewItem = newsList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return position;
        }
        return newsList.get(position).getId();    }
    @Override
    public int getItemCount() {
        if(newsList != null) {
            return newsList.size();
        }
        else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = newsList.get(position);
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
    private void loadBanner(AdView adView) {
        AdRequest adRequest =
                new AdRequest.Builder()
                        .build();

        AdSize adSize = mListener.getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }
}
