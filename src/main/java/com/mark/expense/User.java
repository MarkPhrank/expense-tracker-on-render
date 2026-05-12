package com.mark.expense;

public class User {
    public int id;
    public String username;
    public String passwordHash; // ⚠️ Никогда не храни пароли в открытом виде!

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Геттеры для Jackson
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}
