package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class DrawingPanel extends JPanel {
    private int prevX, prevY;
    private Color currentColor = Color.BLACK; // 기본 색상
    private boolean isEraserMode = false; // 지우개 모드 플래그
    private int eraserSize = 20; // 지우개 크기
    private BufferedImage canvas; // 그리기 버퍼

    public DrawingPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 400));
        canvas = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB); // 초기화
        clearCanvas(); // 캔버스 초기화

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Graphics2D g2d = canvas.createGraphics();
                if (isEraserMode) {
                    g2d.setColor(getBackground()); // 지우개는 배경색으로 그림
                    g2d.fillOval(x - eraserSize / 2, y - eraserSize / 2, eraserSize, eraserSize);
                } else {
                    g2d.setColor(currentColor); // 선택된 색상으로 그림
                    g2d.setStroke(new BasicStroke(2)); // 선 두께 설정
                    g2d.drawLine(prevX, prevY, x, y);
                }
                g2d.dispose();

                prevX = x;
                prevY = y;
                repaint(); // 패널 다시 그리기
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null); // 캔버스를 패널에 그림

        if (isEraserMode) {
            // 지우개 범위 표시
            Point mousePosition = getMousePosition();
            if (mousePosition != null) {
                g.setColor(Color.GREEN); // 라임색
                g.drawOval(mousePosition.x - eraserSize / 2, mousePosition.y - eraserSize / 2, eraserSize, eraserSize);
            }
        }
    }

    // 캔버스를 초기화 (흰색 배경)
    public void clearCanvas() {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.dispose();
        repaint();
    }

    // 그림이 비어 있는지 확인하는 메서드
    public boolean isDrawingEmpty() {
        if (canvas == null) {
            return true; // 캔버스가 초기화되지 않았으면 비어있음
        }

        // 이미지의 모든 픽셀이 초기 상태인지 확인 (흰색)
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                int pixel = canvas.getRGB(x, y) & 0xFFFFFF; // 투명도 제외
                if (pixel != Color.WHITE.getRGB()) {
                    return false; // 흰색이 아닌 픽셀 발견
                }
            }
        }
        return true; // 모든 픽셀이 흰색
    }


    // BufferedImage 반환
    public BufferedImage getBufferedImage() {
        return canvas;
    }

    // 색상 설정 메서드
    public void setCurrentColor(Color color) {
        this.currentColor = color;
        this.isEraserMode = false; // 색상 변경 시 지우개 모드 해제
    }

    // 지우개 모드 설정 메서드
    public void activateEraser() {
        this.isEraserMode = true;
    }

    // 라디오 버튼 패널 생성 메서드
    public JPanel createColorSelectionPanel() {
        JPanel colorPanel = new JPanel(new GridLayout(1, 8)); // 지우개 버튼을 추가하여 8개의 버튼

        // 색상 옵션과 라디오 버튼 설정
        JRadioButton blackButton = new JRadioButton("검정색", true);
        JRadioButton redButton = new JRadioButton("빨간색");
        JRadioButton orangeButton = new JRadioButton("주황색");
        JRadioButton yellowButton = new JRadioButton("노란색");
        JRadioButton greenButton = new JRadioButton("초록색");
        JRadioButton blueButton = new JRadioButton("파란색");
        JRadioButton purpleButton = new JRadioButton("보라색");
        JRadioButton eraserButton = new JRadioButton("지우개"); // 지우개 버튼

        // 버튼 그룹으로 묶기
        ButtonGroup colorGroup = new ButtonGroup();
        colorGroup.add(blackButton);
        colorGroup.add(redButton);
        colorGroup.add(orangeButton);
        colorGroup.add(yellowButton);
        colorGroup.add(greenButton);
        colorGroup.add(blueButton);
        colorGroup.add(purpleButton);
        colorGroup.add(eraserButton);

        // 라디오 버튼에 이벤트 리스너 추가
        blackButton.addActionListener(e -> setCurrentColor(Color.BLACK));
        redButton.addActionListener(e -> setCurrentColor(Color.RED));
        orangeButton.addActionListener(e -> setCurrentColor(Color.ORANGE));
        yellowButton.addActionListener(e -> setCurrentColor(Color.YELLOW));
        greenButton.addActionListener(e -> setCurrentColor(Color.GREEN));
        blueButton.addActionListener(e -> setCurrentColor(Color.BLUE));
        purpleButton.addActionListener(e -> setCurrentColor(new Color(128, 0, 128))); // 보라색 RGB 값
        eraserButton.addActionListener(e -> activateEraser()); // 지우개 모드 활성화

        // 패널에 라디오 버튼 추가
        colorPanel.add(blackButton);
        colorPanel.add(redButton);
        colorPanel.add(orangeButton);
        colorPanel.add(yellowButton);
        colorPanel.add(greenButton);
        colorPanel.add(blueButton);
        colorPanel.add(purpleButton);
        colorPanel.add(eraserButton);

        return colorPanel;
    }
    
    // BufferedImage를 설정하는 메서드
    public void setBufferedImage(BufferedImage drawing) {
        if (drawing != null) {
            this.canvas = drawing;
            repaint(); // 변경된 그림을 다시 그리기
        }
    }
    
    
}
