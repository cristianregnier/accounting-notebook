package com.cristianregnier.accountingnotebook.api.command;

import com.cristianregnier.accountingnotebook.model.Transaction;
import lombok.Getter;

@Getter
public class CommitTransactionCommand {
   private Transaction.Type type;
   private Double amount;

   public CommitTransactionCommand(Transaction.Type type, Double amount) {
      if (type == null)
         throw new IllegalArgumentException("Operation type is required.");
      if (amount == null)
         throw new IllegalArgumentException("Amount is required.");

      this.type = type;
      this.amount = amount;
   }
}
