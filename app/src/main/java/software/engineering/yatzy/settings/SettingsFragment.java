package software.engineering.yatzy.settings;

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

import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;

public class SettingsFragment extends Fragment implements Updatable {

    private String tag = "Info";
    private ImageView pingBtnRequest;
    private Button logOutButton;
    private TextView pingText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Log.d(tag, "In the Settings Fragment");
        AppManager.getInstance().currentFragment = this;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestPingFromServer();
    }

    private void requestPingFromServer(){
        String pingRequest = "50";
        AppManager.getInstance().startPingTimer();
        AppManager.getInstance().addClientRequest(pingRequest);
    }

    private void setPingText(){
        pingText.setText("Ping: " + String.valueOf(AppManager.getInstance().latency));
        Log.d("Info","in setPingText method");
    }

    private void requestLogout(){
        String logOutRequest = "3";
        AppManager.getInstance().addClientRequest(logOutRequest);
    }

    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        if (protocolIndex==51){
            setPingText();
        }
    }
}