package com.rodrigo.colecione.ui.produto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rodrigo.colecione.R;
import com.rodrigo.colecione.data.Categoria;
import com.rodrigo.colecione.data.Raridade;
import com.rodrigo.colecione.helper.DbHelper;
import com.rodrigo.colecione.model.Produto;
import com.rodrigo.colecione.ui.menu.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProdutoFormActivity extends BaseActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    static final int CAPTURE_IMAGE_REQUEST = 2;
    static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private EditText nomeEditText, descricaoEditText, precoEditText;
    private Spinner categoriaSpinner, raridadeSpinner;
    private ImageView fotoImageView;
    private ProgressBar progressBar;
    private Uri fotoUri;
    private String fotoUrl;
    private FirebaseStorage storage;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_produto_form, findViewById(R.id.container));
        nomeEditText = findViewById(R.id.nomeProduto);
        descricaoEditText = findViewById(R.id.descricaoProduto);
        precoEditText = findViewById(R.id.precoProduto);
        categoriaSpinner = findViewById(R.id.categoriaProduto);
        raridadeSpinner = findViewById(R.id.raridadeProduto);
        fotoImageView = findViewById(R.id.imagemProduto);
        ImageButton uploadButton = findViewById(R.id.uploadButton);
        ImageButton cameraButton = findViewById(R.id.cameraButton);
        Button salvarButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);

        storage = FirebaseStorage.getInstance();
        dbHelper = new DbHelper(this);

        uploadButton.setOnClickListener(v -> abrirSeletorDeArquivo());
        cameraButton.setOnClickListener(v -> verificarPermissaoCamera());
        salvarButton.setOnClickListener(v -> salvarColecionavel());
        precoEditText.setText("0");

        setupCategoriaSpinner();
        setupRaridadeSpinner();
    }

    private void setupCategoriaSpinner() {
        List<Categoria> categorias = new ArrayList<>(Arrays.asList(Categoria.values()));
        ArrayAdapter<Categoria> categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriaSpinner.setAdapter(categoriaAdapter);
    }

    private void setupRaridadeSpinner() {
        List<Raridade> raridades = new ArrayList<>(Arrays.asList(Raridade.values()));
        ArrayAdapter<Raridade> raridadeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, raridades);
        raridadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        raridadeSpinner.setAdapter(raridadeAdapter);
    }

    private void verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            abrirCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamera();
            } else {
                Toast.makeText(this, "Permissão para usar a câmera negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirSeletorDeArquivo() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void abrirCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE_REQUEST || requestCode == CAPTURE_IMAGE_REQUEST) && resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                fotoUri = data.getData();
            } else if (data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                fotoUri = getImageUri(photo);
            }
            fotoImageView.setImageURI(fotoUri);
            uploadImagemParaFirebase();
        }
    }

    private void uploadImagemParaFirebase() {
        if (fotoUri != null) {
            String fotoId = UUID.randomUUID().toString();
            StorageReference fotoRef = storage.getReference().child("fotos/" + fotoId);
            UploadTask uploadTask = fotoRef.putFile(fotoUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                fotoUrl = uri.toString();
                Toast.makeText(this, "Imagem carregada com sucesso", Toast.LENGTH_SHORT).show();
            })).addOnFailureListener(e -> {
                Toast.makeText(this, "Falha no upload da foto", Toast.LENGTH_SHORT).show();
                Log.e("ErroUpload", "Erro ao fazer upload da foto", e);
            });
        }
    }

    private void salvarColecionavel() {
        String nome = nomeEditText.getText().toString();
        String descricao = descricaoEditText.getText().toString();
        Categoria categoria = (Categoria) categoriaSpinner.getSelectedItem();
        Raridade raridade = (Raridade) raridadeSpinner.getSelectedItem();
        String precoStr = precoEditText.getText().toString();

        if (!validarCampos(nome, descricao, precoStr, fotoUrl)) {
            Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Produto produto = new Produto(0, nome, descricao, categoria, raridade, new BigDecimal(precoStr), fotoUrl);
        salvarColecionavelNoSQLite(produto);
    }

    private void salvarColecionavelNoSQLite(Produto produto) {
        dbHelper.addProduto(produto);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Colecionável salvo com sucesso", Toast.LENGTH_SHORT).show();
        navegarParaMainActivity();
    }

    private void navegarParaMainActivity() {
        Intent intent = new Intent(ProdutoFormActivity.this, ListarProdutoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static boolean validarCampos(String nome, String descricao, String precoStr, String fotoUrl) {
        if (nome.isEmpty() || descricao.isEmpty() || precoStr.isEmpty() || fotoUrl == null) {
            return false;
        }

        try {
            new BigDecimal(precoStr);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}