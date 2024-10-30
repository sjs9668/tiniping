import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WriteDiary extends JPanel {
    private JTextArea textArea;

    public WriteDiary() {
        textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = textArea.getText();
                saveContent(content);
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(scrollPane);
        add(saveButton);
    }

    // 일기 내용을 저장하는 메서드 (현재는 콘솔 출력)
    private void saveContent(String content) {
        System.out.println("저장된 내용: " + content);
    }
}