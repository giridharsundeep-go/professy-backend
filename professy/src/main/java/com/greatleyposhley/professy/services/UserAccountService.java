package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public List<UserAccount> getAllUsers() {

        return (List<UserAccount>) userAccountRepository.findAll();
    }

}
