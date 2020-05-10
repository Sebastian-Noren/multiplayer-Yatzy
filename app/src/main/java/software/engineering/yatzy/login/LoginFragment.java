package software.engineering.yatzy.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;

public class LoginFragment extends Fragment implements Updatable {
    /**
     * Login GUI should contain:
     * - User name (nameID): text field
     * - Password: password text field
     * - Exception label (hidden/empty until update() receives an Exception message. Maybe: Only dsiplay for 4-5 sec. Red color font?)
     * - Login button (call to method: login)
     *
     * - Create account button (to implement later). Maybe direct to a pop-up
     *
     * Text field & password should not accept colons ":" or be empty: string.trim().isEmpty()
     */

    private String tag = "Info";
    private Button loginBtn;
    private NavController navController;
    private EditText editText_nameID, editTextPassword;
    private TextView login_label;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Log.d(tag, "In the LoginFragment");
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.endColorBar));
        loginBtn = view.findViewById(R.id.loginButton);
        editText_nameID = view.findViewById(R.id.name_id_ed);
        editTextPassword = view.findViewById(R.id.password_edittext);
        login_label = view.findViewById(R.id.login_label);

        // Report currently displayed fragment to AppManager. Maybe from onViewCreated?
        AppManager.getInstance().currentFragment = this;

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    login();

            }
        });

        return view;
    }

    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {
        // If exception message (ex invalid login attempt or unable to connect to Server)
        if(protocolIndex == 40) {
            // Display exceptionMessage in label
            login_label.setText(exceptionMessage);
        }
    }

    public void login() {
        String nameID = editText_nameID.getText().toString().trim(); // Get from text field
        String password = editTextPassword.getText().toString().trim(); // Get from password text field

        if (nameID.equals("") || password.equals("")){
            login_label.setText("Enter NameID and Password");
        }else if (nameID.equals(":") || password.equals(":")){
            login_label.setText("Unknown Character ");
        }else {
            String loginRequest = "1:" + nameID + ":" + password;
            AppManager.getInstance().establishCloudServerConnection(loginRequest);
        }
    }

    // CAN THE BELOW LIFECYCLE METHODS BE REMOVED?

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "LoginFragment: In the onDestroyView() event");
    }

    // 1
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(tag, "LoginFragment: In the onAttach() event");
    }
    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "LoginFragment: In the OnCreate event()");
    }
    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(tag, "LoginFragment: In the onActivityCreated() event");
    }
    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(tag, "LoginFragment: In the onStart() event");
    }
    //6
    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, "LoginFragment: In the onResume() event");
    }
    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(tag, "LoginFragment: In the onPause() event");
    }
    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag, "LoginFragment: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "LoginFragment: In the onDestroy() event");
    }
    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(tag, "LoginFragment: In the onDetach() event");
    }

}