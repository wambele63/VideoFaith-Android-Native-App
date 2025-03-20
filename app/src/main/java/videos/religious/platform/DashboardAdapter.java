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
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Dash> DashList;
    private LayoutInflater inflater;
    private static OnRecyclerViewItemClickListener mListener;
    private ActivityCompat activity;
    private View globalholderview;
    private View itemView;
    private View animated;
    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(int wheretogo);
        void onItemShareClicked(String image, String videodesctext, String newheader);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Dashtext;
        ImageView nposter;

        private MyViewHolder(View view) {
            super(view);
            globalholderview = view;
            nposter= view.findViewById(R.id.noposter);
            Dashtext= view.findViewById(R.id.videodesc);
        }
    }
    public DashboardAdapter(Context mContext, ArrayList<Dash> DashList) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.DashList = DashList;
    }
    @Override @NonNull
    public DashboardAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
         itemView = inflater.inflate(R.layout.dash_card, parent, false);
       final MyViewHolder holder = new MyViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.RubberBand).duration(200).playOn(holder.itemView);
           mListener.onItemClicked(holder.getAdapterPosition());
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull final DashboardAdapter.MyViewHolder holder, final int position) {
        holder.Dashtext.setText(DashList.get(position).getContentId());
        final int posterimg = DashList.get(position).getDashImage();
        Glide.with(mContext).load(posterimg).into(holder.nposter);
        if(position == 0){
            Toast.makeText(mContext, ""+Constants.myChannelName, Toast.LENGTH_SHORT).show();
            if(!Constants.myChannelName.equals("")){
                holder.Dashtext.setText(Constants.myChannelName);
                Glide.with(mContext).load(Constants.myChannelProfile).circleCrop().into(holder.nposter);
            }
        }
        if(position == 2){
            if(!Constants.myFullname.equals("")){
                holder.Dashtext.setText("Edit Account");
            }
        }
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
                    return true;
            }
                else if(menuItem.getItemId() == R.id.hidepost) {
                Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                return true;
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
        if(DashList != null) {
            return DashList.size();
        }
        else {
            return 1000;
        }
    }

}
