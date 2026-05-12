package com.mark.expense;

import java.sql.*;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class UserManager {
    private static final String URL = System.getenv("JDBC_URL") != null ?
            System.getenv("JDBC_URL") : "jdbc:postgresql://localhost:5432/expense_db";
    private static final String USER = System.getenv("DB_USER") != null ?
            System.getenv("DB_USER") : "mars";
    private static final String PASS = System.getenv("DB_PASS") != null ?
            System.getenv("DB_PASS") : "12345";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // 🔥 Инициализация таблицы пользователей (вызови один раз при старте)
    public static void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id SERIAL PRIMARY KEY, " +
                     "username VARCHAR(50) UNIQUE NOT NULL, " +
                     "password_hash VARCHAR(60) NOT NULL)";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) { /* игнорируем, если таблица уже есть */ }
    }

    // Регистрация: хешируем пароль перед сохранением
    public static boolean register(String username, String rawPassword) {
        initTable();
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false; // пользователь уже существует или ошибка БД
        }
    }

    // Проверка логина: сравниваем хеши
    public static Optional<User> login(String username, String rawPassword) {
        initTable();
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (BCrypt.checkpw(rawPassword, hash)) {
                    User u = new User(rs.getString("username"), hash);
                    u.id = rs.getInt("id");
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) { /* логировать в реальном проекте */ }
        return Optional.empty();
    }

    // Поиск по ID (нужен для валидации токена)
    public static Optional<User> findById(int id) {
        initTable();
        String sql = "SELECT id, username, password_hash FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User(rs.getString("username"), rs.getString("password_hash"));
                u.id = rs.getInt("id");
                return Optional.of(u);
            }
        } catch (SQLException e) { /* ... */ }
        return Optional.empty();
    }
}
