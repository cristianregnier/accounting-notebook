package com.cristianregnier.accountingnotebook.api;

import com.cristianregnier.accountingnotebook.api.command.CommitTransactionCommand;
import com.cristianregnier.accountingnotebook.model.Transaction;
import com.cristianregnier.accountingnotebook.service.AccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

   @Autowired
   private AccountManager accountManager;

   @GetMapping
   public List<Transaction> getHistory() {
      return accountManager.getHistory();
   }

   @PostMapping
   public Transaction commitTransaction(
           @RequestBody CommitTransactionCommand commitTransaction) {
      return accountManager.commitTransaction(commitTransaction);
   }

   @GetMapping("/{id}")
   public Transaction findTransactionById(@PathVariable String id) {
      return accountManager.findTransactionById(id);
   }
}
