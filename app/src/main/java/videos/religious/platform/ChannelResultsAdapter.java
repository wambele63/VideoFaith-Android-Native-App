package videos.religious.platform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.emoji.widget.EmojiTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

public class ChannelResultsAdapter extends RecyclerView.Adapter<ChannelResultsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ChannelResults> ResultsList;
    private LayoutInflater inflater;
    private static OnRecyclerViewItemClickListener mListener;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemResultsClicked(String[] videoinfos);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contentId,contentpictureurl,contentdesc,contenttag;
        EmojiTextView contenthead;
        ImageView contentpicture;
        private MyViewHolder(View view) {
            super(view);
            contenthead = view.findViewById(R.id.contenthead);
            contentId = view.findViewById(R.id.contentid);
            contenttag = view.findViewById(R.id.contenttag);
            contentpictureurl = view.findViewById(R.id.contentpictureurl);
            contentdesc = view.findViewById(R.id.contentdesc);
            contentpicture =view.findViewById(R.id.poster);
        }
    }
    public ChannelResultsAdapter(Context mContext, ArrayList<ChannelResults> resultsList) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.ResultsList = resultsList;
    }
    @Override @NonNull
    public ChannelResultsAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.result_card, parent, false);
        final MyViewHolder holder = new MyViewHolder(itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(600).playOn(holder.itemView);
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorLightAccent));
                // send the text to the listener, i.e Activity.
                String contenturl = holder.contentpictureurl.getText().toString();
                String contenthead = holder.contenthead.getText().toString();
                String contentid = holder.contentId.getText().toString();
                String contentdesc = holder.contentdesc.getText().toString();
                String[] contents = contentdesc.split(" ");
                contentdesc = contents[0];
                String contenttag = holder.contenttag.getText().toString();
                String url = holder.contentpictureurl.getTag().toString();
                String[] contentinfo = {contenttag,contentdesc,contenthead,contentid,contenturl,url};
                mListener.onItemResultsClicked(contentinfo);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull final ChannelResultsAdapter.MyViewHolder holder, final int position) {
        holder.contentId.setText(ResultsList.get(position).getContentId());
        holder.contentpictureurl.setText(ResultsList.get(position).getContentPicture());
        holder.contenthead.setText(ResultsList.get(position).getContentHeader());
        holder.contenttag.setText(ResultsList.get(position).getTag());
        holder.contentpictureurl.setTag(ResultsList.get(position).getContentUrl());
        // loading video poster using Glide library
        if(ResultsList.get(position).getTag().equals("video")){
            holder.contentdesc.setText(ResultsList.get(position).getContentDesc() + " Views");
        }
        if(ResultsList.get(position).getTag().equals("new")){
            holder.contentdesc.setText(ResultsList.get(position).getContentDesc() + " Reads");
        }
        if(ResultsList.get(position).getTag().equals("channel")){
            holder.contentdesc.setText(ResultsList.get(position).getContentDesc() + " Subscribers");
        }
        Glide.with(mContext).load( ResultsList.get(position).getContentPicture()).circleCrop().into(holder.contentpicture);
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
            }
               else if(menuItem.getItemId() == R.id.hidepost) {
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
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
        if(ResultsList != null) {
            return ResultsList.size();
        }
        else {
            return 0;
        }
    }

}
