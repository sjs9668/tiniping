
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WriteDiary extends JPanel {
    private JTextArea textArea; // 내용 입력 영역
    
    public WriteDiary() {
        // 내용 입력 영역 생성
        textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 레이아웃 설정 및 컴포넌트 추가
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(scrollPane);
    }
    
 // 텍스트 영역 반환 메서드
    public JTextArea getTextArea() {
        return textArea;
    }
}
