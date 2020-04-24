package software.engineering.yatzy.testing.localDatabase;

import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import software.engineering.yatzy.game.Player;


public class DataBaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DataBaseAccess instance;
    private String tag = "Info";

    //TABLE Players
    private static final String TABLE_PLAYERS = "Players";
    private static final String PLAYER_NAME = "name";

    //private constructor so that object creation rom outside the class is avoided
    public DataBaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    //To return a single instance of database
    public static DataBaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DataBaseAccess(context);
        }
        return instance;
    }

    //Open conection to the database
    public void openDatabase() {
        Log.d(tag, "DATABASE: OPEN DATABASE!");
        this.db = openHelper.getWritableDatabase();
    }

    //closing connection to database
    public void closeDatabe() {
        if (db != null) {
            Log.d(tag, "DATABASE: CLOSING DATABASE!");
            this.db.close();
        }
    }

    //Methods to get results from database


    public ArrayList<String> getPlayerName() {
        ArrayList<String> playersFromDatabase = new ArrayList<>();
        Cursor c;
        String query = String.format("SELECT * FROM %s", TABLE_PLAYERS);
        c = db.rawQuery(query, null);

        while (c.moveToNext()) {
            String name = c.getString(1);
            playersFromDatabase.add(name);
        }
        c.close();
        return playersFromDatabase;
    }

    public Player getPlayerbyName(String playerName) {
        Cursor c;
        Player m = null;
        String query = String.format("SELECT * FROM %s WHERE LOWER(%s) = '%s'", TABLE_PLAYERS, PLAYER_NAME, playerName);
        c = db.rawQuery(query, null);

        while (c.moveToNext()) {
            String name = c.getString(1);
            m = new Player(name);
            }
        c.close();
        Log.i(tag, "Object: " + m.toString());
        return m;
    }

}
