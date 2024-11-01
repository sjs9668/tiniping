import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB_Contents {
    private static final String DB_URL = "jdbc:sqlite:DB_database.db";

    // 내용 저장 메서드
    public void saveContent(String content) {
        String sql = "INSERT INTO Contents (content) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, content);
            pstmt.executeUpdate();
            System.out.println("내용이 성공적으로 저장되었습니다.");

        } catch (SQLException e) {
            System.out.println("데이터베이스 오류: " + e.getMessage());
        }
    }
}