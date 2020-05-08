package software.engineering.yatzy.overview.join_game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import software.engineering.yatzy.R;
import software.engineering.yatzy.game.Game;
import software.engineering.yatzy.overview.Room;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationHolder> {

    private Context context;
    private ArrayList<Room> inviteItems;

    public InvitationAdapter(Context context, ArrayList<Room> gameInvitation) {
        this.context = context;
        this.inviteItems = gameInvitation;
    }

    @NonNull
    @Override
    public InvitationHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.invitation_item, parent, false);
        return new InvitationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationHolder holder, int position) {
        Room items = this.inviteItems.get(position);
        holder.setDetails(items);
    }

    @Override
    public int getItemCount() {
        return inviteItems.size();
    }

    static class InvitationHolder extends RecyclerView.ViewHolder {
        private TextView invitedGame, invitationHost, userstatus;


        InvitationHolder(View itemView) {
            super(itemView);
            invitedGame = itemView.findViewById(R.id.text_invitation_gamename);
            invitationHost = itemView.findViewById(R.id.text_invitation_host);
            userstatus = itemView.findViewById(R.id.text_invitation_status);

        }

        void setDetails(Room details) {
            invitedGame.setText(details.getTitle());
            invitationHost.setText(details.getDescription());
            userstatus.setText(details.getStatus());
        }
    }
}
