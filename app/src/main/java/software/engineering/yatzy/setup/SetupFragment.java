package software.engineering.yatzy.setup;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;

public class SetupFragment extends Fragment implements Updatable{

    /**
     * GUI: This fragment does not have user interaction or any readable content
     * This fragment could just have a big spinning progressbar (denoting a loading state)
     */

    private String tag = "Info";
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        Log.d(tag, "In the SetupFragment");
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
        AppManager.getInstance().currentFragment = this;

        //Fake loading to server
       // fakeServerConection();
        //navController.navigate(R.id.navigation_Login);

        return view;
    }

    private void fakeServerConection(){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(tag, "Thread started!");
                try {
                    Thread.sleep(1000);
                    // code for going to login, IMPORTANT!
                    navController.navigate(R.id.navigation_Login);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectToCloudWithSessionKey();
    }

    public void connectToCloudWithSessionKey() {
        AppManager.getInstance().readUserDataFromCache();
    }

    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        // Keep empty here
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "SetupFragment: In the onDestroyView() event");
    }

    // 1
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(tag, "SetupFragment: In the onAttach() event");
    }
    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "SetupFragment: In the OnCreate event()");
    }
    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(tag, "SetupFragment: In the onActivityCreated() event");
    }
    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(tag, "SetupFragment: In the onStart() event");
    }
    //6
    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, "SetupFragment: In the onResume() event");
    }
    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(tag, "SetupFragment: In the onPause() event");
    }
    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag, "SetupFragment: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "SetupFragment: In the onDestroy() event");
    }
    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(tag, "SetupFragment: In the onDetach() event");
    }


}