package com.rodrigo.colecione.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rodrigo.colecione.R;
import com.rodrigo.colecione.helper.DbHelper;
import com.rodrigo.colecione.model.Produto;
import com.rodrigo.colecione.ui.produto.DetalhesProdutoActivity;
import com.rodrigo.colecione.ui.produto.EditarProdutoActivity;
import com.rodrigo.colecione.util.CurrencyUtil;
import com.rodrigo.colecione.util.FirebaseUtil;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder> {

    private final List<Produto> produtos;
    private final Context context;

    public ProdutoAdapter(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_produto, parent, false);
        return new ProdutoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Produto produto = produtos.get(position);
        holder.nomeProduto.setText(produto.getNome());
        holder.descricaoProduto.setText(produto.getDescricao());
        holder.categoriaProduto.setText(produto.getCategoria().getDescricao());
        holder.raridadeProduto.setText(produto.getRaridade().getDescricao());
        String precoStr = CurrencyUtil.formatToBRL(new BigDecimal(String.valueOf(produto.getPreco())));

        holder.precoProduto.setText(precoStr);
        holder.progressBar.setVisibility(View.VISIBLE);

        Glide.with(holder.itemView.getContext())
                .load(produto.getUrlDaImagem())
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_error_24)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imagemProduto);

        // Listener para abrir detalhes do produto
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesProdutoActivity.class);
            intent.putExtra("produto", produto);
            context.startActivity(intent);
        });

        holder.botaoEditarProduto.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarProdutoActivity.class);
            intent.putExtra("produto", produto);
            context.startActivity(intent);
        });

        holder.botaoDeletarProduto.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmar Apagar?")
                    .setMessage("Você tem certeza que deseja apagar este item?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        FirebaseUtil.deleteImage(produto.getUrlDaImagem(), context, aVoid -> {
                            try (DbHelper dbHelper = new DbHelper(context)) {
                                dbHelper.deleteProduto(produto.getId());
                            }
                            produtos.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, produtos.size());
                        });
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public void setProdutos(List<Produto> novosProdutos) {
        this.produtos.clear();
        this.produtos.addAll(novosProdutos);
        notifyDataSetChanged();
    }

    public static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        public TextView nomeProduto;
        public TextView descricaoProduto;
        public TextView precoProduto;
        public TextView categoriaProduto;
        public ProgressBar progressBar;
        public TextView raridadeProduto;
        public ImageView imagemProduto;
        public ImageButton botaoDeletarProduto;
        public ImageButton botaoEditarProduto;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeProduto = itemView.findViewById(R.id.nomeProduto);
            descricaoProduto = itemView.findViewById(R.id.descricaoProduto);
            precoProduto = itemView.findViewById(R.id.preco);
            progressBar = itemView.findViewById(R.id.progressBar);
            categoriaProduto = itemView.findViewById(R.id.categoria);
            raridadeProduto = itemView.findViewById(R.id.raridade);
            imagemProduto = itemView.findViewById(R.id.imagem);
            botaoDeletarProduto = itemView.findViewById(R.id.deleteButton);
            botaoEditarProduto = itemView.findViewById(R.id.editButton);
        }
    }
}