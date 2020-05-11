package software.engineering.yatzy.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private ArrayList<String> ChatList;
    //item click listner
    private ItemClickListener itemClickListener;


    public ChatAdapter(ArrayList<String> mChatList) {
        this.ChatList = mChatList;
    }

    //item click listner
    public interface ItemClickListener {
        void onItemClickListner(int position);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }
    //----------------------------

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
//        ItemChatBoxBinding itemChatBox = DataBindingUtil.inflate(layoutInflater,R.layout.item_chat_box,viewGroup,false);
        View view = layoutInflater.inflate(R.layout.chat_box_item, viewGroup, false);
        return new ChatViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        //Based on what is added to the arraylist underneath GUI will be shown in different
        //TextViews!
        String[] info = ChatList.get(position).split(":");


        //change: if current logged in user equals info[0] (it will hold the name of the one who sent the string
        if (info[0].equals(AppManager.getInstance().loggedInUser.getNameID())) {

            holder.infoRight.setText(info[0] + "\n" + info[2] + ":" + info[3] + ":" + info[4]);
            holder.textViewRight.setText(info[1]);

            //Right reply (logged in user)
            holder.text_view_replyingTo.setVisibility(View.GONE);
            holder.text_view_user.setVisibility(View.GONE);
            holder.text_view_description.setVisibility(View.GONE);
            holder.reply_message.setVisibility(View.GONE);

            //Left reply (not logged in user)
            holder.text_view_replyingTo_left.setVisibility(View.GONE);
            holder.text_view_description_left.setVisibility(View.GONE);
            holder.text_view_user_left.setVisibility(View.GONE);
            holder.reply_message_left.setVisibility(View.GONE);

            //left text
            holder.infoLeft.setVisibility(View.GONE);
            holder.textViewLeft.setVisibility(View.GONE);

            //deleted image
            holder.delete_image.setVisibility(View.GONE);

        }

        //currently logged in user instead of ali
        if (!info[0].equals(AppManager.getInstance().loggedInUser.getNameID()) && !info[0].equals("delete")) {

            holder.infoLeft.setText(info[0] + "\n" + info[2] + ":" + info[3] + ":" + info[4]);
            holder.textViewLeft.setText(info[1]);


            //Right reply (logged in user)
            holder.text_view_replyingTo.setVisibility(View.GONE);
            holder.text_view_user.setVisibility(View.GONE);
            holder.textViewRight.setVisibility(View.GONE);
            holder.text_view_description.setVisibility(View.GONE);
            holder.reply_message.setVisibility(View.GONE);

            //Left reply (not logged in user)
            holder.text_view_replyingTo_left.setVisibility(View.GONE);
            holder.text_view_description_left.setVisibility(View.GONE);
            holder.text_view_user_left.setVisibility(View.GONE);
            holder.reply_message_left.setVisibility(View.GONE);


            //right remove
            holder.infoRight.setVisibility(View.GONE);
            holder.textViewRight.setVisibility(View.GONE);

            //delete image
            holder.delete_image.setVisibility(View.GONE);

        }

        //reply only is for the logged in user
        if (info[0].equals("AliReply")) { //-------------------------------------
            //0            1            2        3                      4            5-6-7
            //AliReply:whoIsReplying:Towhom:theirMessageWeReplyTo:OurReplyMessage:timestamp (three :

            holder.text_view_user.setText(info[2]);
            //the text that we responds to
            holder.text_view_description.setText(info[3]);

            //info timestamp and current user
            holder.reply_timestamp.setText(info[1] + "\n" + info[5]+ ":" + info[6] + ":" + info[7] );

            //Right reply (logged in user)
            holder.reply_message.setText(info[4]);
            holder.text_view_replyingTo.setVisibility(View.VISIBLE);
            holder.text_view_description.setVisibility(View.VISIBLE);
            holder.text_view_user.setVisibility(View.VISIBLE);
            holder.reply_message.setVisibility(View.VISIBLE);

            //Left reply (not logged in user)
            holder.text_view_replyingTo_left.setVisibility(View.GONE);
            holder.text_view_description_left.setVisibility(View.GONE);
            holder.text_view_user_left.setVisibility(View.GONE);
            holder.reply_message_left.setVisibility(View.GONE);

            //right remove
            holder.textViewRight.setVisibility(View.GONE);
            holder.infoRight.setVisibility(View.GONE);

            //left remove
            holder.infoLeft.setVisibility(View.GONE);
            holder.textViewLeft.setVisibility(View.GONE);

            //delete image
            holder.delete_image.setVisibility(View.GONE);

        }

        if(info[0].equals("AliReply") && !info[1].equals(AppManager.getInstance().loggedInUser.getNameID())){

            holder.text_view_user_left.setText(info[2]);
            //the text that we responds to
            holder.text_view_description_left.setText(info[3]);


            //info timestamp and current user
            holder.reply_timestamp_left.setText(info[1] + "\n" + info[5]+ ":" + info[6] + ":" + info[7] );

            //Left reply (not logged in user)
            holder.text_view_replyingTo_left.setVisibility(View.VISIBLE);
            holder.text_view_description_left.setVisibility(View.VISIBLE);
            holder.text_view_user_left.setVisibility(View.VISIBLE);
            holder.reply_message_left.setVisibility(View.VISIBLE);


            //Right reply (logged in user)
            holder.reply_message.setText(info[4]);
            holder.text_view_replyingTo.setVisibility(View.GONE);
            holder.text_view_description.setVisibility(View.GONE);
            holder.text_view_user.setVisibility(View.GONE);
            holder.reply_message.setVisibility(View.GONE);


            //right remove
            holder.textViewRight.setVisibility(View.GONE);
            holder.infoRight.setVisibility(View.GONE);

            //left remove
            holder.infoLeft.setVisibility(View.GONE);
            holder.textViewLeft.setVisibility(View.GONE);

            //delete image
            holder.delete_image.setVisibility(View.GONE);

        }


        if (info[0].equals("delete")) {
            //instead of "Ali" will hold the logged in user

            holder.deletedMessage.setText("Ali: erased his message");
            holder.delete_image.setVisibility(View.VISIBLE);


            //Right reply (logged in user)
            holder.text_view_replyingTo.setVisibility(View.GONE);
            holder.text_view_user.setVisibility(View.GONE);
            holder.text_view_description.setVisibility(View.GONE);
            holder.reply_message.setVisibility(View.GONE);
            holder.reply_timestamp.setVisibility(View.GONE);


            //Left reply (not logged in user)
            holder.text_view_replyingTo_left.setVisibility(View.GONE);
            holder.text_view_description_left.setVisibility(View.GONE);
            holder.text_view_user_left.setVisibility(View.GONE);
            holder.reply_message_left.setVisibility(View.GONE);
            holder.reply_timestamp_left.setVisibility(View.GONE);

            //left remove
            holder.infoLeft.setVisibility(View.GONE);
            holder.textViewLeft.setVisibility(View.GONE);

            //right remove
            holder.infoRight.setVisibility(View.GONE);
            holder.textViewRight.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }

    //inner class
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        //delete message, left and right message, left and right info
        TextView textViewLeft, infoLeft, textViewRight, infoRight, deletedMessage;
        //reply
        TextView text_view_replyingTo, text_view_user, text_view_description, reply_message, reply_timestamp;
        TextView text_view_replyingTo_left, text_view_user_left, text_view_description_left, reply_message_left,reply_timestamp_left;
        ImageView delete_image;
       // RelativeLayout right_reply_relativeLayout;

        public ChatViewHolder(@NonNull View itemView, final ItemClickListener listener) {
            super(itemView);
            textViewLeft = itemView.findViewById(R.id.chat_message_left);
            infoLeft = itemView.findViewById(R.id.chat_info_left);

            textViewRight = itemView.findViewById(R.id.chat_message_right);
            infoRight = itemView.findViewById(R.id.chat_info_right);

            deletedMessage = itemView.findViewById(R.id.removed_right_message);
            delete_image = itemView.findViewById(R.id.delete_image);

            //user we reply to (right)
            //this text will not be used by us, its final.
            text_view_replyingTo = itemView.findViewById(R.id.text_view_replyingTo);
            text_view_user = itemView.findViewById(R.id.text_view_user);
            text_view_description = itemView.findViewById(R.id.text_view_description);
            reply_message = itemView.findViewById(R.id.reply_message);
            reply_timestamp = itemView.findViewById(R.id.reply_timestamp);

            //user we reply to (left)
            //this text will not be used by us, its final
            text_view_replyingTo_left = itemView.findViewById(R.id.text_view_replyingTo_left);
            text_view_user_left = itemView.findViewById(R.id.text_view_user_left);
            text_view_description_left = itemView.findViewById(R.id.text_view_description_left);
            reply_message_left = itemView.findViewById(R.id.reply_message_left);
            reply_timestamp_left = itemView.findViewById(R.id.reply_timestamp_left);

          //  right_reply_relativeLayout = itemView.findViewById(R.id.right_reply_relativeLayout);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClickListner(position);

                        }

                    }
                }
            });
        }
    }

}
