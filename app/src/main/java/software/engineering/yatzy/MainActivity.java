package software.engineering.yatzy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Objects;

import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.NetworkState;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Info";
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(Objects.requireNonNull(this), R.id.nav_host_fragment);

        AppManager.getInstance().bindToService(getApplicationContext(), navController);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity: In the onStart() event");
    }

    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "MainActivity: In the onRestart() event");
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity: In the onResume() event");

        AppManager.getInstance().appInFocus = true;

        // If app has been put to background during connection phase
        switch (AppManager.getInstance().networkState) {
            case LOGIN:
                navController.navigate(R.id.navigation_Login);
                break;
            case ALLOWED:
                AppManager.getInstance().networkState = NetworkState.ENTERED;
                navController.navigate(R.id.navigation_main);
                break;
            default:
                // Has already entered successfully or has not begun connecting.
                break;
        }
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity: In the onPause() event");

        // UNCOMMENT LATER:
        AppManager.getInstance().appInFocus = false;
    }

    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity: In the onStop() event");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity: In the onDestroy() event");

        AppManager.getInstance().unbindFromService();
    }
}
