package com.rodrigo.colecione.adapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rodrigo.colecione.model.Produto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdutoViewModel extends ViewModel {
    private final Map<Integer, List<Produto>> cachedPages = new HashMap<>();
    private final MutableLiveData<List<Produto>> produtos = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>();

    public LiveData<Integer> getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        currentPage.setValue(page);
        if (cachedPages.containsKey(page)) {
            produtos.setValue(cachedPages.get(page));
        }
    }

    public LiveData<List<Produto>> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        Integer page = currentPage.getValue();
        if (page != null) {
            cachedPages.put(page, produtos);
        }
        this.produtos.setValue(produtos);
    }
}