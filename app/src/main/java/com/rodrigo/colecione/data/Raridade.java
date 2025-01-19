package com.rodrigo.colecione.data;

import androidx.annotation.NonNull;

public enum Raridade {
    COMUM("Comum", "#B0BEC5"),
    RARO("Raro", "#2196F3"),
    EPICO("Épico", "#9C27B0"),
    LENDARIO("Lendário", "#FFD700");

    private final String descricao;
    private final String cor;

    Raridade(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCor() {
        return cor;
    }

    @NonNull
    @Override
    public String toString() {
        return descricao;
    }

    public static Raridade fromDescricao(String descricao) {
        for (Raridade raridade : Raridade.values()) {
            if (raridade.name().equalsIgnoreCase(descricao) || raridade.getDescricao().equalsIgnoreCase(descricao)) {
                return raridade;
            }
        }
        throw new IllegalArgumentException("No enum constant with descricao: " + descricao);
    }
}