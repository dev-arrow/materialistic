package io.github.hidroh.materialistic.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

@Database(
        entities = {
                MaterialisticDatabase.SavedStory.class,
                MaterialisticDatabase.ReadStory.class,
                MaterialisticDatabase.Readable.class
        },
        version = 4)
public abstract class MaterialisticDatabase extends RoomDatabase {

    private static final Uri BASE_URI = Uri.parse("content://io.github.hidroh.materialistic.syncprovider");
    public static final Uri URI_VIEWED = BASE_URI.buildUpon()
            .appendPath("viewed")
            .build();
    public static final Uri URI_FAVORITE = BASE_URI.buildUpon()
            .appendPath("favorite")
            .build();

    private static MaterialisticDatabase sInstance;

    public static synchronized MaterialisticDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = setupBuilder(Room.databaseBuilder(context.getApplicationContext(),
                    MaterialisticDatabase.class,
                    DbConstants.DB_NAME))
                    .build();
        }
        return sInstance;
    }

    @VisibleForTesting
    protected static Builder<MaterialisticDatabase> setupBuilder(Builder<MaterialisticDatabase> builder) {
        return builder.addMigrations(new Migration(3, 4) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL(DbConstants.SQL_CREATE_SAVED_TABLE);
                database.execSQL(DbConstants.SQL_INSERT_FAVORITE_SAVED);
                database.execSQL(DbConstants.SQL_DROP_FAVORITE_TABLE);

                database.execSQL(DbConstants.SQL_CREATE_READ_TABLE);
                database.execSQL(DbConstants.SQL_INSERT_VIEWED_READ);
                database.execSQL(DbConstants.SQL_DROP_VIEWED_TABLE);

                database.execSQL(DbConstants.SQL_CREATE_READABLE_TABLE);
                database.execSQL(DbConstants.SQL_INSERT_READABILITY_READABLE);
                database.execSQL(DbConstants.SQL_DROP_READABILITY_TABLE);
            }
        });
    }

    public abstract SavedStoriesDao getSavedStoriesDao();

    public abstract ReadStoriesDao getReadStoriesDao();

    public abstract ReadableDao getReadableDao();

    @Entity(tableName = "read")
    public static class ReadStory {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        private int id;
        @ColumnInfo(name = "itemid")
        private String itemId;

        public ReadStory(String itemId) {
            this.itemId = itemId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }
    }

    @Entity
    public static class Readable {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        private int id;
        @ColumnInfo(name = "itemid")
        private String itemId;
        private String content;

        public Readable(String itemId, String content) {
            this.itemId = itemId;
            this.content = content;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @Entity(tableName = "saved")
    public static class SavedStory {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        private int id;
        @ColumnInfo(name = "itemid")
        private String itemId;
        private String url;
        private String title;
        private String time;

        static SavedStory from(WebItem story) {
            SavedStory savedStory = new SavedStory();
            savedStory.itemId = story.getId();
            savedStory.url = story.getUrl();
            savedStory.title = story.getDisplayedTitle();
            savedStory.time = String.valueOf(story instanceof Favorite ?
                    ((Favorite) story).getTime() :
                    String.valueOf(System.currentTimeMillis()));
            return savedStory;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    @Dao
    public interface SavedStoriesDao {
        @Query("SELECT * FROM saved ORDER BY time DESC")
        Cursor selectAllToCursor();

        @Query("SELECT * FROM saved WHERE title LIKE '%' || :query || '%' ORDER BY time DESC")
        Cursor searchToCursor(String query);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(SavedStory... savedStories);

        @Query("DELETE FROM saved")
        int deleteAll();

        @Query("DELETE FROM saved WHERE itemid = :itemId")
        int deleteByItemId(String itemId);

        @Query("DELETE FROM saved WHERE title LIKE '%' || :query || '%'")
        int deleteByTitle(String query);

        @Query("SELECT * FROM saved WHERE itemid = :itemId")
        @Nullable
        SavedStory selectByItemId(String itemId);
    }

    @Dao
    public interface ReadStoriesDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(ReadStory readStory);

        @Query("SELECT * FROM read WHERE itemid = :itemId LIMIT 1")
        ReadStory selectByItemId(String itemId);
    }

    @Dao
    public interface ReadableDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Readable readable);

        @Query("SELECT * FROM readable WHERE itemid = :itemId LIMIT 1")
        Readable selectByItemId(String itemId);
    }

    static class DbConstants {
        static final String DB_NAME = "Materialistic.db";
        static final String SQL_CREATE_READ_TABLE =
                "CREATE TABLE read (_id INTEGER NOT NULL PRIMARY KEY, itemid TEXT)";
        static final String SQL_CREATE_READABLE_TABLE =
                "CREATE TABLE readable (_id INTEGER NOT NULL PRIMARY KEY, itemid TEXT, content TEXT)";
        static final String SQL_CREATE_SAVED_TABLE =
                "CREATE TABLE saved (_id INTEGER NOT NULL PRIMARY KEY, itemid TEXT, url TEXT, title TEXT, time TEXT)";
        static final String SQL_INSERT_FAVORITE_SAVED = "INSERT INTO saved SELECT * FROM favorite";
        static final String SQL_INSERT_VIEWED_READ = "INSERT INTO read SELECT * FROM viewed";
        static final String SQL_INSERT_READABILITY_READABLE = "INSERT INTO readable SELECT * FROM readability";
        static final String SQL_DROP_FAVORITE_TABLE = "DROP TABLE IF EXISTS favorite";
        static final String SQL_DROP_VIEWED_TABLE = "DROP TABLE IF EXISTS viewed";
        static final String SQL_DROP_READABILITY_TABLE = "DROP TABLE IF EXISTS readability";
    }

    public interface FavoriteEntry extends BaseColumns {
        String COLUMN_NAME_ITEM_ID = "itemid";
        String COLUMN_NAME_URL = "url";
        String COLUMN_NAME_TITLE = "title";
        String COLUMN_NAME_TIME = "time";
    }
}
