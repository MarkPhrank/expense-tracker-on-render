package com.mark.expense;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;


public class TransactionTest {
    
    @Test
    void testToCsv() {
        Transaction t = new Transaction("Еда", 450.0, LocalDate.of(2024, 5, 10));
        assertEquals("2024-05-10,Еда,450.0", t.toCsv());
    }
    
    @Test
    void testToString() {
        Transaction t = new Transaction("Транспорт", 120.5, LocalDate.now());
        assertTrue(t.toString().contains("Транспорт"));
        assertTrue(t.toString().contains("120.50"));
    }
}