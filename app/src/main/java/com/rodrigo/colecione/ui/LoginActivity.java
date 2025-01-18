package com.rodrigo.colecione.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rodrigo.colecione.R;
import com.rodrigo.colecione.ui.produto.ListarProdutoActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, ListarProdutoActivity.class));
            finish();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");
        emailEditText.setText(savedEmail);
        passwordEditText.setText(savedPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, task -> {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.apply();

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        startActivity(new Intent(LoginActivity.this, ListarProdutoActivity.class));
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Autenticação falhou", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.apply();

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        startActivity(new Intent(LoginActivity.this, ListarProdutoActivity.class));
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Registro falhou", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}