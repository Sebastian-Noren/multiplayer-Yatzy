package software.engineering.yatzy.loginAndCreateAccount;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.Updatable;

public class CreateAccount extends Fragment implements Updatable{

    Button signUp_button;
    EditText name_edittext, nameID,password,re_type_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        signUp_button = view.findViewById(R.id.sign_up_button);
        name_edittext = view.findViewById(R.id.name_editText);
        nameID = view.findViewById(R.id.nameID_edittext);
        password = view.findViewById(R.id.password_ed);
        re_type_password = view.findViewById(R.id.re_type_password);


        return view;
    }

    public void createUserAccount(){
        String name       = name_edittext.getText().toString().trim();
        String name_id    = nameID.getText().toString().trim();
        String pass       = password.getText().toString().trim();
        String retypePass = re_type_password.getText().toString().trim();

        if (name.contains(":")|| name_id.contains(":")|| pass.contains(":")||retypePass.contains(":")){

        }

    }
    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {

    }
}
