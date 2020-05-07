package software.engineering.yatzy.overview.join_game;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import software.engineering.yatzy.MainActivity;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.game.Game;
import software.engineering.yatzy.game.GameState;
import software.engineering.yatzy.game.PlayerParticipation;
import software.engineering.yatzy.overview.Room;


public class JoinGameDialog extends AppCompatDialogFragment {
    private String tag = "Info";
    private InvitationAdapter invitationAdapter;
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;
    private ImageButton cancelBtn;
    private ArrayList<Room> pendingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_game_popup, container, false);
        Log.d(tag, "Create account dialog open");


        itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                switch (direction){
                    case ItemTouchHelper.LEFT:
                        Utilities.toastMessage(getContext(),"Decline");
                        pendingList.remove(position);
                        invitationAdapter.notifyItemRemoved(position);
                        break;
                    case ItemTouchHelper.RIGHT:
                        //TODO send request to server
                        Utilities.toastMessage(getContext(),"Send accept to server");
                        pendingList.remove(position);
                        invitationAdapter.notifyItemRemoved(position);
                        break;
                }
                if (pendingList.size() == 0){
                    getDialog().dismiss();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.delete_icon)
                        .addSwipeRightActionIcon(R.drawable.check)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };


        initDialog(view);

        String gameRoom;
        String gameState;
        String host = "No host???";
        for (int i = 0; i < AppManager.getInstance().gameList.size(); i++) {
            boolean pending = AppManager.getInstance().gameList.get(i).getState() == GameState.PENDING;
            if (pending) {
                gameRoom = AppManager.getInstance().gameList.get(i).getGameName();
                gameState = AppManager.getInstance().gameList.get(i).getState().toString();
                for (int j = 0; j < AppManager.getInstance().gameList.get(i).getPlayerListSize() ; j++) {
                    boolean hostCheck = AppManager.getInstance().gameList.get(i).getPlayer(j).participation == PlayerParticipation.HOST;
                    if (hostCheck){
                        host = AppManager.getInstance().gameList.get(i).getPlayer(j).getName();
                    }
                }
                pendingList.add(new Room(gameRoom, "Game Host: " + host, gameState));
            }
        }
        invitationAdapter.notifyDataSetChanged();


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Cancel clicked");
                Utilities.hideSoftKeyboard(getActivity());
                getDialog().dismiss();
            }
        });


        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.colorPickerStyle);
        // this setStyle is VERY important.
        // STYLE_NO_FRAME means that I will provide my own layout and style for the whole dialog
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initDialog(View view) {

        cancelBtn = view.findViewById(R.id.account_cancelBtn);
        RecyclerView recViewInvitePlayers = view.findViewById(R.id.create_game_invite_recyclerlist);
        recViewInvitePlayers.setLayoutManager(new LinearLayoutManager(getContext()));
        pendingList = new ArrayList<>();
        invitationAdapter = new InvitationAdapter(getContext(), pendingList);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recViewInvitePlayers);
        recViewInvitePlayers.setAdapter(invitationAdapter);
    }

}
