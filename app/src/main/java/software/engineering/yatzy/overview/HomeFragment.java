package software.engineering.yatzy.overview;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;
import software.engineering.yatzy.game.Game;
import software.engineering.yatzy.game.GameState;
import software.engineering.yatzy.game.PlayerParticipation;
import software.engineering.yatzy.overview.create_game.CreateGameDialog;
import software.engineering.yatzy.overview.join_game.JoinGameDialog;

public class HomeFragment extends Fragment implements CreateGameDialog.OnSelectedInput, JoinGameDialog.SendInviteAcceptData, Updatable {

    private static final String TAG = "Info";
    private NavController navController;
    private FloatingActionButton fabStart, fabCreateGame, fabInvite;
    private TextView textCreateGame, textFabInvite, invitationCounter;
    private FrameLayout invitationFrame;
    private float translationYX = 100f;
    private boolean isMenuOpen = false;
    private RecyclerView recyclerView;
    private OvershootInterpolator interpolator = new OvershootInterpolator();
    private GameOverviewAdapter gameAdapter;
    private ArrayList<Room> gameSessionLists = new ArrayList<>();
    private int inviteCounter;
    private String accountName;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "In the HomeFragment");
        AppManager.getInstance().currentFragment = this;
        Log.i(TAG, "Oncreate: " + AppManager.getInstance().currentFragment.toString());
        accountName = AppManager.getInstance().loggedInUser.getNameID();
        init(view);

        //Main button
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }
        });

        fabCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                openCreateGameSessionDialog();
            }
        });

        fabInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                openInviteDialog();
            }
        });

        gameAdapter.setOnItemClickListener(new GameOverviewAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                // Sends which game index the player chose
                if (AppManager.getInstance().gameList.get(position).getState() == GameState.PENDING) {
                    Utilities.toastMessage(getContext(), "Need to wait other players to accept!");
                }
                else if (AppManager.getInstance().gameList.get(position).getState() == GameState.ENDED){
                    Bundle endData = new Bundle();
                    endData.putInt("gameEnded", position);
                    navController.navigate(R.id.navigation_ending, endData);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("gameToPlay", position);
                    navController.navigate(R.id.navigation_game, bundle);
                }
            }
        });

        return view;
    }

    //Initialization of view
    private void init(View view) {
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
        fabStart = view.findViewById(R.id.fabStart);
        fabInvite = view.findViewById(R.id.fab_invite);
        textFabInvite = view.findViewById(R.id.text_invitation);
        textCreateGame = view.findViewById(R.id.text_create_game);
        fabCreateGame = view.findViewById(R.id.fab_create_game);
        invitationFrame = view.findViewById(R.id.invitation_frame);
        invitationCounter = view.findViewById(R.id.invitation_counter);
        recyclerView = view.findViewById(R.id.overview_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      
        inviteCounter = 0;
        for (int i = 0; i < AppManager.getInstance().gameList.size(); i++) {
            boolean pending = AppManager.getInstance().gameList.get(i).getPlayerByName(accountName).participation == PlayerParticipation.PENDING;
            if (!pending) {
                String gameRoom = AppManager.getInstance().gameList.get(i).getGameName();
                String gameState = AppManager.getInstance().gameList.get(i).getState().toString();
                int roomId = AppManager.getInstance().gameList.get(i).getGameID();
                String description = "A real game";
                gameSessionLists.add(new Room(gameRoom, description, gameState, roomId));
            } else {
                inviteCounter++;
            }
        }



        gameAdapter = new GameOverviewAdapter(getContext(), gameSessionLists);
        recyclerView.setAdapter(gameAdapter);

        fabInvite.setAlpha(0f);
        textFabInvite.setAlpha(0f);
        fabInvite.setEnabled(false);

        fabCreateGame.setAlpha(0f);
        textCreateGame.setAlpha(0f);
        fabCreateGame.setEnabled(false);

        fabCreateGame.setTranslationY(translationYX);
        textCreateGame.setTranslationY(translationYX);
        fabInvite.setTranslationX(translationYX);
        textFabInvite.setTranslationX(translationYX);

        changeInviteFrame();

    }

    private void openMenu() {
        isMenuOpen = !isMenuOpen;
        invitationFrame.setAlpha(0f);
        fabStart.animate().rotation(45f).setInterpolator(interpolator).setDuration(300).start();
        fabCreateGame.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        textCreateGame.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabCreateGame.setEnabled(true);

        fabInvite.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        invitationFrame.animate().translationX(-255f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        textFabInvite.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabInvite.setEnabled(true);
    }

    private void closeMenu() {
        isMenuOpen = !isMenuOpen;
        fabCreateGame.setEnabled(false);
        fabStart.animate().rotation(0f).setInterpolator(interpolator).setDuration(300).start();
        fabCreateGame.animate().translationY(translationYX).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
        textCreateGame.animate().translationY(translationYX).alpha(0f).setInterpolator(interpolator).setDuration(500).start();

        fabInvite.animate().translationX(translationYX).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        invitationFrame.animate().translationX(0f).setInterpolator(interpolator).setDuration(300).start();
        textFabInvite.animate().translationX(translationYX).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabInvite.setEnabled(false);
    }

    //TODO add more stuff here.
    private void openInviteDialog() {
        JoinGameDialog joinGameDialog = new JoinGameDialog();
        joinGameDialog.getDialog();
        joinGameDialog.setTargetFragment(this, 2);
        joinGameDialog.show(Objects.requireNonNull(getFragmentManager()), "joinGame");
    }

    private void openCreateGameSessionDialog() {
        CreateGameDialog createGameDialog = new CreateGameDialog();
        createGameDialog.getDialog();
        createGameDialog.setTargetFragment(this, 1);
        createGameDialog.show(Objects.requireNonNull(getFragmentManager()), "createGame");
    }

    private void changeInviteFrame(){
        if (inviteCounter == 0) {
            invitationFrame.setVisibility(View.INVISIBLE);
            invitationCounter.setText(String.valueOf(inviteCounter));
        } else {
            invitationFrame.setVisibility(View.VISIBLE);
            invitationCounter.setText(String.valueOf(inviteCounter));
        }
    }

    @Override
    public void saveComplete(String gameName, String host, ArrayList<String> listOfInvitedPlayers) {
        AppManager.getInstance().currentFragment = this;
        String players = "";
        for (int i = 0; i < listOfInvitedPlayers.size(); i++) {
            players = players.concat(listOfInvitedPlayers.get(i) + ":");
            if(i == listOfInvitedPlayers.size()-1) {
                // Remove last colon
                players =  players.substring(0, players.lastIndexOf(":"));
            }
        }
        //Send request to server
        String createGameRequest = MessageFormat.format("32:{0}:{1}", gameName, players);
        AppManager.getInstance().addClientRequest(createGameRequest);
    }

    @Override
    public void sendDecline(int minusInviteCounter) {
        inviteCounter -= minusInviteCounter;
        changeInviteFrame();
    }

    @Override
    public void sendAccept(int minusInviteCounter, ArrayList<Room> listOfAccepted) {
        inviteCounter -= minusInviteCounter;
        changeInviteFrame();

        gameSessionLists.addAll(listOfAccepted);
        gameAdapter.notifyDataSetChanged();
    }

    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        String gameRoom;
        String gameState;
        int roomId;
        String description;
        switch (protocolIndex) {
            case 15:
                for (Game game : AppManager.getInstance().gameList) {
                    if (game.getGameID() == gameID && game.getPlayerByName(AppManager.getInstance().loggedInUser.getNameID()).participation != PlayerParticipation.HOST) {
                        inviteCounter++;
                        changeInviteFrame();
                        break;
                    }else if (game.getGameID() == gameID){
                        gameRoom = game.getGameName();
                        gameState = game.getState().toString();
                        roomId = game.getGameID();
                        description = "Waiting for other players to accept";
                        gameSessionLists.add(new Room(gameRoom, description, gameState, roomId));
                        gameAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
            case 16:
                for (Game game : AppManager.getInstance().gameList) {
                    if (game.getGameID() == gameID) {
                        gameRoom = game.getGameName();
                        gameState = game.getState().toString();
                        roomId = game.getGameID();
                        description = "A game from server"; // TODO lös något
                        gameSessionLists.add(new Room(gameRoom, description, gameState, roomId));
                        gameAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
            case 21:
                Utilities.toastMessage(getContext(),"Case 21 happend!");
                for (int i = 0; i < gameSessionLists.size() ; i++) {
                    if (gameSessionLists.get(i).getRoomID() == gameID){
                        gameSessionLists.get(i).setDescription("Game in session");
                        gameSessionLists.get(i).setStatus(GameState.ONGOING.toString());
                        gameAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
            case 22:

                Utilities.toastMessage(getContext(),"Case 22 happend!");
                break;
            case 40:
                Log.e(TAG, exceptionMessage);
                break;
            default:
                Log.d(TAG, "Unknown request from server...");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "HomeFragment: In the onDestroyView() event");
    }

    // 1
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "HomeFragment: In the onAttach() event");
    }

    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HomeFragment: In the OnCreate event()");
        // This callback will only be called when Fragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "HomeFragment: In the onActivityCreated() event");
    }

    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "HomeFragment: In the onStart() event");
    }

    //6
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "HomeFragment: In the onResume() event");
    }

    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "HomeFragment: In the onPause() event");
    }

    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "HomeFragment: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeFragment: In the onDestroy() event");
    }

    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "HomeFragment: In the onDetach() event");
    }
}
