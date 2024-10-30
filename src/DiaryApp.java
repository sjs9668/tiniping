import javax.swing.*;

public class DiaryApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Diary Application");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        WriteDiary writeDiaryPanel = new WriteDiary();
        DrawingPanel drawingPanel = new DrawingPanel();
        ImageInsertPanel imageInsertPanel = new ImageInsertPanel();

       
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("글쓰기", writeDiaryPanel);
        tabbedPane.addTab("그림 그리기", drawingPanel);
        tabbedPane.addTab("사진 삽입", imageInsertPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }
}