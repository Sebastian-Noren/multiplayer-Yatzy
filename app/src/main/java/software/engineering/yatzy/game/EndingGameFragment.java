package software.engineering.yatzy.game;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;
import software.engineering.yatzy.overview.Room;

public class EndingGameFragment extends Fragment implements Updatable {

    private static final String TAG = "Info";
    private NavController navController;
    private TextView winnerText, winnerName, winnerScore;
    private Animation animation;
    private ImageView star1, star2,star3,star4;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ending, container, false);
        Log.d(TAG, "In the EndingGameFragment");
        AppManager.getInstance().currentFragment = this;
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.endColorBar));
        winnerText = view.findViewById(R.id.winner_text);
        winnerName = view.findViewById(R.id.text_winner_name);
        winnerScore = view.findViewById(R.id.text_winner_score);
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);
        star4 = view.findViewById(R.id.star4);
        animation = AnimationUtils.loadAnimation(getContext(),R.anim.rotate);
        star1.startAnimation(animation);
        star2.startAnimation(animation);
        star3.startAnimation(animation);
        star4.startAnimation(animation);



        int gameIndex = getArguments().getInt("gameEnded");
        Game currentWinnerGame = AppManager.getInstance().gameList.get(gameIndex);
        Log.i(TAG, "Game Ended: " + AppManager.getInstance().gameList.get(gameIndex).toString());

        Shader textShader = new LinearGradient(0, 20, 0, 48,
                new int[]{Color.parseColor("#FDB004"), Color.parseColor("#fee17f")},
                new float[]{0, 1}, Shader.TileMode.MIRROR);
        winnerText.setTextColor(getResources().getColor(R.color.clrfe));
        winnerText.getPaint().setShader(textShader);

        String name = currentWinnerGame.getWinnerName();
        String score = String.valueOf(currentWinnerGame.getWinnerScore());

        winnerName.setText(name);
        winnerScore.setText(score + " points");

        return view;
    }


    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        switch (protocolIndex) {
            case 40:
                Log.e(TAG, exceptionMessage);
                break;
            default:
                Log.d(TAG, "Unknown request from server...");
                break;
        }
    }

    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "GameFragment: In the OnCreate event()");
        // This callback will only be called when Fragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.navigation_main);
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }
}