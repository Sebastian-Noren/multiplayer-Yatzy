package software.engineering.yatzy.overview;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.text.MessageFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.widget.ListViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;
import software.engineering.yatzy.game.Player;
import software.engineering.yatzy.testing.localDatabase.DataBaseAccess;


public class CreateGameDialog extends AppCompatDialogFragment {
    private String tag = "Info";
    private TextView inputGameName, listCounterText;
    private RecyclerView recViewInvitePlayers;
    private InviteSearchAdapter inviteSearchAdapter;
    private AutoCompleteTextView searchPlayer;
    private ArrayList<Player> invitedPlayerList;
    private ImageButton saveBtn, cancelBtn;
    private short counter = 0;

    //TODO 1. Create to remove invited player from list
    //TODO 2. Try to hide the F*** keyboard
    //TODO 3. More graphic styling
    //TODO 4. Send correct data down to HomeFragment, Work with interface
    //TODO Delete later on when database is online
    private DataBaseAccess dataBaseAccess;

    public interface OnSelectedInput {
        void saveComplete(String input, double value, String notes);
    }

    private OnSelectedInput onSelectedInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_new_game_popup, container, false);
        Log.d(tag, "Create account dialog open");
        dataBaseAccess =  DataBaseAccess.getInstance(getContext());

        initDialog(view);
        initSearchList();

        searchPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playerName = parent.getItemAtPosition(position).toString();
                Log.e(tag, "onItemClick:" + playerName );
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

                String str1 = inputGameName.getText().toString().trim();
                getDialog().dismiss();
                Utilities.hideSoftKeyboard(getActivity());
                onSelectedInput.saveComplete(str1, 5, "xxxx");
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
        searchPlayer = view.findViewById(R.id.autoCompleteTextView);
        recViewInvitePlayers = view.findViewById(R.id.create_game_invite_recyclerlist);
        recViewInvitePlayers.setLayoutManager(new LinearLayoutManager(getContext()));
        recViewInvitePlayers.setHasFixedSize(true);
        invitedPlayerList = new ArrayList<>();
        inviteSearchAdapter = new InviteSearchAdapter(getContext(),invitedPlayerList);
        recViewInvitePlayers.setAdapter(inviteSearchAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "In the onDestroyView() event");
    }

}
