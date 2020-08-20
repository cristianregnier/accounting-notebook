package com.cristianregnier.accountingnotebook.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
public class Transaction {

   public enum Type {credit, debit}

   private String id;
   private Type type;
   private BigDecimal amount;
   private Date effectiveDate;

   public Transaction(String id, Type type, BigDecimal amount, Date effectiveDate) {
      if (id == null || id.isEmpty())
         throw new IllegalArgumentException("Id id required");
      if (type == null)
         throw new IllegalArgumentException("Type is required");
      if (amount == null)
         throw new IllegalArgumentException("Amount is required");
      if (effectiveDate == null)
         throw new IllegalArgumentException("Effective Date is required");

      this.id = id;
      this.type = type;
      this.amount = amount;
      this.effectiveDate = effectiveDate;
   }

}
