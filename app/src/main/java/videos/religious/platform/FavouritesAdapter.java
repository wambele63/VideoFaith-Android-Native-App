package videos.religious.platform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Favourites> FavouritesList;
    private LayoutInflater inflater;
    private static OnRecyclerViewItemClickListener mListener;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemFavouritesClicked(String[] videoinfos);}
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView channelname,channelid, totalvideos,channelowner,postdate,videodesc,videoviews,videolikes,thischannelownerid,thisvideoid,thisvideourl;
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
    public FavouritesAdapter(Context mContext, List favouritesList) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.FavouritesList =favouritesList;
    }
    @Override @NonNull
    public FavouritesAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.favourites_card, parent, false);
        final MyViewHolder holder = new MyViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send the text to the listener, i.e Activity.
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorLightAccent));
                String videourl = holder.thisvideourl.getText().toString();
                String videoid = holder.thisvideoid.getText().toString();
                String videolike = holder.videolikes.getText().toString();
                String thisvideoviews = holder.videoviews.getText().toString();
                String thisvideodesc = holder.videodesc.getText().toString();
                String[] videoinfo = {videourl,videoid,videolike,thisvideoviews,thisvideodesc};
                mListener.onItemFavouritesClicked(videoinfo);
                YoYo.with(Techniques.RubberBand).delay(10).duration(600).playOn(holder.itemView);

            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull final FavouritesAdapter.MyViewHolder holder, final int position) {
        holder.channelname.setText(FavouritesList.get(position).getChannelName());
        holder.channelid.setText(FavouritesList.get(position).getChannelId());
        holder.videodesc.setText(FavouritesList.get(position).getVideoDesc());
        holder.postdate.setText(FavouritesList.get(position).getTotalVideos());
        holder.thischannelownerid.setText(FavouritesList.get(position).getChannelownerid());
        holder.thisvideoid.setText(FavouritesList.get(position).getVideoId());
        holder.thisvideourl.setText(FavouritesList.get(position).getVideourl());
        holder.channelowner.setText(FavouritesList.get(position).getPosterName());
        holder.totalvideos.setText(FavouritesList.get(position).getTotalVideos());
        holder.videolikes.setText(FavouritesList.get(position).getNumOfBlesses());

        // loading video poster using Glide library
        Glide.with(mContext).load(FavouritesList.get(position).getPoster()).transform(new FitCenter(), new RoundedCorners(10)).into(holder.poster);
        Glide.with(mContext).load(FavouritesList.get(position).getChannelProfile()).apply(RequestOptions.circleCropTransform()).into(holder.channelprofile);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
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
            if (menuItem.getItemId() == R.id.action_add_favourite) {
                Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                return true;
            }
                else {
                Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        if(FavouritesList != null) {
            return FavouritesList.size();
        }
        else {
            return 1000;
        }
    }

}
