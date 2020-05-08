package software.engineering.yatzy.overview.join_game;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.text.MessageFormat;
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
import software.engineering.yatzy.appManagement.Updatable;
import software.engineering.yatzy.game.Game;
import software.engineering.yatzy.game.GameState;
import software.engineering.yatzy.game.PlayerParticipation;
import software.engineering.yatzy.overview.Room;


public class JoinGameDialog extends AppCompatDialogFragment implements Updatable {
    private static final String TAG = "Info";
    private InvitationAdapter invitationAdapter;
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;
    private ImageButton cancelBtn;
    private ArrayList<Room> pendingList;
    private String accountName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_game_popup, container, false);
        Log.d(TAG, "Join dialog open");
        accountName = AppManager.getInstance().loggedInUser.getNameID();

        itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String serverRequest;
                switch (direction){
                    case ItemTouchHelper.LEFT:
                        Utilities.toastMessage(getContext(),"Decline");
                        serverRequest = MessageFormat.format("33:{0}:{1}", pendingList.get(position).getRoomID(), PlayerParticipation.DECLINED.toString());
                        AppManager.getInstance().addClientRequest(serverRequest);
                        pendingList.remove(position);
                        invitationAdapter.notifyItemRemoved(position);
                        break;
                    case ItemTouchHelper.RIGHT:
                        //TODO send request to server
                        Utilities.toastMessage(getContext(),"Send accept to server");
                        serverRequest = MessageFormat.format("33:{0}:{1}", pendingList.get(position).getRoomID(), PlayerParticipation.ACCEPTED.toString());
                        AppManager.getInstance().addClientRequest(serverRequest);
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
        int gameID = -1;
        for (int i = 0; i < AppManager.getInstance().gameList.size(); i++) {
            boolean pending = AppManager.getInstance().gameList.get(i).getPlayerByName(accountName).participation == PlayerParticipation.PENDING;
            if (pending) {
                gameRoom = AppManager.getInstance().gameList.get(i).getGameName();
                gameState = AppManager.getInstance().gameList.get(i).getState().toString();
                gameID = AppManager.getInstance().gameList.get(i).getGameID();
                for (int j = 0; j < AppManager.getInstance().gameList.get(i).getPlayerListSize() ; j++) {
                    boolean hostCheck = AppManager.getInstance().gameList.get(i).getPlayer(j).participation == PlayerParticipation.HOST;
                    if (hostCheck){
                        host = AppManager.getInstance().gameList.get(i).getPlayer(j).getName();
                    }
                }
                pendingList.add(new Room(gameRoom, "Game Host: " + host, gameState,gameID));
            }
        }
        invitationAdapter.notifyDataSetChanged();


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Cancel clicked");
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
        Log.d(TAG, "JoinGameDialog: In the onDestroyView() event");
        // this setStyle is VERY important.
        // STYLE_NO_FRAME means that I will provide my own layout and style for the whole dialog
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "JoinGameDialog: In the onDestroyView() event");
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

    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "JoinGameDialog: In the onDestroyView() event");
    }

    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "JoinGameDialog: In the onActivityCreated() event");
    }

    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "JoinGameDialog: In the onStart() event");
    }

    //6
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "JoinGameDialog: In the onResume() event");
    }

    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "JoinGameDialog: In the onPause() event");
    }

    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "JoinGameDialog: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "JoinGameDialog: In the onDestroy() event");
    }

    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "JoinGameDialog: In the onDetach() event");
    }
}
