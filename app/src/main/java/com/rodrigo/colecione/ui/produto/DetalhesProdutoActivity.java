package com.rodrigo.colecione.ui.produto;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rodrigo.colecione.R;
import com.rodrigo.colecione.model.Produto;
import com.rodrigo.colecione.ui.menu.BaseActivity;
import com.rodrigo.colecione.util.CurrencyUtil;



public class DetalhesProdutoActivity extends BaseActivity {

    private ImageView imagemProduto;
    private ProgressBar progressBar;
    private Button shareButton;
    private Produto produto;
    private TextView nomeProduto;
    private TextView raridadeProduto;
    private TextView precoProduto;
    private TextView descricaoProduto;
    private TextView categoriaProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_detalhes_produto, findViewById(R.id.container));
        imagemProduto = findViewById(R.id.imagemProduto);
        nomeProduto = findViewById(R.id.nomeProduto);
        precoProduto = findViewById(R.id.precoProduto);
        progressBar = findViewById(R.id.progressBar);
        descricaoProduto = findViewById(R.id.descricaoProduto);
        categoriaProduto = findViewById(R.id.categoriaProduto);
        raridadeProduto = findViewById(R.id.raridadeProduto);
        shareButton = findViewById(R.id.shareButton);

        produto = (Produto) getIntent().getSerializableExtra("produto");

        shareButton.setOnClickListener(v -> compartilharProduto());

        if (produto != null) {
            nomeProduto.setText(produto.getNome());
            raridadeProduto.setText(produto.getRaridade().getDescricao());
            precoProduto.setText(CurrencyUtil.formatToBRL(produto.getPreco()));
            descricaoProduto.setText(produto.getDescricao());
            categoriaProduto.setText(produto.getCategoria().getDescricao());
            raridadeProduto.setTextColor(Color.parseColor(produto.getRaridade().getCor()));

            progressBar.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(produto.getUrlDaImagem())
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            progressBar.setVisibility(View.GONE);
                            imagemProduto.setImageDrawable(resource);
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(DetalhesProdutoActivity.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void compartilharProduto() {
        if (produto != null) {
            String linkUnico = gerarLinkUnico(produto);
            String mensagemCompartilhamento = "Confira este colecionável no app Colecione: " + linkUnico;

            Intent intentCompartilhar = new Intent(Intent.ACTION_SEND);
            intentCompartilhar.setType("text/plain");
            intentCompartilhar.putExtra(Intent.EXTRA_TEXT, mensagemCompartilhamento);

            startActivity(Intent.createChooser(intentCompartilhar, "Compartilhar via"));
        } else {
            Toast.makeText(this, "Erro ao compartilhar o colecionável", Toast.LENGTH_SHORT).show();
        }
    }

    private String gerarLinkUnico(Produto produto) {
        return produto.getUrlDaImagem();
    }
}
