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

public class AllWatchedAdapter extends RecyclerView.Adapter<AllWatchedAdapter.MyViewHolder> {

    private Context mContext;
    private List<AllWatched> AllWatchedList;
    private LayoutInflater inflater;
    private static OnRecyclerViewItemClickListener mListener;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemAllWatchedClicked(String[] videoinfos);}
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
    public AllWatchedAdapter(Context mContext, List AllWatchedList) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.AllWatchedList =AllWatchedList;
    }
    @Override @NonNull
    public AllWatchedAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.allwatched_card, parent, false);
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
                mListener.onItemAllWatchedClicked(videoinfo);
                YoYo.with(Techniques.RubberBand).delay(10).duration(600).playOn(holder.itemView);

            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull final AllWatchedAdapter.MyViewHolder holder, final int position) {
        holder.channelname.setText(AllWatchedList.get(position).getChannelName());
        holder.channelid.setText(AllWatchedList.get(position).getChannelId());
        holder.videodesc.setText(AllWatchedList.get(position).getVideoDesc());
        holder.postdate.setText(AllWatchedList.get(position).getTotalVideos());
        holder.thischannelownerid.setText(AllWatchedList.get(position).getChannelownerid());
        holder.thisvideoid.setText(AllWatchedList.get(position).getVideoId());
        holder.thisvideourl.setText(AllWatchedList.get(position).getVideourl());
        holder.channelowner.setText(AllWatchedList.get(position).getPosterName());
        holder.totalvideos.setText(AllWatchedList.get(position).getTotalVideos());

        // loading video poster using Glide library
        Glide.with(mContext).load(AllWatchedList.get(position).getPoster()).transform(new FitCenter(), new RoundedCorners(10)).into(holder.poster);
        Glide.with(mContext).load(AllWatchedList.get(position).getChannelProfile()).apply(RequestOptions.circleCropTransform()).into(holder.channelprofile);

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
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.hidepost:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if(AllWatchedList != null) {
            return AllWatchedList.size();
        }
        else {
            return 1000;
        }
    }

}
