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
                // SQL 작업 수행 (테이블 생성, 데이터 삽입 등)
                
                // 제목을 저장하는 테이블 생성
                String createTitlesTable = "CREATE TABLE IF NOT EXISTS Titles (" +
                                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                            "title TEXT);";
                stmt.execute(createTitlesTable);

                // 내용을 저장하는 테이블 생성
                String createContentsTable = "CREATE TABLE IF NOT EXISTS Contents (" +
                                              "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                              "content TEXT);";
                stmt.execute(createContentsTable);

                // 그림을 저장하는 테이블 생성
                String createDrawingsTable = "CREATE TABLE IF NOT EXISTS Drawings (" +
                                              "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                              "drawing_path TEXT);";
                stmt.execute(createDrawingsTable);

                // 사진을 저장하는 테이블 생성
                String createPhotosTable = "CREATE TABLE IF NOT EXISTS Photos (" +
                                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                            "photo_path TEXT);";
                stmt.execute(createPhotosTable);

                // 날짜를 저장하는 테이블 생성
                String createDatesTable = "CREATE TABLE IF NOT EXISTS Dates (" +
                                           "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                           "date TEXT NOT NULL);";
                stmt.execute(createDatesTable);

                System.out.println("테이블이 성공적으로 생성되었습니다.");
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}