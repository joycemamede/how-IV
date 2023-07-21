package com.example.livraria;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextAuthor;
    private Button buttonAdd;
    private ListView listViewBooks;
    private ArrayList<String> bookList;
    private ArrayAdapter<String> adapter;
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;
    private static final String DATABASE_NAME = "livraria.db";
    private static final String TABLE_NAME = "livros";
    private static final String COLUMN_TITLE = "titulo";
    private static final String COLUMN_AUTHOR = "autor";
    private String oldTitle;
    private Boolean buttonController = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextAuthor = findViewById(R.id.editTextAuthor);
        buttonAdd = findViewById(R.id.buttonAdd);
        listViewBooks = findViewById(R.id.listViewBooks);

        // Criar uma instância da subclasse SQLiteOpenHelper
        dbHelper = new LivrariaDBHelper(this);

        // Abrir o db para operações de leitura e escrita
        database = dbHelper.getWritableDatabase();

        // Criar um ArrayList vazio para guardar os livros
        bookList = new ArrayList<>();

        // Criar um ArrayAdapter para mostrar os livros no ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookList);
        listViewBooks.setAdapter(adapter);

        // Carregar na tela livros que já estejam no db
        loadBooks();

        // Ações relacionados ao clique no botão de add
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonController) {
                    String title = editTextTitle.getText().toString().trim();
                    String author = editTextAuthor.getText().toString().trim();

                    if (!title.isEmpty() && !author.isEmpty()) {
                        addBook(title, author);
                        editTextTitle.setText("");
                        editTextAuthor.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, "Por favor, coloque livro e autor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String title = editTextTitle.getText().toString().trim();
                    String author = editTextAuthor.getText().toString().trim();

                    editBook(title, author, oldTitle);
                    editTextTitle.setText("");
                    editTextAuthor.setText("");
                    buttonController = false;
                    buttonAdd.setText("Adicionar Livro");
                }
            }
        });

        // Ações relacionadas ao clique na lista de livros - EDIÇÃO
        listViewBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBook = bookList.get(position);
                String[] split = selectedBook.split(" por ");
                System.out.println(selectedBook);
                oldTitle = split[0];
                String oldAuthor = split[1];

                editTextTitle.setText(oldTitle);
                editTextAuthor.setText(oldAuthor);
                buttonController = true;
                buttonAdd.setText("Editar Livro");
            }
        });

        // Ações relacionadas ao clique prolongado na lista de livros - DELEÇÃO
        listViewBooks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeBook(position);
                return true;
            }
        });

    }

    // Função para inserir livro no banco de dados
    private void addBook(String title, String author) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AUTHOR, author);
        long rowId = database.insert(TABLE_NAME, null, values);

        if (rowId != -1) {
            String book = title + " por " + author;
            bookList.add(book);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Livro adicionado com Sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Falha ao adicionar livro", Toast.LENGTH_SHORT).show();
        }
    }

    // Função para deletar livro do banco de dados
    private void removeBook(int position) {
        String book = bookList.get(position);
        String[] split = book.split(" por ");
        String title = split[0];
        String author = split[1];

        int rowsDeleted = database.delete(TABLE_NAME,
                COLUMN_TITLE + " = ? AND " + COLUMN_AUTHOR + " = ?",
                new String[]{title, author});

        if (rowsDeleted > 0) {
            bookList.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Livro removido com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Falha ao remover livro", Toast.LENGTH_SHORT).show();
        }
    }

    // Carregar na tela livros presentes no banco
    private void loadBooks() {
        // Limpar array com livros + autor
        bookList.clear();

        // Buscar livros no banco de dados
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{COLUMN_TITLE, COLUMN_AUTHOR},
                null, null, null, null, null);

        // Iterar pelo cursor contendo o resultado da query e adicionar um livro por vez ao array bookList
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            @SuppressLint("Range") String author = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR));
            String book = title + " por " + author;
            bookList.add(book);
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    //Função para editar livros
    private void editBook(String title, String author, String oldTitle) {

        if (!title.isEmpty() && !author.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_AUTHOR, author);

            database.update(TABLE_NAME, values, "titulo=?", new String[]{oldTitle});

            loadBooks();
            Toast.makeText(this, "Livro editado com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Livro e autor não podem estar vazios", Toast.LENGTH_SHORT).show();
        }
    }
}

