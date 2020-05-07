package software.engineering.yatzy;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import java.util.Objects;

import software.engineering.yatzy.appManagement.AppManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppManager.getInstance().bindToService(getApplicationContext(), Navigation.findNavController(Objects.requireNonNull(this), R.id.nav_host_fragment));
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

        // UNCOMMENT LATER:
        AppManager.getInstance().appInFocus = true;
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
