package com.mark.expense;

import java.time.LocalDate;

public class Transaction {
    public int id;
    String category;
    double amount;
    LocalDate date;

    Transaction(String category, double amount, LocalDate date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public int getId() { return id; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }

    // Преобразуем объект в строку для CSV
    public String toCsv() {
        return date + "," + category + "," + amount;
    }

    @Override
    public String toString() {
        return String.format("[%s] %.2f ₽ — %s", date, amount, category);
    }
}