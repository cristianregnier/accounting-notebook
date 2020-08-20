package com.cristianregnier.accountingnotebook.service;

import com.cristianregnier.accountingnotebook.api.command.CommitTransactionCommand;
import com.cristianregnier.accountingnotebook.exception.NotFoundException;
import com.cristianregnier.accountingnotebook.model.Account;
import com.cristianregnier.accountingnotebook.model.Transaction;
import com.cristianregnier.accountingnotebook.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class AccountManager {

   private ReadWriteLock lock = new ReentrantReadWriteLock();

   /**
    * If no threads are reading or writing then only one thread can acquire the write lock
    */
   private Lock writeLock = lock.writeLock();

   /**
    * If no thread acquired the write lock or requested for it then multiple threads can acquire the read lock.
    * Reading locks writing to avoid concurrence errors.
    */
   private Lock readLock = lock.readLock();

   @Autowired
   private AccountRepository accounts;

   public BigDecimal getAccountBalance() {
      try {
         readLock.lock();
         return accounts.getAccount().getBalance();
      } finally {
         readLock.unlock();
      }
   }

   public Transaction commitTransaction(CommitTransactionCommand commitTransaction) {
      try {
         writeLock.lock();
         Account account = accounts.getAccount();

         Transaction newTransaction;
         if (commitTransaction.getType().equals(Transaction.Type.credit))
            newTransaction = account.credit(commitTransaction.getAmount());
         else if (commitTransaction.getType().equals(Transaction.Type.debit))
            newTransaction = account.debit(commitTransaction.getAmount());
         else
            throw new IllegalArgumentException("Unssuported operation: " + commitTransaction.getType().name());

         return newTransaction;
      } finally {
         writeLock.unlock();
      }
   }

   public List<Transaction> getHistory() {
      try {
         readLock.lock();
         return accounts.getAccount().getHistory();
      } finally {
         readLock.unlock();
      }
   }

   public Transaction findTransactionById(String id) {
      try {
         readLock.lock();
         List<Transaction> transactions =
                 accounts.getAccount().getHistory().stream().filter(trx -> {
                    return id.equals(trx.getId());
                 }).collect(Collectors.toList());

         if (transactions.isEmpty())
            throw new NotFoundException("transaction not found");
         else
            return transactions.get(0);
      } finally {
         readLock.unlock();
      }
   }

}
