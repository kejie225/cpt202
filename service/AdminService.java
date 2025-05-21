package com.cpt202.music_management.service;

import com.cpt202.music_management.model.Admin;

public interface AdminService {
    Admin register(String username, String password);

    Admin login(String username, String password);
}