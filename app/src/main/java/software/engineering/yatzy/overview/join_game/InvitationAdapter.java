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

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationHolder> {

    private Context context;
    private ArrayList<Game> games;

    public InvitationAdapter(Context context, ArrayList<Game> gameInvitation) {
        this.context = context;
        this.games = gameInvitation;
    }

    @NonNull
    @Override
    public InvitationHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inviteplayer, parent, false);
        return new InvitationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationHolder holder, int position) {
        Game game = this.games.get(position);
        holder.setDetails(game);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class InvitationHolder extends RecyclerView.ViewHolder {
        private TextView invitedGame, invitationHost, userstatus;


        InvitationHolder(View itemView) {
            super(itemView);
            invitedGame = itemView.findViewById(R.id.text_invitation_gamename);
            invitationHost = itemView.findViewById(R.id.text_invitation_host);
            userstatus = itemView.findViewById(R.id.text_invitation_status);

        }

        void setDetails(Game details) {
            //playerAccountName.setText(details.getName());
        }
    }
}
