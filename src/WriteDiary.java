import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WriteDiary extends JPanel {
    private JTextField titleField; // 제목 입력 필드
    private JTextArea textArea; // 내용 입력 영역

    public WriteDiary() {
        // 제목 입력 필드 생성
        titleField = new JTextField(15); // 제목 필드의 열 크기 조정
        titleField.setMaximumSize(new Dimension(200, 30)); // 필드의 최대 크기 설정
        titleField.setBorder(BorderFactory.createTitledBorder("제목")); // 테두리에 "제목" 표시

        // 제목 필드를 패널에 추가하여 왼쪽 정렬
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // 왼쪽 정렬
        titlePanel.add(titleField);

        // 내용 입력 영역 생성
        textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 저장 버튼 생성
        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String content = textArea.getText();
                saveContent(title, content);
            }
        });

        // 레이아웃 설정 및 컴포넌트 추가
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(titlePanel); // 제목 패널을 상단에 추가
        add(scrollPane);
        add(saveButton);
    }

    // 일기 내용을 저장하는 메서드 (현재는 콘솔 출력)
    private void saveContent(String title, String content) {
        System.out.println("저장된 제목: " + title);
        System.out.println("저장된 내용: " + content);
    }
}
