package alchemist.fit.uom.alchemists.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.autoplayvideos.AAH_CustomViewHolder;
import com.allattentionhere.autoplayvideos.AAH_VideosAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import alchemist.fit.uom.alchemists.Constants;
import alchemist.fit.uom.alchemists.R;
import alchemist.fit.uom.alchemists.activities.TabContentActivity;
import alchemist.fit.uom.alchemists.activities.ViewUserPostsActivity;
import alchemist.fit.uom.alchemists.database.AlchemistsDataSource;
import alchemist.fit.uom.alchemists.fragments.NewsFeedFragment;
import alchemist.fit.uom.alchemists.interfaces.InterruptVideoListener;
import alchemist.fit.uom.alchemists.interfaces.OnViewCommentsListener;
import alchemist.fit.uom.alchemists.interfaces.OnViewCommentsListener2;
import alchemist.fit.uom.alchemists.models.MyModel;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;


public class MyVideosAdapter extends AAH_VideosAdapter {

    private final List<MyModel> list;
    private final Picasso picasso;
    private static final int TYPE_VIDEO = 0, TYPE_TEXT = 1;
    private AlchemistsDataSource alchemistsDataSource;
    private static final String MY_PREFS_NAME = "alchemist";
    private String sharedPrefEmailAddress;
    private String sharedPrefBatteryLevel;

    private static OnViewCommentsListener listener = null;
    private static OnViewCommentsListener2 listener2 = null;

    public static void setOnCommentsReceivedListener(TabContentActivity OnViewCommentsListener) {
        listener = OnViewCommentsListener;
    }

    public static void setOnCommentsReceivedListener2(ViewUserPostsActivity OnViewCommentsListener2) {
        listener2 = OnViewCommentsListener2;
    }


    public class MyViewHolder extends AAH_CustomViewHolder{
        final TextView recylerview_items_user_location, recylerview_items_post_text,
                recylerview_items_user_name, recylerview_items_post_time_stamp;
        final ImageView img_vol, img_playback, recylerview_items_profile_url;
        //to mute/un-mute video (optional)
        boolean isMuted;

        final Button moveComments;
        LinearLayout recyclerview_bottom_layout;


        public MyViewHolder(View x) {

            super(x);
            img_vol = ButterKnife.findById(x, R.id.img_vol);
            img_playback = ButterKnife.findById(x, R.id.img_playback);
            recylerview_items_user_location = ButterKnife.findById(x, R.id.recylerview_items_user_location);
            recylerview_items_post_text = ButterKnife.findById(x, R.id.recylerview_items_post_text);
            recylerview_items_user_name = ButterKnife.findById(x, R.id.recylerview_items_user_name);
            recylerview_items_profile_url = ButterKnife.findById(x, R.id.recylerview_items_profile_url);
            recylerview_items_post_time_stamp = ButterKnife.findById(x, R.id.recylerview_items_post_time_stamp);
            recyclerview_bottom_layout = ButterKnife.findById(x,R.id.recyclerview_bottom_layout);
            moveComments = ButterKnife.findById(x, R.id.moveComments);

            alchemistsDataSource = new AlchemistsDataSource(x.getContext());
            alchemistsDataSource.open();
            SharedPreferences prefs = x.getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            sharedPrefEmailAddress = prefs.getString("email_address", "No name defined");
            sharedPrefBatteryLevel = prefs.getString("battery_level", "No name defined");
        }

        //override this method to get callback when video starts to play
        @Override
        public void videoStarted() {
            super.videoStarted();
            isMuted = Constants.setMute;//custom
            img_playback.setImageResource(R.drawable.ic_pause);
            if (isMuted) {
                muteVideo();
                img_vol.setImageResource(R.drawable.ic_mute);
            } else {
                unmuteVideo();
                img_vol.setImageResource(R.drawable.ic_unmute);
            }
        }

        @Override
        public void pauseVideo() {
            super.pauseVideo();
            img_playback.setImageResource(R.drawable.ic_play);
        }
    }

    public class MyTextViewHolder extends AAH_CustomViewHolder {

        public MyTextViewHolder(View x) {
            super(x);
        }
    }

    public MyVideosAdapter(List<MyModel> list_urls, Picasso p) {
        this.list = list_urls;
        this.picasso = p;
    }

