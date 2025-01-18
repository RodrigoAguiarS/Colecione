package com.rodrigo.colecione.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.rodrigo.colecione.R;
import com.rodrigo.colecione.ui.LoginActivity;
import com.rodrigo.colecione.ui.produto.ListarProdutoActivity;
import com.rodrigo.colecione.ui.produto.ProdutoFormActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_list) {
                    startActivity(new Intent(BaseActivity.this, ListarProdutoActivity.class));
                    return true;
                } else if (itemId == R.id.nav_logout) {
                    startActivity(new Intent(BaseActivity.this, ProdutoFormActivity.class));
                    return true;
                } else if (itemId == R.id.nav_other) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}