package software.engineering.yatzy.appManagement;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import software.engineering.yatzy.Utilities;

/**
 *  - Manages the Android Bind-Service (acts as bridge between application and Android Service)
 *  - Holds (and updates) globally accessible variables.
 */

public class AppManager implements Updatable {

    // Service. UI thread -> Service
    public NetworkService networkService;
    public volatile boolean isBound;
    public volatile boolean appInFocus; // Redundant since isBound is true when app is in focus?

    // Handler of UI-thread. For communication: Service threads -> UI thread
    public Handler handler;

    // Holds data of logged in user, or null if on one is logged in
    public LoggedInUser loggedInUser;

    // Holds an updatable reference to the main activity (to be passed)
    //private Updatable mainActivity = this;

    // Holds a reference to the fragment currently displayed
    public Updatable currentFragment;
    //private MainActivity mainActivity;
    private Context applicationContext;

    // To recognize a socket exception due to invalid sessionKey vs invalid login
    public boolean loginAttemptWithSessionKey;

    // ============================ SINGLETON =======================================

    private AppManager() {
        networkService = null;
        isBound = false;
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

    // ====================== START AND BIND SERVICE =====================================

    //MainActivity: onCreate
    public void startService(Context context) {
        applicationContext = context;
        Intent intent = new Intent(applicationContext, NetworkService.class);
        applicationContext.startService(intent);
        // startService: Server is alive for client(s) to bind & unbind to it, until stopSelf() is called in onTaskRemoved().
    }

    //MainActivity: onResume
    public void bindToService() {
        Intent intent = new Intent(applicationContext, NetworkService.class);
        applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        // bindService: A call to the the service's onBind() method
        // BIND_AUTO_CREATE: The service will be created if it hasn't already been created
        appInFocus = true;
    }

    //MainActivity: onPause
    public void unBindFromService() {
        if (isBound) {
            // networkService.stopConnectionToCloudServer(); Uncomment once implemented
            Log.d("NetworkActivity", "ON PAUSE");
            applicationContext.unbindService(serviceConnection);
            isBound = false;
        }
        appInFocus = false;
    }

    //MainActivity: onDestroy
    public void killService() {

    }

    // ============================ TEST =========================================
    public volatile int testInt = 0;

    @Override
    public void update(String command) {
        if(isBound) { //appInFocus
            Utilities.toastMessage(applicationContext, "Android Update " + testInt);
            Log.d("NetworkActivity2", "Android Update " + testInt);
        }

    }

    // ===========================================================================

    public void onResume(Context context) {
        applicationContext = context;
        // The Android service will be (re)started, or connected to if already running
        //startService();
        appInFocus = true;
    }

    public void onPause() {
        if (isBound) {
            // networkService.stopConnectionToCloudServer(); Uncomment once implemented
            Log.d("NetworkActivity", "ON PAUSE");
            applicationContext.unbindService(serviceConnection);
        }
        isBound = false;
        appInFocus = false;

        Log.d("NetworkActivity", "is bound " + isBound);
    }

    // Server is alive for client(s) to bind & unbind to it, until onTaskRemoved is called.
    private void startService() {
        Intent intent = new Intent(applicationContext, NetworkService.class);
        //applicationContext.startService(intent); // test to remove
        applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        // bindService: A call to the the service's onBind() method
        // BIND_AUTO_CREATE: The service will be created if it hasn't already been created
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

            Log.d("NetworkActivity", "onServiceConnected " + isBound + " COUNT " + ccc++);

            if(firstBind) {
                networkService.test(handler);
                firstBind = false;
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Will trigger on service connection exception
            isBound = false;
        }
    };

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
                    Log.d("NetworkActivity", "Establish connection " + Thread.currentThread().getName());
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
                                if (isBound && appInFocus) {
                                    // Connect to cloud, or ignore if already connected
                                    networkService.connectToCloudServer(handler);
                                    // Verify successfully launched connection thread
                                    connected = networkService.inputThreadRunning;
                                    //connected = networkService.connectedToCloud;
                                    if (connected) {
                                        Log.d("NetworkActivity", "CONNECTED");
                                        try {
                                            // Add log in request to be sent to cloud server.
                                            networkService.requestsToServer.put(loginRequest);
                                            requestAdded = true;
                                        } catch (InterruptedException e) {
                                            writeToast("Unable to add login request");
                                        }
                                    }
                                }
                                if (attempt == 9 && !connected) {
                                    //writeToast("Unable to connect to cloud server");
                                }
                            }
                        });
                    }
                    if (connected || !appInFocus || networkService.socketException) {
                        return;
                        //attempt = 10;
                        //break;
                    }
                }
            }
        }).start();
    }

    // ======================== INTERFACE METHOD =========================================
    // Used by Service thread(s) to notify UI and pass update data
    // The data passed conform to the ALMA communication protocol and is decoded in update()

    /*@Override
    public void update(String command) {
        // Avoid UI-thread operations when client is not in focus on device
        if (appInFocus) {

            String[] commands = command.split(":");

            switch (commands[0]) {
                case "2":
                    loginResult(commands);
                    break;
                case "5":
                    reconnectionResult(commands);
                    break;
                case "14":
                    updateGadgetList(commands);
                    break;
                case "18": // Exception message from cloud server (18) or from system (19)
                case "19":
                    writeToast(commands[1]);
                    break;
                case "20": // Connection to cloud lost/terminated
                    lostCloudConnection();
                    break;
                default:
                    writeToast("Unknown request from server: " + command);
                    break;
            }
        }
    }*/

    private void loginResult(String[] commands) {
        if (commands[1].equals("ok")) {
            String userName = commands[3];
            boolean admin = commands[4].equals("1");
            String sysName = commands[5];
            String sessionKey = commands[6];
            // loggedInUser = new LoggedInUser(userName, admin, sysName, sessionKey);

            fragmentTransaction("home");
            // getSupportActionBar().show();

            writeUserToCache();
        } else {
            writeToast(commands[2]);
        }
    }

    private void reconnectionResult(String[] commands) {
        if (commands[1].equals("ok")) {
            fragmentTransaction("home");
            // getSupportActionBar().show();
        } else {
            fragmentTransaction("login");
            writeToast("Invalid cache");
        }
    }

    private void lostCloudConnection() {
        writeToast("No cloud connection");
        // getSupportActionBar().hide();
        if (loginAttemptWithSessionKey) {
            fragmentTransaction("login");
        } else {
            fragmentTransaction("setup");
        }
    }

    private void updateGadgetList(String[] commands) {
    }

    // ========================== TOOLBAR MENU ===========================================

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    // =========================== UTILITY METHODS ======================================

    protected void writeToast(String message) {

    }

    public void fragmentTransaction(String fragment) {

    }

    public void writeUserToCache() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = applicationContext.getCacheDir() + "userdata";

                try (ObjectOutputStream objectOutput = new ObjectOutputStream(new FileOutputStream(new File(filePath)))) {
                    //correct path?

                    objectOutput.writeObject(loggedInUser);

                } catch (IOException e) {
                    writeToast("Unable to write user tho cache");
                }
            }
        }).start();
    }

}
