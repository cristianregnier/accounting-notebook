package com.cristianregnier.accountingnotebook.api;

import com.cristianregnier.accountingnotebook.service.AccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class AccountController {

   @Autowired
   private AccountManager accountManager;

   @GetMapping("/")
   public BigDecimal getAccountBalance() {
      return accountManager.getAccountBalance();
   }
}
