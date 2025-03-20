package videos.religious.platform;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;

public class downloadAdapter extends RecyclerView.Adapter<downloadAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Download> DownloadList;
    static SimpleExoPlayer exoPlayer;
    private Bitmap bMap;
    private String videopath;
    private LayoutInflater inflater;
    private static OnRecyclerViewItemClickListener mListener;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemDownloadClicked(String[] videoinfos);
        void onItemPlayCliked(String playurl);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView videoname;
        TextView play;
        ImageView video;
        ImageButton videoOptions;
        private MyViewHolder(View view) {
            super(view);
            video = view.findViewById(R.id.video_play);
            videoOptions = view.findViewById(R.id.more);
            videoname = view.findViewById(R.id.videoinfo);
            play = view.findViewById(R.id.url);
        }
    }
    public downloadAdapter(Context mContext, ArrayList videoList) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.DownloadList = videoList;
    }
    String videopathdelete;
    private FrameLayout downloadcard;
    @Override @NonNull
    public downloadAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        final View itemView = inflater.inflate(R.layout.download_card, parent, false);
        final MyViewHolder holder = new MyViewHolder(itemView);
        downloadcard = (FrameLayout) holder.itemView;
        holder.video.setOnClickListener(play->{
            videopath = holder.play.getText().toString();
            mListener.onItemPlayCliked(videopath);
        });
        holder.videoOptions.setOnClickListener(view -> {
            videopathdelete = holder.play.getText().toString();
            showPopupMenu(holder.videoOptions);
        });
        return holder;
    }
    private void play(PlayerView video, String videopath) {
        //do something
        exoPlayer = setupPlayer(video, videopath);
        exoPlayer.setPlayWhenReady(true);
        video.setUseController(true);
        video.setPlayer(exoPlayer);
    }

    private SimpleExoPlayer setupPlayer(PlayerView videoView, String videouri) {
        videoView.setUseController(true);
        exoPlayer = newSimpleExoPlayer();
        videoView.setPlayer(exoPlayer);

        MediaSource videoSource = newVideoSource(videouri);
        exoPlayer.prepare(videoSource);
        videoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        return exoPlayer;
    }

    private SimpleExoPlayer newSimpleExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        return new SimpleExoPlayer.Builder(mContext)
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .build();
    }
    private MediaSource newVideoSource(String url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(mContext).build();
        String userAgent = Util.getUserAgent(mContext, "VideoFaith");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, userAgent, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    }
    @Override
    public void onBindViewHolder(@NonNull downloadAdapter.MyViewHolder holder,int position) {
        Glide.with(holder.video.getContext()).load(DownloadList.get(position).getVideourl()).into(holder.video);
        holder.videoname.setText(DownloadList.get(position).getVideoname());
        holder.play.setText(DownloadList.get(position).getVideourl());
    }
    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_downloads, popup.getMenu());
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
                case R.id.deletevideo:
                    try {
                        File file = new File(videopathdelete);
                        file.delete();
                        if (file.exists()) {
                            file.getCanonicalFile().delete();
                            if (file.exists()) {
                                mContext.getApplicationContext().deleteFile(file.getName());
                            } else {
                                YoYo.with(Techniques.FadeOutLeft).duration(300).onEnd(onEnd -> {
                                    downloadcard.setVisibility(View.GONE);
                                }).playOn(downloadcard);
                            }
                        } else {
                            YoYo.with(Techniques.FadeOutLeft).duration(300).onEnd(onEnd -> {
                                downloadcard.setVisibility(View.GONE);
                            }).playOn(downloadcard);
                        }
                            YoYo.with(Techniques.FadeOutLeft).duration(300).onEnd(onEnd -> {
                                downloadcard.setVisibility(View.GONE);
                            }).playOn(downloadcard);
                        return true;
                    }catch (Exception n){

                    }
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
        if(DownloadList != null) {
            return DownloadList.size();
        }
        else {
            return 1;
        }
    }

}
