package com.skcraft.launcher.auth;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OfflineLoginService implements LoginService {


    public Session login(String id, String password) {

        return new OfflineSession(id);
    }

    @Override
    public Session restore(SavedSession savedSession) {

        return new OfflineSession(savedSession.getUsername());
    }

}
