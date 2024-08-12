package com.ebidding.account.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private String id;

    private String name;

    private String role;

}