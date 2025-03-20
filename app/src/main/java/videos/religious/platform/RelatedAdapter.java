package videos.religious.platform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;
public class RelatedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Related> relatedList;
    private static final int POST_ITEM_VIEW_TYPE = 0;
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;
    private LayoutInflater inflater;
    private static OnRecyclerViewItemClickListener mListener;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemRelatedClicked(String[] videoinfos);}
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView channelname,channelid, thischannelpro,subs, totalvideos,channelowner,postdate,videodesc,videoviews,videolikes,thischannelownerid,thisvideoid,thisvideourl;
        ImageView poster,channelprofile,overflow;
        ImageButton amen,download;
        private MyViewHolder(View view) {
            super(view);
            channelname = view.findViewById(R.id.channelname);
            channelid = view.findViewById(R.id.channelid);
            totalvideos = view.findViewById(R.id.totalvideos);
            thischannelownerid = view.findViewById(R.id.thischannelownerid);
            thisvideoid = view.findViewById(R.id.thisvideoid);
            thisvideourl = view.findViewById(R.id.thisvideourl);
            thischannelpro = view.findViewById(R.id.thischannelurl);
            subs = view.findViewById(R.id.subscriptions);
            channelowner = view.findViewById(R.id.channelOwner);
            postdate = view.findViewById(R.id.postdate);
            videodesc = view.findViewById(R.id.videodesc);
            videoviews = view.findViewById(R.id.videoviews);
            videolikes = view.findViewById(R.id.videolikes);
            poster = view.findViewById(R.id.poster);
            channelprofile = view.findViewById(R.id.channelprofile);
            overflow = view.findViewById(R.id.overflow);
        }
    }
    public RelatedAdapter(Context mContext, ArrayList relatedList2) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.relatedList = relatedList2;
    }
    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = inflater.inflate(R.layout.ad_unified_mini,
                        parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case POST_ITEM_VIEW_TYPE:
                final RelatedAdapter.MyViewHolder holder;
                View itemView = inflater.inflate(R.layout.post_card_related, parent, false);
                 holder = new MyViewHolder(itemView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.RubberBand).duration(600).playOn(holder.itemView);
                        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorLightAccent));
                        // send the text to the listener, i.e Activity.
                        String videourl = holder.thisvideourl.getText().toString();
                        String videoid = holder.thisvideoid.getText().toString();
                        String videolike = holder.videolikes.getText().toString();
                        String chanid = holder.channelid.getText().toString();
                        String thisvideoviews = holder.videoviews.getText().toString();
                        String thisvideodesc = holder.videodesc.getText().toString();
                        String thischanpro = holder.thischannelpro.getText().toString();
                        String thischancover = holder.channelname.getTag().toString();
                        String totalvids = holder.channelid.getTag().toString();
                        String thissubs = holder.subs.getText().toString();
                        String channame = holder.postdate.getText().toString();
                        String isfavoured = relatedList.get(holder.getAdapterPosition()).getFavoured();
                        String isliked = relatedList.get(holder.getAdapterPosition()).getLikeda();
                        String issubscribed = relatedList.get(holder.getAdapterPosition()).getSubscribed();
                        String[] videoinfo = {videourl, videoid, videolike, thisvideoviews, thisvideodesc, thischanpro, chanid, thissubs, channame, thischancover, totalvids, isliked, isfavoured, issubscribed};
                        mListener.onItemRelatedClicked(videoinfo);
                    }
                });
                return holder;
            default:
                itemView = inflater.inflate(R.layout.post_card_related, parent, false);
                holder = new RelatedAdapter.MyViewHolder(itemView);
                return holder;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder2, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                List postList2 = relatedList;
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) postList2.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder2).getAdView());
                break;
            case POST_ITEM_VIEW_TYPE:
               final RelatedAdapter.MyViewHolder holder = (RelatedAdapter.MyViewHolder) holder2;
                holder.channelname.setText(relatedList.get(position).getChannelName());
                holder.channelid.setText(relatedList.get(position).getChannelId());
                holder.videodesc.setText(relatedList.get(position).getVideoDesc());
                holder.postdate.setText(relatedList.get(position).getChannelName());
                holder.thischannelownerid.setText(relatedList.get(position).getChannelownerid());
                holder.thisvideoid.setText(relatedList.get(position).getVideoId());
                holder.thisvideourl.setText(relatedList.get(position).getVideourl());
                holder.thischannelpro.setText(relatedList.get(position).getChannelProfile());
                holder.channelowner.setText(relatedList.get(position).getPosterName());
                holder.totalvideos.setText(relatedList.get(position).getTotalVideos());
                holder.subs.setText(relatedList.get(position).getSubscriptions());
                holder.videolikes.setText(relatedList.get(position).getNumOfBlesses());
                holder.videoviews.setText(relatedList.get(position).getNumOfComments());
                holder.thischannelpro.setText(relatedList.get(position).getChannelProfile());
                holder.channelname.setTag(relatedList.get(position).getChannelCover());
                holder.channelid.setTag(relatedList.get(position).getTotalVideos());
                String issubscribed = relatedList.get(position).getSubscribed();
                if (issubscribed.equals("1")) {
                    holder.postdate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
                // loading video poster using Glide library
                Glide.with(mContext).load(relatedList.get(position).getPoster()).transform(new RoundedCorners(12)).into(holder.poster);
                Glide.with(mContext).load(relatedList.get(position).getChannelProfile()).apply(RequestOptions.circleCropTransform()).into(holder.channelprofile);

                holder.overflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(holder.overflow);
                    }
                });
        }
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

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_add_favourite) {
                Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.hidepost) {
                Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    }

    @Override
    public long getItemId(int position) {
        Object recyclerViewItem = relatedList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return position;
        }
        return relatedList.get(position).getId();
    }
    @Override
    public int getItemCount() {
        if(relatedList != null) {
            return relatedList.size();
        }
        else {
            return 1000;
        }
    }
    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = relatedList.get(position);
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
