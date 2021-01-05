package com.example.projektet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Klassen som skapar vår privata databas.
 * @author Andreas Roghe, Sofia Ågren.
 * @version 2020-01-05.
 */
public class SqlCryptoHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Crypto";
    private static final int VERSION = 1;

    public SqlCryptoHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, VERSION);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL(
                    "CREATE TABLE CRYPTOLEDGER( " +
                            "FRIEND TEXT PRIMARY KEY," +
                            "PRIVATE_KEY TEXT)"
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }
}
