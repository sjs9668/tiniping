import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB_Titles {
    private static final String DB_URL = "jdbc:sqlite:DB_database.db";

    // 제목 저장 메서드
    public boolean saveTitle(String title) {
        String sql = "INSERT INTO Titles (title) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.executeUpdate();
            System.out.println("제목이 성공적으로 저장되었습니다.");
            return true; // 제목 저장 성공

        } catch (SQLException e) {
            System.out.println("데이터베이스 오류: " + e.getMessage());
            return false; // 제목 저장 실패
        }
    }
}