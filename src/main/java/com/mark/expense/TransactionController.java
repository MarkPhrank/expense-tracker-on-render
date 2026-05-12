package com.mark.expense;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    // GET /api/transactions → список всех расходов
    @GetMapping
    public List<Transaction> getAll(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        // 🔥 Здесь можно добавить фильтр: DatabaseManager.loadByUserId(userId)
        return DatabaseManager.loadAll();
    }
    
    // POST /api/transactions → добавить новый
    @PostMapping
    public Transaction create(@RequestBody Transaction t, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        // 🔥 Здесь можно добавить поле user_id в Transaction и БД
        DatabaseManager.save(t);
        return t;
    }
    
    // GET /api/transactions/stats → статистика
    @GetMapping("/stats")
    public Stats getStats() {
        List<Transaction> list = DatabaseManager.loadAll();
        if (list.isEmpty()) return new Stats(0, 0, 0, 0);
        
        double sum = list.stream().mapToDouble(t -> t.amount).sum();
        double avg = sum / list.size();
        double min = list.stream().mapToDouble(t -> t.amount).min().orElse(0);
        double max = list.stream().mapToDouble(t -> t.amount).max().orElse(0);
        
        return new Stats(sum, avg, min, max);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { DatabaseManager.deleteById(id); }

    @GetMapping("/filter")
    public List<Transaction> filter(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String category) {
        LocalDate s = (start != null) ? LocalDate.parse(start) : null;
        LocalDate e = (end != null) ? LocalDate.parse(end) : null;
        return DatabaseManager.filter(s, e, category);
    }
    
    // Внутренний класс для ответа (чтобы не тащить Jackson-аннотации)
    public static class Stats {
        public double total, average, min, max;
        public Stats(double total, double average, double min, double max) {
            this.total = total; this.average = average;
            this.min = min; this.max = max;
        }
    }
}
