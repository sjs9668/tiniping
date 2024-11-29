import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:DB_database.db"; // 데이터베이스 파일 경로

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("데이터베이스에 연결되었습니다!");
                Statement stmt = conn.createStatement();

                // 공통 테이블 생성
                String createEntriesTable = "CREATE TABLE IF NOT EXISTS Entries (" +
                                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                            "date TEXT NOT NULL);"; // 날짜 정보 저장
                stmt.execute(createEntriesTable);

                // 텍스트 일기 테이블 생성 (사진 추가 가능)
                String createTextEntriesTable = "CREATE TABLE IF NOT EXISTS TextEntries (" +
                                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                "entry_id INTEGER NOT NULL, " + // 공통 테이블 참조
                                                "title TEXT, " +
                                                "content TEXT, " +
                                                "photo_path TEXT, " + // 사진 경로 추가
                                                "FOREIGN KEY(entry_id) REFERENCES Entries(id) ON DELETE CASCADE);";
                stmt.execute(createTextEntriesTable);

                // 그림 일기 테이블 생성 (사진 필드 없음)
                String createDrawingEntriesTable = "CREATE TABLE IF NOT EXISTS DrawingEntries (" +
                                                   "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                   "entry_id INTEGER NOT NULL, " + // 공통 테이블 참조
                                                   "title TEXT, " +
                                                   "drawing_path TEXT, " + // 그림 경로
                                                   "FOREIGN KEY(entry_id) REFERENCES Entries(id) ON DELETE CASCADE);";
                stmt.execute(createDrawingEntriesTable);

                System.out.println("테이블이 성공적으로 생성되었습니다.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
