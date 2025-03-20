package videos.religious.platform;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Downloads.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Downloads#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Downloads extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private downloadAdapter adapter;
    private SimpleExoPlayer exoPlayer;
    ArrayList<Download> myVideoList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Downloads() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Downloads.
     */
    // TODO: Rename and change types and number of parameters
    public static Downloads newInstance(String param1, String param2) {
        Downloads fragment = new Downloads();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private FrameLayout adContainerView;
    private AdView adView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.downloads, container, false);
        RecyclerView recyclerView = inflated.findViewById(R.id.list_downloads);
        myVideoList = new ArrayList<>();
        videoCont = inflated.findViewById(R.id.playdownload);
        videoView = inflated.findViewById(R.id.video_view);
        ImageButton closevideo = inflated.findViewById(R.id.closeplay);
        closevideo.setOnClickListener(v->{
            exoPlayer.release();
            exoPlayer = null;
            lastvideouri = "";
            YoYo.with(Techniques.SlideOutUp).duration(600).onEnd(onEnd->{
                videoCont.setVisibility(View.GONE);
            }).playOn(videoCont);
        });
        adContainerView = inflated.findViewById(R.id.ad_viewtv_container);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(getContext());
        adView.setAdUnitId(getString(R.string.NewsBannerAd_unit_id));
        adContainerView.addView(adView);
        loadBanner();
        adapter = new downloadAdapter(this.getContext(), myVideoList);
        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/VideoFaith/Videos/");
            String[] filenames = directory.list();
            for (int i = 0; i < filenames.length; i++) {
                if(filenames[i].contains(".mp4")) {
                    String[] full_i = filenames[i].split("_");
                    String videoname = full_i[0];
                    Download videopost = new Download();
                    videopost.setVideoname(videoname);
                    videopost.setVideourl(directory + "/" + filenames[i]);
                    myVideoList.add(videopost);
                    adapter = new downloadAdapter(this.getContext(), myVideoList);
                    adapter.notifyItemInserted(i);
                }
            }
        } catch (NullPointerException jsonerr) {
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(3);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        SlideInLeftAnimationAdapter alphaAdapter = new SlideInLeftAnimationAdapter(adapter);
        alphaAdapter.setDuration(1000);
        alphaAdapter.setInterpolator(new FastOutLinearInInterpolator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        final LinearLayoutManager llm = (LinearLayoutManager) manager;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (null != downloadAdapter.exoPlayer) {
                    if (downloadAdapter.exoPlayer.isPlaying()) {
                        downloadAdapter.exoPlayer.setPlayWhenReady(false);
                    }
                    super.onScrolled(recyclerView, dx, dy);
                }
            }
        });
        adapter.setOnRecyclerViewItemClickListener(new downloadAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemDownloadClicked(String[] videoinfos) {

            }

            @Override
            public void onItemPlayCliked(String playurl) {
                FrameLayout videoViewCont = inflated.findViewById(R.id.video_view_cont);
                double returnheight = getActivity().getWindow().getDecorView().getWidth()/1.778;
                RelativeLayout.LayoutParams playBoxParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)returnheight);
                videoViewCont.setLayoutParams(playBoxParams);
                if(videoCont.getVisibility() == View.GONE){
                    YoYo.with(Techniques.SlideInDown).duration(300).onStart(onStart->{
                        videoCont.setVisibility(View.VISIBLE);
                    }).playOn(videoCont);
                }
                if(playurl.equals(lastvideouri) && null != exoPlayer){
                    exoPlayer.setPlayWhenReady(true);
                    Toast.makeText(getContext(), "Video Playing", Toast.LENGTH_SHORT).show();
                    return;
                }
                setupPlayer(playurl);
            }
        });
        return inflated;
    }
    private ConstraintLayout videoCont;
    @Override
    public void onDestroy(){
        if(null != exoPlayer){
                lastvideouri="";
                exoPlayer.release();
                exoPlayer = null;
        }
            super.onDestroy();
    }
    private String lastvideouri="";
    private PlayerView videoView;
    private void setupPlayer( String videouri) {
        if(null == exoPlayer){
            exoPlayer = newSimpleExoPlayer();
        }
        lastvideouri = videouri;
        videoView.setUseController(true);
            MediaSource videoSource = newVideoSource(videouri);
            exoPlayer.prepare(videoSource);
            exoPlayer.setPlayWhenReady(true);
            videoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        videoView.setPlayer(exoPlayer);
    }
    private SimpleExoPlayer newSimpleExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        return new SimpleExoPlayer.Builder(getContext())
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .build();
    }
    private MediaSource newVideoSource(String url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(getContext()).build();
        String userAgent = Util.getUserAgent(getContext(), "VideoFaith");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), userAgent, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void loadBanner() {
        AdRequest adRequest =
                new AdRequest.Builder()
                        .build();
        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }
    private AdSize getAdSize() {
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
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        if(null != exoPlayer)
        {
            if(exoPlayer.isPlaying())
            exoPlayer.setPlayWhenReady(false);
        }
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
