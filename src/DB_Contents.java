package Project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Statement 추가
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class DB_Contents {
    private static final String DB_URL = "jdbc:sqlite:DB_database.db";

    private Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);

        // 외래 키 활성화
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("Foreign keys enabled for DB_Contents.");
        }

        return conn;
    }

    public void saveContent(int entryId, String title, String content, String photoPath) {
        String insertQuery = "INSERT INTO TextEntries (entry_id, title, content, photo_path) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, entryId); // `Entries` 테이블의 `id` 값
            stmt.setString(2, title); // 제목
            stmt.setString(3, content); // 내용
            stmt.setString(4, photoPath); // 사진 경로
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTitlesByDate(String date) {
        List<String> titles = new ArrayList<>();
        String query = "SELECT t.title FROM TextEntries t " +
                       "JOIN Entries e ON t.entry_id = e.id " +
                       "WHERE e.date = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DB_database.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, date); // 선택된 날짜로 제목 검색
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                titles.add(title); // 제목 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return titles;
    }
    //수정
    public Map<String, String> getContentByTitleAndDate(String title, String date) {
        String query = "SELECT t.content, t.photo_path FROM TextEntries t " +
                       "JOIN Entries e ON t.entry_id = e.id " +
                       "WHERE t.title = ? AND e.date = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, date);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, String> result = new HashMap<>();
                result.put("content", rs.getString("content"));
                result.put("photo_path", rs.getString("photo_path"));
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public void updateContent(String oldTitle, String date, String newTitle, String newContent, String photoPath) {
        String query = "UPDATE TextEntries SET title = ?, content = ?, photo_path = ? " +
                       "WHERE title = ? AND entry_id IN " +
                       "(SELECT id FROM Entries WHERE date = ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newTitle);       // 새 제목
            stmt.setString(2, newContent);    // 새 내용
            stmt.setString(3, photoPath);     // 이미지 경로
            stmt.setString(4, oldTitle);      // 이전 제목
            stmt.setString(5, date);          // 일기 날짜
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean deleteContentByTitleAndDate(String title, String date) {
        String query = "DELETE FROM TextEntries WHERE title = ? AND entry_id IN " +
                       "(SELECT id FROM Entries WHERE date = ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title); // 삭제할 제목
            stmt.setString(2, date); // 삭제할 날짜

            int affectedRows = stmt.executeUpdate(); // 삭제된 행 수 확인
            return affectedRows > 0; // 성공 여부 반환
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
