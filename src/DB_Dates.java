import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB_Dates {
    private static final String DB_URL = "jdbc:sqlite:DB_database.db";

    // 날짜 저장 메서드
    public void saveDate(String date) {
        String sql = "INSERT INTO Dates (date) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, date);
            pstmt.executeUpdate();
            System.out.println("날짜가 성공적으로 저장되었습니다: " + date);

        } catch (SQLException e) {
            System.out.println("데이터베이스 오류: " + e.getMessage());
        }
    }
}
