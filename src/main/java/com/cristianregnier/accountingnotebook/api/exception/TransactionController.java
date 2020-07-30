package com.cristianregnier.accountingnotebook.api.exception;

import com.cristianregnier.accountingnotebook.api.command.CommitTransactionCommand;
import com.cristianregnier.accountingnotebook.model.Account;
import com.cristianregnier.accountingnotebook.model.Transaction;
import com.cristianregnier.accountingnotebook.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

   @Autowired
   private AccountRepository accounts;

   @GetMapping
   public List<Transaction> getHistory() {
      return accounts.getAccount().getHistory();
   }

   @PostMapping
   public Transaction commitTransaction(
           @RequestBody CommitTransactionCommand commitTransaction) {
      Account account = accounts.getAccount();

      Transaction newTransaction;
      if (commitTransaction.getType().equals(Transaction.Type.credit))
         newTransaction = account.credit(commitTransaction.getAmount());
      else if (commitTransaction.getType().equals(Transaction.Type.debit))
         newTransaction = account.debit(commitTransaction.getAmount());
      else
         throw new IllegalArgumentException("Unssuported operation: " + commitTransaction.getType().name());

      return newTransaction;
   }

   @GetMapping("/{id}")
   public Transaction findTransactionById(@PathVariable String id) {
      List<Transaction> transactions =
              accounts.getAccount().getHistory().stream().filter(trx -> {
                 return id.equals(trx.getId());
              }).collect(Collectors.toList());

      if(transactions.isEmpty())
         throw new NotFoundException("transaction not found");
      else
         return transactions.get(0);
   }
}
