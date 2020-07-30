package com.cristianregnier.accountingnotebook;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log
public class AccountingNotebookApplication {

   public static void main(String[] args) {
      SpringApplication.run(AccountingNotebookApplication.class, args);
   }

}
