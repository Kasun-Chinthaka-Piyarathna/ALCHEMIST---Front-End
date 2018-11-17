package alchemist.fit.uom.alchemists.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.util.List;

import alchemist.fit.uom.alchemists.models.FileUploadInfo;
import alchemist.fit.uom.alchemists.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<FileUploadInfo> mainFileUploadInfoList;




    // Current playback position (in milliseconds).
    private int mCurrentPosition = 0;

    // Tag for the instance state bundle.
    private static final String PLAYBACK_TIME = "play_time";
    private MediaController controller;

    public RecyclerViewAdapter(Context context, List<FileUploadInfo> TempList) {
        this.mainFileUploadInfoList = TempList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FileUploadInfo UploadInfo = mainFileUploadInfoList.get(position);
        holder.imageNameTextView.setText(UploadInfo.getFileName());
        if(UploadInfo.getFileType().equals("image")) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            //Loading image from Glide library.
            Glide.with(context).load(UploadInfo.getFileURL()).into(holder.imageView);
        }else {
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(Uri.parse(UploadInfo.getFileURL()));
//            controller= new MediaController(context);
//            controller.setMediaPlayer(holder.videoView);
//            controller.setAnchorView(holder.videoView);
//            holder.videoView.setMediaController(controller);
//            initializePlayer(holder,Uri.parse(UploadInfo.getFileURL()));
        }
    }

    @Override
    public int getItemCount() {
        return mainFileUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public VideoView videoView;
        public TextView imageNameTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclerview_items_image_view);
            videoView = itemView.findViewById(R.id.recyclerview_items_video_view);
            imageNameTextView = itemView.findViewById(R.id.ImageNameTextView);
        }
    }
}