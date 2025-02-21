package persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbContext {
    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:tasklist.db");
            structureDb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void structureDb() throws SQLException {
        connection.prepareStatement(
                """
                        CREATE TABLE IF NOT EXISTS boards (
                            id INTEGER PRIMARY KEY,
                            title TEXT NOT NULL,
                            created_at TEXT NOT NULL
                        );"""
        ).execute();

        connection.prepareStatement(
                """
                        CREATE TABLE IF NOT EXISTS columns (
                            id INTEGER PRIMARY KEY,
                            title TEXT NOT NULL,
                            created_at TEXT NOT NULL,
                            type INT NOT NULL,
                            order_in_board INT NOT NULL,
                            board_id INT NOT NULL
                        );"""
        ).execute();

        connection.prepareStatement(
                """
                        CREATE TABLE IF NOT EXISTS cards (
                            id INTEGER PRIMARY KEY,
                            title TEXT NOT NULL,
                            created_at TEXT NOT NULL,
                            is_blocked INT NOT NULL,
                            description TEXT NOT NULL,
                            block_change_log TEXT NOT NULL,
                            column_id INT NOT NULL
                        );"""
        ).execute();
    }
}
