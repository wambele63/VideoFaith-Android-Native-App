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
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.MyViewHolder> {

    private Context mContext;
    private List<Notify> NotifyList;
    private LayoutInflater inflater;
    private static OnRecyclerViewItemClickListener mListener;
    private ActivityCompat activity;
    private View globalholderview;
    private View animated;
    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(String[] videoinfos);
        void onItemAmenClicked(String videoid);
        void onItemDownloadClicked(String downloadvideourl);
        void onItemShareClicked(String image, String videodesctext,String newheader);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView notifyimage,notifytype,notifytext, totalvideos,contentid,contenturl,postdate,contentv,contentl,contentd,notifyid;
        ImageView nposter,channelprofile,overflow;
        LinearLayoutCompat cardview;

        private MyViewHolder(View view) {
            super(view);
            globalholderview = view;
            contentid = view.findViewById(R.id.thischannelownerid);
            contentd = view.findViewById(R.id.videodownloads);
            contentv = view.findViewById(R.id.videoviews);
            notifyid = view.findViewById(R.id.thisvideoid);
            contenturl = view.findViewById(R.id.thisvideourl);
            contentl = view.findViewById(R.id.videolikes);
            postdate = view.findViewById(R.id.timenotify);
            notifytype = view.findViewById(R.id.channelid);
            nposter= view.findViewById(R.id.noposter);
            notifytext= view.findViewById(R.id.videodesc);
            notifyimage = view.findViewById(R.id.notifyimage);
            cardview = view.findViewById(R.id.card_view);
        }
    }
    public NotifyAdapter(Context mContext, List<Notify> NotifyList) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.NotifyList = NotifyList;
    }
    @Override @NonNull
    public NotifyAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
         View itemView = inflater.inflate(R.layout.notify_card, parent, false);
       final MyViewHolder holder = new MyViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.RubberBand).duration(300).playOn(holder.itemView);
                holder.notifytext.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDiluted));
                String type = holder.notifytype.getText().toString();
                String contentur = holder.contenturl.getText().toString();
                String contentids = holder.contentid.getText().toString();
                String contentls = holder.contentl.getText().toString();
                String contentviews = holder.contentv.getText().toString();
                String contentdesc = holder.notifytext.getText().toString();
                String contentdwn = holder.contentd.getText().toString();
                String[] videoinfo = {contentur,contentids,contentls,contentviews,contentdesc,contentdwn,type};
                mListener.onItemClicked(videoinfo);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull final NotifyAdapter.MyViewHolder holder, final int position) {
        holder.notifyid.setText(NotifyList.get(position).getNotificationId());
        holder.postdate.setText(NotifyList.get(position).getNotifyDate());
        holder.contentid.setText(NotifyList.get(position).getContentId());
        holder.notifytype.setText(NotifyList.get(position).getNotifyType());
        holder.notifyimage.setText(NotifyList.get(position).getNotifyImage());
        holder.contenturl.setText(NotifyList.get(position).getContenturl());
        holder.notifytext.setText(NotifyList.get(position).getNotification());
        holder.contentl.setText(NotifyList.get(position).getContentl());
        holder.contentv.setText(NotifyList.get(position).getContentv());
        holder.contentd.setText(NotifyList.get(position).getContentd());
        // loading video nposterusing Glide library
        final String posterimg = NotifyList.get(position).getNotifyImage();
        Glide.with(holder.nposter).load(posterimg).circleCrop().into(holder.nposter);
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
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_add_favourite) {
                Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.hidepost) {
                Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        if(NotifyList != null) {
            return NotifyList.size();
        }
        else {
            return 1000;
        }
    }

}
