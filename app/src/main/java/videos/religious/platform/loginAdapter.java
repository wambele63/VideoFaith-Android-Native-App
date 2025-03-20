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

import java.util.List;

public class loginAdapter extends RecyclerView.Adapter<loginAdapter.MyViewHolder> {

    private Context mContext;
    private List<User> logins;
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
    public loginAdapter(Context mContext, List<User> logins) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.logins = logins;
    }
    private  View itemView;
    @Override @NonNull
    public loginAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
         itemView = inflater.inflate(R.layout.fullnew_card, parent, false);
        final MyViewHolder holder = new MyViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.RubberBand).duration(600).playOn(holder.itemView);
                String fulltext = holder.fullcardtext.getText().toString();
                String fullimage = holder.fullcardimageurl.getText().toString();
                String fullid = holder.fullcardid.getText().toString();
                String fullheader = holder.fullcardheader.getText().toString();
                String fulltime = holder.fullcardtime.getText().toString();
                String[] newinfo =  new String[]{fullimage,fulltime,fullheader,"Asia",fulltext};
                mListener.onItemRelatedClicked(newinfo);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull final loginAdapter.MyViewHolder holder, final int position) {
        holder.fullcardheader.setText(logins.get(position).getChannelName());
        holder.fullcardid.setText(logins.get(position).getAge());
        holder.fullcardtext.setText(logins.get(position).getFullName());
        holder.fullcardtime.setText(logins.get(position).getPassword());
        holder.fullcardimageurl.setText(logins.get(position).getImageUrl());

        Toast.makeText(mContext, logins.get(position).getChannelName(), Toast.LENGTH_SHORT).show();
        // loading video poster using Glide library
        Glide.with(mContext).load(logins.get(position).getImageUrl()).thumbnail(0.2f).into(holder.fullcardimage);
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */

    /**
     * Click listener for popup menu items
     */

    @Override
    public int getItemCount() {
        if(logins != null) {
            return logins.size();
        }
        else {
            return 5;
        }
    }

}
