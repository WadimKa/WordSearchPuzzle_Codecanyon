package net.codecanyon.trimax.wordsearch.data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import net.codecanyon.trimax.wordsearch.R;




public class WSDatabase extends SQLiteAssetHelper {


    private static final String DATABASE_NAME = "wordsearch.db";
    private static final int DATABASE_VERSION = 4;

    private Context context;
    private String selectedLangTable;
    private SQLiteDatabase database;


    public WSDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        this.context = context;
    }


    public void open(){
        database = getReadableDatabase();
        selectedLangTable = Settings.getStringValue(context, context.getResources().getString(R.string.pref_key_language), null);
    }



    public String[] getRandomWords(){

        database.execSQL("CREATE TEMP TABLE temp_rows AS SELECT word FROM "+selectedLangTable+" WHERE random() % 4 = 0 LIMIT 2000");
        Cursor cursor = database.rawQuery("SELECT word FROM temp_rows ORDER BY random()", null);

        cursor.moveToFirst();

        String[] words = new String[cursor.getCount()];
        int i = 0;
        while (!cursor.isAfterLast()) {
            words[i++] = cursor.getString(0);
            cursor.moveToNext();
        }

        database.execSQL("drop table temp_rows");
        cursor.close();

        return words;
    }


}
