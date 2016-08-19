package com.app.bricenangue.timeme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bricenangue on 05/02/16.
 *
 */
public class UserFriendForRequestListAdapter extends BaseAdapter
{
    private ArrayList<User> userlist;
    private Context context;
    private boolean[] checked;
    String senderName,mesg,status,currentusername;

    public interface UserCheckedForRequest{
        void onUserisChecked(boolean[] position);
    }

    private UserCheckedForRequest userCheckedForRequest;
    public UserFriendForRequestListAdapter(Context context, ArrayList<User> userlist,UserCheckedForRequest userCheckedForRequest){
        this.context=context;
        this.userlist=userlist;
        checked=new boolean[userlist.size()];
        this.userCheckedForRequest=userCheckedForRequest;

    }

    public void setUserStatus(boolean[] status){

            this.checked=status;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return userlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.user_friend_item,null);
            holder=new Holder();

            holder.email=(TextView)convertView.findViewById(R.id.usernamefriend_user_friend_item);
            holder.userPicture=(ImageView)convertView.findViewById(R.id.avatarfriend_user_friend_item);
            holder.checker=(ImageView)convertView.findViewById(R.id.checkerImageView_user_friend_item);


            convertView.setTag(holder);
        }else{

            holder=(Holder)convertView.getTag();
        }

        String usernam=userlist.get(position).email;
        Bitmap picture=userlist.get(position).picture;

        if(picture!=null){
            holder.userPicture.setImageBitmap(picture);
        }

        holder.email.setText(usernam);



       holder.checker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked[position]){
                    checked[position]=false;
                    holder.checker.setImageResource(R.drawable.unchecked);
                }else {
                    checked[position]=true;
                    holder.checker.setImageResource(R.drawable.checked);
                }
                if(userCheckedForRequest!=null){
                    userCheckedForRequest.onUserisChecked(checked);
                }
            }
        });


        if(!checked[position]){
            holder.checker.setImageResource(R.drawable.unchecked);
        }else {
            holder.checker.setImageResource(R.drawable.checked);
        }
        return convertView;
    }

    static class Holder {
        public TextView email;
        public ImageView userPicture;
        public ImageView checker;

    }
}
