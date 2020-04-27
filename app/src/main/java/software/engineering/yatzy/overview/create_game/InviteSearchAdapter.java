package software.engineering.yatzy.overview.create_game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import software.engineering.yatzy.R;
import software.engineering.yatzy.game.Player;

public class InviteSearchAdapter extends RecyclerView.Adapter<InviteSearchAdapter.InviteSearchHolder> {

    private Context context;
    private ArrayList<Player> players;

    public InviteSearchAdapter(Context context, ArrayList<Player> players) {
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public InviteSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inviteplayer, parent, false);
        return new InviteSearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteSearchHolder holder, int position) {
        Player player = this.players.get(position);
        holder.setDetails(player);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class InviteSearchHolder extends RecyclerView.ViewHolder {
        private TextView playerAccountName;


        InviteSearchHolder(View itemView) {
            super(itemView);
            playerAccountName = itemView.findViewById(R.id.player_item_name);

        }

        void setDetails(Player details) {
            playerAccountName.setText(details.getName());
        }
    }
}
