package software.engineering.yatzy.appManagement;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import software.engineering.yatzy.R;
import software.engineering.yatzy.game.ChatMessage;
import software.engineering.yatzy.game.Game;
import software.engineering.yatzy.game.GameState;
import software.engineering.yatzy.game.Player;
import software.engineering.yatzy.game.PlayerParticipation;
import software.engineering.yatzy.game.TurnState;

/**
 * - Manages the Android Bind-Service (acts as bridge between application and Android Service)
 * - Holds (and updates) globally accessible variables.
 */

public class AppManager {

    // private static final String TAG = "Network AppManager";
    private static final String TAG = "Info";
    // Service. UI thread -> Service
    private NetworkService networkService;
    // Guide UI in case of being put in background during cloud connection phase
    public NetworkState networkState;
    private volatile boolean isBound;
    public volatile boolean appInFocus; // Redundant since isBound is true when app is in focus?

    // Handler of UI-thread. For communication: Service threads -> UI thread
    private Handler handler;
    // Holds a reference to the fragment currently displayed
    public Updatable currentFragment;
    // Reference to application context;
    private Context applicationContext;
    // To recognize a socket exception due to invalid sessionKey vs invalid login
    private boolean loginAttemptWithSessionKey;
    // To initiate fragment transactions
    private NavController navController;

    // ======================== GLOBAL VARIABLES =====================================

    // Holds data of logged in user, or null if on one is logged in
    public LoggedInUser loggedInUser;
    // List of active games for the client.
    public ArrayList<Game> gameList;
    // Top 3 high score names and scores.
    public ArrayList<HighScoreRecord> universalHighScores;
    // Searchable client names for creating new game
    public ArrayList<String> searchableNames;

    //variables that hold timer Data
    public long startTimeMillis;
    public long finalTimeMillis;
    public long latency;
    // ============================ SINGLETON =======================================

    private AppManager() {
        networkService = null;
        networkState = NetworkState.UNDEFINED;
        isBound = false;
        appInFocus = false;
        handler = new Handler();
        loginAttemptWithSessionKey = false;
        loggedInUser = null;
        currentFragment = null;
        gameList = new ArrayList<>();
        universalHighScores = new ArrayList<>();
        searchableNames = new ArrayList<>();
    }

    private static AppManager instance = null;

    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    // =========================== BOUND SERVICE ======================================

    //MainActivity: onCreate
    public void bindToService(Context context, NavController navController) {
        Log.i(TAG, "App requests binding to Android service");
        this.navController = navController;
        applicationContext = context;
        Intent intent = new Intent(applicationContext, NetworkService.class);
        applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        // bindService: A call to the the service's onBind() method
        // BIND_AUTO_CREATE: The service will be created if it hasn't already been created
    }

    //MainActivity: onDestroy 
    public void unbindFromService() {
        if (isBound) {
            stopServiceThreads();
            Log.i(TAG, "App unbound from Android service");
            applicationContext.unbindService(serviceConnection);
            isBound = false;
        }
        appInFocus = false;
    }

    public void stopServiceThreads() {
        if (isBound) {
            networkService.stopConnectionToCloudServer();
            networkService.serviceShutDown = true;
        }
    }

    // Remove later:
    private int ccc = 0;
    private boolean firstBind = true;

    // Called once the client-server connection (connection to the Service) has been established or disconnected
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Will trigger when the client is successfully bound to the service, via bindService()
            // IBinder: The link between the client and the service. Will be used for the connection.

            NetworkService.MyBinder binder = (NetworkService.MyBinder) service;

            //Obtain a reference to the service instance
            networkService = binder.getService();
            //Indicate that a connection has been successfully established
            isBound = true;
            appInFocus = true;

            readUserDataFromCache(); // Moved here from Setup Fragment

