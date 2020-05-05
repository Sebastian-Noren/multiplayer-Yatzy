package software.engineering.yatzy.overview.join_game;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import software.engineering.yatzy.game.Game;
import software.engineering.yatzy.game.Player;
import software.engineering.yatzy.overview.create_game.InviteSearchAdapter;
import software.engineering.yatzy.testing.localDatabase.DataBaseAccess;


public class JoinGameDialog extends AppCompatDialogFragment {
    private String tag = "Info";
    private InvitationAdapter invitationAdapter;
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;
    private ImageButton cancelBtn;
    private ArrayList<Game> pendingList;


    public interface OnSelectedInput {
        void saveComplete(String gameName, String host, ArrayList<String> invitedPlayers);
    }

    private OnSelectedInput onSelectedInput;

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

            }
        };

        initDialog(view);


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
        try {
            onSelectedInput = (OnSelectedInput) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(tag, e.toString() + " in CreateAccountDialog");
        }
        super.onAttach(context);
    }

    private void initDialog(View view) {

        cancelBtn = view.findViewById(R.id.account_cancelBtn);
        RecyclerView recViewInvitePlayers = view.findViewById(R.id.create_game_invite_recyclerlist);
        recViewInvitePlayers.setLayoutManager(new LinearLayoutManager(getContext()));
        recViewInvitePlayers.setHasFixedSize(true);
        pendingList = new ArrayList<>();
        invitationAdapter = new InvitationAdapter(getContext(), pendingList);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recViewInvitePlayers);
        recViewInvitePlayers.setAdapter(invitationAdapter);
    }

}
