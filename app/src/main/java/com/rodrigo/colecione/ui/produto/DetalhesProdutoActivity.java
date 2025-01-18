package com.rodrigo.colecione.ui.produto;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rodrigo.colecione.R;
import com.rodrigo.colecione.data.Raridade;
import com.rodrigo.colecione.model.Produto;
import com.rodrigo.colecione.ui.menu.BaseActivity;
import com.rodrigo.colecione.util.CurrencyUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class DetalhesProdutoActivity extends BaseActivity {

    private ImageView imagemProduto;
    private ProgressBar progressBar;
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

        Produto produto = (Produto) getIntent().getSerializableExtra("produto");


        if (produto != null) {
            nomeProduto.setText(produto.getNome());
            raridadeProduto.setText(produto.getRaridade().getDescricao());
            precoProduto.setText(CurrencyUtil.formatToBRL(produto.getPreco()));
            descricaoProduto.setText(produto.getDescricao());
            categoriaProduto.setText(produto.getCategoria().getDescricao());
            raridadeProduto.setTextColor(Color.parseColor(produto.getRaridade().getCor()));
            Picasso.get().load(produto.getUrlDaImagem()).into(imagemProduto, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    imagemProduto.setVisibility(View.VISIBLE);
                    nomeProduto.setVisibility(View.VISIBLE);
                    precoProduto.setVisibility(View.VISIBLE);
                    descricaoProduto.setVisibility(View.VISIBLE);
                    categoriaProduto.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
