package com.rodrigo.colecione.ui.produto;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigo.colecione.R;
import com.rodrigo.colecione.adapter.ProdutoAdapter;
import com.rodrigo.colecione.helper.DbHelper;
import com.rodrigo.colecione.model.Produto;
import com.rodrigo.colecione.ui.menu.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListarProdutoActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProdutoAdapter adapter;
    private List<Produto> produtos;
    private DbHelper dbHelper;
    private SearchView searchInput;
    private ImageButton prevButton, nextButton;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_listar_produtos, findViewById(R.id.container));
        recyclerView = findViewById(R.id.recyclerViewProdutos);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchInput = findViewById(R.id.searchView);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);

        dbHelper = new DbHelper(this);
        produtos = new ArrayList<>();
        carregarProdutos(currentPage);

        adapter = new ProdutoAdapter(produtos, ListarProdutoActivity.this);
        recyclerView.setAdapter(adapter);

        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText);
                return false;
            }
        });

        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                carregarProdutos(currentPage);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (produtos.size() == PAGE_SIZE) {
                currentPage++;
                carregarProdutos(currentPage);
            }
        });

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt("currentPage", 1);
            carregarProdutos(currentPage);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        carregarProdutos(currentPage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPage", currentPage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPage = savedInstanceState.getInt("currentPage", 1);
        carregarProdutos(currentPage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        carregarProdutos(currentPage);
    }

    private void carregarProdutos(int page) {
        new Thread(() -> {
            List<Produto> loadedProdutos = dbHelper.getProdutosByPage(page, PAGE_SIZE);
            runOnUiThread(() -> {
                produtos.clear();
                produtos.addAll(loadedProdutos);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void searchProducts(String query) {
        List<Produto> produtosByName = dbHelper.buscaProdutosPorNome(query);
        List<Produto> produtosByCategory = dbHelper.buscaProdutosPorNomeCategoria(query);

        Map<Integer, Produto> productMap = new HashMap<>();

        for (Produto produto : produtosByName) {
            productMap.put(produto.getId(), produto);
        }

        for (Produto produto : produtosByCategory) {
            productMap.putIfAbsent(produto.getId(), produto);
        }

        List<Produto> finalResults = new ArrayList<>(productMap.values());

        if (finalResults.isEmpty()) {
            Toast.makeText(ListarProdutoActivity.this, "Nenhum produto encontrado", Toast.LENGTH_SHORT).show();
        }

        adapter = new ProdutoAdapter(finalResults, ListarProdutoActivity.this);
        recyclerView.setAdapter(adapter);
    }
}