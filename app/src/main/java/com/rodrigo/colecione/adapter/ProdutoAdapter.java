package com.rodrigo.colecione.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.rodrigo.colecione.util.CurrencyUtil;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder> implements Filterable {

    private final List<Produto> produtos;
    private final List<Produto> produtosFull;
    private final Context context;
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;

    public ProdutoAdapter(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.produtosFull = new ArrayList<>(produtos);
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

        BigDecimal preco = produto.getPreco();
        holder.precoProduto.setText(CurrencyUtil.formatToBRL(preco.doubleValue()));

        Picasso.get().load(produto.getUrlDaImagem()).into(holder.imagemProduto);

        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(holder.itemView.getContext())
                .load(produto.getUrlDaImagem())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imagemProduto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetalhesProdutoActivity.class);
                intent.putExtra("produto", (Serializable) produto);
                context.startActivity(intent);
            }
        });

        holder.botaoDeletarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirmar Desativar")
                        .setMessage("Você tem certeza que deseja desativar este produto?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try (DbHelper dbHelper = new DbHelper(context)) {
                                    dbHelper.deleteProduto(produto.getId());
                                }
                                produtos.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, produtos.size());
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(produtos.size(), (currentPage + 1) * PAGE_SIZE);
    }

    @Override
    public Filter getFilter() {
        return produtoFilter;
    }

    private Filter produtoFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Produto> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(produtosFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Produto item : produtosFull) {
                    if (item.getNome().toLowerCase().contains(filterPattern) ||
                            item.getCategoria().getDescricao().toLowerCase().contains(filterPattern) ||
                            item.getRaridade().getDescricao().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            produtos.clear();
            produtos.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public void loadNextPage() {
        currentPage++;
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