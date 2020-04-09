package software.engineering.yatzy.overview;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;


public class CreateGameDialog extends AppCompatDialogFragment {
    private String tag = "Info";
    private TextView inputGameName;

    public interface OnSelectedInput {
        void saveComplete(String input, double value, String notes);
    }

    private OnSelectedInput onSelectedInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_new_game_popup, container, false);
        Log.d(tag, "Create account dialog open");
        inputGameName = view.findViewById(R.id.input_new_game);

        Button saveBtn = view.findViewById(R.id.account_saveBtn);
        Button cancelBtn = view.findViewById(R.id.account_cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Cancel clicked");
                Utilities.hideSoftKeyboard(getActivity());
                getDialog().dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Save clicked");

                String str1 = inputGameName.getText().toString().trim();
                getDialog().dismiss();
                Utilities.hideSoftKeyboard(getActivity());
                onSelectedInput.saveComplete(str1, 5, "xxxx");
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.colorPickerStyle);
        // this setStyle is VERY important.
        // STYLE_NO_FRAME means that I will provide my own layout and style for the whole dialog
        // so for example the size of the default dialog will not get in my way
        // the style extends the default one. see bellow.
    }

    @Override
    public void onAttach(Context context) {
        try {
            onSelectedInput = (OnSelectedInput) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(tag, e.toString() + " in CreateAccountDialog");
        }
        super.onAttach(context);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "In the onDestroyView() event");
    }

}
