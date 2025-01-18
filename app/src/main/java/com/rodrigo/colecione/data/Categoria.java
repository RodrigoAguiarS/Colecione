package com.rodrigo.colecione.data;

import androidx.annotation.NonNull;

public enum Categoria {
    SELECIONE("Escolha uma Categoria"),
    FIGURAS_DE_ACAO("Figuras de Ação"),
    CARTAS_COLECIONAVEIS("Cartas Colecionáveis"),
    MOEDAS("Moedas"),
    SELOS("Selos"),
    MINIATURAS("Miniaturas"),

    OUTROS("Outros");

    private final String descricao;

    Categoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @NonNull
    @Override
    public String toString() {
        return descricao;
    }
}
