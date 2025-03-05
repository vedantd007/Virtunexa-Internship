import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:database/movies.db";
    private static Connection conn = null;

    public static void connect() {
        try {
            // 🔹 Ensure the SQLite JDBC driver is loaded
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
            System.out.println("Database Connected Successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createTables() {
        if (conn == null) {
            System.err.println("Error: No database connection!");
            return;
        }
        String sql = "CREATE TABLE IF NOT EXISTS movies (id INTEGER PRIMARY KEY, name TEXT, seats INTEGER)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tables Created/Verified.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }
}
