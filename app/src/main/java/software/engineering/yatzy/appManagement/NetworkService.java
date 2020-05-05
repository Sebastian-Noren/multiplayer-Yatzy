package software.engineering.yatzy.appManagement;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NetworkService extends Service {

    private static final String TAG = "Network NetworkService";

    // Binder object to bind client(s) to the service
    private IBinder binder = new MyBinder();
    // Binder: For retrieving a service instance, used by the client to communicate with the service
    // Ex if MainActivity wants to bind to the service: MainActivity will be the client
    // The Binder will facilitate that binding/connection between the client and the service
    public class MyBinder extends Binder {
        NetworkService getService() {
            //Return an instance of the service.
            return NetworkService.this;
        }
    }

    // Cloud communication
    private volatile Socket socket;
    private Thread inputThread;
    public volatile boolean inputThreadRunning; // Variable to be checked inside thread loop.
    public volatile boolean connectedToCloud;
    public volatile boolean serviceShutDown;
    public BlockingQueue<String> requestsToServer;

    // Gets called when the service is first started
    @Override
    public void onCreate() {
        super.onCreate();
        socket = null;
        inputThread = null;
        inputThreadRunning = false;
        connectedToCloud = false;
        serviceShutDown = false;

        requestsToServer = new ArrayBlockingQueue<>(10);
    }

    // When a client binds to a service; a Binder object will be returned (for bound service),
    // to facilitate the communication: client -> service
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return binder;
    }

    /*
    onTaskRemoved is called when the application is terminated from the list of open apps (seen by the user)
    -> stopSelf = a hard stop.
    -> Adapted for start AND bind services, not bound services (where the service self-terminates when last client unbinds).
    -> Safety measure to avoid having the service running in the background even though the application has been closed
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // + stop threads running
        stopSelf();
    }

    // =================================== TEST ====================================================

    public void test(final Handler handler) {
        new Thread(new Runnable() {
            int count = 0;
            @Override
            public void run() {
                while (count < 12 && !serviceShutDown) {
                    try {
                        Thread.sleep(4000);
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppManager.getInstance().testInt = count;
                            AppManager.getInstance().update();
                            Log.i(TAG, "count " + count);
                        }
                    });
                }
            }
        }).start();
    }

    // ============= CUSTOM UTILITY THREADS AND METHODS ==========================
    // inputThread will launch outputThread
    // inputThread will terminate outputThread when itself is terminated.

    public volatile boolean socketException = false;
    private volatile boolean intendedSocketClose = false;

    public void connectToCloudServer(Handler handler) {
        if(inputThread == null) {
            inputThread = new Thread(new ClientInputThread(handler));
            inputThread.start();
            //inputThreadRunning = true;
        }
    }

    public void stopConnectionToCloudServer() {
        Log.i(TAG, "Stopping server connection threads");
        if (inputThreadRunning) {
            // Terminate inputThread
            if(socket != null) {
                intendedSocketClose = true;
                try {
                    // Throw IOException
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        inputThread = null;
        inputThreadRunning = false;
        requestsToServer.clear();
    }

    // ====================== INPUT THREAD =======================================
    // - Invoked and managed by UI thread
    // - Initiates cloud Server communication (Socket)
    // - Launches and manages OutputThread
    // - Listens for requests from cloud server, and notifies UI thread

    // inputThread is terminated by socket.close() -> IOException
    // outputThread is terminated by thread.interrupt() -> InterruptedException

    private class ClientInputThread implements Runnable {
        // To reach method update() of UI thread (main activity)
        //private Updatable appManager;
        // Get a reference to UI thread's message queue
        private Handler handler;
        // Connection to cloud server
        //private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;
        private String requestFromServer;
        private Thread outputThread;
        boolean outputThreadRunning;

        ClientInputThread(Handler handler) {
            this.handler = handler;
            // Updatable this.appManager = appManager;
            socket = null;
            input = null;
            output = null;
            requestFromServer = null;
            outputThread = null;
            outputThreadRunning = false;
        }

        @Override
        public void run() {
            try {
                inputThreadRunning = true;
                intendedSocketClose = false;

                Log.i(TAG, "Input thread started " + Thread.currentThread().getName());
                // Try to establish the connection with cloud server (IOException if not possible)
                socket = new Socket("134.209.198.123", 8083);
                // 134.209.198.123

                connectedToCloud = true;

                // Obtaining input and output streams
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                // Launch custom output thread
                outputThread = new Thread(new ClientOutputThread(output));
                outputThread.start();
                outputThreadRunning = true;

                // Start listening for input from cloud server
                while (true) {
                    // Read input from cloud server
                    requestFromServer = input.readUTF();
                    // Update UI thread
                    updateUIThread(requestFromServer);
                }
            } catch (IOException e) {
                socketException = true;
            } finally {
                if (outputThreadRunning) {
                    outputThread.interrupt();
                    outputThreadRunning = false;
                }
                requestsToServer.clear(); // Necessary??
                closeResources();
                inputThreadRunning = false;
                connectedToCloud = false;
                inputThread = null;
                // Notify UI thread: Connection lost/terminated/unable to establish
                updateUIThread("41");

                Log.i(TAG, "Input thread closed " + Thread.currentThread().getName());
                if(!intendedSocketClose) {
                    // Reconnect
                }
            }
        }

        private void updateUIThread(final String request) {
            // Update UI thread
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AppManager.getInstance().update(request);
                    //appManager.update(request);
                }
            });
        }

        private void closeResources() {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                //Handle
            }
        }
    }

    // ====================== OUTPUT THREAD =======================================
    // Invoked and managed by inputThread

    private class ClientOutputThread implements Runnable {
        DataOutputStream output;

        ClientOutputThread(DataOutputStream output) {
            this.output = output;
        }

        @Override
        public void run() {

            Log.i(TAG, "Output thread started " + Thread.currentThread().getName());

            try {
                while (true) {
                    String command = requestsToServer.take();

                    Log.i(TAG, "REQUEST TO SERVER: " + command);

                    output.writeUTF(command);
                    output.flush();
                }
            } catch (InterruptedException e) {
                // Handle ??
            } catch (IOException e) {
                // Handle ??
            } finally {
                closeResources();
            }
        }

        private void closeResources() {
            try {
                if (output != null) {
                    output.close();
                    Log.i(TAG, "Output thread closed " + Thread.currentThread().getName());
                }
            } catch (IOException e) {
                //Handle ??
            }
        }
    }
}
