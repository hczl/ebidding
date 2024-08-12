package com.ebidding.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

public class Account {

    @Id
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private AccountRole role;

}
