package com.mark.expense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        UserManager.initTable(); // 🔥 Создаёт таблицу users при старте
        SpringApplication.run(Application.class, args);
    }
}
