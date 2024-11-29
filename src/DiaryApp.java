import javax.swing.*;
import java.awt.*;

public class DiaryApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Diary Application");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 각 기능 패널을 생성
        WriteDiary writeDiaryPanel = new WriteDiary();
        DrawingPanel drawingPanel = new DrawingPanel();
        ImageInsertPanel imageInsertPanel = new ImageInsertPanel();

        // DrawingPanel에서 라디오 버튼 패널을 생성하여 가져옴
        JPanel colorPanel = drawingPanel.createColorSelectionPanel();

        // 탭을 사용하여 패널을 전환 가능하도록 설정
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("글쓰기", writeDiaryPanel);
        tabbedPane.addTab("그림 그리기", drawingPanel);
        tabbedPane.addTab("사진 삽입", imageInsertPanel);

        // 프레임 레이아웃 설정
        frame.setLayout(new BorderLayout());
        frame.add(colorPanel, BorderLayout.NORTH);
        frame.add(tabbedPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
