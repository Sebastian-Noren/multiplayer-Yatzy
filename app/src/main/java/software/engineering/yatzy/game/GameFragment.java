package software.engineering.yatzy.game;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;

//TODO 1: Make a frame move to next player
//TODO 2: Create gamerules wherte to place score

public class GameFragment extends Fragment {


    enum State
    {
        PLAYING, ENDOFTURN
    }

    private static final String TAG = "Info";
    private static final int PLAYERS = 4;
    private static final int SCOREBOARD_SIZE = 18;
    private static final float DICE_START_POSITIONX = 500f;

    private TextView turnStateText;
    private ArtEngine artEngine;
    private SoundEngine soundEngine;
    private State state;
    private ImageView[] diceImages;
    private AnimationDrawable[] diceAnim;
    private BounceInterpolator interpolator = new BounceInterpolator();
    private Button rollButton;
    private ImageButton soundButton, chatButton;

    private Dice[] dices = {new Dice("Dice 1", true, 0, false), new Dice("Dice  2", true, 0, false),
            new Dice("Dice 3", true, 0, false), new Dice("Dice 4", true, 0, false),
            new Dice("Dice 5", true, 0, false)};

    private int[] dice;
    private ArrayList<TableLayout> tables;

    // Temp variable
    private short tempTurn = 0;
    private static int CURRENT_PLAYER = 0;
    private boolean firstThrowAllDiceHasLanded = false;
    private boolean scoreHasBeenPlaced = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        Log.d(TAG, "In the GameFragment");

        artEngine = new ArtEngine(getResources());
        soundEngine = new SoundEngine(getContext());
        rollButton = view.findViewById(R.id.rollBtn);
        soundButton = view.findViewById(R.id.soundBtn);
        chatButton = view.findViewById(R.id.chatBtn);
        turnStateText = view.findViewById(R.id.tgame_currentTurn);
        turnStateText.setText(MessageFormat.format("{0}/3", tempTurn));
        diceImages = new ImageView[5];
        diceAnim = new AnimationDrawable[5];

        //initialize dice views
        for (int i = 0; i < diceImages.length; i++) {
            int res = getResources().getIdentifier("diceImg" + (i + 1), "id", getContext().getPackageName());
            diceImages[i] = view.findViewById(res);
        }

        initDice();
        addTable(getContext(), view);

        //Start game sound
        soundEngine.createApplicationSound();
        soundEngine.createGameBgSound();


        setCurrentPlayersTable(CURRENT_PLAYER);

        state = State.PLAYING;





        // Roll button
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Rolling dice! ");
                soundEngine.buttonClick();

