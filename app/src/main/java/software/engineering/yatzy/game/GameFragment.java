package software.engineering.yatzy.game;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import software.engineering.yatzy.R;

public class GameFragment extends Fragment {

    private String tag = "Info";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        Log.d(tag, "In the GameFragment");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "GameFragment: In the onDestroyView() event");
    }

    // 1
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(tag, "GameFragment: In the onAttach() event");
    }
    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "GameFragment: In the OnCreate event()");
    }
    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(tag, "GameFragment: In the onActivityCreated() event");
    }
    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(tag, "GameFragment: In the onStart() event");
    }
    //6
    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, "GameFragment: In the onResume() event");
    }
    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(tag, "GameFragment: In the onPause() event");
    }
    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag, "GameFragment: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "GameFragment: In the onDestroy() event");
    }
    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(tag, "GameFragment: In the onDetach() event");
    }

}