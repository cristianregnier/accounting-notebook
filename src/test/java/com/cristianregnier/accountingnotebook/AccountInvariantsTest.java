package com.cristianregnier.accountingnotebook;

import com.cristianregnier.accountingnotebook.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountInvariantsTest {

   @Test
   @DisplayName("Account creation should succeed")
   public void testCreation() {
      new Account();
   }

   @Test
   @DisplayName("Account creation with initial balance should succeed")
   public void testCreationWithInitialBalance() {
      new Account(BigDecimal.ZERO);
   }

   @Test
   @DisplayName("Account creation with null balance should fail")
   public void testCreationWithNullInitialBalance() {
      assertThrows(IllegalArgumentException.class,
              () -> new Account(null),
              "Initial balance is required.");
   }

   @Test
   @DisplayName("Account creation with negative balance should fail")
   public void testCreationWithIllegalInitialBalance() {
      assertThrows(IllegalArgumentException.class,
              () -> new Account(BigDecimal.valueOf(-1L)),
              "Initial balance must be positive.");
   }

   @Test
   @DisplayName("Valid credit should suceed")
   public void testCredit() {
      new Account().credit(BigDecimal.ONE);
   }

   @Test
   @DisplayName("Null credit should fail")
   public void testNullCredit() {
      assertThrows(IllegalArgumentException.class,
              () -> new Account().credit(null),
              "Amount to credit is required.");
   }

   @Test
   @DisplayName("Illegal credit should fail")
   public void testIllegalCredit() {
      assertThrows(IllegalArgumentException.class,
              () -> new Account().credit(BigDecimal.ZERO),
              "Amount to credit must be more than zero.");
   }

   @Test
   @DisplayName("Valid debit should succeed")
   public void testDebit() {
      new Account(BigDecimal.ONE).debit(BigDecimal.ONE);
   }

   @Test
   @DisplayName("Null debit should fail")
   public void testNullDebit() {
      assertThrows(IllegalArgumentException.class,
              () -> new Account().debit(null),
              "Amount to debit is required.");
   }

   @Test
   @DisplayName("Illegal debit should fail")
   public void testIllegalDebit() {
      assertThrows(IllegalArgumentException.class,
              () -> new Account().debit(BigDecimal.ZERO),
              "Amount to debit must be positive.");
   }

   @Test
   @DisplayName("Withdraw with insufficient founds should fail")
   public void testInsufficientFounds() {
      assertThrows(IllegalStateException.class,
              () -> new Account().debit(BigDecimal.ONE),
              "Insufficient founds. Account Balance $0.");
   }
}
