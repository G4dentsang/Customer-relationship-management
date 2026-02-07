package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.user.model.User;

public interface LoginAttemptService {
    void loginFailed(User user);
    void loginSuccess(User user);
    boolean isAccUnlockedWhenTimeExpired(User user);
}
