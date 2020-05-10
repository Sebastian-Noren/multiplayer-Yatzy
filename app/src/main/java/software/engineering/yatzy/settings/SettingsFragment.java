package software.engineering.yatzy.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.NetworkService;
import software.engineering.yatzy.appManagement.Updatable;

public class SettingsFragment extends Fragment implements Updatable {

    private String tag = "Info";
    private Button pingBtnRequest;
    private TextView pingText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        Log.d(tag, "In the TestFragment");
        AppManager.getInstance().currentFragment = this;
        pingBtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPingBtnClicked();
            }
        });

        return view;
    }

    private void onPingBtnClicked(){
        String pingRequest = "50";
        AppManager.getInstance().addClientRequest(pingRequest);
    }

    private void setPingText(){
        pingText.setText("Ping: " + String.valueOf(AppManager.getInstance().latency));
    }

    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        if (protocolIndex==51){
            setPingText();
        }
    }
}