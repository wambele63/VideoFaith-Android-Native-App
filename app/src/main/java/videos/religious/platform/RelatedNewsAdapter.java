package videos.religious.platform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

public class RelatedNewsAdapter extends RecyclerView.Adapter<RelatedNewsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<RelatedNews> relatedNews;
    private LayoutInflater inflater;
    private static OnRecyclerViewItemClickListener mListener;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemRelatedClicked(String[] newinfos);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fullcardtext,fullcardtime,fullcardheader,fullcardid,fullcardimageurl;
        ImageView fullcardimage;
        private View iview;
        private MyViewHolder(View view) {
            super(view);
            iview = view;
            fullcardheader = view.findViewById(R.id.fullcardheader);
            fullcardid = view.findViewById(R.id.fullcardid);
            fullcardtext = view.findViewById(R.id.fullcardtext);
            fullcardtime = view.findViewById(R.id.fullcardtime);
            fullcardimage = view.findViewById(R.id.fullcardimage);
            fullcardimageurl = view.findViewById(R.id.fullcardimageurl);

        }
        public View getView() {
            return iview;
        }

    }
    public RelatedNewsAdapter(Context mContext, ArrayList<RelatedNews> relatedNews) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.relatedNews = relatedNews;
    }
    private  View itemView;
    @Override @NonNull
    public RelatedNewsAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
         itemView = inflater.inflate(R.layout.fullnew_card, parent, false);
        final MyViewHolder holder = new MyViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.RubberBand).duration(600).playOn(holder.itemView);
                String fulltext = holder.fullcardtext.getText().toString();
                String fullimage = holder.fullcardimageurl.getTag().toString();
                String fullid = holder.fullcardid.getText().toString();
                String fullheader = holder.fullcardheader.getText().toString();
                String fulltime = holder.fullcardtime.getText().toString();
                String fulllocation = holder.fullcardheader.getTag().toString();
                Toast.makeText(mContext, ""+fullheader, Toast.LENGTH_LONG).show();
                String[] newinfo =  new String[]{fullimage,fulltime,fullheader,fulllocation,fulltext};
                mListener.onItemRelatedClicked(newinfo);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull final RelatedNewsAdapter.MyViewHolder holder, final int position) {
        holder.fullcardheader.setText(relatedNews.get(position).getCardHeader());
        holder.fullcardid.setText(relatedNews.get(position).getCardID());
        holder.fullcardtext.setText(relatedNews.get(position).getCardText());
        holder.fullcardtime.setText(relatedNews.get(position).getCardTime());
        holder.fullcardimageurl.setText(relatedNews.get(position).getCardImageUrl());
        holder.fullcardimageurl.setTag(relatedNews.get(position).getFullImage());
        holder.fullcardheader.setTag(relatedNews.get(position).getCardLocation());
        // loading video poster using Glide library
        Glide.with(holder.fullcardimage.getContext()).load(relatedNews.get(position).getCardImageUrl()).into(holder.fullcardimage);
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */

    /**
     * Click listener for popup menu items
     */

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        if(relatedNews != null) {
            return relatedNews.size();
        }
        else {
            return 5;
        }
    }

}
