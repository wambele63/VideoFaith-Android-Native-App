package videos.religious.platform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;

public class searchActivity extends AppCompatActivity {
private TextView progressBarr,all,news,channel,video;
private RecyclerView recyclerViewResults;
private ResultsAdapter radapter;
private VideoResultsAdapter videoResultsAdapter;
private NewsResultsAdapter newsResultsAdapter;
private ChannelResultsAdapter channelResultsAdapter;
private FirebaseFirestore db;
private SlideInRightAnimationAdapter alphaAdapter;
private ImageButton searchAction;
private boolean allow = false;
private ArrayList<Results> myAllResults = new ArrayList<>();
private ArrayList<VideoResults> myVideoResults = new ArrayList<>();
private ArrayList<NewsResults> myNewsResults = new ArrayList<>();
private ArrayList<ChannelResults> myChannelsResults = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest)
                .setReplaceAll(true)
                .setEmojiSpanIndicatorEnabled(true)
                .setEmojiSpanIndicatorColor(Color.GREEN)
                .registerInitCallback(new EmojiCompat.InitCallback() {
                    @Override
                    public void onInitialized() {
                        super.onInitialized();
                    }
                    @Override
                    public void onFailed(@Nullable Throwable throwable) {
                        super.onFailed(throwable);
                    }
                });
        EmojiCompat.init(config);
        db = FirebaseFirestore.getInstance();
        progressBarr = findViewById(R.id.progressmoreresults);
        searchAction= findViewById(R.id.searchnow);
        all = findViewById(R.id.all);
        video = findViewById(R.id.video);
        news= findViewById(R.id.news);
        channel = findViewById(R.id.channel);
        recyclerViewResults = findViewById(R.id.searchResults);
        recyclerViewResults.setFocusableInTouchMode(true);
        radapter = new ResultsAdapter(this,myAllResults);
        final EditText editText = findViewById(R.id.keyword);
        channelResultsAdapter = new ChannelResultsAdapter(getApplicationContext(),myChannelsResults);
        newsResultsAdapter = new NewsResultsAdapter(getApplicationContext(),myNewsResults);
        videoResultsAdapter = new VideoResultsAdapter(getApplicationContext(),myVideoResults);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allow=false;
                video.setBackgroundColor(getResources().getColor(R.color.colorLightAccent));
                video.setBackgroundResource(R.drawable.borderbottombutton);
                all.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                news.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                channel.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                recyclerViewResults.setAdapter(null);
                if(myVideoResults.size() != 0) {
                    videoResultsAdapter = new VideoResultsAdapter(getApplicationContext(),myVideoResults);
                    progressBarr.setVisibility(View.GONE);
                    recyclerViewResults.setAdapter(videoResultsAdapter);
                    return;
                }
                progressBarr.setVisibility(View.VISIBLE);
            }
        });
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allow = false;
                news.setBackgroundColor(getResources().getColor(R.color.colorLightAccent));
                news.setBackgroundResource(R.drawable.borderbottombutton);
                video.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                all.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                channel.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                recyclerViewResults.setAdapter(null);
                if(myNewsResults.size() != 0) {
                    newsResultsAdapter = new NewsResultsAdapter(getApplicationContext(),myNewsResults);
                    progressBarr.setVisibility(View.GONE);
                    recyclerViewResults.setAdapter(newsResultsAdapter);
                    return;
                }
                progressBarr.setVisibility(View.VISIBLE);
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setBackgroundColor(getResources().getColor(R.color.colorLightAccent));
                all.setBackgroundResource(R.drawable.borderbottombutton);
                video.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                news.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                channel.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                recyclerViewResults.setAdapter(null);
                if(myAllResults.size() != 0) {
                    radapter = new ResultsAdapter(getApplicationContext(),myAllResults);
                    progressBarr.setVisibility(View.GONE);
                    recyclerViewResults.setAdapter(radapter);
                    return;
                }
                progressBarr.setVisibility(View.VISIBLE);
            }
        });
        channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allow=false;
                channel.setBackgroundColor(getResources().getColor(R.color.colorLightAccent));
                channel.setBackgroundResource(R.drawable.borderbottombutton);
                video.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                news.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                all.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                recyclerViewResults.setAdapter(null);
                if(myChannelsResults.size() != 0) {
                    channelResultsAdapter = new ChannelResultsAdapter(getApplicationContext(), myChannelsResults);
                    progressBarr.setVisibility(View.GONE);
                    recyclerViewResults.setAdapter(channelResultsAdapter);
                    return;
                }
                progressBarr.setVisibility(View.VISIBLE);
            }
        });
        searchAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allow=false;
                myAllResults.clear();
                myChannelsResults.clear();
                myNewsResults.clear();
                myVideoResults.clear();
                String searchterm = editText.getText().toString();
                progressBarr.setVisibility(View.VISIBLE);
                if(searchterm.length() >= 1) {
                    searchAction.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    searchAction.setEnabled(false);
                    String lsearch = searchterm.toLowerCase();
                    Glide.with(getApplicationContext()).asGif().load(R.drawable.searching).sizeMultiplier((float) 0.3).into(searchAction);
                    fetchResultsJSON(lsearch);
                    all.setBackgroundColor(getResources().getColor(R.color.colorLightAccent));
                    video.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    news.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    channel.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    return;
                }
                Toast.makeText(searchActivity.this, "Enter Search Keyword", Toast.LENGTH_LONG).show();
            }
        });
    }
    private ArrayList<Map> allmap = new ArrayList<>();
    private QuerySnapshot videosresult,newresults,channelresults;
    @SuppressLint("StaticFieldLeak")
    private void fetchResultsJSON(final String videoname) {
        try {
            Task<QuerySnapshot> searchvideos,searchnews,searchchannels;
            progressBarr.setVisibility(View.VISIBLE);
            String[] listsearch = videoname.split(" ");
            searchvideos = db.collection("videos")
                    .whereEqualTo("religion",Constants.myReligion)
                    .whereArrayContainsAny("keywords",Arrays.asList(listsearch))
                    .limit(5)
                    .orderBy("time", Query.Direction.DESCENDING)
                    .get();
            searchnews = db.collection("news")
                    .whereEqualTo("religion",Constants.myReligion)
                    .whereArrayContainsAny("keywords",Arrays.asList(listsearch))
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(5)
                    .get();
            searchchannels = db.collection("channels")
                    .whereEqualTo("religion", Constants.myReligion)
                    .whereArrayContainsAny("keywords",Arrays.asList(listsearch))
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(5)
                    .get();
            Tasks.whenAllSuccess(searchvideos,searchnews,searchchannels).addOnSuccessListener(this,objects -> {
                    myAllResults.clear();
                    myChannelsResults.clear();
                    myNewsResults.clear();
                    myVideoResults.clear();
                    searchAction.setEnabled(true);
                    allmap.clear();
                    searchAction.setBackgroundColor(getResources().getColor(R.color.Transparent));
                    Glide.with(searchAction).load(getResources().getDrawable(R.drawable.ic_search_black_24dp)).into(searchAction);
                    newresults = searchnews.getResult();
                    videosresult = searchvideos.getResult();
                    channelresults = searchchannels.getResult();
                    new Handler().postDelayed(() -> {
                            loopthroughnews();
                    },1200);
            }).addOnFailureListener(e->{
            });
        }catch(Exception d){

        }
    }
    private int newsresultcontroller=0;
    private int videosresultcontroller=0;
    private int channelresultcontroller=0;
    private void loopthroughnews(){
        int newsizes = newresults.size();
        if(newsizes > 0) {
            for (int i=0;i <=newsizes;i++) {
                DocumentSnapshot document = newresults.getDocuments().get(i);
                Map<String, Object> post = document.getData();
                Map<String, Object> allmap1 = document.getData();
                String contenthead = post.get("head") + "";
                String contentpicture = post.get("imagesmall") + "";
                String contenttag = "new";
                String contentid = document.getId();
                String contentdesc = post.get("location") + "";
                String contenturl = post.get("image") + "";
                allmap1.put("contenthead", contenthead);
                allmap1.put("contentpicture", contentpicture);
                allmap1.put("contenttag", contenttag);
                allmap1.put("contentid", contentid);
                allmap1.put("contenturl", contenturl);
                allmap1.put("contentdesc", contentdesc);
                allmap.add(allmap.size(),allmap1);
                if(i==newsizes-1){
                    loopthroughvideos();
                    break;
                }
            }
        }
        else {
            loopthroughvideos();
        }
    }
    private void loopthroughvideos() {
        int videosizes = videosresult.size();
        try {
            if (videosizes > 0) {
                for (int i = 0; i <= videosizes - 1; i++) {
                    DocumentSnapshot document = videosresult.getDocuments().get(i);
                    Map<String, Object> post = document.getData();
                    Map<String, Object> allmap2 = document.getData();
                    String contenthead = post.get("videotext") + "";
                    String contentpicture = post.get("postersmall") + "";
                    String contenttag = "video";
                    String contentid = document.getId();
                    String contentdesc = post.get("views") + "";
                    String contenturl = post.get("video") + "";
                    allmap2.put("contenthead", contenthead);
                    allmap2.put("contentdesc", contentdesc);
                    allmap2.put("contentpicture", contentpicture);
                    allmap2.put("contenttag", contenttag);
                    allmap2.put("contentid", contentid);
                    allmap2.put("contenturl", contenturl);
                    allmap.add(allmap.size(),allmap2);
                    if (i == videosizes - 1) {
                        loopthroughchannels();
                        break;
                    }
                }
            } else {
                loopthroughchannels();
            }
        }catch (Exception c){
        }
    }
    private void loopthroughchannels(){
        int channelsizes = channelresults.size();
        if(channelsizes > 0) {
            for (int i=0;i<= channelsizes-1;i++) {
                DocumentSnapshot document = channelresults.getDocuments().get(i);
                Map<String, Object> post = document.getData();
                Map<String, Object> allmap3 = document.getData();
                String contenthead = post.get("channelname") + "";
                String contentpicture = post.get("profile") + "";
                String contenttag = "channel";
                String contentid = document.getId();
                String contentdesc = post.get("videos") + " videos";
                String contenturl = post.get("cover") + "";
                assert allmap3 != null;
                allmap3.put("contenthead", contenthead);
                allmap3.put("contentpicture", contentpicture);
                allmap3.put("contenttag", contenttag);
                allmap3.put("contentid", contentid);
                allmap3.put("contenturl", contenturl);
                allmap3.put("contentdesc", contentdesc);
                allmap.add(allmap.size(),allmap3);
                if(i==channelsizes-1){
                    preparefinalresults();
                }
            }
        } else {
            preparefinalresults();
        }
    }
    private void preparefinalresults(){
        try {
            if (allmap.size() > 0) {
                JSONArray array = new JSONArray(allmap);
            for (int i = 0; i <= array.length() - 1; i++) {
                JSONObject allmap4 = array.getJSONObject(i);
                String contenthead = allmap4.getString("contenthead");
                String contentpicture = allmap4.getString("contentpicture");
                String contenttag = allmap4.getString("contenttag");
                String contentid = allmap4.getString("contentid");
                String contentdesc = allmap4.getString("contentdesc");
                String contenturl = allmap4.getString("contenturl");
                switch (contenttag) {
                    case "video":
                        VideoResults videopost = new VideoResults();
                        videopost.setContentHeader(contenthead);
                        videopost.setContentDesc(contentdesc);
                        videopost.setContentId(contentid);
                        videopost.setTag(contenttag);
                        videopost.setContentUrl(contenturl);
                        videopost.setContentPicture(contentpicture);
                        myVideoResults.add(videopost);
                        break;
                    case "new":
                        NewsResults videopost2 = new NewsResults();
                        videopost2.setContentHeader(contenthead);
                        videopost2.setContentDesc(contentdesc);
                        videopost2.setContentId(contentid);
                        videopost2.setTag(contenttag);
                        videopost2.setContentUrl(contenturl);
                        videopost2.setContentPicture(contentpicture);
                        myNewsResults.add(videopost2);
                        break;
                    case "channel":
                        ChannelResults videopost3 = new ChannelResults();
                        videopost3.setContentHeader(contenthead);
                        videopost3.setContentDesc(contentdesc);
                        videopost3.setContentId(contentid);
                        videopost3.setTag(contenttag);
                        videopost3.setContentPicture(contentpicture);
                        videopost3.setContentUrl(contenturl);
                        myChannelsResults.add(videopost3);
                    default:
                        break;
                }
                Results allresults = new Results();
                allresults.setContentHeader(contenthead);
                allresults.setContentDesc(contentdesc);
                allresults.setContentId(contentid);
                allresults.setTag(contenttag);
                allresults.setContentUrl(contenturl);
                allresults.setContentPicture(contentpicture);
                myAllResults.add(allresults);
                radapter.notifyItemInserted(myAllResults.size());
                if (i == allmap.size() - 1) {
                    attachdata();
                    break;
                }
            }
        }
            }
            catch (Exception f){
                Log.d("mmme",f.getMessage());
            }
        progressBarr.setVisibility(View.GONE);
    }
    private void attachdata() {
        recyclerViewResults.setAdapter(null);
        Collections.shuffle(myAllResults);
        radapter = new ResultsAdapter(this,myAllResults);
        recyclerViewResults.setItemViewCacheSize(20);
        radapter.setOnRecyclerViewItemClickListener(new ResultsAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemResultsClicked(String[] videoinfo) {
                String contenturl = videoinfo[4];
                String contenthead = videoinfo[2];
                String contentid = videoinfo[3];
                String contentdesc = videoinfo[1];
                String contenttag = videoinfo[0];
                String url = videoinfo[5];
                switch (contenttag) {
                    case "video":
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        DialogFragment newFragment = sfragmentdialog.newInstance(url,contentid,contentdesc,contenthead);
                        newFragment.show(ft, "dialog");
                        break;
                    case "new":
                        Intent news = new Intent(getApplicationContext(),NewsActivity.class);
                        news.putExtra("image",contenturl).putExtra("videodesctext",contentdesc)
                                .putExtra("head",contenthead);
                        startActivity(news);
                        break;
                        case "channel":
                            Intent intent = new Intent(searchActivity.this, VisitChannel.class);
                            intent.putExtra("fragid", contentid);
                            intent.putExtra("cname", contenthead);
                            intent.putExtra("ccover", "");
                            intent.putExtra("chanpro", contenturl);
                            intent.putExtra("videos", url);
                            intent.putExtra("subsc", contentdesc);
                            intent.putExtra("issub", "0");
                            startActivity(intent);
                            break;
                            default:
                }
            }
        });
        videoResultsAdapter.setOnRecyclerViewItemClickListener(new VideoResultsAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemResultsClicked(String[] videoinfo) {
                String contenturl = videoinfo[4];
                String contenthead = videoinfo[2];
                String contentid = videoinfo[3];
                String contentdesc = videoinfo[1];
                String contenttag = videoinfo[0];
                String url = videoinfo[5];
                switch (contenttag) {
                    case "video":
                        onPause();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        DialogFragment newFragment = sfragmentdialog.newInstance(url,contentid,contentdesc,contenthead);
                        newFragment.show(ft, "dialog");
                        break;
                    case "new":
                        Intent news = new Intent(getApplicationContext(),NewsActivity.class);
                        news.putExtra("image",contenturl).putExtra("videodesctext",contenthead).putExtra("head",contentdesc);
                        startActivity(news);
                        break;
                        case "channel":
                            Intent intent = new Intent(searchActivity.this, VisitChannel.class);
                            intent.putExtra("fragid", contentid);
                            intent.putExtra("cname", contenthead);
                            intent.putExtra("ccover", url);
                            intent.putExtra("chanpro", contenturl);
                            intent.putExtra("videos", contentdesc);
                            intent.putExtra("subsc", "");
                            intent.putExtra("issub", "0");
                            startActivity(intent);
                            break;
                            default:
                }
            }
        });
        newsResultsAdapter.setOnRecyclerViewItemClickListener(new NewsResultsAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemResultsClicked(String[] videoinfo) {
                String contenturl = videoinfo[4];
                String contenthead = videoinfo[2];
                String contentid = videoinfo[3];
                String contentdesc = videoinfo[1];
                String contenttag = videoinfo[0];
                String url = videoinfo[5];
                switch (contenttag) {
                    case "video":
                        onPause();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        DialogFragment newFragment = sfragmentdialog.newInstance(url,contentid,contentdesc,contenthead);
                        newFragment.show(ft, "dialog");
                        break;
                    case "new":
                        Intent news = new Intent(getApplicationContext(),NewsActivity.class);
                        news.putExtra("image",contenturl).putExtra("videodesctext",contenthead).putExtra("head",contentdesc);
                        startActivity(news);
                        break;
                        case "channel":
                            Intent intent = new Intent(searchActivity.this, VisitChannel.class);
                            intent.putExtra("fragid", contentid);
                            intent.putExtra("cname", contenthead);
                            intent.putExtra("ccover", "");
                            intent.putExtra("chanpro", contenturl);
                            intent.putExtra("videos", url);
                            intent.putExtra("subsc", contentdesc);
                            intent.putExtra("issub", "0");
                            startActivity(intent);
                            break;
                            default:
                }
            }
        });
        channelResultsAdapter.setOnRecyclerViewItemClickListener(new ChannelResultsAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemResultsClicked(String[] videoinfo) {
                String contenturl = videoinfo[4];
                String contenthead = videoinfo[2];
                String contentid = videoinfo[3];
                String contentdesc = videoinfo[1];
                String contenttag = videoinfo[0];
                String url = videoinfo[5];
                switch (contenttag) {
                    case "video":
                        onPause();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        DialogFragment newFragment = sfragmentdialog.newInstance(url,contentid,contentdesc,contenthead);
                        newFragment.show(ft, "dialog");
                        break;
                    case "new":
                        Intent news = new Intent(getApplicationContext(),NewsActivity.class);
                        news.putExtra("image",contenturl).putExtra("videodesctext",contenthead).putExtra("head",contentdesc);
                        startActivity(news);
                        break;
                        case "channel":
                            Intent intent = new Intent(searchActivity.this, VisitChannel.class);
                            intent.putExtra("fragid", contentid);
                            intent.putExtra("cname", contenthead);
                            intent.putExtra("ccover", "");
                            intent.putExtra("chanpro", contenturl);
                            intent.putExtra("videos", url);
                            intent.putExtra("subsc", contentdesc);
                            intent.putExtra("issub", "0");
                            startActivity(intent);
                            break;
                            default:
                }
            }
        });
        recyclerViewResults.setHasFixedSize(true);
        radapter.setHasStableIds(true);
        channelResultsAdapter.setHasStableIds(true);
        newsResultsAdapter.setHasStableIds(true);
        videoResultsAdapter.setHasStableIds(true);
        recyclerViewResults.setAdapter(radapter);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
    }
    @Override
    public void onBackPressed(){
        if(!allow){
            if(myAllResults.size() == 0){
                super.onBackPressed();
                return;
            }
            allow = true;
            recyclerViewResults.setAdapter(null);
            progressBarr.setVisibility(View.GONE);
            recyclerViewResults.setAdapter(radapter);
            all.setBackgroundColor(getResources().getColor(R.color.colorLightAccentAds));
            video.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            news.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            channel.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            return;
        }
        super.onBackPressed();
    }
    @Override
    public void onDestroy()
    {
        db.terminate();
        super.onDestroy();
    }
}