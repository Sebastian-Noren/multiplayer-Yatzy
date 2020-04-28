package software.engineering.yatzy.overview.create_game;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.game.Player;
import software.engineering.yatzy.testing.localDatabase.DataBaseAccess;


public class CreateGameDialog extends AppCompatDialogFragment {
    private String tag = "Info";
    private String host;
    private TextView hostName, listCounterText;
    private EditText inputGameName;
    private InviteSearchAdapter inviteSearchAdapter;
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;
    private AutoCompleteTextView searchPlayer;
    private ArrayList<Player> invitedPlayerList;
    private ImageButton saveBtn, cancelBtn;
    private short counter = 0;

    //TODO 5. Make so string input are safe
    private DataBaseAccess dataBaseAccess; // Delete when database is online

    public interface OnSelectedInput {
        void saveComplete(String gameName, String host, ArrayList<String> invitedPlayers);
    }

    private OnSelectedInput onSelectedInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_new_game_popup, container, false);
        Log.d(tag, "Create account dialog open");
        dataBaseAccess =  DataBaseAccess.getInstance(getContext());

        itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                invitedPlayerList.remove(viewHolder.getAdapterPosition());
                inviteSearchAdapter.notifyDataSetChanged();
            }
        };

        initDialog(view);
        initSearchList();

        searchPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playerName = parent.getItemAtPosition(position).toString();
                Log.e(tag, "onItemClick:" + playerName );
                View v = getDialog().getCurrentFocus();
                if (v != null) {
                    InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }
                searchPlayer.setText("");
                startSearch(playerName.toLowerCase());
                counter++;
                listCounterText.setText(MessageFormat.format("{0}/10", counter));
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Cancel clicked");
                Utilities.hideSoftKeyboard(getActivity());
                getDialog().dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Save clicked");
                String gameName = inputGameName.getText().toString().trim();
                ArrayList<String> listPlayers = new ArrayList<>();
                for (Player players: invitedPlayerList) {
                    listPlayers.add(players.getName());
                }
                Utilities.hideSoftKeyboard(getActivity());
                getDialog().dismiss();
                onSelectedInput.saveComplete(gameName, host, listPlayers);
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
        try {
            onSelectedInput = (OnSelectedInput) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(tag, e.toString() + " in CreateAccountDialog");
        }
        super.onAttach(context);
    }


    private void startSearch(String text){
        //TODO Rewrite method when database is up
        dataBaseAccess.openDatabase();
        Player m = dataBaseAccess.getPlayerbyName(text);
        invitedPlayerList.add(m);
        inviteSearchAdapter.notifyDataSetChanged();
        dataBaseAccess.closeDatabe();
    }

    private void initSearchList(){
        dataBaseAccess.openDatabase();
        ArrayList<String> suggestList = dataBaseAccess.getPlayerName();
        dataBaseAccess.closeDatabe();
        ArrayAdapter<String> suggestAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,suggestList);
        searchPlayer.setAdapter(suggestAdapter);
    }

    private void initDialog(View view){
        listCounterText = view.findViewById(R.id.max_players_text);
        listCounterText.setText(MessageFormat.format("{0}/10", counter));
        saveBtn = view.findViewById(R.id.account_saveBtn);
        cancelBtn = view.findViewById(R.id.account_cancelBtn);
        inputGameName = view.findViewById(R.id.input_new_game);
        hostName = view.findViewById(R.id.new_game_host);
        //TODO unmark when account is up remove local String host
       //host = AppManager.getInstance().loggedInUser.getNameID();
        String host = "PlayerHost";
        hostName.setText(host);
        searchPlayer = view.findViewById(R.id.autoCompleteTextView);
        RecyclerView recViewInvitePlayers = view.findViewById(R.id.create_game_invite_recyclerlist);
        recViewInvitePlayers.setLayoutManager(new LinearLayoutManager(getContext()));
        recViewInvitePlayers.setHasFixedSize(true);
        invitedPlayerList = new ArrayList<>();
        inviteSearchAdapter = new InviteSearchAdapter(getContext(),invitedPlayerList);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recViewInvitePlayers);
        recViewInvitePlayers.setAdapter(inviteSearchAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "In the onDestroyView() event");
    }

}
