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
    private ArrayList<Room> gameSessionLists; // this array lis create a which parameters defiune in our model class;
    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClickListener(int position);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public GameOverviewAdapter(Context context, ArrayList<Room> gameSessionListSessionsArrayList) {
        this.context = context;
        this.gameSessionLists = gameSessionListSessionsArrayList;
    }

    @NonNull
    @Override
    public GameItemCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, null); // this line inflate the account_card

        return new GameItemCardHolder(view, itemClickListener); // this will return our view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull GameItemCardHolder holder, int position) {

        //TODO add game names etc here later
//        holder.title.setText(gameSessionLists.get(position).getTitle());
//        holder.gameStatusText.setText(gameSessionLists.get(position).getGameStatus());
        //in resource folder which is drawable

        Room room = gameSessionLists.get(position);

        holder.status.setText(room.getStatus());
        holder.description.setText(room.getDescription());
        holder.title.setText(room.getTitle());

    }

    @Override
    public int getItemCount() {
        return gameSessionLists.size();
    }

    static class GameItemCardHolder extends RecyclerView.ViewHolder {

        //TextView title, gameStatusText;
        TextView status, description, title;


        GameItemCardHolder(@NonNull View itemView, final ItemClickListener listener) {
            super(itemView);

//            this.title = itemView.findViewById(R.id.gamecard_row_title);
//            this.gameStatusText = itemView.findViewById(R.id.gamecard_row_text);

            this.status = itemView.findViewById(R.id.text_view_status);
            this.description = itemView.findViewById(R.id.text_view_description);
            this.title = itemView.findViewById(R.id.text_view_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClickListener(position);
                        }
                        ;
                    }
                }
            });

        }

    }
}



