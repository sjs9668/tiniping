import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement; // Statement 추가
import java.util.List; // List 클래스 import
import java.util.ArrayList; // ArrayList 클래스 import
import java.sql.ResultSet;


public class DB_Drawings {
    private static final String DB_URL = "jdbc:sqlite:DB_database.db";

    private Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);

        // 외래 키 활성화
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("Foreign keys enabled for DB_Drawings.");
        }

        return conn;
    }

    public void saveDrawing(int entryId, BufferedImage drawing, String title) { 
        try {
            // "drawings" 디렉토리 생성
            File drawingsDir = new File("drawings");
            if (!drawingsDir.exists()) {
                drawingsDir.mkdir(); // 디렉토리 생성
            }

            // 파일 이름 생성 (고유한 이름)
            String fileName = "drawing_" + System.currentTimeMillis() + ".png";
            // 이미지 파일로 저장
            File outputfile = new File(drawingsDir, fileName);
            ImageIO.write(drawing, "png", outputfile);

            // 파일 경로를 데이터베이스에 저장
            saveDrawingPath(entryId, outputfile.getAbsolutePath(), title);
        } catch (IOException e) {
            System.out.println("그림 저장 오류: " + e.getMessage());
        }
    }

    private void saveDrawingPath(int entryId, String path, String title) { 
        String sql = "INSERT INTO DrawingEntries (entry_id, title, drawing_path) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, entryId); // 공통 테이블 ID 참조
            pstmt.setString(2, title); // 제목 저장
            pstmt.setString(3, path); // 그림 경로 저장

            pstmt.executeUpdate();
            System.out.println("그림 경로가 성공적으로 저장되었습니다. Title: " + title);

        } catch (SQLException e) {
            System.out.println("데이터베이스 오류: " + e.getMessage());
        }
    }
    
    public List<String> getDrawingsByDate(String date) {
        List<String> drawingTitles = new ArrayList<>();
        String query = "SELECT d.title FROM DrawingEntries d " +
                       "JOIN Entries e ON d.entry_id = e.id " +
                       "WHERE e.date = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DB_database.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, date); // 선택된 날짜를 쿼리에 전달
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                drawingTitles.add(title); // 그림 제목 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drawingTitles;
    }
    
    //수정
    public BufferedImage getDrawingByTitleAndDate(String title, String date) {
        String query = "SELECT d.drawing_path FROM DrawingEntries d " +
                       "JOIN Entries e ON d.entry_id = e.id " +
                       "WHERE d.title = ? AND e.date = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, date);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String path = rs.getString("drawing_path");
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    return ImageIO.read(imageFile);
                } else {
                    System.err.println("File not found: " + path);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateDrawing(String oldTitle, String date, String newTitle, BufferedImage newDrawing) {
        String query = "UPDATE DrawingEntries SET title = ?, drawing_path = ? " +
                       "WHERE title = ? AND entry_id IN " +
                       "(SELECT id FROM Entries WHERE date = ?)";
        try {
            // 그림 저장 디렉토리 설정
            File drawingsDir = new File("drawings");
            if (!drawingsDir.exists()) {
                drawingsDir.mkdir(); // 디렉토리 생성
            }

            // 새 그림 파일 저장
            String fileName = "drawing_" + System.currentTimeMillis() + ".png";
            File outputfile = new File(drawingsDir, fileName);
            ImageIO.write(newDrawing, "png", outputfile);

            // 데이터베이스 업데이트
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newTitle);
                stmt.setString(2, outputfile.getAbsolutePath());
                stmt.setString(3, oldTitle);
                stmt.setString(4, date);
                stmt.executeUpdate();
            }
        } catch (IOException e) {
            System.err.println("Failed to save the new drawing file: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteDrawingByTitleAndDate(String title, String date) {
        String query = "DELETE FROM DrawingEntries WHERE title = ? AND entry_id IN " +
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
