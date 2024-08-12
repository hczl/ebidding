package com.ebidding.account.service;

import com.ebidding.account.api.LoginResponseDTO;
import com.ebidding.account.domain.Account;
import com.ebidding.account.repository.AccountRepository;
import com.ebidding.common.utils.HashUtils;
import com.ebidding.common.utils.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Account findByName(String inputName) {
        return this.accountRepository.findByName(inputName).orElse(null);
    }

    public Account findById(Long accountId) {
        return this.accountRepository.findById(accountId).orElse(null);
    }


    public Optional<LoginResponseDTO> login(String username, String password) {
        return this.accountRepository.findByName(username)
                .filter(acc -> {
                    try {
                        return Objects.equals(acc.getPasswordHash(), HashUtils.encode(password));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(acc -> {
                    LoginResponseDTO user = modelMapper.map(acc, LoginResponseDTO.class);
                    user.setToken(JwtUtils.SignToken(user.getId(), user.getName(), user.getRole()));
                    return user;
                });
    }

    public String getAccountNameByAccountId(Long accountId) {
        Optional<Account> accountOptional = accountRepository.findByAccountId(accountId);
        if(accountOptional.isPresent()) {
            return accountOptional.get().getName();
        } else {
            throw new NoSuchElementException("Account with id " + accountId + " not found");
        }
    }

}