    @Override
    public AAH_CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_items, parent, false);
        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(final AAH_CustomViewHolder holder, final int position) {

        ((MyViewHolder) holder).recylerview_items_user_name.setText(list.get(position).getUserName());
        ((MyViewHolder) holder).recylerview_items_user_location.setText(list.get(position).getUserNearestCity());
        ((MyViewHolder) holder).recylerview_items_post_text.setText(list.get(position).getPostText());
        ((MyViewHolder) holder).recylerview_items_post_time_stamp.setText(list.get(position).getPostTimeStamp());


        if(sharedPrefBatteryLevel.equals("critical")){
            ((MyViewHolder) holder).recyclerview_bottom_layout.setVisibility(GONE);
        }

        picasso.load(list.get(position).getUserProfileImageUrl())
                .placeholder(R.drawable.s1).error(R.drawable.s1)
                .into(((MyViewHolder) holder).recylerview_items_profile_url);

        //todo
        if (list.get(position).getPostType().equals("image")) {
            holder.setImageUrl(list.get(position).getPostFileUrl());

            //load image into imageview
            if (list.get(position).getPostFileUrl() != null && !list.get(position).getPostFileUrl().isEmpty()) {
                picasso.load(holder.getImageUrl()).config(Bitmap.Config.RGB_565).into(holder.getAAH_ImageView());
            }
        } else {
            holder.setVideoUrl(list.get(position).getPostFileUrl());
        }


        holder.setLooping(true); //optional - true by default

        //to play pause videos manually (optional)
        ((MyViewHolder) holder).img_playback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isPlaying()) {
                    holder.pauseVideo();
                    holder.setPaused(true);
                } else {
                    holder.playVideo();
                    holder.setPaused(false);
                }
            }
        });

        //to mute/un-mute video (optional)
        ((MyViewHolder) holder).img_vol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MyViewHolder) holder).isMuted) {
                    holder.unmuteVideo();
                    ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_unmute);
                    Constants.setMute = false;
                    String[] retrievedData = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                    if (!retrievedData[0].equals("NO EXIST")) {
                        int unMuteFrequency = 0;
                        if (retrievedData[10] != null) {
                            unMuteFrequency = Integer.parseInt(retrievedData[10]);
                        }
                        unMuteFrequency += 1;

                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                                null, null, null,
                                null, null, null,
                                null,
                                null, null,
                                String.valueOf(unMuteFrequency),null);
                    }

                } else {
                    holder.muteVideo();
                    ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_mute);
                    Constants.setMute = true;

                    String[] retrievedData2 = alchemistsDataSource.getAllDataFromBehaviourDetails(sharedPrefEmailAddress);
                    if (!retrievedData2[0].equals("NO EXIST")) {
                        int MuteFrequency = 0;
                        if (retrievedData2[9] != null) {
                            MuteFrequency = Integer.parseInt(retrievedData2[9]);
                        }
                        MuteFrequency += 1;
                        alchemistsDataSource.updateDataBehaviourDetails(sharedPrefEmailAddress, null,
                                null, null, null,
                                null, null, null,
                                null, null, String.valueOf(MuteFrequency),
                                null,null);
                    }
                }
                ((MyViewHolder) holder).isMuted = !((MyViewHolder) holder).isMuted;
            }
        });


        ((MyViewHolder) holder).moveComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getCurrentContext().equals("NF")) {
                    if (listener != null) {
                        listener.onCommentsReceived(list.get(position).getPostId());
                    }
                } else {
                    if (listener2 != null) {
                        listener2.onCommentsReceived(list.get(position).getPostId());
                    }
                }
            }
        });


        if (list.get(position).getPostType().equals("image")) {
            ((MyViewHolder) holder).img_vol.setVisibility(GONE);
            ((MyViewHolder) holder).img_playback.setVisibility(GONE);
        } else {
            ((MyViewHolder) holder).img_vol.setVisibility(View.VISIBLE);
            ((MyViewHolder) holder).img_playback.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public List<MyModel> getData() {
        return list;
    }


//    @Override
//    public int getItemViewType(int position) {
//        if (list.get(position).getName().startsWith("text")) {
//            return TYPE_TEXT;
//        } else return TYPE_VIDEO;
//    }


}