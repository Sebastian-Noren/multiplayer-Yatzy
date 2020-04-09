package software.engineering.yatzy.overview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import software.engineering.yatzy.R;


public class GameOverviewAdapter extends RecyclerView.Adapter<GameOverviewAdapter.GameItemCardHolder> {

    Context context;
    private ArrayList<Game> games; // this array lis create a which parameters defiune in our model class;
    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClickListener(int position);
    }

    public void setOnItemClickListener(ItemClickListener listener){
        this.itemClickListener = listener;
    }

    public GameOverviewAdapter(Context context, ArrayList<Game> gameSessionsArrayList) {
        this.context = context;
        this.games = gameSessionsArrayList;
    }

    @NonNull
    @Override
    public GameItemCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_card_item, null); // this line inflate the account_card

        return new GameItemCardHolder(view, itemClickListener); // this will return our view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull GameItemCardHolder holder, int position) {

        //TODO add game names etc here later
        holder.title.setText(games.get(position).getTitle());
        holder.gameStatusText.setText(games.get(position).getGameStatus());
        //in resource folder which is drawable

    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameItemCardHolder extends RecyclerView.ViewHolder {

        TextView title, gameStatusText;


        GameItemCardHolder(@NonNull View itemView, final ItemClickListener listener) {
            super(itemView);

            this.title = itemView.findViewById(R.id.gamecard_row_title);
            this.gameStatusText = itemView.findViewById(R.id.gamecard_row_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener !=null){
                        int position = getAdapterPosition();
                        if (position !=RecyclerView.NO_POSITION){
                            listener.onItemClickListener(position);
                        };
                    }
                }
            });

        }

     }
}



