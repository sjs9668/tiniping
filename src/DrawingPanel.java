import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DrawingPanel extends JPanel {
    private int prevX, prevY;
    private Color currentColor = Color.BLACK; // 기본 색상

    public DrawingPanel() {
        setBackground(Color.WHITE);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Graphics g = getGraphics();
                g.setColor(currentColor); // 현재 선택된 색상
                g.drawLine(prevX, prevY, x, y);

                prevX = x;
                prevY = y;
                g.dispose();
            }
        });
    }

    // 색상 설정 메서드
    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    // 라디오 버튼 패널 생성 메서드
    public JPanel createColorSelectionPanel() {
        JPanel colorPanel = new JPanel(new GridLayout(1, 7));

        // 색상 옵션과 라디오 버튼 설정
        JRadioButton blackButton = new JRadioButton("검정색", true);
        JRadioButton redButton = new JRadioButton("빨간색");
        JRadioButton orangeButton = new JRadioButton("주황색");
        JRadioButton yellowButton = new JRadioButton("노란색");
        JRadioButton greenButton = new JRadioButton("초록색");
        JRadioButton blueButton = new JRadioButton("파란색");
        JRadioButton purpleButton = new JRadioButton("보라색");

        // 버튼 그룹으로 묶기
        ButtonGroup colorGroup = new ButtonGroup();
        colorGroup.add(blackButton);
        colorGroup.add(redButton);
        colorGroup.add(orangeButton);
        colorGroup.add(yellowButton);
        colorGroup.add(greenButton);
        colorGroup.add(blueButton);
        colorGroup.add(purpleButton);

        // 라디오 버튼에 이벤트 리스너 추가
        blackButton.addActionListener(e -> setCurrentColor(Color.BLACK));
        redButton.addActionListener(e -> setCurrentColor(Color.RED));
        orangeButton.addActionListener(e -> setCurrentColor(Color.ORANGE));
        yellowButton.addActionListener(e -> setCurrentColor(Color.YELLOW));
        greenButton.addActionListener(e -> setCurrentColor(Color.GREEN));
        blueButton.addActionListener(e -> setCurrentColor(Color.BLUE));
        purpleButton.addActionListener(e -> setCurrentColor(new Color(128, 0, 128))); // 보라색 RGB 값

        // 패널에 라디오 버튼 추가
        colorPanel.add(blackButton);
        colorPanel.add(redButton);
        colorPanel.add(orangeButton);
        colorPanel.add(yellowButton);
        colorPanel.add(greenButton);
        colorPanel.add(blueButton);
        colorPanel.add(purpleButton);

        return colorPanel;
    }
}