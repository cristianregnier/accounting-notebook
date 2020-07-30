package com.cristianregnier.accountingnotebook.api;

import com.cristianregnier.accountingnotebook.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

   @Autowired
   private AccountRepository accounts;

   @GetMapping("/")
   public Double getAccountBalance() {
      return accounts.getAccount().getBalance();
   }
}