            Log.i(TAG, "App bound to Android service");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Will trigger on service connection exception
            stopServiceThreads();
            Log.i(TAG, "onServiceDisconnected " + isBound + " COUNT " + ccc++);
            isBound = false;
        }
    };

    // ========================= ADD REQUEST TO SERVER ========================================

    public void addClientRequest(String requestToServer) {
        if (isBound) {
            try {
                networkService.requestsToServer.put(requestToServer);
            } catch (InterruptedException e) {
                // Handle ??
            }
        }
    }

    // ======================== PROCESS SERVER REQUESTS =======================================
    // Used by Service thread(s) to notify UI and pass update data
    // The data passed conform to the application protocol and is decoded in update()


    public void update(String command) {
        Log.i(TAG, "REQUEST FROM SERVER: " + command);

        String[] commands = command.split(":");
        try {

            switch (commands[0]) {
                case "2":
                    loginResult(commands);
                    break;
                case "5":
                    reconnectionResult(commands);
                    break;
                case "15":
                    receiveGamePENDING(commands);
                    break;
                case "16":
                    receiveGame(commands);
                    break;
                case "18":
                    rollResult(commands);
                    break;
                case "20":
                    turnResult(commands);
                    break;
                case "21":
                    gameStart(commands);
                    break;
                case "22":
                    gameEnd(commands);
                    break;
                case "23":
                    updateIndividualGameStats(commands);
                    break;
                case "24":
                    updateUniversalHighScoreList(Arrays.copyOfRange(commands, 1, 7));
                    break;
                case "31":
                    searchableClientNames(Arrays.copyOfRange(commands, 1, commands.length));
                    break;
                case "34":
                    updateInvitationReply(commands);
                    break;
                case "36":
                    receiveChatMessages(commands);
                    break;
                case "38":
                    receiveOneChatMessage(commands);
                    break;
                case "40":
                    exceptionFromCloud(commands[1]);
                    break;
                case "41": // Connection to cloud lost/terminated
                    lostCloudConnection(commands[1]);
                    break;
                case "51":
                    //ping sent from server
                    calculatePingTimer();
                    break;
                default:
                    //writeToast("Unknown request from server: " + command);
                    break;
            }
        } catch (Exception e) {
            Log.i(TAG, "update exception " + e.getMessage(), e);
        }
    }

    // #2
    private void loginResult(String[] commands) throws Exception {
        Log.i(TAG, "From server: Result of manual login");
        if (commands[1].equals("ok")) {
            // Initiate loggedInUser
            String nameID = commands[3];
            String sessionKey = commands[4];
            int gamesPlayed = Integer.parseInt(commands[5]);
            int highScore = Integer.parseInt(commands[6]);
            loggedInUser = new LoggedInUser(nameID, sessionKey, gamesPlayed, highScore);
            // Write user data to cache
            writeUserToCache();
            // Initiate universal top 3 high score
            updateUniversalHighScoreList(Arrays.copyOfRange(commands, 7, 13));
            // Direct to main menu
            networkState = NetworkState.ALLOWED;
            if (appInFocus) {
                navController.navigate(R.id.navigation_main);
                networkState = NetworkState.ENTERED;
            }
        } else {
            // Pass exception message to Login fragment
            if (appInFocus) {
                currentFragment.update(40, -1, commands[2]);
            }
        }
    }

    // #5
    private void reconnectionResult(String[] commands) throws Exception {
        Log.i(TAG, "From server: Result of automatic login with session key");
        if (commands[1].equals("ok")) {
            loggedInUser.gamesPlayed = Integer.parseInt(commands[3]);
            loggedInUser.highScore = Integer.parseInt(commands[4]);
            updateUniversalHighScoreList(Arrays.copyOfRange(commands, 5, 11));
            // Direct to main menu
            networkState = NetworkState.ALLOWED;
            if (appInFocus) {
                navController.navigate(R.id.navigation_main);
                networkState = NetworkState.ENTERED;
            }
        } else {
            // Direct to manual login
            networkState = NetworkState.LOGIN;
            if (appInFocus) {
                navController.navigate(R.id.navigation_Login);
            }
        }
    }

    // #15
    // A not started game. For those that are not host, this is the invitation
    private void receiveGamePENDING(String[] commands) throws Exception {
        Log.i(TAG, "From server: PENDING game received");
        int count = 0;
        int gameID = Integer.parseInt(commands[++count]);
        String gameName = commands[++count];
        GameState gameState = GameState.valueOf(commands[++count]);
        // Assign an initial scoreboard:
        int[] initialScoreboard = new int[18];
        Arrays.fill(initialScoreboard, -1);
        // Players:
        ArrayList<Player> playerList = new ArrayList<>();
        while (true) {
            String nameID = commands[++count];
            PlayerParticipation participation = PlayerParticipation.valueOf(commands[++count]);
            playerList.add(new Player(nameID, participation, initialScoreboard));
            if (commands[++count].equals("null")) {
                break;
            }
            //count++;
        }
        TurnState initialTurnState = new TurnState();
        // Create new game and add it to list of games:
        Game newGame = new Game(gameID, gameName, gameState, initialTurnState, playerList, "notDefined", 0);
        gameList.add(newGame);
        if (appInFocus) {
            currentFragment.update(15, gameID, null);
        } else {
            // Notification if not host: hostName has invited you to a game
        }
    }

    // #16
    // ONGOING or ENDED game (upon app login)
    private void receiveGame(String[] commands) throws Exception {
        Log.i(TAG, "From server: Game received");
        int count = 0;
        int gameID = Integer.parseInt(commands[++count]);
        String gameName = commands[++count];
        // TurnState:
        int currentPlayer = Integer.parseInt(commands[++count]);
        int rollNr = Integer.parseInt(commands[++count]);
        int[] diceValues = new int[5];
        for (int i = 0; i < diceValues.length; i++) {
            diceValues[i] = Integer.parseInt(commands[++count]);
        }
        TurnState turnState = new TurnState(currentPlayer, rollNr, diceValues);
        // Players:
        ArrayList<Player> playerList = new ArrayList<>();
        while (true) {
            String nameID = commands[++count];
            PlayerParticipation participation = PlayerParticipation.valueOf(commands[++count]);
            int[] scoreboard = new int[18];
            for (int i = 0; i < scoreboard.length; i++) {
                scoreboard[i] = Integer.parseInt(commands[++count]);
            }
            playerList.add(new Player(nameID, participation, scoreboard)); // Initiate
            if (commands[++count].equals("null")) {
                break;
            }
            //count++; ??
        }
        // Remaining data:
        GameState gameState = GameState.valueOf(commands[++count]);
        String winnerName = commands[++count];
        int winnerScore = Integer.parseInt(commands[++count]);
        // Create Game
        Game receivedGame = new Game(gameID, gameName, gameState, turnState, playerList, winnerName, winnerScore);
        // Add to global list
        gameList.add(receivedGame);
        // Notify current fragment
        if (appInFocus) {
            currentFragment.update(16, gameID, null);
        }
    }

    // #18
    private void rollResult(String[] commands) throws Exception {
        Log.i(TAG, "From server: Result of roll");
        int count = 0;
        int gameID = Integer.parseInt(commands[++count]);
        // turnState
        int currentPlayer = Integer.parseInt(commands[++count]);
        int rollNr = Integer.parseInt(commands[++count]);
        int[] diceValues = new int[5];
        for (int i = 0; i < diceValues.length; i++) {
            diceValues[i] = Integer.parseInt(commands[++count]);
        }
        TurnState turnState = new TurnState(currentPlayer, rollNr, diceValues);
        for (Game game : gameList) {
            if (game.getGameID() == gameID) {
                game.setTurnState(turnState);
                for (int bit = 0; bit < game.getTurnState().rolledDiceBitMap.length; bit++) {
                    game.getTurnState().rolledDiceBitMap[bit] = (commands[++count].equals("1") ? true : false);
                }
                break;
            }
        }
        // Notify current fragment
        if (appInFocus) {
            currentFragment.update(18, gameID, null);
        }
    }

    // #20
    private void turnResult(String[] commands) throws Exception {
        Log.i(TAG, "From server: Result of placing point i scoreboard");
        int count = 0;
        int gameID = Integer.parseInt(commands[++count]);
        // turnState
        int currentPlayer = Integer.parseInt(commands[++count]); // Next player's turn now
        int rollNr = Integer.parseInt(commands[++count]);   // Should hence be 1 now
        int[] diceValues = new int[5];
        for (int i = 0; i < diceValues.length; i++) {
            diceValues[i] = Integer.parseInt(commands[++count]);
        }
        TurnState turnState = new TurnState(currentPlayer, rollNr, diceValues);
        // Updates in previous player's scoreboard
        int indexOfPreviousPlayer = Integer.parseInt(commands[++count]);
        int scoreboardIndex = Integer.parseInt(commands[++count]);
        int scoreboardValue = Integer.parseInt(commands[++count]);
        // Commit updates
        for (Game game : gameList) {
            if (game.getGameID() == gameID) {
                game.setTurnState(turnState);
                game.updateScoreBoard(indexOfPreviousPlayer, scoreboardIndex, scoreboardValue);
                if (game.getPlayer(rollNr).getName().equals(loggedInUser.getNameID())) {
                    // Notification: Your turn in "GameName"
                }
            }
        }
        if (appInFocus) {
            currentFragment.update(20, gameID, null);
        }
    }

    // #21
    private void gameStart(String[] commands) throws Exception {
        Log.i(TAG, "From server: Initiates a game start");
        int count = 0;
        int gameID = Integer.parseInt(commands[++count]);
        GameState gameState = GameState.valueOf(commands[++count]);
        // turnState
        int currentPlayer = Integer.parseInt(commands[++count]); // First player's turn now
        int rollNr = Integer.parseInt(commands[++count]);   // Should hence be 1 now
        int[] diceValues = new int[5];
        for (int i = 0; i < diceValues.length; i++) {
            diceValues[i] = Integer.parseInt(commands[++count]);
        }
        TurnState turnState = new TurnState(currentPlayer, rollNr, diceValues);
        // Players: Now reduced only to host + those that have accepted
        ArrayList<Player> playerList = new ArrayList<>();
        while (true) {
            String nameID = commands[++count];
            PlayerParticipation participation = PlayerParticipation.valueOf(commands[++count]);
            // Assign an initial scoreboard:
            int[] initialScoreboard = new int[18];
            Arrays.fill(initialScoreboard, -1);
            playerList.add(new Player(nameID, participation, initialScoreboard));
            if (commands[++count].equals("null")) {
                break;
            }
            // count++; // ??
        }
        for (Game game : gameList) {
            if (game.getGameID() == gameID) {
                game.setState(gameState);
                game.setTurnState(turnState);
                game.updatePlayerList(playerList);
                if (game.getPlayer(rollNr).getName().equals(loggedInUser.getNameID())) {
                    // Notification: Your turn in "GameName"
                }
            }
        }
        if (appInFocus) {
            currentFragment.update(21, gameID, null);
        }
    }

    // #22
    private void gameEnd(String[] commands) throws Exception {
        Log.i(TAG, "From server: A game has ended");
        // Server knows when all 15 rounds are over
        int count = 0;
        int gameID = Integer.parseInt(commands[++count]);
        GameState gameState = GameState.valueOf(commands[++count]);
        // Updates in previous player's scoreboard
        int indexOfLastPlayer = Integer.parseInt(commands[++count]);
        int scoreboardIndex = Integer.parseInt(commands[++count]);
        int scoreboardValue = Integer.parseInt(commands[++count]);
        // Winner data
        String winnerName = commands[++count];
        int winnerScore = Integer.parseInt(commands[++count]);
        // Commit updates
        for (Game game : gameList) {
            if (game.getGameID() == gameID) {
                game.setState(gameState);
                game.updateScoreBoard(indexOfLastPlayer, scoreboardIndex, scoreboardValue);
                game.setWinnerName(winnerName);
                game.setWinnerScore(winnerScore);
            }
        }
        if (appInFocus) {
            currentFragment.update(22, gameID, null);
        } else {
            // Notification: winnerName won game gameName (XXXp)
        }
    }

    // #23
    private void updateIndividualGameStats(String[] commands) throws Exception {
        Log.i(TAG, "From server: Update of this player's game stats");
        loggedInUser.gamesPlayed = Integer.parseInt(commands[1]);
        boolean newIndividualHighScore = commands[2].equals("new");
        if (newIndividualHighScore) {
            loggedInUser.highScore = Integer.parseInt(commands[3]);
        }
        if (appInFocus) {
            currentFragment.update(23, -1, null);
        }
    }

    // #24
    private void updateUniversalHighScoreList(String[] top3) throws NumberFormatException {
        Log.i(TAG, "From server: Update of universal high score list");
        universalHighScores.clear();
        universalHighScores.add(new HighScoreRecord(top3[0], Integer.parseInt(top3[1]))); // #1
        universalHighScores.add(new HighScoreRecord(top3[2], Integer.parseInt(top3[3]))); // #2
        universalHighScores.add(new HighScoreRecord(top3[4], Integer.parseInt(top3[5]))); // #3
        if (appInFocus) {
            currentFragment.update(24, -1, null);
        }
    }

    // #31
    private void searchableClientNames(String[] commands) throws Exception {
        searchableNames.clear();
        searchableNames.addAll(Arrays.asList(commands));
        if (appInFocus) {
            currentFragment.update(31, -1, null);
        }
    }

    // #34
    private void updateInvitationReply(String[] commands) throws Exception {
        Log.i(TAG, "From server: Result of invitation reply");
        int gameID = Integer.parseInt(commands[1]);
        PlayerParticipation participation = PlayerParticipation.valueOf(commands[2]);

        for (int game = 0; game < gameList.size(); game++) {
            if (gameList.get(game).getGameID() == gameID) {
                if (participation == PlayerParticipation.DECLINED) {
                    gameList.remove(game);
                } else {
                    gameList.get(game).getPlayerByName(loggedInUser.getNameID()).participation = participation;
                }
                break;
            }
        }
        if (appInFocus) {
            currentFragment.update(34, gameID, null);
        }
    }

    // #36
    private void receiveChatMessages(String[] commands) throws Exception {
        Log.i(TAG, "From server: Receiving list of chat messages");
        ArrayList<ChatMessage> messageList = new ArrayList<>();
        int count = 0;
        int gameID = Integer.parseInt(commands[++count]);
        String receiveType = commands[++count];

        while (true) {
            int msgIndex = Integer.parseInt(commands[++count]);
            String senderName = commands[++count];
            String msgContent = commands[++count];
            String timeStamp = commands[++count].replace(";", ":");
            int replyToMsgIndex = Integer.parseInt(commands[++count]);

            ChatMessage msg = new ChatMessage(msgIndex, senderName, msgContent, timeStamp, replyToMsgIndex);
            messageList.add(msg);

            if (commands[++count].equals("null")) {
                break;
            }
        }
        switch (receiveType) {
            case "initialList":
                getGameByGameID(gameID).setMessages(messageList);
                break;
            case "appendList":
                getGameByGameID(gameID).appendMultipleMessages(messageList);
                break;
            default:
                // Handle??
                break;
        }
        if (appInFocus) {
            currentFragment.update(36, gameID, null);
        }
    }

    // #38
    private void receiveOneChatMessage(String[] commands) throws Exception {
        Log.i(TAG, "From server: Receiving one chat messages");
        int count = 0;
        int gameID = Integer.parseInt(commands[++count]);

        int msgIndex = Integer.parseInt(commands[++count]);
        String senderName = commands[++count];
        String msgContent = commands[++count];
        String timeStamp = commands[++count].replace(";", ":");
        int replyToMsgIndex = Integer.parseInt(commands[++count]);

        ChatMessage msg = new ChatMessage(msgIndex, senderName, msgContent, timeStamp, replyToMsgIndex);
        getGameByGameID(gameID).appendOneMessage(msg);

        if (appInFocus) {
            currentFragment.update(38, gameID, null);
        }
    }

    // #40
    private void exceptionFromCloud(String exceptionMessage) {
        Log.i(TAG, "From server: Exception message");
        if (appInFocus) {
            currentFragment.update(40, -1, exceptionMessage);
        }
    }

    // #41
    private void lostCloudConnection(String cause) {
        gameList.clear();
        universalHighScores.clear();
        boolean unintendedClose = cause.equals("unintended");
        Log.i(TAG, "#41: Handling " + (unintendedClose ? "unintended" : "intended") + " server connection loss");

        if (unintendedClose) {
            if (networkState == NetworkState.ALLOWED || networkState == NetworkState.ENTERED) {
                if (isBound) {
                    stopServiceThreads();
                    readUserDataFromCache();
                }
            } else {
                //bindToService(applicationContext, navController);
                if (isBound) {
                    stopServiceThreads();
                }
                if (appInFocus && networkState == NetworkState.UNDEFINED) {
                    //currentFragment.update(40, -1, "Unable to connect to cloud server");
                    navController.navigate(R.id.navigation_Login);
                }
                if (appInFocus && networkState == NetworkState.LOGIN) {
                    currentFragment.update(40, -1, "Unable to connect to cloud server");
                }
            }
        }
    }

    public Game getGameByGameID(int gameID) {
        for (Game game : gameList) {
            if (game.getGameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    // =========================== ESTABLISH CLOUD CONNECTION ======================================

    // Parameter loginRequest can be gathered from cache file (SetupFragment) or manual log in (LoginFragment)
    public void establishCloudServerConnection(final String loginRequest) {
        // Tries connecting for 10 seconds
        new Thread(new Runnable() {
            volatile boolean connected = false;
            volatile int attempt = 0;
            volatile boolean requestAdded = false;

            public void run() {
                if (isBound) {
                    networkService.socketException = false;
                    networkService.stopConnectionToCloudServer();
                }
                for (attempt = 0; attempt < 10; attempt++) {
                    Log.i(TAG, "Establish server connection " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!requestAdded && !networkService.socketException) {
                        // Run on UI thread
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Client (UI thread) connected to Android bound service
                                if (isBound) { //if (isBound && appInFocus) {
                                    // Connect to cloud, or ignore if already connected
                                    networkService.connectToCloudServer(handler);
                                    // Verify successfully launched connection thread
                                    connected = networkService.inputThreadRunning;
                                    //connected = networkService.connectedToCloud;
                                    if (connected) {
                                        Log.i(TAG, "CONNECTED to server");
                                        try {
                                            // Add log in request to be sent to cloud server.
                                            networkService.requestsToServer.put(loginRequest);
                                            requestAdded = true;
                                        } catch (InterruptedException e) {
                                            //writeToast("Unable to add login request");
                                        }
                                    }
                                }
                                if ((attempt == 9 && !connected)) {
                                    networkState = NetworkState.LOGIN;
                                }
                            }
                        });
                    }
                    if (connected) {
                        return;
                    } else if (networkService.socketException) {
                        networkState = NetworkState.LOGIN;
                        return;
                    }
                }
            }
        }).start();
    }

    // =========================== WRITE TO CACHE ======================================

    private void writeUserToCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Writing user data to cache");
                String filePath = applicationContext.getCacheDir() + "userdata"; // Shouldn't it be "/userdata" ??
                try (ObjectOutputStream objectOutput = new ObjectOutputStream(new FileOutputStream(new File(filePath)))) {
                    objectOutput.writeObject(loggedInUser);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                    //writeToast("Unable to write user to cache");
                }
            }
        }).start();
    }

    // =========================== READ FROM CACHE ======================================

    //Reconnection with sessionKey
    public void readUserDataFromCache() {
        //final Handler setupHandler = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i(TAG, "Read user from file " + Thread.currentThread().getName());

                boolean successfulCacheRead = false;
                String message = "";

                for (int attempt = 0; attempt < 4; attempt++) {
                    try {
                        Thread.sleep(750);

                        if (isBound) {
                            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File(applicationContext.getCacheDir() + "userdata")))) {
                                //Reads from "file" and casts to LoggedInUser-object.
                                loggedInUser = (LoggedInUser) objectInputStream.readObject();
                                //Retrieves information needed to reconnect.
                                String userName = loggedInUser.getNameID();
                                String sessionKey = loggedInUser.getSessionKey();
                                if (userName.equals("")) {
                                    throw new FileNotFoundException("Cache content erased");
                                } else {
                                    message = "4:" + userName + ":" + sessionKey;
                                    successfulCacheRead = true;
                                }
                            } catch (FileNotFoundException e) {
                                message = "Cache history empty";
                            } catch (IOException e) {
                                message = "Failed Setup Stream";
                            } catch (ClassNotFoundException e) {
                                message = "File empty, Log In";
                            } finally {
                                postResult(successfulCacheRead, message);
                            }
                            break;
                        }
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                    if (attempt == 3) {
                        postResult(false, "Unable to access cache");
                        break;
                    }
                }
            }

            private void postResult(final boolean successfulCacheRead, final String message) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setupServerConnection(successfulCacheRead, message);
                    }
                });
            }
        }).start();
    }

    private void setupServerConnection(boolean successfulCacheRead, String message) {
        //Precaution: Make sure connection threads are closed before new are launched
        if (isBound) {
            // If for some reason there is a cloud connection thread already running: Stop it so it can be re-started.
            networkService.stopConnectionToCloudServer();
        }
        // If user data was successfully read from cache: use it as log in criteria to cloud server
        if (successfulCacheRead) {
            loginAttemptWithSessionKey = true;
            //spinner.setVisibility(View.VISIBLE);
            establishCloudServerConnection(message);
        } else {
            // If user data was NOT successfully read from cache; direct to manual log in
            loggedInUser = null;
            loginAttemptWithSessionKey = false;
            if (appInFocus) {
                navController.navigate(R.id.navigation_Login);
            }
            Log.i(TAG, message);
        }
    }

    public void calculatePingTimer() {
        finalTimeMillis = System.currentTimeMillis();
        latency = finalTimeMillis - startTimeMillis;
        currentFragment.update(51, -1, null);
        Log.d("Info", "Latency = " + String.valueOf(latency));
    }

    public void startPingTimer() {
        startTimeMillis = System.currentTimeMillis();
    }

    public void logOutUser() {
        //set loggedInUser to null values.
        loggedInUser = new LoggedInUser("", null, 0, 0);
        writeUserToCache();

        try {
            String logOutRequest = "3";
            addClientRequest(logOutRequest);
        } catch (Exception e) {
            //Ignore
        }

        final Handler logOutHandler = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    //Ignores since in seperate thread
                }

                logOutHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (appInFocus && isBound) {
                            //Stop connection to cloud
                            networkService.stopConnectionToCloudServer();
                            //return to loginScreen
                            navController.navigate(R.id.navigation_Login);
                        }
                    }
                });
            }
        }).start();
    }
}
