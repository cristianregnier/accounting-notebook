package com.cristianregnier.accountingnotebook;

import com.cristianregnier.accountingnotebook.api.command.CommitTransactionCommand;
import com.cristianregnier.accountingnotebook.model.Account;
import com.cristianregnier.accountingnotebook.model.Transaction;
import com.cristianregnier.accountingnotebook.repository.AccountRepository;
import com.cristianregnier.accountingnotebook.service.AccountManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class AccountManagerTest {

   @TestConfiguration
   static class AccountManagerTestContextConf {
      @Bean
      public AccountManager accountManager() {
         return new AccountManager();
      }
   }

   @BeforeEach
   private void setUp() {
      Account account = new Account(BigDecimal.valueOf(100));

      Mockito.when(accounts.getAccount()).thenReturn(account);
   }

   @Autowired
   private AccountManager accountManager;

   @MockBean
   private AccountRepository accounts;

   @Test
   @DisplayName("Account balance should be $100")
   public void testAccountBalance() {
      assertEquals(BigDecimal.valueOf(100L), accountManager.getAccountBalance());
   }

   @Test
   @DisplayName("Credit should update balance to $125.78")
   public void testCredit() {
      accountManager.commitTransaction(
              new CommitTransactionCommand(Transaction.Type.credit, BigDecimal.valueOf(25.78D)));
      assertEquals(BigDecimal.valueOf(125.78D), accountManager.getAccountBalance());
   }

   @Test
   @DisplayName("Debit should update balance to $94.44")
   public void testDebit() {
      accountManager.commitTransaction(
              new CommitTransactionCommand(Transaction.Type.debit, BigDecimal.valueOf(5.56)));
      assertEquals(BigDecimal.valueOf(94.44D), accountManager.getAccountBalance());
   }

   @Test
   @DisplayName("History should contain 3 transactions")
   public void testHistory() {
      Transaction trx_1 = accountManager.commitTransaction(
              new CommitTransactionCommand(Transaction.Type.credit, BigDecimal.valueOf(25)));
      Transaction trx_2 = accountManager.commitTransaction(
              new CommitTransactionCommand(Transaction.Type.credit, BigDecimal.valueOf(5)));
      Transaction trx_3 = accountManager.commitTransaction(
              new CommitTransactionCommand(Transaction.Type.debit, BigDecimal.valueOf(10)));

      assertTrue(accountManager.getHistory().containsAll(Arrays.asList(trx_1, trx_2, trx_3)));
   }

   @Test
   @DisplayName("Transaction should be found by id")
   public void testFindTransactionById() {
      Transaction trx = accountManager.commitTransaction(
              new CommitTransactionCommand(Transaction.Type.credit, BigDecimal.valueOf(25)));

      assertEquals(trx, accountManager.findTransactionById(trx.getId()));
   }
}
