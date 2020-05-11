package software.engineering.yatzy.game;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView turnStateText, gameInfo, latency;
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
    private int lastplayer;
    private ArrayList<TableLayout> tables;
    private int gameIndex;
    private boolean anyDiceSelected = false;
    private boolean scoreHasBeenPlaced = false;
    private Dice[] dices = {new Dice(DiceName.DICE1, 0, false), new Dice(DiceName.DICE2, 0, false),
            new Dice(DiceName.DICE3, 0, false), new Dice(DiceName.DICE4, 0, false),
            new Dice(DiceName.DICE5, 0, false)};

    // The Update method
    @Override
    public void update(int protocolIndex, int specifier, String exceptionMessage) {
        switch (protocolIndex) {
            case 18:
                if (specifier == currentGame.getGameID()) {
                    Utilities.toastMessage(getContext(), "Protocol 18:");
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
                if (specifier == currentGame.getGameID()) {
                    Log.i(TAG, String.valueOf(lastplayer));
                    Utilities.toastMessage(getContext(), "Protocol 20:");
                    for (int i = 1; i < tables.get(lastplayer).getChildCount(); i++) {
                        TableRow row = (TableRow) tables.get(lastplayer).getChildAt(i);
                        TextView cellText = (TextView) row.getChildAt(0); // only one child (textview)
                        if (currentGame.getPlayer(lastplayer).getScoreBoardElement(i - 1) == -1) {
                            cellText.setText("");
                        } else {
                            cellText.setText(String.valueOf(currentGame.getPlayer(lastplayer).getScoreBoardElement(i - 1)));
                        }
                    }
                    tableCalculateSums(lastplayer);
                    removeLastCurrentPlayersTable(lastplayer);
                    scoreHasBeenPlaced = false;
                    currentGame.setTurnState(AppManager.getInstance().getGameByGameID(specifier).getTurnState());
                    setCurrentPlayersTable(currentGame.getTurnState().getCurrentPlayer());
                    String str = currentGame.getPlayer(currentGame.getTurnState().getCurrentPlayer()).getName() + " is playing!";
                    gameInfo.setText(str);
                    Log.e(TAG, "onCreateView: " + currentGame.toString());
                    checkIfPlayerIsAllowedToPlay();
                }
                break;
            case 22:
                if (specifier == currentGame.getGameID()) {
                    Utilities.toastMessage(getContext(), "Protocol 22:");
                    Bundle bundle = new Bundle();
                    bundle.putInt("gameEnded", gameIndex);
                    navController.navigate(R.id.navigation_ending, bundle);
                }

                break;
            case 36:
                //message list
                //chatList.get()
                //chatAdapter.notifyDataSetChanged();
                if (specifier == currentGame.getGameID()) {

                    // chatList = AppManager.getInstance().getGameByGameID(currentGame.getGameID()).messages;

                    for (ChatMessage message : AppManager.getInstance().getGameByGameID(currentGame.getGameID()).messages) {
                        chatList.add(message);
                        chatAdapter.notifyDataSetChanged();

                    }
                    recyclerViewChat.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerViewChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

                        }
                    });

                }
                break;
            case 38:
                if (specifier == currentGame.getGameID()) {
                    int latest = AppManager.getInstance().getGameByGameID(currentGame.getGameID()).messages.size() - 1;

                    ChatMessage message = AppManager.getInstance().getGameByGameID(currentGame.getGameID()).messages.get(latest);

                    chatList.add(message);
                    chatAdapter.notifyItemInserted(latest);

                    recyclerViewChat.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerViewChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

                        }
                    });

                }

                break;
            case 392:

                break;
            case 40:
                Log.e(TAG, exceptionMessage);
                break;
            case 51:
                String str = MessageFormat.format("Latency: {0} ms", String.valueOf(AppManager.getInstance().latency));
                latency.setText(str);
                break;
            default:
                Log.d(TAG, "Unknown request from server...");
                break;
        }
    }

    private void checkSelectedDice() {
        for (int i = 0; i < diceImages.length; i++) {
            if (currentGame.getTurnState().getDiceBitMapElement(i)) {
                dices[i].setSelected(true);
            } else {
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
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Init all views in the game
        initViews(view);
        //init chat
        initChat(view);

        //Start game sound
        soundEngine.createApplicationSound();
        //   soundEngine.createGameBgSound();

        //Get the game user clicked on and get it from games list.
        gameIndex = getArguments().getInt("gameToPlay");
        currentGame = AppManager.getInstance().gameList.get(gameIndex);

        //request messages
        AppManager.getInstance().addClientRequest("35:" + currentGame.getGameID() + ":latest:-1");

        Log.e(TAG, "onCreateView: " + currentGame.toString());

        addTable(getContext(), view);
        setCurrentPlayersTable(currentGame.getTurnState().getCurrentPlayer());
        String str = currentGame.getPlayer(currentGame.getTurnState().getCurrentPlayer()).getName() + " is playing!";
        gameInfo.setText(str);
        String str2 = "Latency: - ms";
        latency.setText(str2);
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
                            } else {
                                rollturnRequest.append(MessageFormat.format(":{0}", "0"));
                            }
                        }
                        AppManager.getInstance().addClientRequest(rollturnRequest.toString().trim());
                        requestPingFromServer();
                        break;
                    case PLACE_SCORE:
                        Log.i(TAG, "PLACE_SCORE STATE! ");

                        break;
                    case END_TURN:
                        Log.i(TAG, "END TURN STATE! ");
                        AppManager.getInstance().addClientRequest(requestToServer);
                        requestPingFromServer();
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
                soundEngine.buttonClick();
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
                soundEngine.buttonClick();
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

    private void requestPingFromServer(){
        String pingRequest = "50";
        AppManager.getInstance().startPingTimer();
        AppManager.getInstance().addClientRequest(pingRequest);
    }

    private void checkIfPlayerIsAllowedToPlay() {
        if (AppManager.getInstance().loggedInUser.getNameID().equals(currentGame.getPlayer(currentGame.getTurnState().getCurrentPlayer()).getName())) {
            rollButton.setEnabled(true);
            // set current playing state
            if (currentGame.getTurnState().getRollTurn() == 1) {
                state = State.NEWPLAYER;
                Log.i(TAG, "Init State: " + state);
            } else if (currentGame.getTurnState().getRollTurn() == 3) {
                state = State.PLACE_SCORE;
                gameInfo.setText("Place your Score!");
                Log.i(TAG, "Init State: " + state);
                checkRules();
            } else {
                state = State.PLAYING;
                Log.i(TAG, "Init State: " + state);
            }
        } else {
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

        if (state == State.NEWPLAYER) {
            for (ImageView diceImage : diceImages) {
                diceImage.setTranslationX(DICE_START_POSITIONX);
                startFirstDiceAnimation();
            }
            Log.i(TAG, "InitDice(): 1");
        } else if (currentGame.getTurnState().getRollTurn() == 1){
            for (ImageView diceImage : diceImages) {
                diceImage.setTranslationX(DICE_START_POSITIONX);
                startFirstDiceAnimation();
            }
            Log.i(TAG, "InitDice(): Spectator first round");
        }
        else {
            Random rand = new Random();
            for (ImageView diceImage : diceImages) {
                diceImage.setRotation(rand.nextInt(360));
            }
            Log.i(TAG, "InitDice(): 3");
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
                if (AppManager.getInstance().loggedInUser.getNameID().equals(currentGame.getPlayer(currentGame.getTurnState().getCurrentPlayer()).getName())) {
                    rollButton.setText("Re roll");
                    rollButton.setEnabled(true);
                    state = State.PLAYING;
                    Log.i(TAG, "Init State: " + state);
                }
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
                    gameInfo.setText("Place your Score!");
                    rollButton.setEnabled(false);
                    state = State.PLACE_SCORE;
                    checkRules();
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
                for (int i = 0; i < diceImages.length; i++) {
                    if (dices[i].isSelected()) {
                        anyDiceSelected = true;
                    }
                }
                checkRules();
            }
        } else {
            diceImages[val].setBackground(null);
            dices[val].setSelected(false);
            Log.i(TAG, String.format("%s deselected!", dices[val].getDiceName()));
            if (state == State.PLACE_SCORE) {
                int count = 0;
                for (int i = 0; i < diceImages.length; i++) {
                    if (dices[i].isSelected()) {
                        count++;
                        Log.i(TAG, "Counter : " + count);
                    }
                }
                if (count == 0) {
                    anyDiceSelected = false;
                }
                checkRules();
            }
        }
    }

    private void resetColors() {
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
        Log.d(TAG, "checkRules True/False: " + anyDiceSelected);
        Arrays.sort(diceRule);

        int ok = getResources().getColor(R.color.colorFieldOK);
        int notOk = getResources().getColor(R.color.colorFieldNotOK);

        Log.i(TAG, Arrays.toString(diceRule));

        for (int i = 1; i < tables.get(x).getChildCount(); i++) {
            TableRow row = (TableRow) tables.get(x).getChildAt(i);
            TextView cellText = (TextView) row.getChildAt(0); // only one child (textview)
            boolean cellHasScore = Utilities.stringIsNumber(cellText.getText().toString());

            switch (i) {
                case 1: // ones
                    if (rules.singelSide(1, diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 2: // Twos
                    if (rules.singelSide(2, diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 3: //Threes
                    if (rules.singelSide(3, diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 4: //Fours
                    if (rules.singelSide(4, diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 5: //Fives
                    if (rules.singelSide(5, diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 6: //Six
                    if (rules.singelSide(6, diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 7: //Sub total
                    break;
                case 8: //Bonus
                    break;
                case 9: // Pair
                    if (rules.onePair(diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 10: // Two Pairs
                    if (rules.twoPair(diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 11: // 3 of a kind
                    if (rules.threeOfAKind(diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 12: //4 of a kind
                    if (rules.fourOfAKind(diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 13: // Small Straight
                    if (rules.smallStraight(diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 14: //Large Straight
                    if (rules.largeStraight(diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 15: //Full house
                    if (rules.fullHouse(diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
                    }
                    break;
                case 16: // Chance
                        if (!cellHasScore) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }

                    break;
                case 17: //Yatzy
                    if (rules.yatzy(diceRule) && !cellHasScore) {
                        row.setBackgroundColor(ok);
                        row.setClickable(true);
                    } else {
                        if (!cellHasScore && !anyDiceSelected) {
                            row.setBackgroundColor(ok);
                            row.setClickable(true);
                        } else {
                            row.setBackgroundColor(notOk);
                            row.setClickable(false);
                        }
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

    private int calculateSubSum() {
        int sum = 0;
        for (int i = 1; i < 7; i++) {
            TableRow row = (TableRow) tables.get(lastplayer).getChildAt(i);
            TextView cellText = (TextView) row.getChildAt(0); // only one child (textview)
            try {
                sum += Integer.parseInt(cellText.getText().toString());
            } catch (NumberFormatException e) {
                sum += 0;
                Log.e(TAG, e.getMessage());
            }
        }
        return sum;
    }
  
    // Calculated place score
    private int calculateScoreValue(int index) {
        switch (index) {
            case 1:
                return !anyDiceSelected ? 0 : caclculateSumSelectedDiceSide(1);
            case 2:
                return !anyDiceSelected ? 0 : caclculateSumSelectedDiceSide(2);
            case 3:
                return !anyDiceSelected ? 0 : caclculateSumSelectedDiceSide(3);
            case 4:
                return !anyDiceSelected ? 0 : caclculateSumSelectedDiceSide(4);
            case 5:
                return !anyDiceSelected ? 0 : caclculateSumSelectedDiceSide(5);
            case 6:
                return !anyDiceSelected ? 0 : caclculateSumSelectedDiceSide(6);
            case 7: // SubTotal do nothing here
                break;
            case 8: // Bonus do nothing here
                break;
            case 9:
                return !anyDiceSelected ? 0 : calculateSelectedDice();
            case 10:
                return !anyDiceSelected ? 0 : calculateSelectedDice();
            case 11:
                return !anyDiceSelected ? 0 : calculateSelectedDice();
            case 12:
                return !anyDiceSelected ? 0 : calculateSelectedDice();
            case 13: //Small straight fixed score: 15;
                return !anyDiceSelected ? 0 : 15;
            case 14: //Large straight fixed score: 20;
                return !anyDiceSelected ? 0 : 20;
            case 15:
                return !anyDiceSelected ? 0 : calculateAllDice();
            case 16:
                return calculateAllDice();
            case 17: // yatzy fixed score: 50
                return !anyDiceSelected ? 0 : 50;
            case 18: //Grand Total do nothing here
                break;
            default:
                Log.e(TAG, "Problem occurred!");
                break;
        }
        return -1;
    }

    private int calculateAllDice() {
        int sum = 0;
        for (int i = 0; i < diceImages.length; i++) {
            sum += dices[i].getDiceValue();
        }
        return sum;
    }

    private int checkBonus(int score) {
        int bonusSum = 0;

        if (score >= 63) {
            bonusSum = 50;
        }
        return bonusSum;
    }

    private int calculateSubSum(int player) {
        int sum = 0;
        for (int i = 1; i < 7; i++) {
            TableRow row = (TableRow) tables.get(player).getChildAt(i);
            TextView cellText = (TextView) row.getChildAt(0); // only one child (textview)
            try {
                sum += Integer.parseInt(cellText.getText().toString());
            } catch (NumberFormatException e) {
                sum += 0;
            }
        }
        return sum;
    }

    private int calculateTotalScore(int subSumValue, int bonusValue, int player) {
        int sum = 0;
        for (int i = 9; i < 18; i++) {
            TableRow row = (TableRow) tables.get(player).getChildAt(i);
            TextView cellText = (TextView) row.getChildAt(0); // only one child (textview)
            try {
                sum += Integer.parseInt(cellText.getText().toString());
            } catch (NumberFormatException e) {
                sum += 0;
            }
        }
        sum += subSumValue + bonusValue;
        return sum;
    }

    private int calculateSelectedDice() {
        int sum = 0;
        for (int i = 0; i < diceImages.length; i++) {
            if (dices[i].isSelected()) {
                sum += dices[i].getDiceValue();
            }
        }
        return sum;
    }

    private int caclculateSumSelectedDiceSide(int side) {
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
            headerPlayerName.setPadding(0, padding, 0, padding);
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
                scoreTextField.setPadding(0, padding, 0, padding);


                if (currentGame.getPlayer(i).getScoreBoardElement(f) == -1) {
                    scoreTextField.setText("");
                } else {
                    scoreTextField.setText(String.valueOf(currentGame.getPlayer(i).getScoreBoardElement(f)));
                }
                tableRow.addView(scoreTextField);

                //allows you to select a specific row
                tableRow.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (state == State.PLACE_SCORE) {
                            TableRow row = (TableRow) view;
                            int tableRowIndex = tableLayout.indexOfChild(row);

                            String value = String.valueOf(calculateScoreValue(tableRowIndex));
                            String index = String.valueOf(tableRowIndex - 1);
                            requestToServer = MessageFormat.format("19:{0}:{1}:{2}", currentGame.getGameID(), index, value);

                            TextView cellText = (TextView) row.getChildAt(0); // only one child (textview)
                            cellText.setText(String.valueOf(calculateScoreValue(tableRowIndex)));

                            //SubSum
                            TableRow subSum = (TableRow) tableLayout.getChildAt(7);
                            TextView subSumCellText = (TextView) subSum.getChildAt(0);
                            int subSumValue = calculateSubSum(currentGame.getTurnState().getCurrentPlayer());
                            subSumCellText.setText(String.valueOf(subSumValue));

                            //Bonus
                            TableRow bonus = (TableRow) tableLayout.getChildAt(8);
                            TextView bonusCellText = (TextView) bonus.getChildAt(0);
                            int bonusValue = checkBonus(subSumValue);
                            bonusCellText.setText(String.valueOf(bonusValue));

                            //Total score
                            TableRow totalScore = (TableRow) tableLayout.getChildAt(18);
                            TextView totalScoreCellText = (TextView) totalScore.getChildAt(0);
                            int totalScoreValue = calculateTotalScore(subSumValue, bonusValue, currentGame.getTurnState().getCurrentPlayer());
                            totalScoreCellText.setText(String.valueOf(totalScoreValue));

                            scoreHasBeenPlaced = true;
                            rollButton.setEnabled(true);
                            rollButton.setText("End Turn");
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
            tableCalculateSums(i);
        }
        // Add all tables in the linearLayout view
        for (TableLayout layout : tables) {
            l.addView(layout);
        }
    }

    private void tableCalculateSums(int i) {

        //SubSum
        TableRow subSum = (TableRow) tables.get(i).getChildAt(7);
        TextView subSumCellText = (TextView) subSum.getChildAt(0);
        int subSumValue = calculateSubSum(i);
        subSumCellText.setTypeface(null, Typeface.BOLD);
        subSumCellText.setText(String.valueOf(subSumValue));

        //Bonus
        TableRow bonus = (TableRow) tables.get(i).getChildAt(8);
        TextView bonusCellText = (TextView) bonus.getChildAt(0);
        int bonusValue = checkBonus(subSumValue);
        bonusCellText.setTypeface(null, Typeface.BOLD);
        bonusCellText.setText(String.valueOf(bonusValue));

        //Total score
        TableRow totalScore = (TableRow) tables.get(i).getChildAt(18);
        TextView totalScoreCellText = (TextView) totalScore.getChildAt(0);
        int totalScoreValue = calculateTotalScore(subSumValue, bonusValue, i);
        totalScoreCellText.setTypeface(null, Typeface.BOLD);
        totalScoreCellText.setText(String.valueOf(totalScoreValue));

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
    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    private View chatLayout;
    private RecyclerView recyclerViewChat;
    private Button send;
    private EditText editTextChat;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> chatList = new ArrayList<>();
    public static boolean doubleClicked = false;
    public static int positionClicked;
    private boolean ignore = false;

    public void initChat(View view) {

        recyclerViewChat = view.findViewById(R.id.rv_chat);
        recyclerViewChat.setLayoutManager(linearLayoutManager);

        editTextChat = view.findViewById(R.id.chat_box_area);
        send = view.findViewById(R.id.btn_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                //Should change this since we are sending it to server and then recieving it
                String message = editTextChat.getText().toString().trim();

                if (message != null && !TextUtils.isEmpty(message)) {

                    if (doubleClicked) {

                        int replyToIndex = chatList.get(positionClicked).msgIndex;

                        AppManager.getInstance().addClientRequest("37:" + currentGame.getGameID() + ":" + message + ":" + replyToIndex);


                        editTextChat.setText("");
                        editTextChat.setHint("");

                        doubleClicked = false;

                        chatAdapter.notifyDataSetChanged();

                    } else {

                        AppManager.getInstance().addClientRequest("37:" + currentGame.getGameID() + ":" + message + ":-1");
                        editTextChat.setText("");

                    }

                    if (chatAdapter != null) {
                        chatAdapter.notifyDataSetChanged();
                    }
                }

            }

        });


        //add array to chatAdapter
        chatAdapter = new ChatAdapter(chatList);
        recyclerViewChat.setAdapter(chatAdapter);


        //if we  double click the text
        chatAdapter.setOnItemClickListener(new ChatAdapter.ItemClickListener() {
            long lastClickedTimeStamp;

            @Override
            public void onItemClickListner(int position) {
                //instantiate a new variable with this current time
                long nowClickedTime = System.currentTimeMillis();

                //1. first time when clicked, only the time of currentTime (that is much more than getDoubleTapTimeout())
                //2. next time clicked, currTime is now a new instantiation of time (This happens when we have double clicked)
                if (nowClickedTime - lastClickedTimeStamp < ViewConfiguration.getDoubleTapTimeout() && !chatList.get(position).senderName.equals(AppManager.getInstance().loggedInUser.getNameID())) {

                    if (ignore) {

                        //reset settings
                        doubleClicked = false;
                        editTextChat.setHint("");
                        editTextChat.setText("");
                        ignore = false;
                        return;
                    }

                    // When we have removed our message, it's no longer activated, therefor we cant respond or double
                    //click the removed text (does not work without this line)
                    //if (linearLayoutManager.findViewByPosition(position).findViewById(R.id.chat_message_right).isActivated()) {

                    //currently clicked item will start animation for itself
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    linearLayoutManager.findViewByPosition(position).findViewById(R.id.chat_message_left).startAnimation(animation);

                    //When double clicked this is set to true
                    //so that next time we double click it will go to the if statement above and reset
                    //settings meaning we don't want to make a reply.
                    ignore = true;

                    Toast.makeText(getActivity(), "Double clicked", Toast.LENGTH_SHORT).show();
                    doubleClicked = true;
                    positionClicked = position;

                    //name of person replying to
                    String responseName = chatList.get(positionClicked).senderName;
                    editTextChat.setHint("Reply to " + responseName + ":");
                    // }

                }
                //1. first time after the if statement fail, we go down here and set lastTime to be curreTime variable
                lastClickedTimeStamp = nowClickedTime;

            }
        });

        //Swiping functions
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; //used if we want to re-arrange chat
            }

            //TODO change to logged in user
            //Disable certain directions
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //instead we will get global variable to get current user - this will enable us to only swipe our
                //recyclerView object/textView/chat
                if (!chatList.get(viewHolder.getAdapterPosition()).senderName.equals(AppManager.getInstance().loggedInUser.getNameID())) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            //On swipe removes message and replace it with removed message
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                switch (direction) {
                    case ItemTouchHelper.LEFT:

                        AppManager.getInstance().addClientRequest("391:" + currentGame.getGameID() + ":" + chatList.get(position));

//                        //Instead we will send this to server, and it will remove this position in a general method that
//                        //all other users will also use so this specific position is deleted
//                        chatList.remove(position);
//                        chatAdapter.notifyItemRemoved(position);
//
//                        Toast.makeText(getActivity(), "Message erased! " + chatList.size(), Toast.LENGTH_SHORT).show();
//
//                        //We remove the message and its place we we add
//                        //our own message this is also something that needs
//                        //to be sent to the server and returned back to a method
//                        //that will have the code underneath
//
//                        //adds the message to that same position we removed
//                        chatList.add(position, "delete:");
//                        chatAdapter.notifyDataSetChanged();

                        break;
                }
            }
        };

        //Attach to the recyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewChat);

    }


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
        gameInfo = view.findViewById(R.id.game_info);
        latency = view.findViewById(R.id.latenzy_game);
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
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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