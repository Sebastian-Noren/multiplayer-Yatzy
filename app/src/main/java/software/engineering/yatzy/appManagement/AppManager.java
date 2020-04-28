package software.engineering.yatzy.appManagement;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

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
import software.engineering.yatzy.Utilities;
import software.engineering.yatzy.game.Game;

/**
 *  - Manages the Android Bind-Service (acts as bridge between application and Android Service)
 *  - Holds (and updates) globally accessible variables.
 */

public class AppManager {

    private static final String TAG = "Network AppManager";

    // Service. UI thread -> Service
    private NetworkService networkService;
    private volatile boolean isBound;
    public volatile boolean appInFocus; // Redundant since isBound is true when app is in focus?

    // Handler of UI-thread. For communication: Service threads -> UI thread
    private Handler handler;
    // Holds a reference to the fragment currently displayed
    public Updatable currentFragment;
    //private MainActivity mainActivity;
    private Context applicationContext;
    // To recognize a socket exception due to invalid sessionKey vs invalid login
    private boolean loginAttemptWithSessionKey;
    // To initiate fragment transactions
    private NavController navController;

    // ======================== GLOBAL VARIABLES =====================================

    // Holds data of logged in user, or null if on one is logged in
    public LoggedInUser loggedInUser;
    // List of active games for the client.
    public ArrayList<Game> myGames;
    // Top 3 high score names and scores.
    public ArrayList<HighScoreRecord> universalHighScores;

    // ============================ SINGLETON =======================================

    private AppManager() {
        networkService = null;
        isBound = false;
        appInFocus = false;
        handler = new Handler();
        loginAttemptWithSessionKey = false;
        loggedInUser = null;
        currentFragment = null;
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
            //networkService.stopConnectionToCloudServer();
            Log.d(TAG, "unbindFromService");
            applicationContext.unbindService(serviceConnection);
            Log.d(TAG, "unbindFromService ");
            isBound = false;
        }
        appInFocus = false;

