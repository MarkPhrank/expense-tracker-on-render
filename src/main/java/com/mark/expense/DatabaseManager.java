package com.mark.expense;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = System.getenv("JDBC_URL") != null ?
        System.getenv("JDBC_URL") :
        "jdbc:postgresql://localhost:5432/expense_db";

    private static final String USER = System.getenv("DB_USER") != null ?
            System.getenv("DB_USER") : "mars";

    private static final String PASS = System.getenv("DB_PASS") != null ?
            System.getenv("DB_PASS") : "12345";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void save(Transaction t) {
        String sql = "INSERT INTO transactions (category, amount, txn_date) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            System.out.println("💾 Attempting to save: " + t.category + " / " + t.amount + " / " + t.date);
            
            ps.setString(1, t.category);
            ps.setDouble(2, t.amount);
            ps.setDate(3, Date.valueOf(t.date));
            
            int rows = ps.executeUpdate();
            System.out.println("💾 Rows affected: " + rows);
            
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    t.id = rs.getInt(1);
                    System.out.println("💾 Generated ID: " + t.id);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Ошибка БД: " + e.getMessage());
            e.printStackTrace(); // 🔥 Полный стектрейс в логи Render
        }
    }

    public static List<Transaction> loadAll() { return query("SELECT id, category, amount, txn_date FROM transactions ORDER BY txn_date DESC", null); }

    public static List<Transaction> filter(LocalDate start, LocalDate end, String category) {
        StringBuilder sql = new StringBuilder("SELECT id, category, amount, txn_date FROM transactions WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (category != null && !category.isBlank()) { sql.append(" AND category = ?"); params.add(category); }
        if (start != null) { sql.append(" AND txn_date >= ?"); params.add(Date.valueOf(start)); }
        if (end != null) { sql.append(" AND txn_date <= ?"); params.add(Date.valueOf(end)); }
        sql.append(" ORDER BY txn_date DESC");
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            return executeQuery(ps);
        } catch (SQLException e) { System.err.println("❌ Ошибка фильтра: " + e.getMessage()); return List.of(); }
    }

    public static void deleteById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions WHERE id = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("❌ Ошибка удаления: " + e.getMessage()); }
    }

    private static List<Transaction> query(String sql, Object[] params) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (params != null) for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            return executeQuery(ps);
        } catch (SQLException e) { System.err.println("❌ Ошибка загрузки: " + e.getMessage()); return List.of(); }
    }

    private static List<Transaction> executeQuery(PreparedStatement ps) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Transaction t = new Transaction(rs.getString("category"), rs.getDouble("amount"), rs.getDate("txn_date").toLocalDate());
                t.id = rs.getInt("id");
                list.add(t);
            }
        }
        return list;
    }
}
