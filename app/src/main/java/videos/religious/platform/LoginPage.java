package videos.religious.platform;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.objectbox.Box;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginPage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private RecyclerView recyclernewsrelated;
    private loginAdapter rnewsadapter;
    private Box<User> userbox = ObjectBox.getBoxStore().boxFor(User.class);
    private List<User> currentUsers = userbox.getAll();
    private View view;
    private Context mcontext;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    private LoginPage.OnFragmentInteractionListener mListener;

    private LoginPage() {
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
    public static LoginPage newInstance(String param1, String param2, String param3) {
        LoginPage fragment = new LoginPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcontext =   this.getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new, container, false);
        TextView videodesc = view.findViewById(R.id.fullnewtext);
        recyclernewsrelated = view.findViewById(R.id.recycler_view_news_related);
        TextView videodescheader = view.findViewById(R.id.fullnewtextheader);
        ImageView newimage = view.findViewById(R.id.fullnewimage);
        Glide.with(mcontext).load(mParam1).into(newimage);
        videodescheader.setText(mParam3);
        videodesc.setText(mParam2);
        FetchedRelatedRecyclerNews();
        return view;
    }
    private void fetchlogin(String header){
        AndroidNetworking.post("/news.php")
                .addBodyParameter("relatedto", header)
                .setTag("fetchrelated")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                         }
                    @Override
                    public void onError(ANError error) {
                    }
                });
    }
    private void FetchedRelatedRecyclerNews() {
        rnewsadapter = new loginAdapter(mcontext, currentUsers);
        recyclernewsrelated.setItemViewCacheSize(4);
        recyclernewsrelated.setHasFixedSize(true);
        recyclernewsrelated.setItemAnimator(null);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mcontext,RecyclerView.HORIZONTAL, false);
        recyclernewsrelated.setLayoutManager(mLayoutManager);
        SlideInBottomAnimationAdapter alphaAdapter = new SlideInBottomAnimationAdapter(rnewsadapter);
        SlideInUpAnimator animator = new SlideInUpAnimator();
        recyclernewsrelated.setItemAnimator(animator);
        recyclernewsrelated.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
        rnewsadapter.setOnRecyclerViewItemClickListener(new loginAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemRelatedClicked(String[] newinfo) {

            }
        });
    }
    private void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private List<User> preparelogin(String response) {
                try {
                    JSONArray dataArray = new JSONArray(response);
                    for (int i = 0; i < dataArray.length(); i++) {
                        try {
                            User newpost = new User();
                            JSONObject posts = dataArray.getJSONObject(i);
                            newpost.setFullName(posts.getString("header"));
                            newpost.setChannelName(posts.getString("header"));
                            newpost.setPassword(posts.getString("header"));
                            newpost.setImageUrl(posts.getString("read"));
                            userbox.put(newpost);
                            rnewsadapter.notifyItemChanged(i);
                        } catch (JSONException jsonex) {
                            Log.d("JsonError","error json");
                        }
                        }
                } catch (JSONException jsonerror) {
                    jsonerror.printStackTrace();
                }
                return userbox.getAll();
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
}