        // networkService.stopConnectionToCloudServer();
    }

    public void stopServiceThreads() {
        networkService.stopConnectionToCloudServer();
        networkService.serviceShutDown = true;
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

            Log.d(TAG, "onServiceConnected " + isBound + " COUNT " + ccc++);

            if(firstBind) {
                networkService.test(handler);
                firstBind = false;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Will trigger on service connection exception
            Log.d(TAG, "onServiceDisconnected " + isBound + " COUNT " + ccc++);
            isBound = false;
        }
    };

    // ============================ TEST =========================================
    public volatile int testInt = 0;

    public void update() {
        if(isBound && appInFocus) {
            //Utilities.toastMessage(applicationContext, "Android Update " + testInt);
            // Log.d(TAG, "Android Update " + testInt);
        }

    }

    // ========================= ADD REQUEST TO SERVER ========================================

    public void addClientRequest(String requestToServer) {
        if(isBound) {
            try {
                networkService.requestsToServer.put(requestToServer);
            }catch(InterruptedException e) {
                // Handle ??
            }
        }
    }

    // ======================== PROCESS SERVER REQUESTS =======================================
    // Used by Service thread(s) to notify UI and pass update data
    // The data passed conform to the application protocol and is decoded in update()


    public void update(String command) {

            String[] commands = command.split(":");

            switch (commands[0]) {
                case "2":
                    loginResult(commands);
                    break;
                case "5":
                    reconnectionResult(commands);
                    break;
                case "16":
                    receiveOneGame(commands);
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
                    updateIndividualHighScore(commands[1]);
                    break;
                case "24":
                    updateUniversalHighScoreList(Arrays.copyOfRange(commands, 1, 6));
                    break;
                case "34":
                    updateInvitationReply(commands);
                    break;
                case "40":
                    exceptionFromCloud(commands[1]);
                    break;
                case "41": // Connection to cloud lost/terminated
                    lostCloudConnection();
                    break;
                default:
                    //writeToast("Unknown request from server: " + command);
                    break;
            }
    }

    private void loginResult(String[] commands) {
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
            updateUniversalHighScoreList(Arrays.copyOfRange(commands, 7, 12));
            // Direct to main menu
            navController.navigate(R.id.navigation_main);
        } else {
            // Pass exception message to Login fragment
            currentFragment.update(40, -1, commands[2]);
        }
    }

    private void reconnectionResult(String[] commands) {
        if (commands[1].equals("ok")) {
            loggedInUser.gamesPlayed = Integer.parseInt(commands[3]);
            loggedInUser.highScore = Integer.parseInt(commands[4]);
            updateUniversalHighScoreList(Arrays.copyOfRange(commands, 5, 10));
            // Direct to main menu
            navController.navigate(R.id.navigation_main);
        } else {
            // Direct to manual login
            if(appInFocus) {
                navController.navigate(R.id.navigation_Login);
            }
        }
    }

    private void receiveOneGame(String[]commands) {
        // Implement
    }

    private void rollResult(String[]commands) {
        // Implement
    }

    private void turnResult(String[]commands) {
        // Implement
    }

    private void gameStart(String[]commands) {
        // Implement
    }

    private void gameEnd(String[]commands) {
        // Implement
    }

    private void updateIndividualHighScore(String highScore) {
        // Implement
    }

    private void updateInvitationReply(String[]commands) {
        // Implement
    }

    private void exceptionFromCloud(String exceptionMessage) {
        if(appInFocus) {
            currentFragment.update(40, -1, exceptionMessage);
        }
    }

    private void updateUniversalHighScoreList(String[] top3) {
        universalHighScores.clear();
        universalHighScores.add(new HighScoreRecord(top3[1], Integer.parseInt(top3[2]))); // #1
        universalHighScores.add(new HighScoreRecord(top3[3], Integer.parseInt(top3[4]))); // #2
        universalHighScores.add(new HighScoreRecord(top3[5], Integer.parseInt(top3[6]))); // #3
        if(appInFocus) {
            currentFragment.update(24, -1, null);
        }
    }

    private void lostCloudConnection() {
        //writeToast("No cloud connection");
        // getSupportActionBar().hide();
        if (loginAttemptWithSessionKey) {
            //fragmentTransaction("login");
        } else {
            //fragmentTransaction("setup");
        }
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
                }
                for (attempt = 0; attempt < 10; attempt++) {
                    Log.d(TAG, "Establish connection " + Thread.currentThread().getName());
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
                                        Log.d(TAG, "CONNECTED");
                                        try {
                                            // Add log in request to be sent to cloud server.
                                            networkService.requestsToServer.put(loginRequest);
                                            requestAdded = true;
                                        } catch (InterruptedException e) {
                                            //writeToast("Unable to add login request");
                                        }
                                    }
                                }
                                if (attempt == 9 && !connected) {
                                    if(appInFocus) {
                                        currentFragment.update(40, -1, "Unable to connect to cloud server" );
                                    }
                                }
                            }
                        });
                    }
                    if (connected || networkService.socketException) {
                        return;
                    }
                }
            }
        }).start();
    }

    // =========================== WRITE TO CACHE ======================================

    public void writeUserToCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = applicationContext.getCacheDir() + "userdata"; // Shouldn't it be "/userdata" ??
                try (ObjectOutputStream objectOutput = new ObjectOutputStream(new FileOutputStream(new File(filePath)))) {
                    objectOutput.writeObject(loggedInUser);
                } catch (IOException e) {
                    //writeToast("Unable to write user tho cache");
                }
            }
        }).start();
    }

    // =========================== READ FROM CACHE ======================================

    //Reconnection with sessionKey
    private void readUserDataFromCache() {
        //final Handler setupHandler = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "Read user from file   " + Thread.currentThread().getName());

                boolean successfulCacheRead = false;
                String message = "";

                try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File(applicationContext.getCacheDir() + "userdata")))) {
                    //Reads from "file" and casts to LoggedInUser-object.
                    loggedInUser = (LoggedInUser) objectInputStream.readObject();
                    //Retrieves information needed to reconnect.
                    String userName = loggedInUser.getNameID();
                    String sessionKey = loggedInUser.getSessionKey();
                    if(userName.equals("")) {
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
            if(appInFocus) {
                navController.navigate(R.id.navigation_Login);
            }
            Log.d(TAG, message);
        }
    }

}
