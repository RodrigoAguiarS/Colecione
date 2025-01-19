package com.rodrigo.colecione.ui.produto;

import static com.rodrigo.colecione.ui.produto.ProdutoFormActivity.CAMERA_PERMISSION_REQUEST_CODE;
import static com.rodrigo.colecione.ui.produto.ProdutoFormActivity.CAPTURE_IMAGE_REQUEST;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rodrigo.colecione.R;
import com.rodrigo.colecione.data.Categoria;
import com.rodrigo.colecione.data.Raridade;
import com.rodrigo.colecione.helper.DbHelper;
import com.rodrigo.colecione.model.Produto;
import com.rodrigo.colecione.ui.menu.BaseActivity;
import com.rodrigo.colecione.util.FirebaseUtil;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;

public class EditarProdutoActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText nomeProduto, descricaoProduto, precoProduto;
    private Spinner categoriaProduto, raridadeProduto;
    private ImageView imagemProduto;
    private ProgressBar progressBar;
    private ImageButton uploadButton, cameraButton;
    private Button saveButton;
    private Produto produto;
    private DbHelper dbHelper;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_produto_form, findViewById(R.id.container));

        nomeProduto = findViewById(R.id.nomeProduto);
        descricaoProduto = findViewById(R.id.descricaoProduto);
        precoProduto = findViewById(R.id.precoProduto);
        categoriaProduto = findViewById(R.id.categoriaProduto);
        raridadeProduto = findViewById(R.id.raridadeProduto);
        imagemProduto = findViewById(R.id.imagemProduto);
        uploadButton = findViewById(R.id.uploadButton);
        cameraButton = findViewById(R.id.cameraButton);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);

        dbHelper = new DbHelper(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        produto = (Produto) getIntent().getSerializableExtra("produto");



        ArrayAdapter<Categoria> categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Categoria.values());
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriaProduto.setAdapter(categoriaAdapter);

        ArrayAdapter<Raridade> raridadeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Raridade.values());
        raridadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        raridadeProduto.setAdapter(raridadeAdapter);

        if (produto != null) {
            nomeProduto.setText(produto.getNome());
            descricaoProduto.setText(produto.getDescricao());
            precoProduto.setText(String.valueOf(produto.getPreco()));
            categoriaProduto.setSelection(Arrays.asList(Categoria.values()).indexOf(produto.getCategoria()));
            raridadeProduto.setSelection(Arrays.asList(Raridade.values()).indexOf(produto.getRaridade()));
        }

        Glide.with(this)
                .load(produto.getUrlDaImagem())
                .into(imagemProduto);

        uploadButton.setOnClickListener(v -> openFileChooser());
        cameraButton.setOnClickListener(v -> verificarPermissaoCamera());

        saveButton.setOnClickListener(v -> saveProduto());
    }

    private void verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            abrirCamera();
        }
    }

    private void abrirCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                imagemProduto.setImageURI(imageUri);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imagemProduto.setImageBitmap(imageBitmap);
                imageUri = getImageUriFromBitmap(imageBitmap);
            }
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void saveProduto() {
        String nome = nomeProduto.getText().toString();
        String descricao = descricaoProduto.getText().toString();
        String preco = precoProduto.getText().toString();
        Categoria categoria = (Categoria) categoriaProduto.getSelectedItem();
        Raridade raridade = (Raridade) raridadeProduto.getSelectedItem();

        if (nome.isEmpty() || descricao.isEmpty() || preco.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(BigDecimal.valueOf(Double.parseDouble(preco)));
        produto.setCategoria(Categoria.fromDescricao(categoria.getDescricao()));
        produto.setRaridade(Raridade.fromDescricao(raridade.getDescricao()));

        progressBar.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            FirebaseUtil.deleteImage(produto.getUrlDaImagem(), this, aVoid -> {
                StorageReference fileReference = storageReference.child("produtos/" + produto.getId() + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            produto.setUrlDaImagem(uri.toString());
                            updateProdutoInDatabase();
                            progressBar.setVisibility(View.GONE);
                        }))
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
            });
        } else {
            updateProdutoInDatabase();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateProdutoInDatabase() {
        dbHelper.updateProduto(produto.getId(), produto);
        Toast.makeText(EditarProdutoActivity.this, "Produto atualizado com sucesso", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditarProdutoActivity.this, ListarProdutoActivity.class);
        setResult(RESULT_OK);
        startActivity(intent);
        finish();
    }
}