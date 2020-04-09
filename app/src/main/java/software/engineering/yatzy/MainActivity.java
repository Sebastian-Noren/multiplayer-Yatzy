package software.engineering.yatzy;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private String tag = "Info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    protected void onStart() {
        super.onStart();
        Log.d(tag, "MainActivity: In the onStart() event");
    }

    protected void onRestart() {
        super.onRestart();
        Log.d(tag, "MainActivity: In the onRestart() event");
    }

    protected void onResume() {
        super.onResume();
        Log.d(tag, "MainActivity: In the onResume() event");
    }

    protected void onPause() {
        super.onPause();
        Log.d(tag, "MainActivity: In the onPause() event");
    }

    protected void onStop() {
        super.onStop();
        Log.d(tag, "MainActivity: In the onStop() event");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d(tag, "MainActivity: In the onDestroy() event");
    }

}
