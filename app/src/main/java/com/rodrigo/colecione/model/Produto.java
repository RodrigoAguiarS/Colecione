package com.rodrigo.colecione.model;

import com.rodrigo.colecione.data.Categoria;
import com.rodrigo.colecione.data.Raridade;

import java.io.Serializable;
import java.math.BigDecimal;

public class Produto implements Serializable {

    private int id;
    private String nome;
    private String descricao;
    private Categoria categoria;
    private Raridade raridade;
    private BigDecimal preco;
    private String urlDaImagem;

    public Produto() {
    }

    public Produto(int id, String nome, String descricao, Categoria categoria, Raridade raridade, BigDecimal preco, String urlDaImagem) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.raridade = raridade;
        this.preco = preco;
        this.urlDaImagem = urlDaImagem;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Raridade getRaridade() {
        return raridade;
    }

    public void setRaridade(Raridade raridade) {
        this.raridade = raridade;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getUrlDaImagem() {
        return urlDaImagem;
    }

    public void setUrlDaImagem(String urlDaImagem) {
        this.urlDaImagem = urlDaImagem;
    }
}