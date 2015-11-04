package com.project.rubikon.braillapp.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jfelipeescobart on 29/10/2015.
 */
public class DBManager extends SQLiteOpenHelper{



    public DBManager(Context context)
    {
        super(context, DBValues.DB_NAME, null, DBValues.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DBTweet.CREATE_TABLE);
        db.execSQL(DBTranslation.CREATE_TABLE);
        db.execSQL(DBAsciiCharacter.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(DBTweet.DROP_TABLE);
        db.execSQL(DBTranslation.DROP_TABLE);
        db.execSQL(DBAsciiCharacter.DROP_TABLE);

        onCreate(db);
    }
}