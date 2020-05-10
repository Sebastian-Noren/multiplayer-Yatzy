package software.engineering.yatzy.game;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import software.engineering.yatzy.R;
import software.engineering.yatzy.Utilities;
import software.engineering.yatzy.appManagement.AppManager;
import software.engineering.yatzy.appManagement.Updatable;

public class GameFragment extends Fragment implements Updatable {

    enum State {
        NEWPLAYER, PLAYING, PLACE_SCORE, END_TURN
    }

    private static final String TAG = "Info";
    private static final int SCOREBOARD_SIZE = 18;
    private static final float DICE_START_POSITIONX = 500f;
    private NavController navController;
    private TextView turnStateText;
    private ArtEngine artEngine;
    private SoundEngine soundEngine;
    private Game currentGame;
    private State state;
    private ImageView[] diceImages;
    private AnimationDrawable[] diceAnim;
    private BounceInterpolator interpolator = new BounceInterpolator();
    private Button rollButton;
    private ImageButton soundButton, chatButton;
    private ConstraintLayout chatLayoutFrame;
    private boolean isChatOpen = false;
    private String requestToServer;
    private int tableRowIndex;
    private int lastplayer;
    private ArrayList<TableLayout> tables;
    private int gameIndex;

    private Dice[] dices = {new Dice(DiceName.DICE1, 0, false), new Dice(DiceName.DICE2,0, false),
            new Dice(DiceName.DICE3, 0, false), new Dice(DiceName.DICE4, 0, false),
            new Dice(DiceName.DICE5, 0, false)};
    // Temp variable
    private boolean scoreHasBeenPlaced = false;

    // The Update method
    @Override
    public void update(int protocolIndex, int specifier, String exceptionMessage) {
        switch (protocolIndex) {
            case 18:
                if (specifier == currentGame.getGameID()){
                    Utilities.toastMessage(getContext(),"Protocol 18:");
                    currentGame.setTurnState(AppManager.getInstance().getGameByGameID(specifier).getTurnState());
                    lastplayer = currentGame.getTurnState().getCurrentPlayer();
                    Log.e(TAG, "onCreateView: " + currentGame.toString());
                    turnStateText.setText(MessageFormat.format("{0}/3", (currentGame.getTurnState().getRollTurn())));
                    checkSelectedDice();
                    resetSelectedDice();
                    rollAgainDiceAnimation();
                }
                break;
            case 20:
                if (specifier == currentGame.getGameID()){
                    Log.i(TAG,String.valueOf(lastplayer));
                    Utilities.toastMessage(getContext(),"Protocol 20:");
                    for (int i = 1; i < tables.get(lastplayer).getChildCount(); i++) {
                        TableRow row = (TableRow) tables.get(lastplayer).getChildAt(i);
                        TextView cellText = (TextView) row.getChildAt(0); // only one child (textview)
                        if (currentGame.getPlayer(lastplayer).getScoreBoardElement(i - 1) == -1){
                            cellText.setText("");
                        }else {
                            cellText.setText(String.valueOf(currentGame.getPlayer(lastplayer).getScoreBoardElement(i - 1)));
                        }
                    }
                    removeLastCurrentPlayersTable(lastplayer);
                    currentGame.setTurnState(AppManager.getInstance().getGameByGameID(specifier).getTurnState());
                    setCurrentPlayersTable(currentGame.getTurnState().getCurrentPlayer());
                    Log.e(TAG, "onCreateView: " + currentGame.toString());
                    checkIfPlayerIsAllowedToPlay();
                }
                break;
            case 22:
                if (specifier == currentGame.getGameID()) {
                    Utilities.toastMessage(getContext(), "Protocol 22:");
                    Bundle bundle = new Bundle();
                    bundle.putInt("gameEnded",gameIndex);
                    navController.navigate(R.id.navigation_ending, bundle);
                }
                break;
            case 40:
                Log.e(TAG, exceptionMessage);
                break;
            default:
                Log.d(TAG, "Unknown request from server...");
                break;
        }
    }

