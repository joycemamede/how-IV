package com.example.livraria;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LivrariaDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "livraria.db";
    private static final String TABLE_NAME = "livros";
    private static final String COLUMN_TITLE = "titulo";
    private static final String COLUMN_AUTHOR = "autor";

    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_AUTHOR + " TEXT" + ")";

    public LivrariaDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Deletar tabela se já existir
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Cria a tabela novamente
        onCreate(db);
    }
}