                switch (state)
                {
                    case PLAYING:
                        tempTurn++;
                        //TODO Sync "dice" with server
                        dice = diceRollAlgorithm();
                        if (!firstThrowAllDiceHasLanded) {
                            startFirstDiceAnimation();
                        } else {
                            rollButton.setText("Re roll");
                            resetSelectedDice();
                            rollAgainDiceAnimation();
                        }
                        turnStateText.setText(MessageFormat.format("{0}/3", tempTurn));

                        //When 3 turns ends and move to another player
                        if (tempTurn >= 3 && !scoreHasBeenPlaced) {
                            rollButton.setEnabled(false);
                        }
                        break;
                    case ENDOFTURN:
                        removeLastCurrentPlayersTable(CURRENT_PLAYER);
                        setCurrentPlayersTable(CURRENT_PLAYER+1);
                        break;

                    default:
                        Log.e(TAG, "Problem occurred!");
                        break;
                }

            }
        });

        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEngine.isSoundOn()) {
                    soundEngine.pauseGameBgSound();
                } else {
                    soundEngine.resumeGameBgSound();
                }
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.toastMessage(getContext(), "Ali will fix this, Go Chat!!");
            }
        });

        // dice 1
        diceImages[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                short val = 0;
                selectedDice(val);
            }
        });

        //dice 2
        diceImages[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                short val = 1;
                selectedDice(val);
            }
        });

        // dice 3
        diceImages[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                short val = 2;
                selectedDice(val);
            }
        });

        // dice 4
        diceImages[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                short val = 3;
                selectedDice(val);
            }
        });

        // dice 5
        diceImages[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                short val = 4;
                selectedDice(val);
            }
        });

        return view;
    }

    private void resetSelectedDice() {
        for (int i = 0; i < diceImages.length; i++) {
            if (dices[i].isSelected()) {
                diceImages[i].setTranslationX(DICE_START_POSITIONX);
                diceImages[i].setImageResource(R.drawable.roll_dice);
                diceAnim[i] = (AnimationDrawable) diceImages[i].getDrawable();
            }
        }
    }

    private void initDice() {
        for (int i = 0; i < diceImages.length; i++) {
            diceImages[i].setTranslationX(DICE_START_POSITIONX);
            diceImages[i].setImageResource(R.drawable.roll_dice);
            diceAnim[i] = (AnimationDrawable) diceImages[i].getDrawable();
        }
    }

    private void startFirstDiceAnimation() {
        Random rand = new Random();
        for (int i = 1; i < diceImages.length; i++) {
            diceImages[i].animate().translationX(0f).rotation(rand.nextInt(360)).setInterpolator(interpolator).setDuration(1500).start();
            diceAnim[i].start();
        }
        diceAnim[0].start();
        diceImages[0].animate().translationX(0f).rotation(rand.nextInt(360)).setInterpolator(interpolator).setDuration(1500).withEndAction(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < diceImages.length; i++) {
                    diceAnim[i].stop();
                }
                setAllDiceLanded();
                updateDiceGraphic();
            }
        }).start();
    }

    private void rollAgainDiceAnimation() {
        Random rand = new Random();
        float val;
        for (int i = 1; i < diceImages.length; i++) {
            if (dices[i].isSelected()) {
                val = rand.nextInt(360);
            } else {
                val = diceImages[i].getRotation();
            }
            diceImages[i].animate().translationX(0f).rotation(val).setInterpolator(interpolator).setDuration(1500).start();
            diceAnim[i].start();
        }
        if (dices[0].isSelected()) {
            val = rand.nextInt(360);
        } else {
            val = diceImages[0].getRotation();
        }
        diceAnim[0].start();
        diceImages[0].animate().translationX(0f).rotation(val).setInterpolator(interpolator).setDuration(1500).withEndAction(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < diceImages.length; i++) {
                    diceAnim[i].stop();
                }
                setAllDiceLanded();
                updateDiceGraphic();
            }
        }).start();
    }

    //Dice roll algorithm
    private int[] diceRollAlgorithm() {
        int[] temp = new int[5];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = (int) (Math.random() * 6) + 1;
            Log.i(TAG, "diceRollAlgorithm: " + temp[i]);
        }
        return temp;
    }

    private void setAllDiceLanded() {
        for (int i = 0; i < diceImages.length; i++) {
            dices[i].setRolling(false);
        }
    }

    //Set dice graphic as the dice values.
    private void updateDiceGraphic() {
        for (int i = 0; i < diceImages.length; i++) {
            if (!dices[i].isSelected() && !firstThrowAllDiceHasLanded) {
                dices[i].setDiceValue(dice[i]);
                diceImages[i].setImageBitmap(artEngine.getDiceSide(dices[i].getDiceValue() - 1));
            } else if (dices[i].isSelected() && firstThrowAllDiceHasLanded) {
                dices[i].setDiceValue(dice[i]);
                diceImages[i].setImageBitmap(artEngine.getDiceSide(dices[i].getDiceValue() - 1));
            }
        }
        firstThrowAllDiceHasLanded = true;
    }

    private void selectedDice(short val) {
        if (!dices[val].isRolling() && !dices[val].isSelected()) {
            Log.i(TAG, String.format("%s selected, with value: %d", dices[val].getDiceName(), dices[val].getDiceValue()));
            diceImages[val].setBackground(artEngine.getHighlight());
            dices[val].setSelected(true);
        } else {
            diceImages[val].setBackground(null);
            dices[val].setSelected(false);
            Log.i(TAG, String.format("%s deselected!", dices[val].getDiceName()));
        }
    }

    private int getSumSelectedDice(){
        int sum = 0;
        for (int i = 0; i < diceImages.length; i++) {
            if (dices[i].isSelected()) {
                sum += dices[i].getDiceValue();
            }
        }
        return sum;
    }

    //************************************** TABLE CODE BELOW HERE *******************************************************************

    // Score Board
    private void addTable(final Context context, View view) {
        // convert to DPI (85 id DPI size wanted)
        final float scale = Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density;
        int pixels = (int) (85 * scale + 0.5f);

        LinearLayout l = view.findViewById(R.id.tables_players);
        tables = new ArrayList<>();

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        tableParams.weight = 1;
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);


        for (int i = 0; i < PLAYERS; i++) {

            // Create a table with 85 DPI width and mathe parent height in Linear layout
            TableLayout tableLayout = new TableLayout(context);
            tableLayout.setLayoutParams(new LinearLayout.LayoutParams(pixels, LinearLayout.LayoutParams.MATCH_PARENT));// assuming the parent view is a LinearLayout

            // create a player and give him a scoreboard
            Player player = new Player("Player " + i);
            int[] scoreBoard = new int[SCOREBOARD_SIZE];
            player.setScoreBoard(scoreBoard);

            //Header name
            TableRow rowHeader = new TableRow(context);
            rowHeader.setLayoutParams(tableParams); // TableLayout is the parent view
            rowHeader.setGravity(Gravity.CENTER);
            rowHeader.setForeground(getResources().getDrawable(R.drawable.row_border));

            // Create textView in the header row
            TextView headerPlayerName = new TextView(context);
            headerPlayerName.setLayoutParams(rowParams); // TableRow is the parent view
            headerPlayerName.setText(player.getName());
            headerPlayerName.setGravity(Gravity.CENTER);

            // add textView item in the header row
            rowHeader.addView(headerPlayerName);

            // add header to table
            tableLayout.addView(rowHeader);

            for (int f = 0; f < SCOREBOARD_SIZE; f++) {
                TableRow tableRow = new TableRow(context);
                tableRow.setLayoutParams(tableParams); // TableLayout is the parent view
                tableRow.setGravity(Gravity.CENTER);
                tableRow.setForeground(getResources().getDrawable(R.drawable.row_border));

                TextView scoreTextField = new TextView(context);
                scoreTextField.setLayoutParams(rowParams); // TableRow is the parent view

               // player.setScoreBoardElement(f, rand.nextInt(20));
               // scoreTextField.setText(String.valueOf(player.getScoreBoardElement(f)));
                scoreTextField.setText("");
                scoreTextField.setGravity(Gravity.CENTER);
                tableRow.addView(scoreTextField);

                //TODO mechanic to place in correct spot etc
                //allows you to select a specific row
                tableRow.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        TableRow tablerow = (TableRow) view;
                        TextView sample = (TextView) tablerow.getChildAt(0); // only one child (textview)
                        // String result=sample.getText().toString();
                        sample.setText(String.valueOf(getSumSelectedDice()));
                        scoreHasBeenPlaced = true;

                        if (tempTurn >= 3) {
                            rollButton.setEnabled(true);
                            rollButton.setText("OK");
                            state = State.ENDOFTURN;
                        }

                    }
                });
                tableRow.setClickable(false);

                // add row to table
                tableLayout.addView(tableRow);
            }

            // add new table in array of tables
            tables.add(tableLayout);
        }

        // Add all tables in the linearLayout viewn
        for (TableLayout layout : tables) {
            l.addView(layout);
        }


    }

    private void setCurrentPlayersTable(int playerIDIndex){
        // Makes table(player) 1 (index 0) active for placing score and clickable
        // tables.get(CURRENT_PLAYER).setBackgroundColor(getResources().getColor(R.color.colorTest));
        tables.get(playerIDIndex).setForeground(getResources().getDrawable(R.drawable.table_border_current_player));
        for (int i = 0; i < tables.get(playerIDIndex).getChildCount(); i++) {
            TableRow row = (TableRow) tables.get(playerIDIndex).getChildAt(i);
            row.setClickable(true);
        }
    }

    private void removeLastCurrentPlayersTable(int playerIDIndex){
        tables.get(playerIDIndex).setForeground(null);
        for (int i = 0; i < tables.get(playerIDIndex).getChildCount(); i++) {
            TableRow row = (TableRow) tables.get(playerIDIndex).getChildAt(i);
            row.setClickable(false);
        }
    }


    //************************************** TABLE CODE END HERE *******************************************************************

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "GameFragment: In the onDestroyView() event");
    }

    // 1
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "GameFragment: In the onAttach() event");
    }

    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "GameFragment: In the OnCreate event()");
    }

    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "GameFragment: In the onActivityCreated() event");
    }

    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "GameFragment: In the onStart() event");
    }

    //6
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "GameFragment: In the onResume() event");
        soundEngine.resumeGameBgSound();
    }

    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "GameFragment: In the onPause() event");
        soundEngine.pauseGameBgSound();
    }

    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "GameFragment: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "GameFragment: In the onDestroy() event");
        soundEngine.stopGameBgSound();
    }

    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "GameFragment: In the onDetach() event");
    }

}