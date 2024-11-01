import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB_Photos {
    private static final String DB_URL = "jdbc:sqlite:DB_database.db";

    // 사진 저장 메서드
    public void savePhoto(BufferedImage photo, File selectedFile) {
        // 파일이나 사진이 없으면 null로 경로 저장
        if (photo == null || selectedFile == null) {
            savePhotoPath(null);
            System.out.println("사진이 선택되지 않았습니다. null로 저장됩니다.");
            return;
        }
    	 
    	try {
            // 디렉토리 확인 및 생성
            File dir = new File("photos");
            if (!dir.exists()) {
                dir.mkdirs(); // 디렉토리 생성
            }
            
            String fileName = selectedFile.getName();
            // 파일 이름과 확장자 결정
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            String uniqueFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_" + System.currentTimeMillis() + "." + extension;
            File outputfile = new File(dir, uniqueFileName);
            
            // 이미지 형식에 따라 저장
            ImageIO.write(photo, extension, outputfile);
            
            // 파일 경로를 데이터베이스에 저장
            savePhotoPath(outputfile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("사진 저장 오류: " + e.getMessage());
        }
    }

    // 이미지 경로를 데이터베이스에 저장하는 메서드
    private void savePhotoPath(String path) {
        String sql = "INSERT INTO Photos (photo_path) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, path);
            pstmt.executeUpdate();
            
            // path가 null이 아닐 때만 성공 메시지 출력
            if (path != null) {
                System.out.println("사진 경로가 성공적으로 저장되었습니다.");
            }

        } catch (SQLException e) {
            System.out.println("데이터베이스 오류: " + e.getMessage());
        }
    }
}
