package com.jast.haarsalon.ui.auth.signup;

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
import com.jast.haarsalon.databinding.ActivitySignupBinding;
import com.jast.haarsalon.ui.auth.AuthViewModel;
import com.jast.haarsalon.ui.auth.login.LoginActivity;
import com.jast.haarsalon.utils.ToastUtils;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignupActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        EdgeToEdge.enable(this);
        setContentView(view);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.registerBtn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.emailEt.getText()).toString();
            String password = Objects.requireNonNull(binding.passwordEt.getText()).toString();
            if (email.isEmpty() || password.isEmpty()) {
                return;
            }
            authViewModel.register(email, password).observe(this, userResource -> {
                switch (userResource.status) {
                    case LOADING:
                        binding.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        ToastUtils.showSuccess(this, "Registration successful");
                        binding.progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case ERROR:
                        ToastUtils.showError(this, userResource.message);
                        binding.progressBar.setVisibility(View.GONE);
                        binding.emailEt.setError(userResource.message);
                        break;
                }
            });
        });

        binding.loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}