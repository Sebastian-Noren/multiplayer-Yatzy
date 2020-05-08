package software.engineering.yatzy.loginAndCreateAccount;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import software.engineering.yatzy.R;
import software.engineering.yatzy.appManagement.Updatable;

public class CreateAccount extends Fragment implements Updatable{

    Button signUp_button;
    EditText name_edittext, nameID,password,re_type_password;
    TextView sign_up_label;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        signUp_button    = view.findViewById(R.id.sign_up_button);
        name_edittext    = view.findViewById(R.id.name_editText);
        nameID           = view.findViewById(R.id.nameID_edittext);
        password         = view.findViewById(R.id.password_ed);
        re_type_password = view.findViewById(R.id.re_type_password);
        sign_up_label    = view.findViewById(R.id.sign_up_label);

        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAccount();
                closeKeyboard();
            }
        });

        return view;
    }

    public void createUserAccount(){
        String name       = name_edittext.getText().toString().trim();
        String name_id    = nameID.getText().toString().trim();
        String pass       = password.getText().toString().trim();
        String retypePass = re_type_password.getText().toString().trim();

        if (name.contains(":")|| name_id.contains(":")|| pass.contains(":")||retypePass.contains(":")){
            sign_up_label.setText("Unknown Character");
        }else if (name.equals("") || name_id.equals("")||pass.equals("")||retypePass.equals("")){
            sign_up_label.setText("The Fields are Empty");
        }else {

        }

    }
    @Override
    public void update(int protocolIndex, int gameID, String exceptionMessage) {

    }

    public void closeKeyboard() {
        name_edittext.onEditorAction(EditorInfo.IME_ACTION_DONE);
        nameID.onEditorAction(EditorInfo.IME_ACTION_DONE);
        password.onEditorAction(EditorInfo.IME_ACTION_DONE);
        re_type_password.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }
}
