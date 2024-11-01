import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB_Drawings {
    private static final String DB_URL = "jdbc:sqlite:DB_database.db";

    // 그림 저장 메서드
    public void saveDrawing(BufferedImage drawing) {
        try {
            // "drawings" 디렉토리 생성
            File drawingsDir = new File("drawings");
            if (!drawingsDir.exists()) {
                drawingsDir.mkdir(); // 디렉토리 생성
            }
            
         // 파일 이름 생성 (고유한 이름)
            String fileName = "drawing_" + System.currentTimeMillis() + ".png"; // 고유한 파일 이름 생성
            // 이미지 파일로 저장
            File outputfile = new File(drawingsDir, fileName);
            ImageIO.write(drawing, "png", outputfile);
            
            // 파일 경로를 데이터베이스에 저장
            saveDrawingPath(outputfile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("그림 저장 오류: " + e.getMessage());
        }
    }

    // 이미지 경로를 데이터베이스에 저장하는 메서드
    private void saveDrawingPath(String path) {
        String sql = "INSERT INTO Drawings (drawing_path) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, path);
            pstmt.executeUpdate();
            System.out.println("그림 경로가 성공적으로 저장되었습니다.");

        } catch (SQLException e) {
            System.out.println("데이터베이스 오류: " + e.getMessage());
        }
    }
}
