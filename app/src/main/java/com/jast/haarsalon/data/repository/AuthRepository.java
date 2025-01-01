package com.jast.haarsalon.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jast.haarsalon.core.share_prefer.SharedPrefManager;
import com.jast.haarsalon.data.models.User;
import com.jast.haarsalon.utils.Resource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final SharedPrefManager sharedPrefManager;
    private final FirebaseFirestore firebaseFirestore;

    @Inject
    public AuthRepository(FirebaseAuth firebaseAuth, SharedPrefManager sharedPrefManager, FirebaseFirestore firebaseFirestore) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
        this.sharedPrefManager = sharedPrefManager;
    }

    public LiveData<Resource<User>> login(String email, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        sharedPrefManager.saveUserInfo(firebaseUser.getUid(), firebaseUser.getEmail(), firebaseUser.getDisplayName());
                        result.setValue(Resource.success(new User(firebaseUser.getUid(), firebaseUser.getEmail())));
                    }
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<Boolean>> register(String email, String password) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        User user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), firebaseUser.getDisplayName());
                        firebaseFirestore.collection("users")
                                .document(firebaseUser.getUid())
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Lưu thông tin vào SharedPreferences
                                    sharedPrefManager.saveUserInfo(firebaseUser.getUid(), firebaseUser.getEmail(), firebaseUser.getDisplayName());
                                    result.setValue(Resource.success(true));
                                })
                                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));
                    }
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage(), null)));

        return result;
    }
}
