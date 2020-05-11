package software.engineering.yatzy.highscore;

import android.content.Context;
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

import java.text.MessageFormat;

import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;
import software.engineering.yatzy.settings.SettingsFragment;

public class HighscoreFragment extends Fragment implements Updatable {

    private String tag = "Info";

    private TextView txtScorePlayer;
    private TextView globalNbrOne;
    private TextView globalNbrTwo;
    private TextView globalNbrThree;
    private ImageView refreshButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_highscore, container, false);
        AppManager.getInstance().currentFragment = this;
        Log.d(tag, "In the Highscore Fragment");

        refreshButton = view.findViewById(R.id.refreshButton);
        txtScorePlayer = view.findViewById(R.id.txtPing);
        globalNbrOne = view.findViewById(R.id.globalNumberOne);
        globalNbrTwo = view.findViewById(R.id.globalNumberTwo);
        globalNbrThree = view.findViewById(R.id.globalNumberThree);

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
        txtScorePlayer.setText(MessageFormat.format("Your Highscore: {0}", AppManager.getInstance().loggedInUser.highScore));
    }

    private void loadGlobalHighScores(){
        globalNbrOne.setText(MessageFormat.format("{0}, {1}", AppManager.getInstance().universalHighScores.get(0).nameID, String.valueOf(AppManager.getInstance().universalHighScores.get(0).score)));
        Log.d(tag,String.valueOf(globalNbrOne.getText()));
        globalNbrTwo.setText(MessageFormat.format("{0}, {1}", AppManager.getInstance().universalHighScores.get(1).nameID, String.valueOf(AppManager.getInstance().universalHighScores.get(1).score)));
        globalNbrThree.setText(MessageFormat.format("{0}, {1}", AppManager.getInstance().universalHighScores.get(2).nameID, String.valueOf(AppManager.getInstance().universalHighScores.get(2).score)));
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

        //This is fucked up and ugly..
        if (protocolIndex==51){
            Log.d("Info","In update in highscore fragment..");
            SettingsFragment.setPingText();
        }
    }

    // CAN THE BELOW LIFECYCLE METHODS BE REMOVED?
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "HighScore: In the onDestroyView() event");
    }

    // 1
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(tag, "HighScore: In the onAttach() event");
    }

    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "HighScore: In the OnCreate event()");

    }

    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(tag, "HighScore: In the onActivityCreated() event");
    }

    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(tag, "HighScore: In the onStart() event");
    }

    //6
    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, "HighScore: In the onResume() event");
    }

    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(tag, "HighScore: In the onPause() event");
    }

    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag, "HighScore: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "HighScore: In the onDestroy() event");
    }

    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(tag, "HighScore: In the onDetach() event");
    }
}