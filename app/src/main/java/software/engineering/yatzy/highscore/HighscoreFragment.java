package software.engineering.yatzy.highscore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;

public class HighscoreFragment extends Fragment implements Updatable {

    private String tag = "Info";

    private TextView txtScorePlayer;
    private TextView globalNbr1;
    private TextView globalNbr2;
    private TextView globalNbr3;
    private ImageView refreshButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_highscore, container, false);
        AppManager.getInstance().currentFragment = this;
        Log.d(tag, "In the Highscore Fragment");

        refreshButton = view.findViewById(R.id.refreshButton);
        txtScorePlayer = view.findViewById(R.id.txtPing);
        globalNbr1 = view.findViewById(R.id.globalNbr1);
        globalNbr2 = view.findViewById(R.id.globalNbr2);
        globalNbr3 = view.findViewById(R.id.globalNbr3);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAllHighScores();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadIndividaulHighScore();
        loadGlobalHighScores();
    }

    private void loadIndividaulHighScore(){
        txtScorePlayer.setText("Your Highscore: " + AppManager.getInstance().loggedInUser.highScore);
    }

    private void loadGlobalHighScores(){
        globalNbr1.setText("1) " + AppManager.getInstance().universalHighScores.get(0).nameID + ", " + String.valueOf(AppManager.getInstance().universalHighScores.get(0).score));
        Log.d(tag,String.valueOf(globalNbr1.getText()));
        globalNbr2.setText("2) " + AppManager.getInstance().universalHighScores.get(1).nameID + ", " + String.valueOf(AppManager.getInstance().universalHighScores.get(1).score));
        globalNbr3.setText("3) " + AppManager.getInstance().universalHighScores.get(2).nameID + ", " + String.valueOf(AppManager.getInstance().universalHighScores.get(2).score));
    }

    private void requestAllHighScores(){
        loadIndividaulHighScore();
        loadGlobalHighScores();
    }


    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        if (protocolIndex==24){
            loadIndividaulHighScore();
            loadGlobalHighScores();
        }
    }
}