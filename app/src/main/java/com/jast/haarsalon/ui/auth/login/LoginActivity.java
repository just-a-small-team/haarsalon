package com.jast.haarsalon.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jast.haarsalon.ui.main.MainActivity;
import com.jast.haarsalon.R;
import com.jast.haarsalon.databinding.ActivityLoginBinding;
import com.jast.haarsalon.ui.auth.AuthViewModel;
import com.jast.haarsalon.ui.auth.signup.SignupActivity;
import com.jast.haarsalon.utils.ToastUtils;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        EdgeToEdge.enable(this);
        setContentView(view);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        binding.loginBtn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.emailEt.getText()).toString();
            String password = Objects.requireNonNull(binding.passwordEt.getText()).toString();
            if (email.isEmpty() || password.isEmpty()) {
                return;
            }
            authViewModel.login(email, password).observe(this, userResource -> {
                switch (userResource.status) {
                    case LOADING:
                        binding.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        ToastUtils.showSuccess(this, userResource.message);
                        binding.progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case ERROR:
                        binding.progressBar.setVisibility(View.GONE);
                        ToastUtils.showError(this, userResource.message);
                        break;
                }
            });
        });

        binding.registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
    }
}