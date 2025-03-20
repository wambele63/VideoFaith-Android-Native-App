package videos.religious.platform;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link New.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link New#newInstance} factory method to
 * create an instance of this fragment.
 */
public class New extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView recyclernewsrelated;
    private ArrayList<RelatedNews> relatedNews = new ArrayList<>();
    private RelatedNewsAdapter rnewsadapter;
    private View view;
    private Context mcontext;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private FirebaseFirestore db;
    private String mParam5;

    private New.OnFragmentInteractionListener mListener;

    private New() {
        // Required empty private constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3 Parameter 2.
     * @return A new instance of fragment New.
     */
    // TODO: Rename and change types and number of parameters
    public static New newInstance(String param1, String param2,String param3,String param4,String param5) {
        New fragment = new New();
        Bundle args = new Bundle();

        args.putString("param1", param1);
        args.putString("param2", param2);
        args.putString("param3", param3);
        args.putString("param4",param4);
        args.putString("param5",param5);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mcontext =   this.getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
            mParam2 = getArguments().getString("param2");
            mParam3 = getArguments().getString("param3");
            mParam4 = getArguments().getString("param4");
            mParam5 = getArguments().getString("param5");
        }
        fetchRelatedNews(mParam3);
    }

    private FrameLayout adContainerView;
    private AdView adView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new, container, false);
        TextView videodesc = view.findViewById(R.id.fullnewtext);
        recyclernewsrelated = view.findViewById(R.id.recycler_view_news_related);
        TextView videodescheader = view.findViewById(R.id.fullnewtextheader);
        TextView timeone = view.findViewById(R.id.fulltime);
        TextView locationarea = view.findViewById(R.id.fullnewlocation);

        ImageView newimage = view.findViewById(R.id.fullnewimage);
        Glide.with(mcontext).load(mParam1).into(newimage);
        videodescheader.setText(mParam3);
        videodesc.setText(mParam2);
        timeone.setText(mParam4);
        locationarea.setText(mParam5);
        adContainerView = view.findViewById(R.id.ad_view_container);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this.getActivity());
        adView.setAdUnitId(getString(R.string.NewsBannerAd_unit_id));
        adContainerView.addView(adView);
        if(!Constants.PRO_USER){
        loadBanner();
        }
        return view;
    }
    private void fetchRelatedNews(String header){
        try {
            db.collection("news").whereEqualTo("religion",Constants.myReligion).orderBy("timestamp", Query.Direction.DESCENDING).limit(8)
                    .get()
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot newsdoc : task.getResult()) {
                                RelatedNews newpost = new RelatedNews();
                                String head = newsdoc.get("head") + "".replace("[", "")
                                        .replace("]", "").replace(",", "");
                                newpost.setCardHeader(head);
                                newpost.setChannelProfile(newsdoc.get("read") + "");
                                newpost.setCardImageUrl(newsdoc.get("imagesmall") + "");
                                newpost.setCardID(newsdoc.getId());
                                newpost.setCardLocation(newsdoc.get("location") + "");
                                newpost.setCardText(newsdoc.get("body") + "");
                                newpost.setFullImage(newsdoc.get("image") + "");
                                String postdate = newsdoc.get("timestamp") + "";
                                String daysagodate = "Hot";
                                long daysago = (System.currentTimeMillis() / (24 * 60 * 60 * 1000)) - Long.parseLong(postdate) / (24 * 60 * 60 * 1000);
                                if (daysago > 1 && daysago <= 7) {
                                    daysagodate = daysago + " days ago";
                                }
                                if (daysago > 7 && daysago <= 30) {
                                    daysagodate = (int) daysago / 7 + " weeks ago";
                                    if ((int) daysago / 7 < 2) {
                                        daysagodate = "Last Week";
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
                                newpost.setCardTime(daysagodate);
                                relatedNews.add(newpost);
                            }
                            attachRelated();
                        }
                    });
        } catch (Exception f){

        }
    }
    private void attachRelated() {
        rnewsadapter = new RelatedNewsAdapter(mcontext, relatedNews);
        recyclernewsrelated.setItemViewCacheSize(20);
        recyclernewsrelated.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mcontext,RecyclerView.HORIZONTAL, false);
        recyclernewsrelated.setLayoutManager(mLayoutManager);
        SlideInBottomAnimationAdapter alphaAdapter = new SlideInBottomAnimationAdapter(rnewsadapter);
        SlideInUpAnimator animator = new SlideInUpAnimator();
        recyclernewsrelated.setItemAnimator(animator);
        recyclernewsrelated.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
        rnewsadapter.setOnRecyclerViewItemClickListener(new RelatedNewsAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemRelatedClicked(String[] newinfo) {
                ImageView fullimage = view.findViewById(R.id.fullnewimage);
                TextView fulltime = view.findViewById(R.id.fulltime);
                TextView fulltext = view.findViewById(R.id.fullnewtext);
                TextView fullheader = view.findViewById(R.id.fullnewtextheader);
                TextView fulllocation = view.findViewById(R.id.fullnewlocation);
                String image = newinfo[0];
                String time = newinfo[1];
                String header = newinfo[2];
                String location = newinfo[3];
                String text = newinfo[4];
                fulltime.setText(time);
                fullheader.setText(header);
                fulltext.setText(text);
                fulllocation.setText(location);
                Glide.with(fullimage).load(image).into(fullimage);
            }
        });
    }
    private void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        return AdSize.getCurrentOrientationBannerAdSizeWithWidth(getActivity(),adWidth);
    }
}
