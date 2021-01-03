package gurveen.com.mynews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shelter.db";
    public static final int DATABASE_VERSION = 1;
    public static final String SQL_CREATE_ARTICLES_TABLE = "CREATE TABLE " + DbContract.DbEntry.TABLE_NAME + " ("
            + DbContract.DbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DbContract.DbEntry.COLUMN_TITLE_NAME + " VARCHAR, "
            + DbContract.DbEntry.COLUMN_CONTENT_NAME + " VARCHAR);";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + DbContract.DbEntry.TABLE_NAME;

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ARTICLES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
