package com.ebidding.account.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service", path = "api/v1/account-service", url= "${ebidding.account-endpoint}" )
public interface AccountClient {
    @GetMapping("/")
    ResponseEntity<AccountDTO> getAccount (@RequestParam(name = "name") String name);

    @GetMapping("accounts/account/{accountId}/name")
    String getAccountNameByAccountId(@PathVariable("accountId") Long accountId);


}
