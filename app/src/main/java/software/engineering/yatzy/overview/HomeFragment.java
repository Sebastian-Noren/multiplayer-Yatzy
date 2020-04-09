package software.engineering.yatzy.overview;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;

public class HomeFragment extends Fragment implements CreateGameDialog.OnSelectedInput {

    private String tag = "Info";
    private NavController navController;
    private FloatingActionButton fabStart, fabCreateGame;
    private TextView textCreateGame;
    private float translationY = 100f;
    private boolean isMenuOpen = false;
    private RecyclerView recyclerView;
    private OvershootInterpolator interpolator = new OvershootInterpolator();
    private GameOverviewAdapter gameAdapter;
    private CreateGameDialog createGameDialog;
    ArrayList<Game> games = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(tag, "In the HomeFragment");
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

        gameAdapter.setOnItemClickListener(new GameOverviewAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                navController.navigate(R.id.navigation_game);
            }
        });

        return view;
    }

    //Initialization of view
    private void init(View view){
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
        recyclerView = view.findViewById(R.id.overview_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TODO remove, will be based on a real arraylist later.
        games.add(new Game("Game 1", "Pending"));
        games.add(new Game("Game 2", "playing"));

        gameAdapter = new GameOverviewAdapter(getContext(), games);
        recyclerView.setAdapter(gameAdapter);

        fabStart = view.findViewById(R.id.fabStart);
        textCreateGame = view.findViewById(R.id.text_create_game);
        fabCreateGame = view.findViewById(R.id.fab_create_game);

        fabCreateGame.setAlpha(0f);
        textCreateGame.setAlpha(0f);
        fabCreateGame.setEnabled(false);

        fabCreateGame.setTranslationY(translationY);
        textCreateGame.setTranslationY(translationY);
    }

    private void openMenu() {
        isMenuOpen = !isMenuOpen;
        fabStart.animate().rotation(45f).setInterpolator(interpolator).setDuration(300).start();
        fabCreateGame.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        textCreateGame.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabCreateGame.setEnabled(true);
    }

    private void closeMenu() {
        isMenuOpen = !isMenuOpen;
        fabCreateGame.setEnabled(false);
        fabStart.animate().rotation(0f).setInterpolator(interpolator).setDuration(300).start();
        fabCreateGame.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
        textCreateGame.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
    }

    private void openCreateGameSessionDialog() {
        createGameDialog = new CreateGameDialog();
        createGameDialog.getDialog();
        createGameDialog.setTargetFragment(this, 1);
        createGameDialog.show(Objects.requireNonNull(getFragmentManager()), "dialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "TransactionFragment: In the onDestroyView() event");
    }

    // 1
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(tag, "TransactionFragment: In the onAttach() event");
    }
    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "TransactionFragment: In the OnCreate event()");
    }
    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(tag, "TransactionFragment: In the onActivityCreated() event");
    }
    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(tag, "TransactionFragment: In the onStart() event");
    }
    //6
    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, "TransactionFragment: In the onResume() event");
    }
    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(tag, "TransactionFragment: In the onPause() event");
    }
    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag, "TransactionFragment: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "TransactionFragment: In the onDestroy() event");
    }
    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(tag, "TransactionFragment: In the onDetach() event");
    }

    //TODO Create new values when hosting a game
    @Override
    public void saveComplete(String input, double value, String notes) {

        games.add(new Game(input, notes));
        gameAdapter.notifyDataSetChanged();
    }
}
