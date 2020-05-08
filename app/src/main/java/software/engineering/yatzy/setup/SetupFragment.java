package software.engineering.yatzy.setup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;

public class SetupFragment extends Fragment implements Updatable{

    private String tag = "Info";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        Log.d(tag, "In the SetupFragment");

        AppManager.getInstance().currentFragment = this;

        return view;
    }

    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        // Keep empty here
    }


}