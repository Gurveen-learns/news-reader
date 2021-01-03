package gurveen.com.mynews;

import android.net.Uri;
import android.provider.BaseColumns;

public class DbContract {
    private DbContract(){}

    public static final class DbEntry implements BaseColumns {

        public static final String TABLE_NAME = "articles";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE_NAME = "title";
        public static final String COLUMN_CONTENT_NAME = "content";

        public static final String CONTENT_AUTHORITY = "gurveen.com.mynews.data";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_PETS = DbContract.DbEntry.TABLE_NAME;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);

    }
}
