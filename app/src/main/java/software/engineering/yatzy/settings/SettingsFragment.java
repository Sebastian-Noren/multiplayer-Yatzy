package software.engineering.yatzy.settings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.MessageFormat;

import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;

public class SettingsFragment extends Fragment implements Updatable {

    private String tag = "Info";
    private ImageView pingBtnRequest;
    private Button logOutButton;
    private static TextView pingText;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Log.d(tag, "In the Settings Fragment");
        pingBtnRequest = view.findViewById(R.id.refreshButton);
        logOutButton = view.findViewById(R.id.logoutButton);
        pingText = view.findViewById(R.id.txtPing);

        pingBtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPingFromServer();
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLogout();
            }
        });

        return view;
    }

    private void requestPingFromServer(){
        AppManager.getInstance().startPingTimer();
    }

    public  static void setPingText(){
        pingText.setText(MessageFormat.format("Ping: {0}", String.valueOf(AppManager.getInstance().latency)));
        Log.d("Info","in setPingText method");
    }

    private void requestLogout(){
        AppManager.getInstance().logOutUser();
    }

    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        Log.d("Info","In update in settings fragment..");
        if (protocolIndex==51){
            setPingText();
        }
    }

    // CAN THE BELOW LIFECYCLE METHODS BE REMOVED?
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "Settings: In the onDestroyView() event");
    }

    // 1
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(tag, "Settings: In the onAttach() event");
    }

    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "Settings: In the OnCreate event()");

    }

    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(tag, "Settings: In the onActivityCreated() event");
    }

    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(tag, "Settings: In the onStart() event");
    }

    //6
    @Override
    public void onResume() {
        super.onResume();
        requestPingFromServer();
        Log.d(tag, "Settings: In the onResume() event");
    }

    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(tag, "Settings: In the onPause() event");
    }

    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag, "Settings: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "Settings: In the onDestroy() event");
    }

    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(tag, "Settings: In the onDetach() event");
    }
}