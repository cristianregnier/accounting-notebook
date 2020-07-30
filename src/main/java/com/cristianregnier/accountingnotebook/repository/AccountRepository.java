package com.cristianregnier.accountingnotebook.repository;

import com.cristianregnier.accountingnotebook.model.Account;

@org.springframework.stereotype.Repository
public class AccountRepository {

   private Account account;

   public AccountRepository() {
      account = new Account();
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   public Account getAccount() {
      return account;
   }
}
