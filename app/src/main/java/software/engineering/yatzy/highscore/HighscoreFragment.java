package software.engineering.yatzy.highscore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.HighScoreRecord;
import software.engineering.yatzy.appManagement.Updatable;

public class HighscoreFragment extends Fragment implements Updatable {

    private String tag = "Info";

    private TextView txtScorePlayer;
    private TextView globalNbr1;
    private TextView globalNbr2;
    private TextView globalNbr3;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        AppManager.getInstance().currentFragment = this;
        Log.d(tag, "In the TestFragment");

        return view;
    }

    private void loadIndividaulHighScore(){

    }

    private void loadGlobalHighScores(){
        globalNbr1.setText("1) " + AppManager.getInstance().universalHighScores.get(0).nameID + ", " + String.valueOf(AppManager.getInstance().universalHighScores.get(0).score));
        globalNbr1.setText("2) " + AppManager.getInstance().universalHighScores.get(1).nameID + ", " + String.valueOf(AppManager.getInstance().universalHighScores.get(0).score));
        globalNbr1.setText("3) " + AppManager.getInstance().universalHighScores.get(2).nameID + ", " + String.valueOf(AppManager.getInstance().universalHighScores.get(0).score));
    }


    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        if (protocolIndex==24){
            loadIndividaulHighScore();
            loadGlobalHighScores();
        }
    }
}