package com.jast.haarsalon.ui.main;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.rx3.TedPermission;
import com.jast.haarsalon.R;
import com.jast.haarsalon.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        EdgeToEdge.enable(this);
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupNavigation();
        registerNotification();
        requestNotificationPermission();
    }

    private void setupNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

    }

    private void registerNotification() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    Timber.d("Token: %s", token);
                });
    }

    private void requestNotificationPermission() {
        // check if the user has granted the permission
        Disposable disposable = TedPermission.create()
                .setPermissions(Manifest.permission.POST_NOTIFICATIONS)
                .setRationaleTitle("Notification Permission")
                .setRationaleMessage("We need your permission to send you notifications")
                .setDeniedTitle("Permission denied")
                .setDeniedMessage("If you reject permission,you can not receive notification\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("Settings")
                .setGotoSettingButton(true)
                .request()
                .subscribe(tedPermissionResult -> {
                    if (tedPermissionResult.isGranted()) {
                        Timber.d("Permission granted");
                    } else {
                        Timber.d("Permission denied");
                    }
                });

        disposable.dispose();
    }
}