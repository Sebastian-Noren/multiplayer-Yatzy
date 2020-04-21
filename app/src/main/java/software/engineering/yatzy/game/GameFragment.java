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
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;

//TODO ALOT OF THINGS
public class GameFragment extends Fragment {

    private String tag = "Info";
    private int PLAYERS = 4;
    private int SCOREBOARD_SIZE = 18;
    private static float DICE_START_POSITIONX = 500f;
    private TextView turnStateText;
    private ArtEngine artEngine;
    private SoundEngine soundEngine;
    private ImageView[] diceImages;
    private int[] dice;
    private Dice[] dices = {new Dice("Dice 1", true, 0, false), new Dice("Dice  2", true, 0, false),
            new Dice("Dice 3", true, 0, false), new Dice("Dice 4", true, 0, false),
            new Dice("Dice 5", true, 0, false)};
    private AnimationDrawable[] diceAnim;
    private BounceInterpolator interpolator = new BounceInterpolator();
    private short tempTurn = 0;

    private boolean firstThrowAllDiceHasLanded = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        Log.d(tag, "In the GameFragment");
        artEngine = new ArtEngine(getResources());
        soundEngine = new SoundEngine(getContext());
        final Button rollButton = view.findViewById(R.id.rollBtn);
        turnStateText = view.findViewById(R.id.tgame_currentTurn);
        turnStateText.setText(MessageFormat.format("{0}/3", tempTurn));
        diceImages = new ImageView[5];
        diceAnim = new AnimationDrawable[5];

        final ImageButton soundButton = view.findViewById(R.id.soundBtn);
        ImageButton chatButton = view.findViewById(R.id.chatBtn);


        //initialize dice views
        for (int i = 0; i < diceImages.length; i++) {
            int res = getResources().getIdentifier("diceImg" + (i + 1), "id", getContext().getPackageName());
            diceImages[i] = view.findViewById(res);
        }

        initDice();
        addTable(getContext(), view);
        soundEngine.createApplicationSound();
        soundEngine.createGameBgSound();

        // Roll button
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundEngine.buttonClick();
                tempTurn++;
                Log.i(tag, "Rolling dice! ");
                //TODO Sync "dice" with server
                dice = diceRollAlgorithm();

                if (!firstThrowAllDiceHasLanded) {
                    startFirstDiceAnimation();
                } else {
                    resetSelectedDice();
                    rollAgainDiceAnimation();
                }

                turnStateText.setText(MessageFormat.format("{0}/3", tempTurn));
                if (tempTurn == 3) {
                    rollButton.setEnabled(false);
                }
            }
        });

        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEngine.isSoundOn()) {
                    soundEngine.pauseGameBgSound();
                }else {
                    soundEngine.resumeGameBgSound();
                }
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.toastMessage(getContext(),"ALi will fix this, Go Chat!!");
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
            Log.i(tag, "diceRollAlgorithm: " + temp[i]);
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
            Log.i(tag, String.format("%s selected, with value: %d", dices[val].getDiceName(), dices[val].getDiceValue()));
            diceImages[val].setBackground(artEngine.getHighlight());
            dices[val].setSelected(true);
        } else {
            diceImages[val].setBackground(null);
            dices[val].setSelected(false);
            Log.i(tag, String.format("%s deselected!", dices[val].getDiceName()));
        }
    }

    // Score Board
    private void addTable(final Context context, View view) {

        LinearLayout l = view.findViewById(R.id.tables_players);

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        Random rand = new Random();
        for (int i = 0; i < PLAYERS; i++) {

            TableLayout tableLayout = new TableLayout(context);
            tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// assuming the parent view is a LinearLayout

            Player player = new Player("Player " + i);
            int[] scoareBoard = new int[SCOREBOARD_SIZE];
            player.setScoreBoard(scoareBoard);

            //Header name
            TableRow tableRowHeader = new TableRow(context);
            tableRowHeader.setLayoutParams(tableParams);// TableLayout is the parent view
            tableLayout.addView(tableRowHeader);

            TextView headerPlayerName = new TextView(context);
            headerPlayerName.setLayoutParams(rowParams);// TableRow is the parent view

            headerPlayerName.setText(player.getName());
            headerPlayerName.setGravity(Gravity.CENTER);
            tableRowHeader.addView(headerPlayerName);

            for (int f = 0; f < SCOREBOARD_SIZE; f++) {
                TableRow tableRowtest = new TableRow(context);
                tableRowtest.setLayoutParams(tableParams);// TableLayout is the parent view
                tableLayout.addView(tableRowtest);

                TextView textView2 = new TextView(context);
                textView2.setLayoutParams(rowParams);// TableRow is the parent view

                player.setScoreBoardElement(f, rand.nextInt(20));

                textView2.setText(String.valueOf(player.getScoreBoardElement(f)));
                textView2.setGravity(Gravity.CENTER);
                tableRowtest.addView(textView2);

                //allows you to select a specific row
                tableRowtest.setClickable(true);
                tableRowtest.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        TableRow tablerow = (TableRow) view;
                        TextView sample = (TextView) tablerow.getChildAt(0);
                        // String result=sample.getText().toString();
                        sample.setText("I clicked this one");

                        // Log.i(tag,result);
                    }
                });
            }

            l.addView(tableLayout);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(tag, "GameFragment: In the onDestroyView() event");
    }

    // 1
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(tag, "GameFragment: In the onAttach() event");
    }

    //2
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "GameFragment: In the OnCreate event()");
    }

    //4
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(tag, "GameFragment: In the onActivityCreated() event");
    }

    //5
    @Override
    public void onStart() {
        super.onStart();
        Log.d(tag, "GameFragment: In the onStart() event");
    }

    //6
    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, "GameFragment: In the onResume() event");
        soundEngine.resumeGameBgSound();
    }

    //7
    @Override
    public void onPause() {
        super.onPause();
        Log.d(tag, "GameFragment: In the onPause() event");
        soundEngine.pauseGameBgSound();
    }

    //8
    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag, "GameFragment: In the onStop() event");
    }

    //10
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "GameFragment: In the onDestroy() event");
        soundEngine.stopGameBgSound();
    }

    //11
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(tag, "GameFragment: In the onDetach() event");
    }

}