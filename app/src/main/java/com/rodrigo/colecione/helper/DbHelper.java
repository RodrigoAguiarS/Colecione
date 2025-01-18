package com.rodrigo.colecione.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rodrigo.colecione.data.Categoria;
import com.rodrigo.colecione.data.Raridade;
import com.rodrigo.colecione.model.Produto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "produtos.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PRODUTO = "produto";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOME = "nome";
    private static final String COLUMN_DESCRICAO = "descricao";
    private static final String COLUMN_CATEGORIA = "categoria";
    private static final String COLUMN_RARIDADE = "raridade";
    private static final String COLUMN_URL_DA_IMAGEM = "url_da_imagem";
    private static final String COLUMN_PRECO = "preco";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PRODUTO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOME + " TEXT,"
                + COLUMN_DESCRICAO + " TEXT,"
                + COLUMN_CATEGORIA + " TEXT,"
                + COLUMN_RARIDADE + " TEXT,"
                + COLUMN_URL_DA_IMAGEM + " TEXT,"
                + COLUMN_PRECO + " REAL" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUTO);
        onCreate(db);
    }

    // Create
    public void addProduto(String nome, String descricao, String categoria, String raridade, String urlDaImagem, BigDecimal preco) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_DESCRICAO, descricao);
        values.put(COLUMN_CATEGORIA, categoria);
        values.put(COLUMN_RARIDADE, raridade);
        values.put(COLUMN_URL_DA_IMAGEM, urlDaImagem);
        values.put(COLUMN_PRECO, preco.doubleValue());
        db.insert(TABLE_PRODUTO, null, values);
        db.close();
    }

    // Read
    public Cursor getProduto(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PRODUTO, new String[]{COLUMN_ID, COLUMN_NOME, COLUMN_DESCRICAO, COLUMN_CATEGORIA, COLUMN_RARIDADE, COLUMN_URL_DA_IMAGEM, COLUMN_PRECO},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
    }

    // Update
    public int updateProduto(int id, String nome, String descricao, String categoria, String raridade, String urlDaImagem, BigDecimal preco) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_DESCRICAO, descricao);
        values.put(COLUMN_CATEGORIA, categoria);
        values.put(COLUMN_RARIDADE, raridade);
        values.put(COLUMN_URL_DA_IMAGEM, urlDaImagem);
        values.put(COLUMN_PRECO, preco.doubleValue());
        return db.update(TABLE_PRODUTO, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<Produto> getProdutosByPage(int page, int pageSize) {
        List<Produto> produtos = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUTO, null, null, null, null, null, null, pageSize + " OFFSET " + offset);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Produto produto = new Produto();
                produto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                produto.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)));
                produto.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)));
                produto.setCategoria(Categoria.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA))));
                produto.setRaridade(Raridade.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RARIDADE))));
                produto.setPreco(new BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRECO))));
                produto.setUrlDaImagem(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL_DA_IMAGEM)));
                produtos.add(produto);
            }
            cursor.close();
        }
        db.close();
        return produtos;
    }

    // Delete
    public void deleteProduto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUTO, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Produto> buscaProdutosPorNome(String nome) {
        List<Produto> produtos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUTO, null, COLUMN_NOME + " LIKE ?", new String[]{"%" + nome + "%"}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Produto produto = new Produto();
                produto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                produto.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)));
                produto.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)));
                produto.setCategoria(Categoria.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA))));
                produto.setRaridade(Raridade.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RARIDADE))));
                produto.setPreco(new BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRECO))));
                produto.setUrlDaImagem(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL_DA_IMAGEM)));
                produtos.add(produto);
            }
            cursor.close();
        }
        db.close();
        return produtos;
    }

    public List<Produto> buscaProdutosPorNomeCategoria(String categoria) {
        List<Produto> produtos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUTO, null, COLUMN_CATEGORIA + " LIKE ?", new String[]{"%" + categoria + "%"}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Produto produto = new Produto();
                produto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                produto.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)));
                produto.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)));
                produto.setCategoria(Categoria.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA))));
                produto.setRaridade(Raridade.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RARIDADE))));
                produto.setPreco(new BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRECO))));
                produto.setUrlDaImagem(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL_DA_IMAGEM)));
                produtos.add(produto);
            }
            cursor.close();
        }
        db.close();
        return produtos;
    }
}