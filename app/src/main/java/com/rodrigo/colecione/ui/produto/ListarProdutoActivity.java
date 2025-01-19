package com.rodrigo.colecione.ui.produto;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigo.colecione.R;
import com.rodrigo.colecione.adapter.ProdutoAdapter;
import com.rodrigo.colecione.adapter.ProdutoViewModel;
import com.rodrigo.colecione.helper.DbHelper;
import com.rodrigo.colecione.model.Produto;
import com.rodrigo.colecione.ui.menu.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListarProdutoActivity extends BaseActivity {

    private ProdutoViewModel produtoViewModel;
    private RecyclerView recyclerView;
    private ProdutoAdapter adapter;
    private DbHelper dbHelper;
    private SearchView searchInput;
    private ImageButton prevButton, nextButton;
    private static final int PAGE_SIZE = 10;
    private static final int INITIAL_PAGE = 1;
    private final Map<Integer, List<Produto>> cachedPages = new HashMap<>();

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
        adapter = new ProdutoAdapter(new ArrayList<>(), ListarProdutoActivity.this);
        recyclerView.setAdapter(adapter);

        produtoViewModel = new ViewModelProvider(this).get(ProdutoViewModel.class);

        produtoViewModel.setCurrentPage(INITIAL_PAGE);

        produtoViewModel.getCurrentPage().observe(this, this::carregarProdutos);
        produtoViewModel.getProdutos().observe(this, produtos -> {
            adapter.setProdutos(produtos);
            adapter.notifyDataSetChanged();
        });

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
            Integer currentPage = produtoViewModel.getCurrentPage().getValue();
            if (currentPage != null && currentPage > 1) {
                produtoViewModel.setCurrentPage(currentPage - 1);
            }
        });

        nextButton.setOnClickListener(v -> {
            Integer currentPage = produtoViewModel.getCurrentPage().getValue();
            if (currentPage != null && adapter.getItemCount() == PAGE_SIZE) {
                produtoViewModel.setCurrentPage(currentPage + 1);
            }
        });

        carregarProdutos(1);
    }

    private void carregarProdutos(int page) {
        if (cachedPages.containsKey(page)) {
            produtoViewModel.setProdutos(cachedPages.get(page));
        } else {
            List<Produto> produtos = dbHelper.getProdutosByPage(page, PAGE_SIZE);
            cachedPages.put(page, produtos);
            produtoViewModel.setProdutos(produtos);
        }
    }

    private void searchProducts(String query) {
        List<Produto> resultados = dbHelper.buscaProdutosPorNomeOuCategoriaOuRaridade(query);
        if (resultados.isEmpty()) {
            Toast.makeText(this, "Nenhum produto encontrado", Toast.LENGTH_SHORT).show();
        }
        adapter.setProdutos(resultados);
        adapter.notifyDataSetChanged();
    }
}