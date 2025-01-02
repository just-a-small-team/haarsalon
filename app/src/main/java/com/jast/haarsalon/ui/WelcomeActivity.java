package com.jast.haarsalon.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.jast.haarsalon.R;
import com.jast.haarsalon.core.share_prefer.SharedPrefManager;
import com.jast.haarsalon.ui.auth.signup.SignupActivity;
import com.jast.haarsalon.ui.main.MainActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

@AndroidEntryPoint
public class WelcomeActivity extends AppCompatActivity {

    @Inject
    SharedPrefManager sharedPrefManager;

    @Inject
    FirebaseAuth firebaseAuth;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Disposable disposable;
        if (sharedPrefManager.isUserLoggedIn() && firebaseAuth.getCurrentUser() != null) {
            disposable = Observable.timer(3, TimeUnit.SECONDS)
                    .subscribe(aLong -> {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
        } else {
            disposable = Observable.timer(3, TimeUnit.SECONDS)
                    .subscribe(aLong -> {
                        Intent intent = new Intent(this, SignupActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }

        compositeDisposable.add(disposable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}