    private void checkSelectedDice(){
        for (int i = 0; i < diceImages.length; i++) {
            if (currentGame.getTurnState().getDiceBitMapElement(i)) {
                dices[i].setSelected(true);
            }else {
                dices[i].setSelected(false);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        Log.d(TAG, "In the GameFragment");
        AppManager.getInstance().currentFragment = this;
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.endColorBar));

        // Init all views in the game
        initViews(view);

        //Start game sound
        soundEngine.createApplicationSound();
        soundEngine.createGameBgSound();

        //Get the game user clicked on and get it from games list.
        gameIndex = getArguments().getInt("gameToPlay");
        currentGame = AppManager.getInstance().gameList.get(gameIndex);
        Log.e(TAG, "onCreateView: " + currentGame.toString());

        addTable(getContext(), view);
        setCurrentPlayersTable(currentGame.getTurnState().getCurrentPlayer());
        checkIfPlayerIsAllowedToPlay();

        // Roll button
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Rolling dice! ");
                soundEngine.buttonClick();
                switch (state) {
                    case NEWPLAYER:
                        Log.i(TAG, "NEW PLAYER STATE! ");
                        break;
                    case PLAYING:
                        Log.i(TAG, "PLAYING STATE! ");
                        //Will respond with 18
                        StringBuilder rollturnRequest = new StringBuilder("17:" + currentGame.getGameID());
                        for (int i = 0; i < diceImages.length; i++) {
                            if (dices[i].isSelected()) {
                                rollturnRequest.append(MessageFormat.format(":{0}", "1"));
                            }else {
                                rollturnRequest.append(MessageFormat.format(":{0}", "0"));
                            }
                        }
                        AppManager.getInstance().addClientRequest(rollturnRequest.toString().trim());
                        break;
                    case PLACE_SCORE:
                        Log.i(TAG, "PLACE_SCORE STATE! ");

                        break;
                    case END_TURN:
                        Log.i(TAG, "END TURN STATE! ");
                        AppManager.getInstance().addClientRequest(requestToServer);
                        deselectAllDice();
                        resetColors();
                        rollButton.setEnabled(false);
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
                    soundButton.setImageResource(R.drawable.sound_off);
                } else {
                    soundEngine.resumeGameBgSound();
                    soundButton.setImageResource(R.drawable.sound_on);
                }
            }
        });

        //TODO Implement chat etc
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChatOpen) {
                    closeChat();
                } else {
                    openChat();
                }
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

    private void checkIfPlayerIsAllowedToPlay(){
        if (AppManager.getInstance().loggedInUser.getNameID().equals(currentGame.getPlayer(currentGame.getTurnState().getCurrentPlayer()).getName())){
            rollButton.setEnabled(true);
            // set current playing state
            if (currentGame.getTurnState().getRollTurn() == 0) {
                state = State.NEWPLAYER;
                Log.i(TAG, "Init State: " + state);
            } else {
                state = State.PLAYING;
                Log.i(TAG, "Init State: " + state);
            }
        }else {
            rollButton.setEnabled(false);
        }
        turnStateText.setText(MessageFormat.format("{0}/3", (currentGame.getTurnState().getRollTurn())));
        initDice();
    }

    //************************************** GRAPHIC/ENGINE CODE BELOW HERE *******************************************************************

    private void resetSelectedDice() {
        for (int i = 0; i < diceImages.length; i++) {
            if (dices[i].isSelected()) {
                diceImages[i].setImageResource(R.drawable.roll_dice);
                diceAnim[i] = (AnimationDrawable) diceImages[i].getDrawable();
                diceImages[i].setTranslationX(DICE_START_POSITIONX);
            }
        }
    }

    private void initDice() {
        initDiceAnimation();
        if (state == State.NEWPLAYER ||currentGame.getTurnState().getRollTurn() == 0) {
            for (ImageView diceImage : diceImages) {
                diceImage.setTranslationX(DICE_START_POSITIONX);
                startFirstDiceAnimation();
            }
            Log.i(TAG, "InitDice(): 1");
        } else {
            Random rand = new Random();
            for (ImageView diceImage : diceImages) {
                diceImage.setRotation(rand.nextInt(360));
            }
            Log.i(TAG, "InitDice(): 2");
            initDiceGraphicCurrentPlay();
        }
    }

    private void initDiceAnimation() {
        for (int i = 0; i < diceImages.length; i++) {
            diceImages[i].setImageResource(R.drawable.roll_dice);
            diceAnim[i] = (AnimationDrawable) diceImages[i].getDrawable();
        }
    }

    private void startFirstDiceAnimation() {
        rollButton.setEnabled(false);
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
                updateDiceGraphic();
                deselectAllDice();
                rollButton.setText("Re roll");
                rollButton.setEnabled(true);
                state = State.PLAYING;
                Log.i(TAG, "Init State: " + state);
            }
        }).start();
    }

    private void rollAgainDiceAnimation() {
        rollButton.setEnabled(false);
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
                updateDiceGraphic();
                deselectAllDice();
                rollButton.setEnabled(true);
                //When 3 turns ends and move to another player
                if (currentGame.getTurnState().getRollTurn() > 2 && !scoreHasBeenPlaced) {
                    rollButton.setEnabled(false);
                    state = State.PLACE_SCORE;
                    Log.i(TAG, "Init State: " + state);
                }
            }
        }).start();
    }

    //Set dice graphic as the dice values.
    private void updateDiceGraphic() {
        for (int i = 0; i < diceImages.length; i++) {
            if (state == State.NEWPLAYER) {
                Log.i(TAG, "Update graphic: new Player");
                dices[i].setDiceValue(currentGame.getTurnState().getDiceElement(i));
                diceImages[i].setImageBitmap(artEngine.getDiceSide(dices[i].getDiceValue() - 1));
            } else if (dices[i].isSelected() && state == State.PLAYING) {
                Log.i(TAG, "Update graphic: Playing");
                dices[i].setDiceValue(currentGame.getTurnState().getDiceElement(i));
                diceImages[i].setImageBitmap(artEngine.getDiceSide(dices[i].getDiceValue() - 1));
            }
        }
    }

    private void initDiceGraphicCurrentPlay() {
        for (int i = 0; i < diceImages.length; i++) {
            dices[i].setDiceValue(currentGame.getTurnState().getDiceElement(i));
            diceImages[i].setImageBitmap(artEngine.getDiceSide(dices[i].getDiceValue() - 1));
        }
    }

    private void deselectAllDice() {
        for (int i = 0; i < diceImages.length; i++) {
            diceImages[i].setBackground(null);
            dices[i].setSelected(false);
        }
    }

    // When the player selects a dice
    private void selectedDice(short val) {
        if (!dices[val].isSelected()) {
            Log.i(TAG, String.format("%s selected, with value: %d", dices[val].getDiceName(), dices[val].getDiceValue()));
            diceImages[val].setBackground(artEngine.getHighlight());
            dices[val].setSelected(true);
            if (state == State.PLACE_SCORE) {
                checkRules();
            }
        } else {
            diceImages[val].setBackground(null);
            dices[val].setSelected(false);
            Log.i(TAG, String.format("%s deselected!", dices[val].getDiceName()));
            if (state == State.PLACE_SCORE) {
                checkRules();
            }
        }
    }

    private void resetColors(){
        int x = currentGame.getTurnState().getCurrentPlayer();
        for (int i = 1; i < tables.get(x).getChildCount(); i++) {
            TableRow row = (TableRow) tables.get(x).getChildAt(i);
            row.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    // Check rules
    private void checkRules() {
        int x = currentGame.getTurnState().getCurrentPlayer();
        GameRules rules = new GameRules();
        int[] diceRule = new int[5];
        for (int i = 0; i < diceImages.length; i++) {
            if (dices[i].isSelected()) {
                diceRule[i] = dices[i].getDiceValue();
            } else {
                diceRule[i] = -1;
            }
        }
        Arrays.sort(diceRule);

        int ok = getResources().getColor(R.color.colorFieldOK);
        int notOk = getResources().getColor(R.color.colorFieldNotOK);

        Log.i(TAG, Arrays.toString(diceRule));

        for (int i = 1; i < tables.get(x).getChildCount(); i++) {
            TableRow row = (TableRow) tables.get(x).getChildAt(i);
            switch (i) {
                case 1: // ones
                    if (rules.singelSide(1, diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 2: // Twos
                    if (rules.singelSide(2, diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 3: //Threes
                    if (rules.singelSide(3, diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 4: //Fours
                    if (rules.singelSide(4, diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 5: //Fives
                    if (rules.singelSide(5, diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 6: //Six
                    if (rules.singelSide(6, diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 7: //Sub total
                    break;
                case 8: //Bonus
                    break;
                case 9: // Pair
                    if (rules.onePair(diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 10: // Two Pairs
                    if (rules.twoPair(diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 11: // 3 of a kind
                    if (rules.threeOfAKind(diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 12: //4 of a kind
                    if (rules.fourOfAKind(diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 13: // Small Straight
                    if (rules.smallStraight(diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 14: //Large Straight
                    if (rules.largeStraight(diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 15: //Full house
                    if (rules.fullHouse(diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 16: // Chance
                    break;
                case 17: //Yatzy
                    if (rules.yatzy(diceRule)) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        row.setBackgroundColor(notOk);
                        row.setClickable(false);
                    }
                    break;
                case 18: //Grand Total
                    break;
                default:
                    Log.e(TAG, "Problem occurred!");
                    break;
            }

        }

    }

    private int calculateSubSum(){
        int sum = 0;
        for (int i = 1; i < 7; i++) {
            TableRow row = (TableRow) tables.get(lastplayer).getChildAt(i);
            TextView cellText = (TextView) row.getChildAt(0); // only one child (textview)
            try {
                sum += Integer.parseInt(cellText.getText().toString());
            }catch (NumberFormatException e){
                sum +=0;
                Log.e(TAG,e.getMessage());
            }
        }
        return sum;
    }

    // Calculated place score
    private int calculateScoreValue(int index) {
        switch (index) {
            case 1:
                return getSumSelectedDice(1);
            case 2:
                return getSumSelectedDice(2);
            case 3:
                return getSumSelectedDice(3);
            case 4:
                return getSumSelectedDice(4);
            case 5:
                return getSumSelectedDice(5);
            case 6:
                return getSumSelectedDice(6);
            case 7:
                //                   Log.i(TAG, "Sub total");
                break;
            case 8:
                //                   Log.i(TAG, "Bonus");
                break;
            case 9:
//                    Log.i(TAG, "Pair");
                break;
            case 10:
                //                   Log.i(TAG, "2Pairs");
                break;
            case 11:
                //                  Log.i(TAG, "3 of a kind");

                break;
            case 12:
                //                  Log.i(TAG, "4 of a kind");

                break;
            case 13: //Small straight fixed score: 15;
                return 15;
            case 14: //Large straight fixed score: 20;
                return 20;
            case 15:
                //                  Log.i(TAG, "Full House");
                break;
            case 16:
                //                  Log.i(TAG, "Chance");
                break;
            case 17: // yatzy fixed score: 50
                return 50;
            case 18:
                //                  Log.i(TAG, "Grand Total");

                break;
            default:
                Log.e(TAG, "Problem occurred!");
                break;
        }
        return -1;
    }

    private int getSumSelectedDice(int side) {
        int sum = 0;
        for (int i = 0; i < diceImages.length; i++) {
            if (dices[i].isSelected() && dices[i].getDiceValue() == side) {
                sum += dices[i].getDiceValue();
            }
        }
        return sum;
    }

    //************************************** GRAPHIC/ENGINE CODE ENDS HERE *******************************************************************

    //************************************** TABLE CODE BELOW HERE *******************************************************************

    // Score Board
    private void addTable(final Context context, View view) {
        // convert to DPI (85 id DPI size wanted)
        final float scale = Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density;
        int pixels = (int) (85 * scale + 0.5f);
        int padding = (int) (3 * scale + 0.5f);

        LinearLayout l = view.findViewById(R.id.tables_players);
        tables = new ArrayList<>();

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        tableParams.weight = 1;
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        // Loop and create tables for every player in the game
        for (int i = 0; i < currentGame.getPlayerListSize(); i++) {

            // Create a table with 85 DPI width and mathe parent height in Linear layout
            final TableLayout tableLayout = new TableLayout(context);
            tableLayout.setLayoutParams(new LinearLayout.LayoutParams(pixels, LinearLayout.LayoutParams.MATCH_PARENT));// assuming the parent view is a LinearLayout

            //Header name
            TableRow rowHeader = new TableRow(context);
            rowHeader.setLayoutParams(tableParams); // TableLayout is the parent view
            rowHeader.setGravity(Gravity.CENTER);
            rowHeader.setForeground(getResources().getDrawable(R.drawable.row_border));

            // Create textView in the header row
            TextView headerPlayerName = new TextView(context);
            headerPlayerName.setLayoutParams(rowParams); // TableRow is the parent view
            headerPlayerName.setText(currentGame.getPlayer(i).getName());
            headerPlayerName.setGravity(Gravity.CENTER);
            headerPlayerName.setPadding(0,padding,0,padding);
            headerPlayerName.setTypeface(null, Typeface.BOLD);

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
                scoreTextField.setGravity(Gravity.CENTER);
                scoreTextField.setPadding(0,padding,0,padding);


                if (currentGame.getPlayer(i).getScoreBoardElement(f) == -1){
                    scoreTextField.setText("");
                }else {
                    scoreTextField.setText(String.valueOf(currentGame.getPlayer(i).getScoreBoardElement(f)));
                }
                tableRow.addView(scoreTextField);

                //allows you to select a specific row
                tableRow.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (state == State.PLACE_SCORE) {
                            TableRow row = (TableRow) view;
                            tableRowIndex = tableLayout.indexOfChild(row);

                            String value = String.valueOf(calculateScoreValue(tableRowIndex));
                            String index = String.valueOf(tableRowIndex-1);
                            requestToServer = MessageFormat.format("19:{0}:{1}:{2}", currentGame.getGameID(), index, value);

                            TextView cellText = (TextView) row.getChildAt(0); // only one child (textview)
                            cellText.setText(String.valueOf(calculateScoreValue(tableRowIndex)));

                            TableRow subSum = (TableRow) tableLayout.getChildAt(7);
                            TextView subSumCellText = (TextView) subSum.getChildAt(0);
                            subSumCellText.setText(String.valueOf(calculateSubSum()));

                            scoreHasBeenPlaced = true;
                            rollButton.setEnabled(true);
                            rollButton.setText("OK");
                            state = State.END_TURN;
                        }
                    }
                });
                tableRow.setClickable(false);
                // add row to table
                tableLayout.addView(tableRow);
            }
            // add new table in array of tables
            tables.add(tableLayout);
            TableRow subSum = (TableRow) tables.get(i).getChildAt(7);
            TextView subSumCellText = (TextView) subSum.getChildAt(0);
            subSumCellText.setText(String.valueOf(calculateSubSum()));
        }
        // Add all tables in the linearLayout view
        for (TableLayout layout : tables) {
            l.addView(layout);
        }
    }

    private void setCurrentPlayersTable(int playerIDIndex) {
        tables.get(playerIDIndex).setForeground(getResources().getDrawable(R.drawable.table_border_current_player));
    }

    private void removeLastCurrentPlayersTable(int playerIDIndex) {
        tables.get(playerIDIndex).setForeground(null);
        for (int i = 0; i < tables.get(playerIDIndex).getChildCount(); i++) {
            TableRow row = (TableRow) tables.get(playerIDIndex).getChildAt(i);
            row.setClickable(false);
        }
    }

    //************************************** TABLE CODE END HERE *******************************************************************

    //************************************** CHAT CODE BELOW HERE *******************************************************************

    private void openChat() {
        isChatOpen = true;
        rollButton.setEnabled(false);
        chatLayoutFrame.setVisibility(View.VISIBLE);
    }

    private void closeChat() {
        isChatOpen = false;
        rollButton.setEnabled(true);
        chatLayoutFrame.setVisibility(View.INVISIBLE);
    }

    //************************************** CHAT CODE END HERE *******************************************************************

    private void initViews(View view) {
        Log.i(TAG, "Init Views");
        artEngine = new ArtEngine(getResources());
        soundEngine = new SoundEngine(getContext());
        rollButton = view.findViewById(R.id.rollBtn);
        soundButton = view.findViewById(R.id.soundBtn);
        chatButton = view.findViewById(R.id.chatBtn);
        turnStateText = view.findViewById(R.id.tgame_currentTurn);
        chatLayoutFrame = view.findViewById(R.id.chat_window);
        diceImages = new ImageView[5];
        diceAnim = new AnimationDrawable[5];
        //initialize dice views
        for (int i = 0; i < diceImages.length; i++) {
            int res = getResources().getIdentifier("diceImg" + (i + 1), "id", getContext().getPackageName());
            diceImages[i] = view.findViewById(res);
        }
    }

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
        // This callback will only be called when Fragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.navigation_main);
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // The callback can be enabled or disabled here or in handleOnBackPressed()
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