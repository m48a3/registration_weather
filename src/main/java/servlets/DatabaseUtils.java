package servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Этот класс предоставляет методы утилит для работы с базой данных и установления соединения.
 */
public class DatabaseUtils {
    private static final String URL = "jdbc:postgresql://localhost:5432/db_users_weather";
    private static final String USER = "postgres";
    private static final String PASSWORD = "080900";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Устанавливает соединение с базой данных.
     *
     * @return Соединение с базой данных.
     * @throws SQLException Если не удается установить соединение с базой данных.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}