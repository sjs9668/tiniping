import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Statement 추가
import java.util.List; // List 클래스 import
import java.util.ArrayList; // ArrayList 클래스 import

public class DB_Dates {
    private static final String DB_URL = "jdbc:sqlite:DB_database.db";

    private Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);

        // 외래 키 활성화
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("Foreign keys enabled for DB_Dates.");
        }

        return conn;
    }

    public int saveDate(String date) {
        int entryId = -1;
        String insertQuery = "INSERT OR IGNORE INTO Entries (date) VALUES (?)";
        String selectQuery = "SELECT id FROM Entries WHERE date = ?";

        try (Connection conn = getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            // 날짜 삽입
            insertStmt.setString(1, date);
            insertStmt.executeUpdate();

            // 삽입된 날짜의 ID 조회
            selectStmt.setString(1, date);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                entryId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("SQLException 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return entryId;
    }

    public List<String> getAllSavedDates() {
        List<String> dates = new ArrayList<>();
        String query = "SELECT DISTINCT date FROM Entries " +
                       "WHERE id IN (SELECT entry_id FROM TextEntries UNION SELECT entry_id FROM DrawingEntries)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DB_database.db");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dates.add(rs.getString("date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dates;
    }


}
